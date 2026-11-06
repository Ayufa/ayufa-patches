package app.ayufa.patches.simeji.collectpoint

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.returnFalseImmediately

/**
 * Decides whether the Simeji Point entry point may be shown.
 * Verified against Simeji 23.7.
 */
internal object CollectPointManagerCanShowFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/collectpoint/CollectPointManager;",
    name = "canShow",
    returnType = "Z"
)

@Suppress("unused")
val hidePointEntryPatch = bytecodePatch(
    name = "Hide point entry",
    description = "Hides the Simeji Point entry point, which leads to " +
        "the point store and its campaign recommendations.",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        CollectPointManagerCanShowFingerprint.method.returnFalseImmediately()
    }
}
