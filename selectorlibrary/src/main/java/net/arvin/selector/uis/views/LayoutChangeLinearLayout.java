package net.arvin.selector.uis.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import net.arvin.selector.utils.PSToastUtil;

/**
 * Created by arvinljw on 18/1/10 17:04
 * Function：
 * Desc：
 */
public class LayoutChangeLinearLayout extends LinearLayout {
    private OnLayoutCallback mLayoutCallback;

    public LayoutChangeLinearLayout(Context context) {
        super(context);
    }

    public LayoutChangeLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LayoutChangeLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mLayoutCallback != null) {
            mLayoutCallback.layoutCallback();
        }
    }

    public void setLayoutCallback(OnLayoutCallback layoutCallback) {
        this.mLayoutCallback = layoutCallback;
    }

    public interface OnLayoutCallback {
        void layoutCallback();
    }
}
