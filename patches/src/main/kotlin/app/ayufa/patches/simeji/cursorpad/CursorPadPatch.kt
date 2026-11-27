package app.ayufa.patches.simeji.cursorpad

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.CURSOR_PAD_CLASS

/**
 * Entry point of the control panel's button row: whenever the panel
 * attaches, this rebuilds the row through either the default layout
 * (initDefultBar) or the user's custom layout (initCustomBar), so a
 * hook here covers both. The hook sits at the very start, where p0
 * still holds the launcher itself; the extension posts the actual
 * insertion, so it runs after the row has been built (the row
 * builders wipe all children first, so inserting synchronously at
 * the start would be erased). Verified against Simeji 22.5.
 */
internal object BaseLauncherAttachLauncherFingerprint : Fingerprint(
    definingClass = "Lcom/adamrocker/android/input/simeji/framework/imp/BaseLauncher;",
    name = "attachLauncher",
    returnType = "V",
    parameters = listOf("Lcom/adamrocker/android/input/simeji/framework/IPlusManager;")
)

@Suppress("unused")
val cursorPadPatch = bytecodePatch(
    name = "コントロールパネルにカーソル移動を追加",
    description = "コントロールパネルのボタン列の右端に、カーソル移動用の" +
        "パッド（左右の矢印）を追加します。パッドを押したまま左右に" +
        "スライドすると、スライドした分だけテキストのカーソルが左右に" +
        "動きます（iPhone版 Simeji のカーソルキーと同じ操作です）。" +
        "日本語の変換中に使うと、方向キーと同じく文節の移動になります。",
    default = true
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    extendWith("extensions/simeji.mpe")

    execute {
        BaseLauncherAttachLauncherFingerprint.method.addInstructions(
            0,
            "invoke-static { p0 }, " +
                "$CURSOR_PAD_CLASS->attach(Landroid/view/View;)V"
        )
    }
}
