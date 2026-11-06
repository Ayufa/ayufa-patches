package app.ayufa.patches.transit.ads

import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.transit.shared.Constants.COMPATIBILITY_YAHOO_TRANSIT
import app.ayufa.patches.transit.shared.goneAfterSuperInit

@Suppress("unused")
val hideTransitAdsPatch = bytecodePatch(
    name = "広告を非表示",
    description = "Yahoo!乗換案内の広告（各画面のバナー・ネイティブ広告、" +
        "全画面のオーバーレイ広告）を非表示にします。",
    default = true
) {
    compatibleWith(COMPATIBILITY_YAHOO_TRANSIT)

    execute {
        // Hide every ad placement view at construction time; an
        // ad slot that starts out GONE has no area to render into.
        adViewConstructorFingerprints.forEach { fingerprint ->
            fingerprint.method.goneAfterSuperInit()
        }
    }
}
