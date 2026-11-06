package app.ayufa.patches.simeji.review

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.finishImmediatelyAfterSuperOnCreate
import app.ayufa.patches.simeji.shared.returnVoidImmediately

/**
 * Simeji's own Google Play guide / rating dialog activity.
 * Verified against Simeji 20.7.
 */
internal object GpGuideDialogActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simejicore/popup/gppop/GPGuideDialogActivity;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

/**
 * Entry point of the Play in-app review flow. Verified against
 * Simeji 20.7.
 */
internal object GpInAppReviewAskFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simejicore/popup/gppop/GpInAppReview;",
    name = "askForReview",
    returnType = "V",
    parameters = listOf("Landroid/app/Activity;")
)

@Suppress("unused")
val disableReviewPromptsPatch = bytecodePatch(
    name = "Disable review prompts",
    description = "Disables the Google Play rating prompts: both " +
        "Simeji's own guide dialog and the Play in-app review flow.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        GpGuideDialogActivityOnCreateFingerprint.method
            .finishImmediatelyAfterSuperOnCreate()
        GpInAppReviewAskFingerprint.method.returnVoidImmediately()
    }
}
