package app.ayufa.patches.weathernews.ads

import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.weathernews.shared.Constants.COMPATIBILITY_WEATHERNEWS
import app.ayufa.patches.weathernews.shared.returnVoidImmediately

@Suppress("unused")
val hideWeathernewsAdsPatch = bytecodePatch(
    name = "広告を非表示",
    description = "ウェザーニュースの広告（バナー広告、起動時の全画面広告）を" +
        "非表示にします。",
    default = true
) {
    compatibleWith(COMPATIBILITY_WEATHERNEWS)

    execute {
        // Banners: never build the ad inside the banner views. The
        // app-wide isAdEnabled() state is left untouched on purpose
        // (see the note in Fingerprints.kt).
        BannerAdViewCreateBannerAdFingerprint.method
            .returnVoidImmediately()
        BannerAdViewCreateBannerAdWithTamFingerprint.method
            .returnVoidImmediately()

        // Pinpoint native banners: never hand them ad data.
        WniAdBannerViewSetDataFingerprint.method
            .returnVoidImmediately()

        // Startup full-screen (app-open) ad: never load or show.
        AdDelegateImplLoadAndShowAppOpenAdFingerprint.method
            .returnVoidImmediately()
        AdDelegateImplShowAppOpenAdFingerprint.method
            .returnVoidImmediately()
    }
}
