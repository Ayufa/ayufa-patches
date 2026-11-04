package app.ayufa.patches.youtube.layout.livechat

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.resource.ResourceType
import app.morphe.patcher.resourceLiteral

/**
 * Matches the live chat controller method that builds the chat view
 * hierarchy: it inflates the `constraint_default_engagement_panel`
 * layout and looks up the `fixed` frame layout, which becomes the chat
 * content view whose bottom padding the controller manages. Both
 * literals occur only in this method. Verified against YouTube
 * 21.30.216.
 */
internal object LiveChatViewSetupFingerprint : Fingerprint(
    returnType = "V",
    parameters = listOf(),
    filters = listOf(
        resourceLiteral(ResourceType.LAYOUT, "constraint_default_engagement_panel"),
        resourceLiteral(ResourceType.ID, "fixed")
    )
)
