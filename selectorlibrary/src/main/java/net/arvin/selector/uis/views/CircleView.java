package net.arvin.selector.uis.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import net.arvin.selector.R;
import net.arvin.selector.utils.PSScreenUtil;

/**
 * Created by arvinljw on 18/1/8 15:25
 * Function：
 * Desc：
 */
public class CircleView extends View {
    private Paint mPaint;
    private float mRadius;
    private int mColor;
    private int mPadding;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleView);
        mColor = typedArray.getColor(R.styleable.CircleView_ps_color, Color.WHITE);
        mRadius = typedArray.getDimension(R.styleable.CircleView_ps_radius, PSScreenUtil.dp2px(8));
        typedArray.recycle();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPadding = PSScreenUtil.dp2px(2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(widthMeasureSpec), getSize(heightMeasureSpec));
    }

    private int getSize(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                result = 0;
                break;
            case MeasureSpec.AT_MOST:
                result = (int) ((mRadius + mPadding) * 2);
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius + mPadding, mPaint);
        mPaint.setColor(mColor);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
    }

    public void setRadius(float radius) {
        this.mRadius = radius;
        requestLayout();
    }

    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    public int getColor() {
        return mColor;
    }
}
