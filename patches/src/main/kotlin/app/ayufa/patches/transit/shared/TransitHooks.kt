package app.ayufa.patches.transit.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction3rc
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Makes a view permanently GONE by setting its visibility right
 * after the super-constructor call, before the constructor body
 * runs. At that point no local register holds anything the rest of
 * the constructor needs (the pre-super code only passes arguments
 * through the parameter registers), so writing the constant to v0
 * is safe. p0 is `this` and stays valid for the whole constructor.
 *
 * Used on the ad placement views, whose own load/render methods
 * are R8-obfuscated per screen, while the view classes and their
 * constructors kept stable names and signatures.
 */
internal fun MutableMethod.goneAfterSuperInit() {
    addInstructions(
        superInitIndex() + 1,
        """
            const/16 v0, 0x8
            invoke-virtual { p0, v0 }, Landroid/view/View;->setVisibility(I)V
        """
    )
}

internal fun MutableMethod.superInitIndex(): Int {
    val methodInstructions = implementation?.instructions
        ?: throw PatchException("constructor has no implementation: $this")
    val superIndex = methodInstructions.indexOfFirst { instruction ->
        (instruction.opcode == Opcode.INVOKE_DIRECT ||
            instruction.opcode == Opcode.INVOKE_DIRECT_RANGE) &&
            ((instruction as? ReferenceInstruction)?.reference as? MethodReference)
                ?.name == "<init>"
    }
    if (superIndex < 0) {
        throw PatchException("No super constructor call found in $this")
    }
    return superIndex
}

/**
 * Makes an activity finish itself right after its super.onCreate()
 * call, so the screen never becomes visible. Used for the LY
 * linkage promotion modal.
 */
internal fun MutableMethod.finishImmediatelyAfterSuperOnCreate() {
    addInstructions(
        superOnCreateIndex() + 1,
        """
            invoke-virtual { p0 }, Landroid/app/Activity;->finish()V
            return-void
        """
    )
}

internal fun MutableMethod.superOnCreateIndex(): Int {
    val methodInstructions = implementation?.instructions
        ?: throw PatchException("onCreate has no implementation: $this")
    val superIndex = methodInstructions.indexOfFirst { instruction ->
        instruction.opcode == Opcode.INVOKE_SUPER &&
            ((instruction as? ReferenceInstruction)?.reference as? MethodReference)
                ?.name == "onCreate"
    }
    if (superIndex < 0) {
        throw PatchException("No super.onCreate() call found in $this")
    }
    return superIndex
}

/**
 * Rewrites every View.setVisibility(int) call inside this method
 * so the argument is forced to GONE (8) immediately before the
 * call, whatever the caller computed. Used on the ad views'
 * visibility state machines, which otherwise force the ad slot's
 * inner root and ad container back to VISIBLE on every ad state
 * change. Insertions run from the last call site backwards so
 * earlier indices stay valid.
 */
internal fun MutableMethod.forceVisibilityCallsGone() {
    val methodInstructions = implementation?.instructions ?: return
    val callSites = methodInstructions.mapIndexedNotNull { index, instruction ->
        val reference =
            (instruction as? ReferenceInstruction)?.reference as? MethodReference
        if ((instruction.opcode == Opcode.INVOKE_VIRTUAL ||
                instruction.opcode == Opcode.INVOKE_VIRTUAL_RANGE) &&
            reference?.name == "setVisibility" &&
            reference.returnType == "V" &&
            reference.parameterTypes == listOf("I")
        ) {
            index
        } else {
            null
        }
    }
    callSites.sortedDescending().forEach { index ->
        val argRegister = when (val instruction = methodInstructions[index]) {
            is Instruction35c -> instruction.registerD
            is Instruction3rc -> instruction.startRegister + 1
            else -> return@forEach
        }
        addInstructions(index, "const/16 v$argRegister, 0x8")
    }
}
