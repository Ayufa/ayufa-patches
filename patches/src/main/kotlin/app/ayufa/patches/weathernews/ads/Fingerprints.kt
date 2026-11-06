package app.ayufa.patches.weathernews.ads

import app.morphe.patcher.Fingerprint

/**
 * Populates a banner ad view (bottom banners, the radar screen's
 * banner, ...): asks the ad wrapper for a TAM (Amazon) response and
 * builds the actual ad view. If this never runs, the banner area
 * stays empty. Verified against Weathernews 7.5.1.
 *
 * NOTE: the app-wide Ad.isAdEnabled() gate is deliberately NOT
 * patched. Forcing it to false in v1.3.0-dev.13/14 coincided with
 * the radar screens losing their map on a real device, so ad
 * suppression happens at the creation points instead and the rest
 * of the app keeps seeing the stock "ads enabled" state.
 */
internal object BannerAdViewCreateBannerAdFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/view/BannerAdView;",
    name = "createBannerAd",
    returnType = "V",
    parameters = listOf(
        "Lcom/weathernews/wrapper/ad/Ad\$BannerAdType;",
        "[Lcom/weathernews/wrapper/ad/Ad\$AdSizeType;",
        "Ljava/util/Map;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/Double;",
        "Ljava/lang/Double;",
        "Z",
        "Ljava/lang/String;",
        "Lkotlin/jvm/functions/Function2;"
    )
)

/**
 * The preload variant of [BannerAdViewCreateBannerAdFingerprint]
 * that takes an already-fetched TAM response. Verified against
 * Weathernews 7.5.1.
 */
internal object BannerAdViewCreateBannerAdWithTamFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/view/BannerAdView;",
    name = "createBannerAd",
    returnType = "V",
    parameters = listOf(
        "Lcom/weathernews/wrapper/ad/Ad\$BannerAdType;",
        "[Lcom/weathernews/wrapper/ad/Ad\$AdSizeType;",
        "Ljava/util/Map;",
        "Ljava/lang/String;",
        "Ljava/lang/String;",
        "Ljava/lang/Double;",
        "Ljava/lang/Double;",
        "Z",
        "Lcom/weathernews/wrapper/ad/TamResponse;",
        "Ljava/lang/String;",
        "Z",
        "Ljava/lang/String;",
        "Lkotlin/jvm/functions/Function2;"
    )
)

/**
 * Hands ad data to the pinpoint (forecast detail) native banner
 * view. Without data the banner never renders. Verified against
 * Weathernews 7.5.1.
 */
internal object WniAdBannerViewSetDataFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/view/pinpoint/WniAdBannerView;",
    name = "setData",
    returnType = "V",
    parameters = listOf(
        "Lcom/weathernews/touch/model/pinpoint/WniAdBanner\$AdBanner;",
        "Lcom/weathernews/android/app/LifecycleContext;"
    )
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
