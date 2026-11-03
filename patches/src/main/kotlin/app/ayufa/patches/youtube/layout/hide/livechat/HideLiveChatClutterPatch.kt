package app.ayufa.patches.youtube.layout.hide.livechat

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_MODERN
import org.w3c.dom.Document
import org.w3c.dom.Element

private const val EXTENSION_CLASS = "Lapp/ayufa/extension/youtube/LiveChatClutterHider;"

private const val KEY_HIDE_PINNED_BANNER = "ayufa_hide_live_chat_pinned_banner"
private const val KEY_HIDE_POLL = "ayufa_hide_live_chat_poll"

private val hidePinnedBannerOption = booleanOption(
    key = "hideLiveChatPinnedBanner",
    default = true,
    title = "Hide pinned message banner",
    description = "Hides the pinned comment (banner) shown above the live chat."
)

private val hidePollsOption = booleanOption(
    key = "hideLiveChatPolls",
    default = true,
    title = "Hide live chat polls",
    description = "Hides poll (questionnaire) cards shown in the live chat."
)

/**
 * Declares the two features as switches on a dedicated "Ayufa patch
 * settings" screen in Morphe settings, using the official add-on
 * preference mechanism: the settings patch of the Morphe bundle merges
 * `morphe_addon_prefs.xml` into its preference files and removes the
 * declaration afterwards. If the Morphe settings patch is not applied,
 * the declaration file is left behind in the APK root, which is
 * harmless, and the features simply stay on.
 *
 * The switches write to the "morphe_prefs" shared preferences, which the
 * extension reads at runtime, so toggling takes effect without
 * re-patching (from the next time a chat is opened).
 */
private val hideLiveChatClutterSettingsPatch = resourcePatch {
    execute {
        if (!get("morphe_addon_prefs.xml").exists()) {
            get("morphe_addon_prefs.xml").writeText(
                "<morphe-add-on-preferences " +
                    "xmlns:android=\"http://schemas.android.com/apk/res/android\"/>"
            )
        }
        document("morphe_addon_prefs.xml").use { document ->
            addClutterSwitches(document)
        }

        addStrings(
            "res/values/strings.xml",
            mapOf(
                "ayufa_settings_screen_title" to "Ayufa patch settings",
                "ayufa_settings_screen_summary" to "Settings for Ayufa YouTube Patches.",
                "${KEY_HIDE_PINNED_BANNER}_title" to "Hide pinned message banner",
                "${KEY_HIDE_PINNED_BANNER}_summary" to
                    "Hides the pinned comment shown above the live chat. " +
                    "Takes effect the next time a chat is opened.",
                "${KEY_HIDE_POLL}_title" to "Hide live chat polls",
                "${KEY_HIDE_POLL}_summary" to
                    "Hides poll cards shown in the live chat. " +
                    "Takes effect the next time a chat is opened."
            )
        )
        if (get("res/values-ja/strings.xml").exists()) {
            addStrings(
                "res/values-ja/strings.xml",
                mapOf(
                    "ayufa_settings_screen_title" to "Ayufaパッチ設定",
                    "ayufa_settings_screen_summary" to "Ayufa YouTube Patches の設定です。",
                    "${KEY_HIDE_PINNED_BANNER}_title" to "固定メッセージ（バナー）を隠す",
                    "${KEY_HIDE_PINNED_BANNER}_summary" to
                        "ライブチャットの上に表示される固定コメントを非表示にします。" +
                        "次にチャットを開いたときから反映されます。",
                    "${KEY_HIDE_POLL}_title" to "アンケートを隠す",
                    "${KEY_HIDE_POLL}_summary" to
                        "ライブチャットに表示されるアンケートカードを非表示にします。" +
                        "次にチャットを開いたときから反映されます。"
                )
            )
        }
    }
}

private fun addClutterSwitches(document: Document) {
    val root = document.documentElement

    // Already declared?
    val existing = root.getElementsByTagName("PreferenceScreen")
    for (i in 0 until existing.length) {
        val element = existing.item(i) as? Element ?: continue
        if (element.getAttribute("android:key") == "ayufa_settings_screen") {
            return
        }
    }

    // A screen declaration without a key or position is added to the
    // Morphe settings root screen, so this creates a dedicated
    // "Ayufa patch settings" entry holding the two switches.
    val ayufaScreen = document.createElement("PreferenceScreen")
    ayufaScreen.setAttribute("android:key", "ayufa_settings_screen")
    ayufaScreen.setAttribute("android:title", "@string/ayufa_settings_screen_title")
    ayufaScreen.setAttribute("android:summary", "@string/ayufa_settings_screen_summary")
    ayufaScreen.appendChild(switchPreference(document, KEY_HIDE_PINNED_BANNER))
    ayufaScreen.appendChild(switchPreference(document, KEY_HIDE_POLL))

    val screen = document.createElement("screen")
    screen.appendChild(ayufaScreen)
    root.appendChild(screen)
}

private fun switchPreference(document: Document, key: String): Element {
    val preference = document.createElement("SwitchPreference")
    preference.setAttribute("android:key", key)
    preference.setAttribute("android:title", "@string/${key}_title")
    preference.setAttribute("android:summary", "@string/${key}_summary")
    preference.setAttribute("android:defaultValue", "true")
    return preference
}

/**
 * Appends string resources, skipping names that already exist.
 * Runs inside a resource patch execute block.
 */
private fun app.morphe.patcher.patch.ResourcePatchContext.addStrings(
    path: String,
    strings: Map<String, String>
) {
    document(path).use { document ->
        val root = document.documentElement
        val existing = mutableSetOf<String>()
        val nodes = root.getElementsByTagName("string")
        for (i in 0 until nodes.length) {
            val element = nodes.item(i) as? Element ?: continue
            existing += element.getAttribute("name")
        }
        for ((name, value) in strings) {
            if (name in existing) {
                continue
            }
            val element = document.createElement("string")
            element.setAttribute("name", name)
            element.textContent = value
            root.appendChild(element)
        }
    }
}

@Suppress("unused")
val hideLiveChatClutterPatch = bytecodePatch(
    name = "Hide live chat clutter",
    description = "Hides the pinned message banner and poll cards in live chat. " +
        "Each can be toggled in Morphe settings (Ayufa patch settings) without re-patching. " +
        "For YouTube 21.30.216.",
    default = false
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_MODERN)

    dependsOn(hideLiveChatClutterSettingsPatch)

    extendWith("extensions/youtube.mpe")

    hidePinnedBannerOption()
    hidePollsOption()

    execute {
        val hidePinnedBanner by hidePinnedBannerOption
        val hidePolls by hidePollsOption

        var hookCount = 0

        if (hidePinnedBanner == true) {
            // Hook the end of the banner presenter constructor. The extension
            // receives the live chat root view and keeps the banner container
            // inside it hidden for the whole chat session.
            // matchAll throws when nothing matches; treat that as zero hooks.
            val matches = runCatching { LiveChatBannerPresenterFingerprint.matchAll() }
                .getOrDefault(emptyList())
            for (match in matches) {
                val register = match.method.viewParameterRegister() ?: continue
                match.method.insertBeforeReturn(
                    invokeStaticCall(register, "$EXTENSION_CLASS->onBannerRootView(Landroid/view/View;)V")
                )
                hookCount++
            }
        }

        if (hidePolls == true) {
            // The fingerprint matches a poll presenter getter. Hook the
            // constructors of every matched presenter's class. The extension
            // receives the live chat root view and hides only the poll
            // container inside it.
            val hookedClasses = mutableSetOf<String>()
            val matches = runCatching { LiveChatPollPresenterFingerprint.matchAll() }
                .getOrDefault(emptyList())
            for (match in matches) {
                val classDef = match.classDef
                if (!hookedClasses.add(classDef.type)) {
                    continue
                }
                for (method in classDef.methods) {
                    if (method.name == "<init>") {
                        val register = method.viewParameterRegister() ?: continue
                        method.insertBeforeReturn(
                            invokeStaticCall(register, "$EXTENSION_CLASS->onPollRootView(Landroid/view/View;)V")
                        )
                        hookCount++
                    }
                }
            }
        }

        if (hookCount == 0) {
            throw PatchException(
                "Hide live chat clutter: no target presenter was found. " +
                    "This patch was verified only for YouTube 21.30.216."
            )
        }
    }
}

/**
 * Inserts the given instruction right before the method's final return
 * instruction.
 */
private fun MutableMethod.insertBeforeReturn(instruction: String) {
    val index = implementation?.instructions?.size?.minus(1)
        ?: throw PatchException("Method has no instructions: $this")
    addInstruction(index, instruction)
}

/**
 * Builds an invoke-static instruction passing a single parameter register.
 * Always uses the /range form: in methods with many local registers, even
 * a low p-register maps to a v-register above v15, which the plain form
 * cannot encode.
 */
private fun invokeStaticCall(register: Int, methodDescriptor: String): String =
    "invoke-static/range { p$register .. p$register }, $methodDescriptor"

/**
 * Returns the parameter register (pN) of this method's
 * `android.view.View` parameter, or null if it has none.
 * Register p0 is `this`; long/double parameters take two registers.
 */
private fun MutableMethod.viewParameterRegister(): Int? {
    var register = 1
    for (type in parameterTypes) {
        if (type == "Landroid/view/View;") {
            return register
        }
        register += if (type == "J" || type == "D") 2 else 1
    }
    return null
}
