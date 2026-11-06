package app.ayufa.patches.transit.ads

import app.morphe.patcher.Fingerprint

private val CONTEXT_ATTRS = listOf(
    "Landroid/content/Context;",
    "Landroid/util/AttributeSet;"
)
private val CONTEXT_ATTRS_INT = CONTEXT_ATTRS + "I"

private fun constructorFingerprints(
    definingClass: String,
    parameterSets: List<List<String>>
): List<Fingerprint> = parameterSets.map { parameters ->
    Fingerprint(
        definingClass = definingClass,
        name = "<init>",
        returnType = "V",
        parameters = parameters
    )
}

/**
 * Constructors of every ad placement view in the app. The views'
 * load/render methods are R8-obfuscated and differ per screen,
 * but the view classes kept their names and the standard Android
 * view constructor signatures, so each view is hidden (GONE) at
 * construction time instead. Verified against Yahoo! Transit 8.5.8.
 */
internal val adViewConstructorFingerprints: List<Fingerprint> = listOf(
    // Each screen's banner / native ad views (transit/ad).
    "Ljp/co/yahoo/android/apps/transit/ad/NaviSearchTopAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/NaviSearchBottomAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/SearchResultListBottomAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/StationAdTopView;",
    "Ljp/co/yahoo/android/apps/transit/ad/StationAdBottomView;",
    "Ljp/co/yahoo/android/apps/transit/ad/DiainfoBottomAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/DiainfoDetailAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/MyPageAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/RailAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/ReservationAdView;",
    // Shared native-ad base views and the infeed variants.
    "Ljp/co/yahoo/android/apps/transit/ad/YdnAdView;",
    "Ljp/co/yahoo/android/apps/transit/ad/YdnAdAutoSizeView;",
    "Ljp/co/yahoo/android/apps/transit/ad/YdnInfeed001ImageSize600View;",
    "Ljp/co/yahoo/android/apps/transit/ad/YdnInfeed002FullWidthImageView;",
    "Ljp/co/yahoo/android/apps/transit/ad/YdnInfeedImageWidth1200View;",
    "Ljp/co/yahoo/android/apps/transit/ad/YdnInfeedImageWidth65View;",
    // Full-screen overlay ads (Yahoo ads SDK).
    "Ljp/co/yahoo/android/ads/YJCpfOverlayView;"
).flatMap { definingClass ->
    constructorFingerprints(definingClass, listOf(CONTEXT_ATTRS, CONTEXT_ATTRS_INT))
} + constructorFingerprints(
    // Search-result rows only expose the 3-argument constructor.
    "Ljp/co/yahoo/android/apps/transit/ui/view/navi/list/SearchResultListRowAdView;",
    listOf(CONTEXT_ATTRS_INT)
)
