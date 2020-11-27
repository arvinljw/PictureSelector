package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/9/18 14:57
 * Function：
 * Desc：
 */
public class CropRotateView extends View {
    private Paint shadowPaint;
    private Paint borderPaint;

    public RectF cropRect;
    //图片和裁剪边框任一拖动就表示拖动
    private boolean isDragging;
    private float borderWidth;
    private float lineWidth;
    private float cornerWidth;
    private float cornerHeight;

    public CropRotateView(Context context) {
        this(context, null);
    }

    public CropRotateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropRotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int shadowColor = Color.parseColor("#66000000");
        shadowPaint.setColor(shadowColor);
        shadowPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);

        borderWidth = UiUtil.dp2px(getContext(), 2);
        lineWidth = UiUtil.dp2px(getContext(), 1);
        cornerWidth = UiUtil.dp2px(getContext(), 4);
        cornerHeight = UiUtil.dp2px(getContext(), 20);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    public static int getSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
            case MeasureSpec.AT_MOST:
                result = size;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    public void setCropRect(RectF rect) {
        if (rect == null) {
            cropRect = null;
            invalidate();
            return;
        }
        if (cropRect == null) {
            cropRect = new RectF();
        }
        cropRect.set(rect);
        invalidate();
    }

    public RectF getCropRect() {
        RectF rectF = new RectF();
        rectF.set(cropRect);
        rectF.left = 0;
        rectF.right = UiUtil.getScreenWidth(getContext());
        float scale = rectF.width() / cropRect.width();
        float scaleHeight = cropRect.height() * scale;
        rectF.top = rectF.top - (scaleHeight - cropRect.height()) / 2 + getTransY();
        rectF.bottom = rectF.bottom + (scaleHeight - cropRect.height()) / 2 + getTransY();
        return rectF;
    }

    public float getScale() {
        return UiUtil.getScreenWidth(getContext()) / cropRect.width();
    }

    public float getTransY() {
        return UiUtil.dp2px(getContext(), 28);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cropRect == null) {
            return;
        }

        drawLeftRect(canvas);
        drawTopRect(canvas);
        drawRightRect(canvas);
        drawBottomRect(canvas);

        drawBorder(canvas);
        drawLine(canvas);
        drawCorner(canvas);
    }

    private void drawBorder(Canvas canvas) {
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(Color.parseColor("#CCFFFFFF"));

        canvas.drawRect(cropRect, borderPaint);
    }

    private void drawLine(Canvas canvas) {
        borderPaint.setStrokeWidth(lineWidth);
        borderPaint.setColor(Color.parseColor("#20FFFFFF"));

        //绘制竖线
        for (int i = 0; i < 2; i++) {
            float vx = cropRect.left + (cropRect.width() * (i + 1)) / 3;
            float startY = cropRect.top;
            float stopY = cropRect.bottom;
            canvas.drawLine(vx, startY, vx, stopY, borderPaint);
        }

        //绘制横线
        for (int i = 0; i < 2; i++) {
            float startX = cropRect.left;
            float hy = cropRect.top + (cropRect.height() * (i + 1)) / 3;
            float stopX = cropRect.right;
            canvas.drawLine(startX, hy, stopX, hy, borderPaint);
        }
    }

    private void drawCorner(Canvas canvas) {
        borderPaint.setStrokeWidth(cornerWidth);
        borderPaint.setColor(Color.parseColor("#FFFFFF"));

        //左上
        drawVerticalLine(canvas, cropRect.left - cornerWidth + borderWidth, cropRect.top - cornerWidth + borderWidth);
        drawHorizontalLine(canvas, cropRect.left - cornerWidth, cropRect.top - cornerWidth + borderWidth);

        //左下
        drawVerticalLine(canvas, cropRect.left - cornerWidth + borderWidth, cropRect.bottom - cornerHeight + cornerWidth);
        drawHorizontalLine(canvas, cropRect.left - cornerWidth + borderWidth, cropRect.bottom + cornerWidth - borderWidth);

        //右上
        drawVerticalLine(canvas, cropRect.right + cornerWidth - borderWidth, cropRect.top - cornerWidth + borderWidth);
        drawHorizontalLine(canvas, cropRect.right - cornerHeight + cornerWidth, cropRect.top - cornerWidth + borderWidth);

        //右下
        drawVerticalLine(canvas, cropRect.right + cornerWidth - borderWidth, cropRect.bottom - cornerHeight + cornerWidth);
        drawHorizontalLine(canvas, cropRect.right - cornerHeight + cornerWidth, cropRect.bottom + cornerWidth - borderWidth);

    }

    private void drawVerticalLine(Canvas canvas, float startX, float startY) {
        canvas.drawLine(startX, startY, startX, startY + cornerHeight, borderPaint);
    }

    private void drawHorizontalLine(Canvas canvas, float startX, float startY) {
        canvas.drawLine(startX, startY, startX + cornerHeight, startY, borderPaint);
    }

    private void drawLeftRect(Canvas canvas) {
        if (isDragging) {
            return;
        }
        canvas.drawRect(0, cropRect.top, cropRect.left, cropRect.bottom, shadowPaint);
    }

    private void drawTopRect(Canvas canvas) {
        if (isDragging) {
            return;
        }
        canvas.drawRect(0, 0, getWidth(), cropRect.top, shadowPaint);
    }

    private void drawRightRect(Canvas canvas) {
        if (isDragging) {
            return;
        }
        canvas.drawRect(cropRect.right, cropRect.top, getWidth(), cropRect.bottom, shadowPaint);
    }

    private void drawBottomRect(Canvas canvas) {
        if (isDragging) {
            return;
        }
        canvas.drawRect(0, cropRect.bottom, getWidth(), getHeight(), shadowPaint);
    }
}
