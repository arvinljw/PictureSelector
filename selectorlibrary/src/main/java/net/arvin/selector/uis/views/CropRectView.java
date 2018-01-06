package net.arvin.selector.uis.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.arvin.selector.utils.PSScreenUtil;

/**
 * Created by arvinljw on 18/1/6 13:02
 * Function：
 * Desc：
 */
public class CropRectView extends View {

    private final int COLOR_SHADOW = Color.parseColor("#aa000000");
    private final int COLOR_TRANSPARENT = Color.parseColor("#00000000");
    private int mHorizontalSpacing;
    private Paint mPaint;

    public CropRectView(Context context) {
        this(context, null);
    }

    public CropRectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropRectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mHorizontalSpacing = PSScreenUtil.dp2px(24);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawClipRect(canvas);
    }

    private void drawClipRect(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int centerRectSize = width - 2 * mHorizontalSpacing;

        mPaint.setStyle(Paint.Style.FILL);
        //上方的rect
        mPaint.setColor(COLOR_SHADOW);
        canvas.drawRect(0, 0, width, (height - centerRectSize) / 2, mPaint);

        //下方的rect
        mPaint.setColor(COLOR_SHADOW);
        canvas.drawRect(0, (height + centerRectSize) / 2, width, height, mPaint);

        //左方的rect
        mPaint.setColor(COLOR_SHADOW);
        canvas.drawRect(0, (height - centerRectSize) / 2, mHorizontalSpacing, (height + centerRectSize) / 2, mPaint);
        //右方的rect
        mPaint.setColor(COLOR_SHADOW);
        canvas.drawRect(width - mHorizontalSpacing, (height - centerRectSize) / 2, width, (height + centerRectSize) / 2, mPaint);

        mPaint.setColor(COLOR_TRANSPARENT);
        canvas.drawRect(mHorizontalSpacing, (height - centerRectSize) / 2, width - mHorizontalSpacing, (height + centerRectSize) / 2, mPaint);

        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(PSScreenUtil.dp2px(1));
        canvas.drawRect(mHorizontalSpacing, (height - centerRectSize) / 2, width - mHorizontalSpacing, (height + centerRectSize) / 2, mPaint);
    }

    public int getHorizontalSpacing() {
        return mHorizontalSpacing;
    }

    public int getCenterSize() {
        return getWidth() - 2 * mHorizontalSpacing;
    }
}
