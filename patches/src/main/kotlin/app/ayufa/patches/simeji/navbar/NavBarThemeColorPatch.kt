package app.ayufa.patches.simeji.navbar

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.substituteNavBarColor

/**
 * Paints the strip at the very bottom of the keyboard root view
 * (the navigation-bar area the IME window covers on Android 15+).
 * The colour comes from the theme's navigation-bar colour; when the
 * theme has none, stock Simeji falls back to black here. The colour
 * lands in v0, and v1 is still unused at this point (both verified
 * against the 22.5 dex: 9 registers, this in p0).
 */
internal object KeyboardRootViewOnDrawFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/keyboard/SimejiKeyboardRootView;",
    name = "onDraw",
    returnType = "V",
    parameters = listOf("Landroid/graphics/Canvas;")
)

/**
 * Applies the theme's navigation-bar colour to the IME window
 * (`Window.setNavigationBarColor`). When the theme has no colour,
 * stock restores a previously saved default colour — on Android 15+
 * a transparent default is saved as black — so the window ends up
 * black as well. The colour lands in v7 (the former theme
 * parameter register) and v0 is still unused at this point
 * (verified against the 22.5 dex: 8 registers).
 */
internal object ThemeManagerUpdateNavBarColorFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/theme/ThemeManager;",
    name = "updateThemeNavBarColor",
    returnType = "V",
    parameters = listOf("Ljp/baidu/simeji/theme/ITheme;")
)

/**
 * Lifts the keyboard above the system bar on Pixel devices (the
 * Android 15 compatibility path driven by the
 * KEY_PIXEL_SYS_BAR_UP cloud flag). Inline in this private method
 * in 22.5 (not a synthetic lambda): after `checkUp()`, when the
 * theme has no navigation-bar colour it sets the IME window's
 * navigation-bar colour to black directly. The colour lands in v2
 * and v5 is dead at this point — it was consumed by an earlier
 * branch and is overwritten with the black constant right after
 * (verified against the 22.5 dex: 11 registers).
 */
internal object OpenWnnUpdateSoftInputWindowLayoutParametersFingerprint : Fingerprint(
    definingClass = "Ljp/co/omronsoft/openwnn/OpenWnn;",
    name = "updateSoftInputWindowLayoutParameters",
    returnType = "V",
    parameters = listOf()
)

@Suppress("unused")
val navBarThemeColorPatch = bytecodePatch(
    name = "キーボード下の帯をテーマ色に合わせる",
    description = "Android 15以降でキーボード下のナビゲーションバー領域が" +
        "黒く塗られるのを、現在のテーマに合わせた色に変えます。テーマが" +
        "専用の色を持たない場合はキーボードの背景色から色を拾います。" +
        "テーマが専用の色を持つ場合はその色がそのまま使われます。" +
        "有効・無効はアプリ内設定（Ayufaパッチ設定）でいつでも切り替え" +
        "られます（無効にすると純正の黒に戻ります）。",
    default = true
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    extendWith("extensions/simeji.mpe")

    execute {
        // Each site substitutes the extension-resolved colour only
        // when the stock theme colour is null; the extension itself
        // returns null while the setting is off, which leaves every
        // stock fallback (black / saved default) untouched.
        KeyboardRootViewOnDrawFingerprint.method.substituteNavBarColor(
            colorRegister = "v0",
            tempRegister = "v1",
            label = "ayufa_nav_root_done",
            contextSetup =
                "invoke-virtual { p0 }, Landroid/view/View;->getContext()Landroid/content/Context;\n" +
                    "move-result-object v1"
        )
        ThemeManagerUpdateNavBarColorFingerprint.method.substituteNavBarColor(
            colorRegister = "v7",
            tempRegister = "v0",
            label = "ayufa_nav_tm_done",
            contextSetup = "const/4 v0, 0x0"
        )
        OpenWnnUpdateSoftInputWindowLayoutParametersFingerprint.method
            .substituteNavBarColor(
                colorRegister = "v2",
                tempRegister = "v5",
                label = "ayufa_nav_wnn_done",
                contextSetup = "const/4 v5, 0x0"
            )
    }
}
