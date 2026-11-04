package app.ayufa.patches.youtube.layout.livechat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.literal

/**
 * Matches the live chat controller method that builds the chat view
 * hierarchy: it inflates the engagement panel layout
 * (2131624334 = layout/constraint_default_engagement_panel) and looks
 * up the engagement panel content view
 * (2131429167 = id/engagement_panel_content). Both values are loaded
 * as const instructions in this method, and together they occur in no
 * other method. The patch hooks this method's store of the `footer`
 * frame layout (id/footer) into the controller's content view field.
 * Verified against YouTube 21.30.216.
 */
internal object LiveChatViewSetupFingerprint : Fingerprint(
    filters = listOf(
        literal(2131624334),
        literal(2131429167)
    )
)
