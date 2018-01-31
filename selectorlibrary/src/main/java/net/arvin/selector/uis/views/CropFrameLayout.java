package net.arvin.selector.uis.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by arvinljw on 18/1/18 16:34
 * Function：
 * Desc：
 */
public class CropFrameLayout extends FrameLayout {
    private CropRectView mClipRectView;

    public CropFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public CropFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mClipRectView = new CropRectView(getContext());
        addView(mClipRectView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
}
