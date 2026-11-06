package app.ayufa.patches.simeji.ads

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnFalseImmediately
import app.ayufa.patches.simeji.shared.returnVoidImmediately

private val blockRewardedAdsOption = booleanOption(
    key = "blockRewardedAds",
    default = false,
    title = "Also block rewarded ads",
    description = "Also blocks rewarded ads. Some features that are unlocked " +
        "by watching an ad (for example custom skin trials) stop working, " +
        "so this is off by default."
)

@Suppress("unused")
val hideAdsPatch = bytecodePatch(
    name = "Hide ads",
    description = "Hides ads in Simeji: suggestion bar ads, store entry " +
        "interstitials and web campaign popups. The central ad gate is " +
        "forced off, and ad candidates are never fetched or stored.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    blockRewardedAdsOption()

    execute {
        val blockRewardedAds by blockRewardedAdsOption

        // Central ad gate: never show ads.
        AdFilterHelperCanShowFingerprint.method.returnFalseImmediately()

        // Suggestion bar ad candidates: never prepare or store them.
        AdCandidateManagerPrepareFingerprint.method.returnVoidImmediately()
        AdCandidateManagerRefreshFingerprint.method.returnVoidImmediately()

        // Store entry interstitial: startGoing() returns itself without
        // starting anything.
        StoreEntryAdTriggerStartGoingFingerprint.method.addInstructions(
            0,
            "return-object p0"
        )

        // Web campaign / EC popup: never show.
        WebOperationPopupShowFingerprint.method.returnFalseImmediately()

        if (blockRewardedAds == true) {
            AdShowHelperPreloadRewardedFingerprint.method.returnVoidImmediately()
            AdShowHelperShowRewardedFingerprint.method.returnVoidImmediately()
        }
    }
}
