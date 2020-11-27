package net.arvin.selector.uis.widgets.editable;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by arvinljw on 2020/9/17 16:34
 * Function：
 * Desc：
 */
public class KeyboardHeightUtil {
    private Activity activity;
    private OnResizeListener onResizeListener;
    private int screenHeight;
    private int nowH;
    private int oldH;

    public KeyboardHeightUtil(Activity activity, OnResizeListener onResizeListener) {
        this.activity = activity;
        this.onResizeListener = onResizeListener;
    }

    public void addSoftOpenListener(View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (activity == null) {
                    return;
                }
                Rect r = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                if (screenHeight == 0) {
                    screenHeight = r.bottom;
                }
                nowH = screenHeight - r.bottom;
                if (oldH != -1 && nowH != oldH) {
                    if (nowH > 0) {
                        if (onResizeListener != null) {
                            onResizeListener.OnSoftPop(nowH);
                        }
                    } else {
                        if (nowH < 0) {
                            nowH = 0;
                            screenHeight = 0;
                        }
                        if (onResizeListener != null) {
                            onResizeListener.OnSoftClose();
                        }
                    }
                }
                oldH = nowH;
            }
        });
    }

    public void onDestroy(){
        activity = null;
        onResizeListener = null;
    }

    public interface OnResizeListener {
        void OnSoftPop(int height);

        void OnSoftClose();
    }
}
