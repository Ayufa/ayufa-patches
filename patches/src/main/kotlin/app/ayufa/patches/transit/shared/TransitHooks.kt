package app.ayufa.patches.transit.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
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
        instruction.opcode == Opcode.INVOKE_DIRECT &&
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
