package app.ayufa.extension.simeji;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Adds the "Ayufaパッチ設定" row at the end of Simeji's settings tab.
 * Called from the patched {@code SettingHomeFragment.onViewCreated}
 * with the fragment's root view; the actual insertion is posted so it
 * runs after the fragment has built its own items.
 */
public final class SimejiSettingsEntry {

    private static final String ROW_TAG = "ayufa_simeji_settings_row";

    private SimejiSettingsEntry() {
    }

    public static void attach(final View root) {
        if (root == null) {
            return;
        }
        root.post(new Runnable() {
            @Override
            public void run() {
                if (!tryAttach(root)) {
                    // The settings list may be built a moment later;
                    // retry once before giving up quietly.
                    root.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tryAttach(root);
                        }
                    }, 600);
                }
            }
        });
    }

    private static boolean tryAttach(View root) {
        try {
            ScrollView scrollView = findScrollView(root);
            if (scrollView == null || scrollView.getChildCount() == 0) {
                return false;
            }
            View child = scrollView.getChildAt(0);
            if (!(child instanceof ViewGroup)) {
                return false;
            }
            ViewGroup container = (ViewGroup) child;
            if (container.findViewWithTag(ROW_TAG) != null) {
                return true;
            }
            container.addView(buildRow(root.getContext()));
            return true;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static View buildRow(final Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        int padH = (int) (16 * density);
        int padV = (int) (14 * density);

        LinearLayout row = new LinearLayout(context);
        row.setTag(ROW_TAG);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(padH, padV, padH, padV);
        row.setClickable(true);

        TextView title = new TextView(context);
        title.setText("Ayufaパッチ設定");
        title.setTextSize(16);
        row.addView(title);

        TextView summary = new TextView(context);
        summary.setText("広告・AIアシスタントなどの表示や、VIP偽装を切り替えます");
        summary.setTextSize(12);
        summary.setTextColor(Color.GRAY);
        row.addView(summary);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimejiSettingsUi.show(context);
            }
        });
        return row;
    }

    private static ScrollView findScrollView(View view) {
        if (view instanceof ScrollView) {
            return (ScrollView) view;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                ScrollView found = findScrollView(group.getChildAt(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
