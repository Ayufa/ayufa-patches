package app.ayufa.patches.simeji.billing

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.finishImmediatelyAfterSuperOnCreate

/**
 * The retention ("please stay") screen shown when cancelling a VIP
 * subscription. Verified against Simeji 23.7.
 */
internal object SubscriptionKeepActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/billing/subscription/SubscriptionKeepAct;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

@Suppress("unused")
val skipSubscriptionRetentionPatch = bytecodePatch(
    name = "Skip subscription retention screen",
    description = "Skips the retention screen shown during VIP " +
        "subscription cancellation, going straight to the cancellation " +
        "flow.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        SubscriptionKeepActivityOnCreateFingerprint.method
            .finishImmediatelyAfterSuperOnCreate()
    }
}
