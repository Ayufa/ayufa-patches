package app.ayufa.patches.simeji.promotion

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.finishImmediatelyAfterSuperOnCreate

/**
 * The full-screen promotion activity (campaign / upsell pages).
 * Verified against Simeji 20.7.
 */
internal object PromotionActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/home/promotion/PromotionActivity;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

@Suppress("unused")
val hidePromotionsPatch = bytecodePatch(
    name = "Hide promotions",
    description = "Prevents the full-screen promotion activity from " +
        "opening, so campaign and upsell pages never appear.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        PromotionActivityOnCreateFingerprint.method
            .finishImmediatelyAfterSuperOnCreate()
    }
}
