package net.arvin.selector.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by arvinljw on 17/12/25 11:27
 * Function：
 * Desc：
 */
public final class PSToastUtil {
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    public static void init(Activity activity) {
        sContext = activity;
    }

    public static void onDestroy() {
        sContext = null;
    }

    public static void showToast(String msg) {
        if (!hasContext()) return;
        Toast.makeText(sContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(int msgResId) {
        if (!hasContext()) return;
        Toast.makeText(sContext, msgResId, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String msg) {
        if (!hasContext()) return;
        Toast.makeText(sContext, msg, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(int msgResId) {
        if (!hasContext()) return;
        Toast.makeText(sContext, msgResId, Toast.LENGTH_LONG).show();
    }

    private static boolean hasContext() {
        return sContext != null;
    }

}
