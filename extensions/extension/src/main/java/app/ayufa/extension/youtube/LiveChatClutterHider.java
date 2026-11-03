/*
 * Copyright 2026 Ayufa.
 * Part of Ayufa YouTube Patches (GPL-3.0).
 */

package app.ayufa.extension.youtube;

import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

/**
 * Hides clutter shown above the live chat: the pinned message banner and
 * poll cards.
 *
 * <p>The "Hide live chat clutter" patch calls the two entry points here at
 * the end of the banner / poll presenter constructors (YouTube 21.30.216),
 * passing the root view each presenter was created with. The banner entry
 * point locates the banner container inside the chat root view; the poll
 * entry point hides the poll root view itself.
 *
 * <p>Presenters re-show their containers when new content arrives (a
 * message gets pinned, a poll starts), so a global layout listener
 * re-applies GONE after every layout pass in which a hidden container
 * became visible again.
 */
@SuppressWarnings("unused")
public final class LiveChatClutterHider {

    private LiveChatClutterHider() {
    }

    /**
     * Injection point. Called with the live chat root view that the banner
     * presenter was created with.
     */
    public static void onBannerRootView(View chatRoot) {
        try {
            if (chatRoot == null) {
                return;
            }
            int bannerId = chatRoot.getResources().getIdentifier(
                    "live_chat_banner", "id", chatRoot.getContext().getPackageName());
            if (bannerId == 0) {
                return;
            }
            View banner = chatRoot.findViewById(bannerId);
            if (banner == null) {
                return;
            }
            keepHidden(banner);
            // Also hide the dedicated wrapper container so no empty strip
            // is left above the chat.
            ViewParent parent = banner.getParent();
            if (parent instanceof View) {
                View parentView = (View) parent;
                int containerId = chatRoot.getResources().getIdentifier(
                        "live_chat_banner_container", "id",
                        chatRoot.getContext().getPackageName());
                if (containerId != 0 && parentView.getId() == containerId) {
                    keepHidden(parentView);
                }
            }
        } catch (Throwable ignored) {
            // Never break the chat because of this feature.
        }
    }

    /**
     * Injection point. Called with the root view the poll presenter was
     * created with (the poll card container).
     */
    public static void onPollRootView(View pollRoot) {
        try {
            if (pollRoot != null) {
                keepHidden(pollRoot);
            }
        } catch (Throwable ignored) {
            // Never break the chat because of this feature.
        }
    }

    /**
     * Hides the view now and re-hides it whenever a later layout pass
     * makes it visible again.
     */
    private static void keepHidden(final View view) {
        view.setVisibility(View.GONE);
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (view.getVisibility() != View.GONE) {
                            view.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
