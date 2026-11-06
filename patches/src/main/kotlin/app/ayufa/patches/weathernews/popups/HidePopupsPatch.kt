package app.ayufa.patches.weathernews.popups

import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.weathernews.shared.Constants.COMPATIBILITY_WEATHERNEWS
import app.ayufa.patches.weathernews.shared.returnNullImmediately
import app.ayufa.patches.weathernews.shared.returnVoidImmediately

@Suppress("unused")
val hideWeathernewsPopupsPatch = bytecodePatch(
    name = "ポップアップを非表示",
    description = "ウェザーニュースのアプリ内メッセージ（キャンペーン等の" +
        "ポップアップ、ヘッズアップ表示）を非表示にします。",
    default = true
) {
    compatibleWith(COMPATIBILITY_WEATHERNEWS)

    execute {
        // Never fetch new campaign triggers from the server.
        InAppMessageManagerFetchFingerprint.method
            .returnVoidImmediately()

        // Never feed events into the trigger engine.
        InAppMessageManagerOnTrackFingerprint.method
            .returnVoidImmediately()

        // Never hand an overlay to the display framework, even for
        // triggers cached before the patch was applied.
        InAppMessageManagerGetOverlayFingerprint.method
            .returnNullImmediately()
    }
}
