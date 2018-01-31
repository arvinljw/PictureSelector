package net.arvin.selector.uis.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import net.arvin.selector.R;
import net.arvin.selector.utils.PSScreenUtil;


/**
 * created by arvin on 16/9/3 12:11
 * email：1035407623@qq.com
 */
public class FolderBackGroundLayout extends FrameLayout {
    private final int mDividerSize = PSScreenUtil.dp2px(1);
    private final int mOuterDividerColor = R.color.ps_black_hint;
    private final int mInnerDividerColor = R.color.ps_black_secondary;
    private Paint mPaint;


    public FolderBackGroundLayout(Context context) {
        this(context, null, 0);
    }

    public FolderBackGroundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FolderBackGroundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setStrokeWidth(mDividerSize);
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //noinspection SuspiciousNameCombination
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(getResources().getColor(mOuterDividerColor));
        int margin = mDividerSize * 2;
        canvas.drawLine(getWidth() - mDividerSize, margin, getWidth() - mDividerSize, getHeight() - mDividerSize, mPaint);
        canvas.drawLine(margin, getHeight() - mDividerSize, getWidth() - mDividerSize, getHeight() - mDividerSize, mPaint);

        margin = mDividerSize * 3;
        mPaint.setColor(getResources().getColor(mInnerDividerColor));
        canvas.drawLine(getWidth() - margin, mDividerSize, getWidth() - margin, getHeight() - margin, mPaint);
        canvas.drawLine(mDividerSize, getHeight() - margin, getWidth() - margin, getHeight() - margin, mPaint);
    }
}
