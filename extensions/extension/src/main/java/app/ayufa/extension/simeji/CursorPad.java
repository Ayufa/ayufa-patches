package app.ayufa.extension.simeji;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.inputmethodservice.InputMethodService;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Adds a cursor-move pad at the end of the control panel's button
 * row (the stock {@code BaseLauncher}). Called from the patched
 * {@code BaseLauncher.attachLauncher}, which (re)builds the row —
 * through either the default or the custom layout — every time the
 * panel attaches.
 *
 * <p>The hook sits at the very start of attachLauncher, where the
 * register holding the launcher is still intact, so the insertion
 * itself is posted to the view's message queue and runs after the
 * row has been built (the same pattern as
 * {@link SimejiSettingsEntry}).
 *
 * <p>While the pad is held, sliding a finger left or right moves
 * the text cursor by one character per step, mirroring the cursor
 * key of Simeji for iPhone. Movement goes through the IME service's
 * own {@code sendDownUpKeyEvents} with the DPAD left/right key
 * codes — exactly how stock Simeji's plus connector implements
 * {@code moveCursorLeft}/{@code moveCursorRight} for mushroom apps —
 * so during Japanese conversion the gesture moves the conversion
 * clause, just like a hardware arrow key.
 *
 * <p>All Simeji classes are reached reflectively, like the rest of
 * the extension, so nothing here depends on the host app at compile
 * time. Every entry point is guarded: a failure anywhere simply
 * leaves the stock panel unchanged.
 */
public final class CursorPad {

    private static final String PAD_TAG = "ayufa_cursor_pad";
    /** Horizontal slide distance that moves the cursor one character. */
    private static final float STEP_DP = 10.0f;
    private static final int FALLBACK_ICON_COLOR = 0xFF808080;

    private static Object cachedTheme;
    private static int cachedIconColor;
    private static boolean hasCachedIconColor;
    private static Object cachedService;

    private CursorPad() {
    }

    public static void attach(final View launcher) {
        if (launcher == null) {
            return;
        }
        launcher.post(new Runnable() {
            @Override
            public void run() {
                tryAttach(launcher, 0);
            }
        });
    }

    private static void tryAttach(final View launcher, final int attempt) {
        try {
            if (!(launcher instanceof ViewGroup)) {
                return;
            }
            ViewGroup bar = (ViewGroup) launcher;
            if (bar.findViewWithTag(PAD_TAG) != null) {
                return;
            }
            if (bar.getChildCount() == 0) {
                // The row is not built yet (or this attach call did
                // not build one); retry briefly, then give up.
                if (attempt < 3) {
                    launcher.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tryAttach(launcher, attempt + 1);
                        }
                    }, 300);
                }
                return;
            }
            PadView pad = new PadView(launcher.getContext());
            pad.setTag(PAD_TAG);
            pad.setContentDescription("カーソル移動");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            bar.addView(pad, params);
        } catch (Throwable ignored) {
            // Never break the stock control panel.
        }
    }

    /**
     * The IME service behind the panel views: unwraps the view's
     * context first (IME views are created with the service as their
     * context, possibly under theme wrappers), then falls back to
     * the app's router service, whose getIME() returns the IME.
     */
    private static Object resolveService(Context context) {
        if (cachedService != null) {
            return cachedService;
        }
        Object found = unwrapService(context);
        if (found == null) {
            found = routerService();
        }
        if (found != null) {
            cachedService = found;
        }
        return found;
    }

    private static Object unwrapService(Context context) {
        Context current = context;
        while (current != null) {
            if (current instanceof InputMethodService) {
                return current;
            }
            if (!(current instanceof ContextWrapper)) {
                return null;
            }
            Context base = ((ContextWrapper) current).getBaseContext();
            if (base == null || base == current) {
                return null;
            }
            current = base;
        }
        return null;
    }

    private static Object routerService() {
        try {
            Class<?> routerServices =
                    Class.forName("com.baidu.simeji.base.router.RouterServices");
            Field field = routerServices.getField("sSimejiIMERouter");
            Object imeRouter = field.get(null);
            if (imeRouter == null) {
                return null;
            }
            Object ime = imeRouter.getClass().getMethod("getIME").invoke(imeRouter);
            return ime instanceof InputMethodService ? ime : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** The theme's candidate icon colour, cached per theme instance. */
    private static int iconColor(Context context) {
        try {
            Class<?> themeManager = Class.forName("jp.baidu.simeji.theme.ThemeManager");
            Object manager = themeManager.getMethod("getInstance").invoke(null);
            if (manager == null) {
                return FALLBACK_ICON_COLOR;
            }
            Object theme = themeManager.getMethod("getCurTheme").invoke(manager);
            if (theme == null) {
                return FALLBACK_ICON_COLOR;
            }
            synchronized (CursorPad.class) {
                if (theme == cachedTheme && hasCachedIconColor) {
                    return cachedIconColor;
                }
            }
            Method getter;
            try {
                getter = theme.getClass()
                        .getMethod("getCandidateIconColor", Context.class);
            } catch (NoSuchMethodException ignored) {
                Class<?> iTheme = Class.forName("jp.baidu.simeji.theme.ITheme");
                getter = iTheme.getMethod("getCandidateIconColor", Context.class);
            }
            Object value = getter.invoke(theme, context);
            if (value instanceof Integer) {
                int color = (Integer) value;
                synchronized (CursorPad.class) {
                    cachedTheme = theme;
                    cachedIconColor = color;
                    hasCachedIconColor = true;
                }
                return color;
            }
        } catch (Throwable ignored) {
            // Fall through to the neutral colour.
        }
        return FALLBACK_ICON_COLOR;
    }

    /** The pad itself: a plain view that draws a double-headed arrow. */
    private static final class PadView extends View {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private float downX;
        private int sentSteps;
        private boolean dragging;

        PadView(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    sentSteps = 0;
                    dragging = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (!dragging) {
                        return true;
                    }
                    float stepPx = STEP_DP
                            * getResources().getDisplayMetrics().density;
                    int steps = (int) ((event.getX() - downX) / stepPx);
                    int delta = steps - sentSteps;
                    if (delta != 0) {
                        moveCursor(delta);
                        sentSteps = steps;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    dragging = false;
                    invalidate();
                    return true;
                default:
                    return super.onTouchEvent(event);
            }
        }

        private void moveCursor(int delta) {
            Object service = resolveService(getContext());
            if (service == null) {
                return;
            }
            int keyCode = delta > 0
                    ? KeyEvent.KEYCODE_DPAD_RIGHT
                    : KeyEvent.KEYCODE_DPAD_LEFT;
            int count = Math.abs(delta);
            for (int i = 0; i < count; i++) {
                if (!sendDownUp(service, keyCode)) {
                    return;
                }
            }
        }

        /**
         * Sends one key press the way the IME service itself does.
         * The reflective call mirrors {@code OpenWnn}'s plus
         * connector; if it is unavailable, explicit key events on
         * the current input connection are the fallback.
         */
        private boolean sendDownUp(Object service, int keyCode) {
            try {
                Method method = service.getClass()
                        .getMethod("sendDownUpKeyEvents", int.class);
                method.invoke(service, keyCode);
                return true;
            } catch (Throwable ignored) {
                // Try the input connection directly.
            }
            try {
                Object connection = service.getClass()
                        .getMethod("getCurrentInputConnection").invoke(service);
                if (connection instanceof InputConnection) {
                    InputConnection inputConnection = (InputConnection) connection;
                    inputConnection.sendKeyEvent(
                            new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    inputConnection.sendKeyEvent(
                            new KeyEvent(KeyEvent.ACTION_UP, keyCode));
                    return true;
                }
            } catch (Throwable ignored) {
                // No way to move the cursor right now.
            }
            return false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float density = getResources().getDisplayMetrics().density;
            float cx = getWidth() / 2.0f;
            float cy = getHeight() / 2.0f;
            float half = 11.0f * density;
            float head = 5.5f * density;
            float shaftHalf = 0.8f * density;

            paint.setColor(iconColor(getContext()));
            paint.setStyle(Paint.Style.FILL);
            if (dragging) {
                paint.setAlpha(255);
            } else {
                paint.setAlpha(230);
            }

            canvas.drawRect(cx - half, cy - shaftHalf, cx + half, cy + shaftHalf, paint);

            path.reset();
            path.moveTo(cx - half - 2.0f * density, cy);
            path.lineTo(cx - half + head, cy - head * 0.62f);
            path.lineTo(cx - half + head, cy + head * 0.62f);
            path.close();
            canvas.drawPath(path, paint);

            path.reset();
            path.moveTo(cx + half + 2.0f * density, cy);
            path.lineTo(cx + half - head, cy - head * 0.62f);
            path.lineTo(cx + half - head, cy + head * 0.62f);
            path.close();
            canvas.drawPath(path, paint);
        }
    }
}
