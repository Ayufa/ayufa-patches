package app.ayufa.patches.youtube.video.livestream

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.intOption
import app.morphe.patcher.patch.resourcePatch
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_ANDROID_8
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_MODERN
import app.ayufa.patches.youtube.shared.declareAyufaSettingsListPreference
import app.ayufa.patches.youtube.shared.declareAyufaSettingsSwitches
import app.morphe.patcher.util.proxy.mutableTypes.encodedValue.MutableIntEncodedValue

private const val EXTENSION_CLASS = "Lapp/ayufa/extension/youtube/LiveLatencyKeeper;"

// Key of the Morphe settings switch that enables/disables the keeper at
// runtime. The extension reads the same key from "morphe_prefs".
private const val KEY_KEEP_LIVE_AT_EDGE = "ayufa_keep_live_at_edge"

// Key of the Morphe settings list that sets the re-sync interval at
// runtime. The extension reads the same key from "morphe_prefs".
private const val KEY_SYNC_INTERVAL_SECONDS = "ayufa_live_sync_interval_seconds"

private val SYNC_INTERVAL_VALUES = listOf("15", "30", "45", "60", "90", "120")
private const val DEFAULT_SYNC_INTERVAL_SECONDS = "45"

/**
 * Declares the keeper's on/off switch and the re-sync interval list on
 * the dedicated "Ayufa patch settings" screen in Morphe settings. See
 * [declareAyufaSettingsSwitches] and [declareAyufaSettingsListPreference].
 */
private val liveLatencyKeeperSettingsPatch = resourcePatch {
    execute {
        declareAyufaSettingsSwitches(
            keys = listOf(KEY_KEEP_LIVE_AT_EDGE),
            enStrings = mapOf(
                "${KEY_KEEP_LIVE_AT_EDGE}_title" to "Keep live streams at the live edge",
                "${KEY_KEEP_LIVE_AT_EDGE}_summary" to
                    "At a fixed interval, momentarily pauses and resumes live playback " +
                    "to snap it back to the newest position. Takes effect immediately."
            ),
            jaStrings = mapOf(
                "${KEY_KEEP_LIVE_AT_EDGE}_title" to "ライブ配信を最新の状態に保つ",
                "${KEY_KEEP_LIVE_AT_EDGE}_summary" to
                    "一定の間隔で、ライブ再生を瞬間的に停止→再生して最新の位置に合わせ直します。" +
                    "その場で反映されます。"
            )
        )
        declareAyufaSettingsListPreference(
            key = KEY_SYNC_INTERVAL_SECONDS,
            values = SYNC_INTERVAL_VALUES,
            enLabels = SYNC_INTERVAL_VALUES.map { "$it seconds" },
            jaLabels = SYNC_INTERVAL_VALUES.map { "${it}秒" },
            defaultValue = DEFAULT_SYNC_INTERVAL_SECONDS,
            enStrings = mapOf(
                "${KEY_SYNC_INTERVAL_SECONDS}_title" to "Live sync interval",
                "${KEY_SYNC_INTERVAL_SECONDS}_summary" to
                    "How often live playback is snapped back to the newest position " +
                    "with a momentary pause and resume. Takes effect immediately."
            ),
            jaStrings = mapOf(
                "${KEY_SYNC_INTERVAL_SECONDS}_title" to "先端に合わせる間隔",
                "${KEY_SYNC_INTERVAL_SECONDS}_summary" to
                    "ライブ再生を停止→再生で最新に合わせ直す間隔です。その場で反映されます。"
            )
        )
    }
}

// Defaults declared in the extension class <clinit>.
// The patch rewrites these literals when the user changes the options.
private const val DEFAULT_CHECK_INTERVAL_SECONDS = 45

private val checkIntervalOption = intOption(
    key = "liveLatencyCheckIntervalSeconds",
    default = DEFAULT_CHECK_INTERVAL_SECONDS,
    values = mapOf(
        "15 seconds" to 15,
        "30 seconds" to 30,
        "45 seconds" to 45,
        "60 seconds" to 60,
        "120 seconds" to 120
    ),
    title = "Default sync interval",
    description = "How often live playback is snapped back to the newest position " +
        "with a momentary pause and resume. " +
        "Used until the interval is changed in Morphe settings (Ayufa patch settings).",
    required = true
)

@Suppress("unused")
val liveLatencyKeeperPatch = bytecodePatch(
    name = "Live latency keeper",
    description = "At a fixed interval, momentarily pauses and resumes live stream playback " +
        "so it snaps back to the live edge (the newest position), no matter how far behind " +
        "it has fallen. The interval and the on/off switch can be changed in " +
        "Morphe settings (Ayufa patch settings) without re-patching. For YouTube 20.26.46 " +
        "(Android 8.0) and 21.30.216. Requires the official Morphe patches to be applied as well.",
    default = false
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_ANDROID_8, COMPATIBILITY_YOUTUBE_MODERN)

    dependsOn(liveLatencyKeeperSettingsPatch)

    extendWith("extensions/youtube.mpe")

    checkIntervalOption()

    execute {
        val checkIntervalSeconds by checkIntervalOption

        // Write the option values into the extension class by replacing the
        // static initial values of its config fields. (The extension is
        // compiled so the defaults live in the fields' encoded initial
        // values, not in <clinit> code.) If the extension class cannot be
        // found or edited, the defaults stay in effect and nothing fails.
        try {
            if (classDefByOrNull(EXTENSION_CLASS) != null) {
                val extensionClass = mutableClassDefBy(EXTENSION_CLASS)
                setFieldInitialValue(extensionClass, "checkIntervalSeconds", checkIntervalSeconds)
            }
        } catch (ex: Exception) {
            // Keep the extension defaults.
        }

        // Hook the media progress callback: its called constructor receives
        // the current playback time as the first parameter (p1, p2).
        // Index 2 is right after the super constructor call, the same
        // insertion point the official Morphe video time hook uses.
        MediaProgressFingerprint.instructionMatches.first().getMethodCalled().addInstruction(
            2,
            "invoke-static { p1, p2 }, $EXTENSION_CLASS->videoTimeChanged(J)V"
        )
    }
}

/**
 * Sets the static initial value of an int field of the extension class.
 * Does nothing if the field is missing or the value is null.
 */
private fun setFieldInitialValue(
    extensionClass: app.morphe.patcher.util.proxy.mutableTypes.MutableClass,
    fieldName: String,
    newValue: Int?
) {
    if (newValue == null) {
        return
    }
    val field = extensionClass.fields.firstOrNull { it.name == fieldName } ?: return
    (field.initialValue as? MutableIntEncodedValue)?.value = newValue
}
