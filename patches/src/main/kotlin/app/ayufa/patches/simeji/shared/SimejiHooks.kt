package app.ayufa.patches.simeji.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Makes an instance method return `false` immediately. Register p0
 * always exists in an instance method; overwriting it with a constant
 * and returning it avoids depending on the method's local registers.
 */
internal fun MutableMethod.returnFalseImmediately() {
    addInstructions(
        0,
        """
            const/4 p0, 0x0
            return p0
        """
    )
}

/**
 * Makes a method return immediately without doing any work.
 */
internal fun MutableMethod.returnVoidImmediately() {
    addInstructions(0, "return-void")
}

/**
 * Makes an activity finish itself right after its super.onCreate()
 * call, so the screen never becomes visible. Used for popup and
 * promotion activities that should never appear.
 */
internal fun MutableMethod.finishImmediatelyAfterSuperOnCreate() {
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
    addInstructions(
        superIndex + 1,
        """
            invoke-virtual { p0 }, Landroid/app/Activity;->finish()V
            return-void
        """
    )
}
