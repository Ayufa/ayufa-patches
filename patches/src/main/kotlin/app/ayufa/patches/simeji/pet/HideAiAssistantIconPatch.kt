package app.ayufa.patches.simeji.pet

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import com.android.tools.smali.dexlib2.AccessFlags
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnFalseImmediately
import app.ayufa.patches.simeji.shared.returnVoidImmediately
import app.ayufa.patches.simeji.shared.returnZeroImmediately

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

/**
 * Height of the "stuff" band at the very top of the keyboard: the
 * strip that hosts the pet icon (and suggestion/ad items sized from
 * the same value). The candidate line has its own separate height,
 * so zeroing this collapses only the top strip. Verified against
 * Simeji 20.7.
 */
internal object StuffHeightFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/KbdControlPanelHeightVal;",
    name = "getStuffHeight",
    returnType = "I",
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC)
)

@Suppress("unused")
val hideAiAssistantIconPatch = bytecodePatch(
    name = "Hide AI assistant icon",
    description = "Hides the AI assistant (robot) icon shown at the " +
        "top right of the keyboard, together with its speech-bubble " +
        "messages: the pet manager is frozen in its off state, so " +
        "the icon is never attached to the keyboard. The top strip " +
        "that hosted the icon is collapsed as well, so no empty band " +
        "remains above the toolbar. Typing and conversion are not " +
        "affected.",
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
        // Collapse the now-empty top strip: the keyboard's total
        // height is the candidate line height plus this "stuff"
        // height, so returning 0 removes the band entirely. Its
        // other consumers only size pet/suggestion/ad items, which
        // are hidden anyway.
        StuffHeightFingerprint.method.returnZeroImmediately()
    }
}
