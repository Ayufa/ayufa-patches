/*
 * Copyright 2026 Ayufa.
 * Part of Ayufa YouTube Patches (GPL-3.0).
 */

package app.ayufa.extension.youtube;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

/**
 * Hides clutter shown above / inside the live chat: the pinned message
 * banner and poll cards.
 *
 * <p>The methods here are called by the "Hide live chat clutter" patch
 * right after the corresponding views are inflated. Because the app can
 * re-show these containers later (for example when a new message is
 * pinned), a global layout listener keeps re-hiding them.
 */
@SuppressWarnings("unused")
public final class LiveChatClutterHider {

    private LiveChatClutterHider() {
    }

    /**
     * Injection point. Called with the inflated pinned banner content view.
     * Hides the whole banner container when it can be found, so no empty
     * banner strip is left above the chat.
     */
    public static void hidePinnedBannerView(final View view) {
        if (view == null) {
            return;
        }
        View banner = findBannerContainer(view);
        if (banner != null) {
            keepHidden(banner);
        }
        keepHidden(view);
        // The content view may not be attached to its container yet at
        // inflation time. Retry finding the container on every layout pass,
        // otherwise an empty banner strip could remain above the chat.
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        View container = findBannerContainer(view);
                        if (container != null && container.getVisibility() != View.GONE) {
                            container.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * Injection point. Called with the inflated poll card view.
     */
    public static void hidePollView(View view) {
        if (view == null) {
            return;
        }
        keepHidden(view);
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

    /**
     * Walks up from the banner content view to the banner container:
     * the view with id {@code live_chat_banner_container}, or an instance
     * of {@code LiveChatBannerContainerLayout}.
     *
     * @return The container view, or null if it cannot be identified yet
     * (the content view may not be attached to the container at inflation
     * time; in that case only the content itself is hidden).
     */
    private static View findBannerContainer(View view) {
        try {
            Resources resources = view.getResources();
            String packageName = view.getContext().getPackageName();
            int containerId = resources.getIdentifier(
                    "live_chat_banner_container", "id", packageName);

            View current = view;
            for (int depth = 0; depth < 8 && current != null; depth++) {
                if (containerId != 0 && current.getId() == containerId) {
                    return current;
                }
                if (current.getClass().getName().endsWith("LiveChatBannerContainerLayout")) {
                    return current;
                }
                ViewParent parent = current.getParent();
                current = parent instanceof View ? (View) parent : null;
            }
        } catch (Throwable ignored) {
            // Fall through: hide the content view itself.
        }
        return null;
    }
}
