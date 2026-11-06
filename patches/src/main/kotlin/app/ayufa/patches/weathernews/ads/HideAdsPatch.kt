package app.ayufa.patches.weathernews.ads

import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.weathernews.shared.Constants.COMPATIBILITY_WEATHERNEWS
import app.ayufa.patches.weathernews.shared.returnFalseImmediately
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
        // Central ad gate: report "ads disabled". Every banner
        // surface checks this before requesting or creating an ad,
        // so no banner is ever loaded.
        AdDelegateImplIsAdEnabledFingerprint.method
            .returnFalseImmediately()

        // Startup full-screen (app-open) ad: never load or show.
        AdDelegateImplLoadAndShowAppOpenAdFingerprint.method
            .returnVoidImmediately()
        AdDelegateImplShowAppOpenAdFingerprint.method
            .returnVoidImmediately()
    }
}
