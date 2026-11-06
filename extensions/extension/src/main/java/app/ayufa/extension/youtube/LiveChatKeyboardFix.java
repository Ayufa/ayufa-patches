package app.ayufa.extension.youtube;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

/**
 * Fixes the live chat text input being covered by the on-screen keyboard
 * (and, without a keyboard, sitting too low behind the navigation bar) on
 * YouTube 21.30.216.
 *
 * Since the engagement panels were made edge-to-edge (upstream Morphe
 * issue #3453; the controlling feature flag 45744676 was removed in
 * YouTube 21.26+, so it can no longer be switched off), the live chat
 * panel extends to the very bottom of the screen, and the bottom padding
 * of the chat content view is only ever set from a static system inset
 * value when the chat is created. Nothing updates it when the keyboard
 * opens, so the keyboard covers the input field.
 *
 * This installs an {@link View.OnApplyWindowInsetsListener} on the chat
 * content view (the "footer" FrameLayout inside the engagement panel
 * layout, the same view whose bottom padding the app itself manages).
 * While the keyboard is open, the bottom padding is raised to at least
 * the IME inset, which lifts the message list and the input field above
 * the keyboard. While the keyboard is closed, the app's own padding is
 * left untouched and simply remembered as the base value. The insets are
 * never consumed, so the rest of the app keeps receiving them unchanged.
 *
 * Can be toggled in Morphe settings (Ayufa patch settings); the setting
 * is read when a chat view is created, so it takes effect the next time
 * a chat is opened.
 */
public final class LiveChatKeyboardFix {

    private static final String MORPHE_PREFS_NAME = "morphe_prefs";
    private static final String KEY_FIX_LIVE_CHAT_KEYBOARD = "ayufa_fix_live_chat_keyboard";

    private LiveChatKeyboardFix() {
    }

    /**
     * Called from the patched live chat controller right after it creates
     * the chat content view.
     */
    public static void install(View view) {
        if (view == null) {
            return;
        }
        if (!isEnabled(view.getContext())) {
            return;
        }
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            /**
             * The bottom padding the app itself wants while no keyboard is
             * shown (normally the navigation bar inset). -1 until observed.
             */
            private int baseBottomPadding = -1;

            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int imeBottom = imeBottom(insets);
                if (imeBottom <= 0) {
                    // Keyboard is closed. Trust whatever padding the app
                    // set most recently and leave it alone.
                    baseBottomPadding = v.getPaddingBottom();
                } else {
                    int base = baseBottomPadding >= 0 ? baseBottomPadding : 0;
                    int target = Math.max(base, imeBottom);
                    if (v.getPaddingBottom() != target) {
                        v.setPadding(
                                v.getPaddingLeft(),
                                v.getPaddingTop(),
                                v.getPaddingRight(),
                                target
                        );
                    }
                }
                // Never consume: the rest of the hierarchy must keep
                // receiving the insets exactly as before.
                return insets;
            }
        });
        // Get an initial dispatch so the base padding is captured even
        // before the keyboard is first opened.
        view.requestApplyInsets();
    }

    /**
     * Called from the patched live chat controller at the start of the
     * method that applies the chat content view's bottom padding, with
     * the value the app is about to use. Returns the value that should
     * actually be used: the app's own value, raised to the current IME
     * inset while the keyboard is open.
     *
     * This hook is what keeps the fix in effect while a stream is
     * running: the controller re-applies its (keyboard-unaware) padding
     * whenever new chat data arrives, which would otherwise push the
     * input field back below the keyboard seconds after it opened.
     */
    public static int effectiveBottomPadding(View view, int appValue) {
        if (view == null || !isEnabled(view.getContext())) {
            return appValue;
        }
        WindowInsets insets = view.getRootWindowInsets();
        if (insets == null) {
            return appValue;
        }
        return Math.max(appValue, imeBottom(insets));
    }

    private static int imeBottom(WindowInsets insets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return insets.getInsets(WindowInsets.Type.ime()).bottom;
        }
        // Before API 30 there is no separate IME inset type; the legacy
        // system window inset bottom is the combined navigation bar +
        // keyboard height, which is exactly the clearance the chat needs.
        //noinspection deprecation
        return insets.getSystemWindowInsetBottom();
    }

    private static boolean isEnabled(Context context) {
        if (context == null) {
            return true;
        }
        try {
            SharedPreferences prefs =
                    context.getSharedPreferences(MORPHE_PREFS_NAME, Context.MODE_PRIVATE);
            return prefs.getBoolean(KEY_FIX_LIVE_CHAT_KEYBOARD, true);
        } catch (Throwable ignored) {
            // If the setting cannot be read, keep the fix on: a visible
            // input field is the safer default.
            return true;
        }
    }
}
