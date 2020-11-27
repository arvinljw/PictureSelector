package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/13 19:45
 * Function：
 * Desc：
 */
public class ColorView extends View {
    private Paint bgPaint;
    private Paint bmPaint;
    private Bitmap bitmap;
    private Paint borderPaint;
    private float itemSize;
    private Rect srcRect;
    private Rect dstRect;

    public ColorView(Context context) {
        this(context, null);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.WHITE);

        srcRect = new Rect();
        dstRect = new Rect();
    }

    public void setColorId(int colorId) {
        bitmap = null;
        bgPaint.setColor(getResources().getColor(colorId));
    }

    public void setImageId(int imageId) {
        bitmap = BitmapFactory.decodeResource(getResources(), imageId);
        bgPaint.setColor(Color.WHITE);
        srcRect.left = 0;
        srcRect.top = 0;
        srcRect.right = bitmap.getWidth();
        srcRect.bottom = bitmap.getHeight();
    }

    public void setItemSize(float itemSize) {
        this.itemSize = itemSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = getSize((int) itemSize, widthMeasureSpec);
        setMeasuredDimension(size, size);
    }

    public int getSize(int size, int measureSpec) {
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float border = UiUtil.dp2px(getContext(), 4);
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = getWidth() / 2 - getPaddingLeft();
        if (bitmap != null) {
            int bitmapSize = (int) (getWidth() - getPaddingLeft() - getPaddingRight() - border * 2);
            dstRect.left = (int) (getPaddingLeft() + border);
            dstRect.top = (int) (getPaddingTop() + border);
            dstRect.right = dstRect.left + bitmapSize;
            dstRect.bottom = dstRect.top + bitmapSize;
            canvas.drawCircle(cx, cy, radius - border, bgPaint);
            canvas.drawBitmap(bitmap, srcRect, dstRect, bmPaint);
        } else {
            canvas.drawCircle(cx, cy, radius - border, bgPaint);
        }

        if (isSelected()) {
            borderPaint.setStrokeWidth(border);
            canvas.drawCircle(cx, cy, radius - border / 2 - 1, borderPaint);
        } else {
            borderPaint.setStrokeWidth(border / 2);
            canvas.drawCircle(cx, cy, radius - border, borderPaint);
        }
    }
}
