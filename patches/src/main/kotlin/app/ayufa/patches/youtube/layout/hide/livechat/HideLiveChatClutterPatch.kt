package app.ayufa.patches.youtube.layout.hide.livechat

import app.morphe.patcher.extensions.InstructionExtensions.addInstruction
import app.morphe.patcher.patch.PatchException
import app.morphe.patcher.patch.booleanOption
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.util.proxy.mutableTypes.MutableMethod
import app.ayufa.patches.youtube.shared.Constants.COMPATIBILITY_YOUTUBE_MODERN

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
            // Hook the end of the banner presenter constructor. The extension
            // receives the presenter instance and keeps its banner container
            // hidden for the whole chat session.
            // matchAll throws when nothing matches; treat that as zero hooks.
            val matches = runCatching { LiveChatBannerPresenterFingerprint.matchAll() }
                .getOrDefault(emptyList())
            for (match in matches) {
                val register = match.method.viewParameterRegister() ?: continue
                match.method.insertBeforeReturn(
                    "invoke-static { p$register }, " +
                        "$EXTENSION_CLASS->onBannerRootView(Landroid/view/View;)V"
                )
                hookCount++
            }
        }

        if (hidePolls == true) {
            // The fingerprint matches a poll presenter getter. Hook the
            // constructors of every matched presenter's class.
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
                            "invoke-static { p$register }, " +
                                "$EXTENSION_CLASS->onPollRootView(Landroid/view/View;)V"
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
