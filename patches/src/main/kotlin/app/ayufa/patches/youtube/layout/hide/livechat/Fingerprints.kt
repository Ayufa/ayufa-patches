package app.ayufa.patches.youtube.layout.hide.livechat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.InstructionLocation.MatchAfterImmediately
import app.morphe.patcher.methodCall
import app.morphe.patcher.opcode
import app.morphe.patcher.resource.ResourceType
import app.morphe.patcher.resourceLiteral
import com.android.tools.smali.dexlib2.Opcode

/**
 * Matches any method that inflates the given layout and keeps the inflated
 * view in a register (MOVE_RESULT_OBJECT right after the inflate call).
 *
 * Same approach as MorpheApp/morphe-patches LiveChatDonatorsBarFingerprint
 * (which hides the live chat donators bar), GPL-3.0, except this fingerprint
 * is not restricted to constructors, because poll and banner views can be
 * inflated by presenter/binder methods as well.
 */
internal fun inflateLayoutFingerprint(layoutName: String) = object : Fingerprint(
    filters = listOf(
        resourceLiteral(ResourceType.LAYOUT, layoutName),
        methodCall(
            name = "inflate",
            returnType = "Landroid/view/View;"
        ),
        opcode(Opcode.MOVE_RESULT_OBJECT, location = MatchAfterImmediately())
    )
) {}
