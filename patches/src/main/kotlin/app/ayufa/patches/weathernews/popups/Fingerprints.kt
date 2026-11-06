package app.ayufa.patches.weathernews.popups

import app.morphe.patcher.Fingerprint

/**
 * Fetches the in-app message event triggers from
 * inappmsg.weathernews.jp. Without a fetch, no new campaign popups
 * are ever scheduled. Verified against Weathernews 7.5.1.
 */
internal object InAppMessageManagerFetchFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/overlay/inapp/InAppMessageManager;",
    name = "fetch",
    returnType = "V"
)

/**
 * Feeds app events into the in-app message trigger engine; matching
 * events are what make a popup / heads-up overlay appear. Verified
 * against Weathernews 7.5.1.
 */
internal object InAppMessageManagerOnTrackFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/overlay/inapp/InAppMessageManager;",
    name = "onTrack",
    returnType = "V",
    parameters = listOf("Lcom/weathernews/touch/track/model/Event;")
)

/**
 * The OverlaySource hand-off: the overlay framework asks this method
 * for the next popup overlay to display. Returning null means
 * nothing is ever shown, even for triggers cached before patching.
 * Verified against Weathernews 7.5.1.
 */
internal object InAppMessageManagerGetOverlayFingerprint : Fingerprint(
    definingClass = "Lcom/weathernews/touch/overlay/inapp/InAppMessageManager;",
    name = "getOverlay",
    returnType = "Lcom/weathernews/touch/overlay/model/Overlay;"
)
