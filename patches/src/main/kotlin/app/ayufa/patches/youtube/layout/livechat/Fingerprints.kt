package app.ayufa.patches.youtube.layout.livechat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.literal

/**
 * Matches the live chat controller method that builds the chat view
 * hierarchy: it inflates the engagement panel layout
 * (id 2131624334 = layout/constraint_default_engagement_panel) and
 * looks up the engagement panel content view
 * (id 2131429167 = id/engagement_panel_content). Both values are loaded
 * as const instructions in this method only.
 *
 * Plain literals are used instead of resource literals on purpose: the
 * `fixed` view (the chat content view this patch hooks) is looked up
 * through an inlined R field reference, so it has no const instruction
 * to match, while these two sibling lookups in the same method do.
 * Verified against YouTube 21.30.216.
 */
internal object LiveChatViewSetupFingerprint : Fingerprint(
    filters = listOf(
        literal(2131624334),
        literal(2131429167)
    )
)
