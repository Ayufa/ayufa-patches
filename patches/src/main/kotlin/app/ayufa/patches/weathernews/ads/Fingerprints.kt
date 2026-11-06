package app.ayufa.patches.weathernews.ads

import app.morphe.patcher.Fingerprint

/**
 * The central ad gate of the app's ad wrapper. Every banner surface
 * (BannerAdView, the pinpoint fragments, the radar fragment) asks
 * Ad.isAdEnabled() — which delegates here — before requesting or
 * creating an ad. The stock implementation returns true
 * unconditionally. Verified against Weathernews 7.5.1.
 */
internal object AdDelegateImplIsAdEnabledFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/wrapper/ad/AdDelegateImpl;",
    name = "isAdEnabled",
    returnType = "Z"
)

/**
 * Loads and shows the app-open (startup full-screen) ad. Called from
 * MainActivity on launch. Verified against Weathernews 7.5.1.
 */
internal object AdDelegateImplLoadAndShowAppOpenAdFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/wrapper/ad/AdDelegateImpl;",
    name = "loadAndShowAppOpenAd",
    returnType = "V",
    parameters = listOf(
        "Landroid/app/Activity;",
        "Lkotlin/jvm/functions/Function3;"
    )
)

/**
 * Shows an already-loaded app-open ad. Gated as a second line of
 * defence behind [AdDelegateImplLoadAndShowAppOpenAdFingerprint].
 * Verified against Weathernews 7.5.1.
 */
internal object AdDelegateImplShowAppOpenAdFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/wrapper/ad/AdDelegateImpl;",
    name = "showAppOpenAd",
    returnType = "V",
    parameters = listOf(
        "Landroid/app/Activity;",
        "Lcom/weathernews/wrapper/ad/AppOpenAdResponse;",
        "Lkotlin/jvm/functions/Function3;"
    )
)
