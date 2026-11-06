package app.ayufa.patches.simeji.promotion

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.finishImmediatelyAfterSuperOnCreate

/**
 * The VIP guide / plan screen. It doubles as the in-app subscription
 * purchase screen. Verified against Simeji 20.7.
 */
internal object VipLevelGuideActivityOnCreateFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/home/vip/VipLevelGuideActivityNew;",
    name = "onCreate",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;")
)

@Suppress("unused")
val blockVipGuidePatch = bytecodePatch(
    name = "Block VIP guide screen",
    description = "Prevents the VIP guide screen from opening. Note: " +
        "this screen is also the in-app subscription purchase screen, " +
        "so subscribing from inside the app becomes impossible.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        VipLevelGuideActivityOnCreateFingerprint.method
            .finishImmediatelyAfterSuperOnCreate()
    }
}
