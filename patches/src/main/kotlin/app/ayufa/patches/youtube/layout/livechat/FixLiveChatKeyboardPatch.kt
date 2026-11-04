package app.ayufa.patches.youtube.layout.livechat

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_MODERN
import app.ayufa.patches.youtube.shared.declareAyufaSettingsSwitches
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

private const val EXTENSION_CLASS = "Lapp/ayufa/extension/youtube/LiveChatKeyboardFix;"

private const val KEY_FIX_LIVE_CHAT_KEYBOARD = "ayufa_fix_live_chat_keyboard"

/**
 * Declares the feature as a switch on the dedicated "Ayufa patch
 * settings" screen in Morphe settings. See [declareAyufaSettingsSwitches].
 */
private val fixLiveChatKeyboardSettingsPatch = resourcePatch {
    execute {
        declareAyufaSettingsSwitches(
            keys = listOf(KEY_FIX_LIVE_CHAT_KEYBOARD),
            enStrings = mapOf(
                "${KEY_FIX_LIVE_CHAT_KEYBOARD}_title" to "Fix live chat keyboard overlap",
                "${KEY_FIX_LIVE_CHAT_KEYBOARD}_summary" to
                    "Lifts the live chat input above the on-screen keyboard so it is " +
                    "not covered while typing. Takes effect the next time a chat is opened."
            ),
            jaStrings = mapOf(
                "${KEY_FIX_LIVE_CHAT_KEYBOARD}_title" to "ライブチャットのキーボード重なりを修正",
                "${KEY_FIX_LIVE_CHAT_KEYBOARD}_summary" to
                    "文字入力中にライブチャットの入力欄がキーボードに隠れないよう、" +
                    "キーボードの高さに合わせてチャットを持ち上げます。" +
                    "次にチャットを開いたときから反映されます。"
            )
        )
    }
}

/**
 * Fixes the live chat input field being covered by the on-screen
 * keyboard on YouTube 21.30.216.
 *
 * Background: upstream Morphe issue #3453. The engagement panels were
 * made to extend edge-to-edge behind the navigation bar, controlled by
 * feature flag 45744676. Upstream forced that flag off for YouTube
 * 21.11-21.25, but the flag was removed in 21.26+, so on 21.30.216 the
 * edge-to-edge layout is unconditional. The live chat controller sets
 * the bottom padding of its content view once, from a static system
 * inset value, when the chat is created; nothing updates it when the
 * keyboard opens, so the keyboard covers the input field.
 *
 * This patch hooks the controller method that creates the chat content
 * view (the `footer` FrameLayout of the engagement panel layout) and
 * lets the extension install an OnApplyWindowInsetsListener on it that
 * raises the bottom padding to the IME inset while the keyboard is
 * open. See LiveChatKeyboardFix.
 */
@Suppress("unused")
val fixLiveChatKeyboardPatch = bytecodePatch(
    name = "Fix live chat keyboard overlap",
    description = "Fixes the live chat input field being covered by the on-screen " +
        "keyboard while typing. Can be toggled in Morphe settings " +
        "(Ayufa patch settings) without re-patching. For YouTube 21.30.216.",
    default = false
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_MODERN)

    dependsOn(fixLiveChatKeyboardSettingsPatch)

    extendWith("extensions/youtube.mpe")

    execute {
        val match = LiveChatViewSetupFingerprint.matchAll().singleOrNull()
            ?: throw PatchException(
                "Fix live chat keyboard overlap: the live chat view setup method " +
                    "was not found exactly once. This patch was verified only " +
                    "for YouTube 21.30.216."
            )

        val method = match.method
        val instructions = method.implementation?.instructions
            ?: throw PatchException("Fix live chat keyboard overlap: method has no instructions.")

        // Find where the created FrameLayout is stored into the
        // controller's content view field (iput-object into the field of
        // type Landroid/widget/FrameLayout; that holds the `fixed` view),
        // and install the insets listener on the stored value right after.
        var hooked = false
        for ((index, instruction) in instructions.withIndex()) {
            if (instruction.opcode != Opcode.IPUT_OBJECT) {
                continue
            }
            val reference = (instruction as? ReferenceInstruction)?.reference as? FieldReference
                ?: continue
            if (reference.definingClass != match.classDef.type ||
                reference.type != "Landroid/widget/FrameLayout;"
            ) {
                continue
            }
            val valueRegister = (instruction as TwoRegisterInstruction).registerA
            method.addInstruction(
                index + 1,
                "invoke-static/range { v$valueRegister .. v$valueRegister }, " +
                    "$EXTENSION_CLASS->install(Landroid/view/View;)V"
            )
            hooked = true
            break
        }

        if (!hooked) {
            throw PatchException(
                "Fix live chat keyboard overlap: the chat content view field " +
                    "assignment was not found. This patch was verified only " +
                    "for YouTube 21.30.216."
            )
        }
    }
}
