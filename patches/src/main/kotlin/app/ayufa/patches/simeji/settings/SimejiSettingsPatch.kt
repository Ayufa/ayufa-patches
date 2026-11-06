package app.ayufa.patches.simeji.settings

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.patch.bytecodePatch
import app.ayufa.patches.simeji.shared.Constants.COMPATIBILITY_SIMEJI
import app.ayufa.patches.simeji.shared.SIMEJI_SETTINGS_ENTRY_CLASS
import app.ayufa.patches.simeji.shared.invokeStaticAfterSuper

/**
 * Simeji's settings tab (the 設定 tab of the home screen). Its root
 * view hosts a scrollable list of setting items, and the extension
 * appends the "Ayufaパッチ設定" row at the end of that list.
 * Verified against Simeji 20.7.
 */
internal object SettingHomeFragmentOnViewCreatedFingerprint : Fingerprint(
    definingClass = "Ljp/baidu/simeji/home/setting/SettingHomeFragment;",
    name = "onViewCreated",
    returnType = "V",
    parameters = listOf("Landroid/view/View;", "Landroid/os/Bundle;")
)

@Suppress("unused")
val simejiSettingsPatch = bytecodePatch(
    name = "アプリ内設定画面を追加",
    description = "Simeji の設定タブの末尾に「Ayufaパッチ設定」の入口を" +
        "追加します。各 Simeji パッチの有効・無効や VIP偽装を、再パッチ" +
        "なしでアプリ内から切り替えられるようになります。他の Simeji " +
        "パッチを切り替えるには、このパッチの適用が必要です（未適用の" +
        "場合は各パッチの初期値どおりに動作します）。",
    default = false
) {
    compatibleWith(COMPATIBILITY_SIMEJI)

    extendWith("extensions/youtube.mpe")

    execute {
        SettingHomeFragmentOnViewCreatedFingerprint.method
            .invokeStaticAfterSuper(
                "onViewCreated",
                "invoke-static { p1 }, " +
                    "$SIMEJI_SETTINGS_ENTRY_CLASS->attach(Landroid/view/View;)V"
            )
    }
}
