package app.ayufa.patches.youtube.layout.hide.livechat

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_MODERN
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

private const val EXTENSION_CLASS = "Lapp/ayufa/extension/youtube/LiveChatClutterHider;"

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

/** Layouts inflated for the pinned message banner (dark/light variants). */
private val bannerLayouts = listOf(
    "live_chat_banner_with_solid_background",
    "live_chat_banner_with_solid_background_inverse"
)

/** Layouts inflated for poll cards in the chat. */
private val pollLayouts = listOf(
    "live_chat_modern_poll"
)

@Suppress("unused")
val hideLiveChatClutterPatch = bytecodePatch(
    name = "Hide live chat clutter",
    description = "Hides the pinned message banner and poll cards in live chat. " +
        "For YouTube 21.30.216.",
    default = false
) {
    compatibleWith(COMPATIBILITY_YOUTUBE_MODERN)

    extendWith("extensions/youtube.mpe")

    execute {
        val hidePinnedBanner by hidePinnedBannerOption
        val hidePolls by hidePollsOption

        var hookCount = 0

        if (hidePinnedBanner == true) {
            hookCount += hookInflatedViews(bannerLayouts, "hidePinnedBannerView")
        }
        if (hidePolls == true) {
            hookCount += hookInflatedViews(pollLayouts, "hidePollView")
        }

        if (hookCount == 0) {
            throw PatchException(
                "Hide live chat clutter: no target layout inflation was found. " +
                    "This patch was verified only for YouTube 21.30.216."
            )
        }
    }
}

/**
 * Hooks every method that inflates one of the given layouts, inserting a
 * call to the extension hide method with the inflated view.
 *
 * @return The number of inflation sites hooked.
 */
private fun app.morphe.patcher.patch.BytecodePatchContext.hookInflatedViews(
    layouts: List<String>,
    extensionMethod: String
): Int {
    var count = 0
    val hookedMethods = mutableSetOf<String>()
    for (layout in layouts) {
        val matches = inflateLayoutFingerprint(layout).matchAll(this@hookInflatedViews)
        for (match in matches) {
            val method = match.method
            val descriptor = "${method.definingClass}->${method.name}"
            if (!hookedMethods.add(descriptor)) {
                continue
            }
            val moveResultIndex = match.instructionMatches.last().index
            method.injectHideViewCall(moveResultIndex, EXTENSION_CLASS, extensionMethod)
            count++
        }
    }
    return count
}

/**
 * Injects a call to a static extension method that hides a view.
 * Same helper as MorpheApp/morphe-patches injectHideViewCall, GPL-3.0.
 *
 * @param moveResultIndex Index of the MOVE_RESULT_OBJECT instruction that
 * holds the inflated view.
 */
private fun MutableMethod.injectHideViewCall(
    moveResultIndex: Int,
    classDescriptor: String,
    targetMethod: String
) {
    val viewRegister = getInstruction<OneRegisterInstruction>(moveResultIndex).registerA
    addInstruction(
        moveResultIndex + 1,
        "invoke-static { v$viewRegister }, $classDescriptor->$targetMethod(Landroid/view/View;)V"
    )
}
