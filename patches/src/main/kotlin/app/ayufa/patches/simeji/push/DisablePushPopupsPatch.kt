package app.ayufa.patches.simeji.push

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.finishImmediatelyAfterSuperOnCreate

/**
 * The dialog-style activity shown for push campaign messages.
 * Verified against Simeji 23.7.
 */
internal object PopupMsgDialogActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/push/PopupMsgDialogActivity;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

@Suppress("unused")
val disablePushPopupsPatch = bytecodePatch(
    name = "Disable push popups",
    description = "Prevents push campaign popup dialogs from appearing " +
        "on screen. Normal notifications are not affected.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        PopupMsgDialogActivityOnCreateFingerprint.method
            .finishImmediatelyAfterSuperOnCreate()
    }
}
