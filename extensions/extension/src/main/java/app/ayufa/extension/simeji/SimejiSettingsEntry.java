package app.ayufa.extension.simeji;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * Adds the "Ayufaパッチ設定" entry to Simeji's settings tab. Called
 * from the patched {@code SettingHomeFragment.initSettingItems} with
 * the fragment's root view, after the fragment has wired up its own
 * items.
 *
 * <p>The settings list is a static layout whose items include a
 * hidden test item (id below) that the app always sets to GONE with
 * an empty title. The primary strategy repurposes that item: same
 * native look, guaranteed placement inside the list. If the item is
 * missing, a plain row is appended next to a known item instead, and
 * as a last resort to the scroll view's content.
 *
 * <p>The work is posted to the view's message queue: attach() itself
 * is called at the start of initSettingItems, and the fragment goes
 * on to reset the items (including hiding the test item again), so
 * the changes must be applied after that method has completed.
 */
public final class SimejiSettingsEntry {

    /** Id of the always-hidden test item in the settings layout. */
    private static final int TEST_ITEM_ID = 2131364907;
    /** Id of the "about" item, used as an anchor for the fallback row. */
    private static final int ABOUT_ITEM_ID = 2131364515;
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
                    // The list may still be settling; retry once.
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
            if (repurposeTestItem(root)) {
                return true;
            }
            if (appendNearKnownItem(root)) {
                return true;
            }
            return appendToScrollContent(root);
        } catch (Throwable ignored) {
            // Never crash the host app because of the settings entry.
            return false;
        }
    }

    private static boolean repurposeTestItem(final View root) {
        View item = root.findViewById(TEST_ITEM_ID);
        if (item == null) {
            return false;
        }
        try {
            Method setTitle = item.getClass().getMethod("setTitle", String.class);
            setTitle.invoke(item, "Ayufaパッチ設定");
        } catch (Throwable ignored) {
            // Without a title the row is useless; keep it hidden.
            return false;
        }
        item.setVisibility(View.VISIBLE);
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimejiSettingsUi.show(view.getContext());
            }
        });
        return true;
    }

    private static boolean appendNearKnownItem(View root) {
        View anchor = root.findViewById(ABOUT_ITEM_ID);
        if (anchor == null || !(anchor.getParent() instanceof ViewGroup)) {
            return false;
        }
        ViewGroup parent = (ViewGroup) anchor.getParent();
        if (parent.findViewWithTag(ROW_TAG) != null) {
            return true;
        }
        parent.addView(buildRow(root.getContext()));
        return true;
    }

    private static boolean appendToScrollContent(View root) {
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
