package app.ayufa.patches.simeji.pet

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnFalseImmediately
import app.ayufa.patches.simeji.shared.returnVoidImmediately

/**
 * Drives every visibility change of the keyboard's pet (AI assistant)
 * icon: it attaches the icon view to the keyboard's top area and
 * shows it. Verified against Simeji 20.7.
 */
internal object PetKeyboardManagerChangeModeFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/pet/PetKeyboardManager;",
    name = "changeMode",
    returnType = "V",
    parameters = listOf("I")
)

/**
 * Reports whether the pet icon may currently be shown on the
 * keyboard. Verified against Simeji 20.7.
 */
internal object PetKeyboardManagerCanShowFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/pet/PetKeyboardManager;",
    name = "canShow",
    returnType = "Z"
)

@Suppress("unused")
val hideAiAssistantIconPatch = bytecodePatch(
    name = "Hide AI assistant icon",
    description = "Hides the AI assistant (robot) icon shown at the " +
        "top right of the keyboard, together with its speech-bubble " +
        "messages: the pet manager is frozen in its off state, so " +
        "the icon is never attached to the keyboard. Typing and " +
        "conversion are not affected.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        // Freezing changeMode keeps the manager's mode at 0 (off)
        // forever: the icon view is never added to the keyboard
        // container and never shown, and canShow()/canOpenAssistant()
        // consistently report the pet as unavailable.
        PetKeyboardManagerChangeModeFingerprint.method.returnVoidImmediately()
        PetKeyboardManagerCanShowFingerprint.method.returnFalseImmediately()
    }
}
