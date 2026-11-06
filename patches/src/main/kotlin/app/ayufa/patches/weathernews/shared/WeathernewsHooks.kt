package app.ayufa.patches.weathernews.shared

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod

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
 * Makes an instance method return `null` immediately (for
 * object-returning methods). See [returnFalseImmediately] for why
 * p0 is safe to overwrite here.
 */
internal fun MutableMethod.returnNullImmediately() {
    addInstructions(
        0,
        """
            const/4 p0, 0x0
            return-object p0
        """
    )
}
