package app.ayufa.patches.youtube.layout.hide.livechat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.checkCast
import app.morphe.patcher.literal
import app.morphe.patcher.methodCall
import app.morphe.patcher.resource.ResourceType
import app.morphe.patcher.resourceLiteral
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Matches the constructor of the live chat banner presenter: the class
 * that looks up the view with id `live_chat_banner` and casts it to
 * LiveChatBannerContainerLayout. Verified against YouTube 21.30.216.
 */
internal object LiveChatBannerPresenterFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.CONSTRUCTOR),
    filters = listOf(
        resourceLiteral(ResourceType.ID, "live_chat_banner"),
        methodCall(
            name = "findViewById",
            returnType = "Landroid/view/View;"
        ),
        checkCast("Lcom/google/android/libraries/youtube/livechat/ui/view/LiveChatBannerContainerLayout;")
    )
)

/**
 * Matches a method of the live chat poll card presenter: a getter that
 * looks up the view with id `poll_metadata_text`. The defining class of
 * the matched method is the poll presenter, whose constructor is hooked.
 * Verified against YouTube 21.30.216.
 */
internal object LiveChatPollPresenterFingerprint : Fingerprint(
    filters = listOf(
        resourceLiteral(ResourceType.ID, "poll_metadata_text"),
        methodCall(
            name = "findViewById",
            returnType = "Landroid/view/View;"
        ),
        checkCast("Landroid/widget/TextView;")
    )
)

/**
 * Matches the live chat controller method that builds the chat view
 * hierarchy: it inflates the engagement panel layout
 * (2131624334 = layout/constraint_default_engagement_panel) and looks
 * up the engagement panel content view
 * (2131429167 = id/engagement_panel_content). Both values are loaded
 * as const instructions in this method, and together they occur in no
 * other method. The keyboard fix hooks this method's store of the
 * `footer` frame layout (id/footer) into the controller's content
 * view field. Verified against YouTube 21.30.216.
 */
internal object LiveChatViewSetupFingerprint : Fingerprint(
    filters = listOf(
        literal(2131624334),
        literal(2131429167)
    )
)

/**
 * Matches the live chat controller method that applies the chat
 * content view's bottom padding (the app sets it from a static system
 * inset value and re-applies it whenever new chat data arrives). The
 * keyboard fix adjusts this method's argument so the padding also
 * clears the on-screen keyboard. Verified against YouTube 21.30.216.
 */
internal object LiveChatBottomPaddingFingerprint : Fingerprint(
    returnType = "V",
    parameters = listOf("I"),
    classFingerprint = LiveChatViewSetupFingerprint,
    filters = listOf(
        methodCall(
            name = "getChildCount",
            returnType = "I"
        ),
        methodCall(
            name = "getPaddingBottom",
            returnType = "I"
        ),
        methodCall(
            name = "setPadding"
        )
    )
)
