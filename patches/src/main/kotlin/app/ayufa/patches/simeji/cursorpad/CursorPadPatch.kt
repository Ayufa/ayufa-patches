package app.ayufa.patches.simeji.cursorpad

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.CURSOR_PAD_CATALOG_CLASS
import app.ayufa.patches.simeji.shared.CURSOR_PAD_CLASS
import app.ayufa.patches.simeji.shared.CURSOR_PAD_PROVIDER_CLASS

/**
 * Entry point of the control panel's button row: whenever the panel
 * attaches, this rebuilds the row through either the default layout
 * (initDefultBar) or the user's custom layout (initCustomBar), so a
 * hook here covers both. The hook sits at the very start, where p0
 * still holds the launcher itself; the extension posts the actual
 * work, so it runs after the row has been built. Verified against
 * Simeji 22.5.
 */
internal object BaseLauncherAttachLauncherFingerprint : Fingerprint(
    definingClass = "Lcom/adamrocker/android/input/simeji/framework/imp/BaseLauncher;",
    name = "attachLauncher",
    returnType = "V",
    parameters = listOf("Lcom/adamrocker/android/input/simeji/framework/IPlusManager;")
)

/**
 * Resolves one slot of the custom control-panel layout to its
 * provider: indices 1-4 map to the fixed launcher list and the rest
 * are delegated to CustomTopBarHelper. The cursor pad takes the new
 * index 19 (indices 6, 11 and 14 are retired stock slots that the
 * app itself filters out of saved configurations). The hook sits at
 * the very start and only intercepts index 19; v0 is a genuine
 * local register of this method (5 registers, 3 of them parameters,
 * verified against the 22.5 dex) and holds no meaningful value at
 * entry. The extension returns a proxy implementing the app's
 * IPlus/ILauchable interfaces, or null on failure, which the stock
 * caller treats as an empty slot. Verified against Simeji 22.5.
 */
internal object BaseLauncherGetIconByIndexFingerprint : Fingerprint(
    definingClass = "Lcom/adamrocker/android/input/simeji/framework/imp/BaseLauncher;",
    name = "getIconByIndex",
    returnType = "Lcom/adamrocker/android/input/simeji/framework/ILauchable;",
    parameters = listOf(
        "Lcom/adamrocker/android/input/simeji/framework/IPlusManager;",
        "I"
    )
)

/**
 * The toolbar customization catalogue: the stock method returns
 * the static item list, and every consumer (the settings screen's
 * adapter, the lookup resolving a saved configuration back to
 * items, the default configuration) goes through it. The hook
 * replaces the return value with the extension's extended copy,
 * which appends the cursor-move item (id "19") built with the
 * resource ids added by the resource patch below. v0 is the
 * method's only local register (2 registers, p0 is the companion)
 * and the original body is a single field read into it, so
 * overwriting it before returning is safe. Verified against
 * Simeji 22.5.
 */
internal object ToolbarItemCompanionGetToolbarDatasFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/home/vip/toolbar/ToolbarItem\$Companion;",
    name = "getToobarDatas",
    returnType = "Ljava/util/List;",
    parameters = listOf()
)

/**
 * Resources for the cursor pad's entry in the toolbar settings:
 * the item's title string and icon drawable, both named
 * "toolbar_cursor_move". The extension looks their ids up by name
 * at runtime, because the ids only exist once the patched app is
 * built. The drawable is the iPhone-style cursor key glyph (a
 * cursor bar flanked by two outward triangles) as a vector.
 */
@Suppress("unused")
val cursorPadResourcePatch = resourcePatch(
    name = "カーソル移動パッド用リソース",
    description = "「コントロールパネルにカーソル移動を追加」がツールバー" +
        "設定に項目を表示するために必要な文字列とアイコンを、アプリに" +
        "追加します。このパッチ単体では何も変わりません。",
    default = true
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    execute {
        document("res/values/strings.xml").use { doc ->
            val stringElements = doc.getElementsByTagName("string")
            var exists = false
            for (i in 0 until stringElements.length) {
                val element = stringElements.item(i) as org.w3c.dom.Element
                if (element.getAttribute("name") == "toolbar_cursor_move") {
                    exists = true
                    break
                }
            }
            if (!exists) {
                val element = doc.createElement("string")
                element.setAttribute("name", "toolbar_cursor_move")
                element.appendChild(doc.createTextNode("カーソル移動"))
                doc.documentElement.appendChild(element)
            }
        }

        get("res/drawable/toolbar_cursor_move.xml").writeText(
            """
            <?xml version="1.0" encoding="utf-8"?>
            <vector xmlns:android="http://schemas.android.com/apk/res/android"
                android:width="24dp"
                android:height="24dp"
                android:viewportWidth="24"
                android:viewportHeight="24">
                <path
                    android:fillColor="#FF000000"
                    android:pathData="M11.05,4.5h1.9v15h-1.9zM8.6,7.6L2.8,12l5.8,4.4zM15.4,7.6L21.2,12l-5.8,4.4z" />
            </vector>
            """.trimIndent()
        )
    }
}

@Suppress("unused")
val cursorPadPatch = bytecodePatch(
    name = "コントロールパネルにカーソル移動を追加",
    description = "コントロールパネルのツールバーカスタマイズに「カーソル" +
        "移動」の項目を追加します（iPhone版 Simeji のカーソルキーと同じ" +
        "機能です）。ツールバー設定で配置すると、他の項目と同じように" +
        "並び替えられます。置いた項目を押したまま左右にスライドすると、" +
        "スライドした分だけテキストのカーソルが左右に動きます。日本語の" +
        "変換中に使うと、方向キーと同じく文節の移動になります。",
    default = true
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    extendWith("extensions/simeji.mpe")

    dependsOn(cursorPadResourcePatch)

    execute {
        BaseLauncherAttachLauncherFingerprint.method.addInstructions(
            0,
            "invoke-static { p0 }, " +
                "$CURSOR_PAD_CLASS->attach(Landroid/view/View;)V"
        )

        BaseLauncherGetIconByIndexFingerprint.method.addInstructions(
            0,
            """
                const/16 v0, 0x13
                if-ne p2, v0, :ayufa_cursor_pad_skip
                invoke-static { p1 }, $CURSOR_PAD_PROVIDER_CLASS->create(Ljava/lang/Object;)Ljava/lang/Object;
                move-result-object v0
                check-cast v0, Lcom/adamrocker/android/input/simeji/framework/ILauchable;
                return-object v0
                :ayufa_cursor_pad_skip
            """
        )

        ToolbarItemCompanionGetToolbarDatasFingerprint.method.addInstructions(
            0,
            """
                invoke-static {}, $CURSOR_PAD_CATALOG_CLASS->extended()Ljava/util/List;
                move-result-object v0
                return-object v0
            """
        )
    }
}
