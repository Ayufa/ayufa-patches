package app.ayufa.extension.simeji;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Builds the "Ayufaパッチ設定" screen for Simeji and shows it, either
 * as the dedicated activity (when the extension manifest was merged
 * into the app) or as a dialog over the current screen (fallback that
 * always works). All switches write through {@link SimejiSettings}
 * immediately, so changes apply without re-patching.
 */
public final class SimejiSettingsUi {

    private static final class Row {
        final String key;
        final String title;
        final String description;

        Row(String key, String title, String description) {
            this.key = key;
            this.title = title;
            this.description = description;
        }
    }

    private static final Row[] ROWS = {
            new Row(SimejiSettings.KEY_HIDE_ADS,
                    "広告を非表示",
                    "候補欄・ストア入口・Webキャンペーンの広告を出しません"),
            new Row(SimejiSettings.KEY_BLOCK_REWARDED_ADS,
                    "リワード広告もブロック",
                    "広告の視聴で解放される機能（お試し等）は使えなくなります"),
            new Row(SimejiSettings.KEY_HIDE_PROMOTIONS,
                    "プロモーションを非表示",
                    "全画面のキャンペーン・宣伝ページを開きません"),
            new Row(SimejiSettings.KEY_BLOCK_VIP_GUIDE,
                    "VIP案内画面をブロック",
                    "この画面は購入画面も兼ねるため、アプリ内での契約もできなくなります"),
            new Row(SimejiSettings.KEY_DISABLE_PUSH_POPUPS,
                    "プッシュポップアップを無効化",
                    "プッシュ通知のポップアップだけを止めます（通常の通知は影響なし）"),
            new Row(SimejiSettings.KEY_SKIP_SUBSCRIPTION_RETENTION,
                    "解約時の引き止め画面をスキップ",
                    "VIP解約時の確認画面を飛ばして解約フローへ進みます"),
            new Row(SimejiSettings.KEY_DISABLE_ANALYTICS,
                    "解析レポートを無効化",
                    "利用状況イベントの送信を止めます"),
            new Row(SimejiSettings.KEY_HIDE_POINT_ENTRY,
                    "ポイント入口を非表示",
                    "ポイントストアへの入口を出しません"),
            new Row(SimejiSettings.KEY_DISABLE_REVIEW_PROMPTS,
                    "レビュー依頼を無効化",
                    "Google Play の評価依頼ダイアログを出しません"),
            new Row(SimejiSettings.KEY_HIDE_AI_ASSISTANT,
                    "AIアシスタントを非表示",
                    "キーボード右上のロボットと、一番上の帯そのものを消します"),
            new Row(SimejiSettings.KEY_SPOOF_VIP,
                    "VIPを偽装",
                    "端末内のVIP判定だけを有効にします。ツールバーのカスタマイズや"
                            + "自作きせかえのVIPパーツが使えるようになりますが、"
                            + "クラウド系の特典は使えません"),
    };

    private SimejiSettingsUi() {
    }

    /** Opens the settings, preferring the activity, with a dialog fallback. */
    public static void show(Context context) {
        SimejiSettings.init(context);
        try {
            Intent intent = new Intent(context, SimejiSettingsActivity.class);
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
            return;
        } catch (Throwable ignored) {
            // The activity is not declared in this build: use a dialog.
        }
        try {
            Context dialogContext = context;
            View content = createContentView(dialogContext);
            ScrollView scroller = new ScrollView(dialogContext);
            scroller.addView(content);
            new AlertDialog.Builder(dialogContext)
                    .setTitle("Ayufaパッチ設定")
                    .setView(scroller)
                    .setPositiveButton("閉じる", null)
                    .show();
        } catch (Throwable ignored) {
            // Never crash the host app because of the settings UI.
        }
    }

    /** The full settings list, used by both the activity and the dialog. */
    public static View createContentView(final Context context) {
        SimejiSettings.init(context);
        float density = context.getResources().getDisplayMetrics().density;
        int pad = (int) (16 * density);

        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad / 2, pad, pad);

        TextView note = new TextView(context);
        note.setText("変更は即時反映されます。キーボード表示中に切り替えた場合は、"
                + "次にキーボードを開いたときから反映されます。");
        note.setTextSize(13);
        note.setTextColor(Color.GRAY);
        note.setPadding(0, pad / 2, 0, pad / 2);
        root.addView(note);

        for (final Row row : ROWS) {
            LinearLayout line = new LinearLayout(context);
            line.setOrientation(LinearLayout.HORIZONTAL);
            line.setPadding(0, pad / 2, 0, pad / 2);

            LinearLayout texts = new LinearLayout(context);
            texts.setOrientation(LinearLayout.VERTICAL);
            TextView title = new TextView(context);
            title.setText(row.title);
            title.setTextSize(16);
            texts.addView(title);
            if (row.description != null && row.description.length() > 0) {
                TextView description = new TextView(context);
                description.setText(row.description);
                description.setTextSize(12);
                description.setTextColor(Color.GRAY);
                texts.addView(description);
            }
            line.addView(texts, new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            Switch toggle = new Switch(context);
            toggle.setChecked(SimejiSettings.isOn(row.key));
            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton button, boolean checked) {
                    SimejiSettings.set(context, row.key, checked);
                }
            });
            line.addView(toggle);

            root.addView(line);
        }
        return root;
    }
}
