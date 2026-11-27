package app.ayufa.extension.simeji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A control-panel item for cursor movement, presented to Simeji as
 * a regular plus/launcher provider so it takes part in the stock
 * toolbar customization exactly like the built-in items (keyboard
 * switch, copy, clipboard, ...): the toolbar settings list it under
 * "カーソル移動" (catalogue index 19, see {@link CursorPadCatalog}),
 * the user places it in any of the four customizable slots, and the
 * stock {@code BaseLauncher} builds its icon view from it.
 *
 * <p>The provider object is a dynamic proxy implementing the host
 * app's {@code IPlus} and {@code ILauchable} interfaces: the
 * extension is compiled without the host app on its classpath, so
 * the interfaces are loaded reflectively from the plus manager's
 * class loader and every call is answered by the handler below.
 * Only the handful of methods the launcher actually uses carry
 * behaviour; the rest (mostly lifecycle callbacks) return defaults.
 *
 * <p>The icon mirrors Simeji for iPhone's cursor key: a text cursor
 * bar in the centre with a triangle pointing outward on each side.
 * It is drawn in solid black and reported as needing the theme
 * colour filter, so the stock icon view tints it with the current
 * theme's candidate icon colour, like the built-in items' fallback
 * icons.
 */
public final class CursorPadProvider {

    /** Key reported to the launcher (recorded for provider panels). */
    static final String KEY = "AyufaCursorPad";
    /** Index in the toolbar customization catalogue (see the patch). */
    public static final int TOOLBAR_INDEX = 19;
    private static final String TITLE = "カーソル移動";
    /** Same icon size as the built-in helper-created providers. */
    private static final int ICON_AUTO_SIZE = 23;

    private CursorPadProvider() {
    }

    /**
     * Creates the provider for the given plus manager. Called from
     * the patched {@code BaseLauncher.getIconByIndex} when the
     * toolbar configuration asks for index {@link #TOOLBAR_INDEX};
     * the declared return type is Object because the host interfaces
     * are not on the extension's compile classpath (the caller casts
     * to ILauchable in the patched code).
     */
    public static Object create(Object plusManager) {
        if (plusManager == null) {
            return null;
        }
        try {
            ClassLoader loader = plusManager.getClass().getClassLoader();
            Class<?> iPlus = Class.forName(
                    "com.adamrocker.android.input.simeji.framework.IPlus",
                    false, loader);
            Class<?> iLauchable = Class.forName(
                    "com.adamrocker.android.input.simeji.framework.ILauchable",
                    false, loader);
            return Proxy.newProxyInstance(
                    loader,
                    new Class<?>[]{iPlus, iLauchable},
                    new Handler(plusManager));
        } catch (Throwable ignored) {
            return null;
        }
    }

    /** Whether a launcher icon view's tag is one of these providers. */
    public static boolean isCursorPad(Object tag) {
        if (tag == null || !Proxy.isProxyClass(tag.getClass())) {
            return false;
        }
        try {
            return Proxy.getInvocationHandler(tag) instanceof Handler;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static final class Handler implements InvocationHandler {

        private final Object plusManager;

        Handler(Object plusManager) {
            this.plusManager = plusManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();
            switch (name) {
                case "getKey":
                    return KEY;
                case "getPlusManager":
                    return plusManager;
                case "getContentDescription":
                    return TITLE;
                case "getIconAutoSize":
                    return ICON_AUTO_SIZE;
                case "getIconNeedFilter":
                    return Boolean.TRUE;
                case "getLauncherIcon":
                    return new CursorIconDrawable();
                case "onLaunch":
                case "run":
                case "onRegister":
                case "onUnregister":
                    return null;
                case "equals":
                    return proxy == (args != null && args.length > 0 ? args[0] : null);
                case "hashCode":
                    return System.identityHashCode(proxy);
                case "toString":
                    return KEY;
                default:
                    return defaultValue(method.getReturnType());
            }
        }

        private static Object defaultValue(Class<?> type) {
            if (!type.isPrimitive() || type == void.class) {
                return null;
            }
            if (type == boolean.class) {
                return Boolean.FALSE;
            }
            if (type == char.class) {
                return (char) 0;
            }
            if (type == byte.class) {
                return (byte) 0;
            }
            if (type == short.class) {
                return (short) 0;
            }
            if (type == int.class) {
                return 0;
            }
            if (type == long.class) {
                return 0L;
            }
            if (type == float.class) {
                return 0f;
            }
            return 0d;
        }
    }

    /**
     * The iPhone-style cursor key glyph: a vertical cursor bar with
     * an outward-pointing triangle on each side, drawn in solid
     * black (the stock icon view applies the theme colour filter on
     * top because the provider reports getIconNeedFilter()).
     */
    static final class CursorIconDrawable extends Drawable {

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        CursorIconDrawable() {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xFF000000);
        }

        @Override
        public int getIntrinsicWidth() {
            return 96;
        }

        @Override
        public int getIntrinsicHeight() {
            return 96;
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            if (bounds.width() <= 0 || bounds.height() <= 0) {
                return;
            }
            float w = bounds.width();
            float h = bounds.height();
            float cx = bounds.left + w / 2.0f;
            float cy = bounds.top + h / 2.0f;
            // Centre cursor bar.
            float barHalfWidth = w * 0.04f;
            float barHalfHeight = h * 0.31f;
            // Triangles: tips near the edges, bases facing the bar.
            float tipInset = w * 0.06f;
            float baseX = w * 0.145f;
            float triHalfHeight = h * 0.185f;

            path.reset();
            path.moveTo(cx - baseX, cy - triHalfHeight);
            path.lineTo(bounds.left + tipInset, cy);
            path.lineTo(cx - baseX, cy + triHalfHeight);
            path.close();
            path.moveTo(cx + baseX, cy - triHalfHeight);
            path.lineTo(bounds.right - tipInset, cy);
            path.lineTo(cx + baseX, cy + triHalfHeight);
            path.close();
            canvas.drawPath(path, paint);
            canvas.drawRect(
                    cx - barHalfWidth, cy - barHalfHeight,
                    cx + barHalfWidth, cy + barHalfHeight, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public int getAlpha() {
            return paint.getAlpha();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
