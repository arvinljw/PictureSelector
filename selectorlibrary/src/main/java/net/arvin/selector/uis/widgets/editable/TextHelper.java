package net.arvin.selector.uis.widgets.editable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.icu.number.Scale;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import net.arvin.selector.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2020/9/11 15:46
 * Function：
 * Desc：
 */
public class TextHelper extends BaseEditHelper {

    private Matrix matrix;
    private Matrix currMatrix;

    private TextPaint textPaint;
    private Paint rectPaint;
    private float margin;
    private float paddingHorizontal;
    private float paddingVertical;
    private float touchSize;
    private float rectSpotRadius;

    private Path path;
    private RectF roundRect;
    private RectF lastLinRect;
    private RectF arcRect;
    private Paint bgPaint;
    private float[] radii;
    private float[] lastRadii;
    private float radius;

    private List<TextItem> textItems;
    private TextItem draggingItem;
    private float downX;
    private float downY;
    private float lastX;
    private float lastY;
    private RectF touchRect;

    private ScaleGestureDetector scaleGestureDetector;
    private RotateGestureDetector rotateGestureDetector;

    private TextDraggingCallback textDraggingCallback;

    public TextHelper(EditableView targetView) {
        super(targetView);

        textItems = new ArrayList<>();

        matrix = new Matrix();
        currMatrix = new Matrix();

        Context context = targetView.getContext();
        float textSize = UiUtil.sp2px(context, 20);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);

        path = new Path();
        roundRect = new RectF();
        lastLinRect = new RectF();
        arcRect = new RectF();

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        radii = new float[8];
        lastRadii = new float[8];
        radius = UiUtil.dp2px(context, 8);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setColor(Color.WHITE);
        touchRect = new RectF();

        rectSpotRadius = UiUtil.dp2px(context, 2);
        rectPaint.setStrokeWidth(UiUtil.dp2px(context, 1));

        margin = UiUtil.dp2px(context, 16);
        paddingHorizontal = UiUtil.dp2px(context, 16);
        paddingVertical = UiUtil.dp2px(context, 12);
        touchSize = UiUtil.dp2px(context, 6);

        scaleGestureDetector = new ScaleGestureDetector(targetView.getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor = detector.getScaleFactor();

                if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                    return false;
                }
                if (draggingItem != null) {
                    draggingItem.textScale = scaleFactor;
                    RectF touchRect = getRealTouchRect(draggingItem);
                    float x = touchRect.left + touchRect.width() / 2;
                    float y = touchRect.top + touchRect.height() / 2;
                    draggingItem.matrix.postScale(scaleFactor, scaleFactor, x, y);
                }
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }
        });

        rotateGestureDetector = new RotateGestureDetector(new RotateGestureDetector.OnRotateGestureListener() {
            @Override
            public boolean onRotate(float degrees, float focusX, float focusY) {
                if (draggingItem != null) {
                    RectF touchRect = getRealTouchRect(draggingItem);
                    float x = touchRect.left + touchRect.width() / 2;
                    float y = touchRect.top + touchRect.height() / 2;
                    draggingItem.matrix.postRotate(degrees, x, y);
                    return true;
                }
                return false;
            }
        });
    }

    public void addText(String text, int textColor, int bgColor, boolean hasBg) {
        TextItem textItem = TextItem.createTextItem(text, textColor, bgColor, hasBg);

        if (imgMatrix == null) {
            setImageMatrix();
        }
        imgMatrix.invert(currMatrix);
        textItem.matrix.set(currMatrix);

        textItem.init();

        textItems.add(textItem);
        invalidate();
    }

    public void setTextDraggingCallback(TextDraggingCallback textDraggingCallback) {
        this.textDraggingCallback = textDraggingCallback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float rawY = event.getRawY() - UiUtil.getStatusBarHeight(targetView.getContext());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getRawX();
                downY = rawY;
                lastX = downX;
                lastY = downY;
                if (draggingItem == null) {
                    for (int i = textItems.size() - 1; i >= 0; i--) {
                        TextItem textItem = textItems.get(i);
                        RectF rect = getRealTouchRect(textItem);
                        if (rect.left < downX && rect.right > downX && rect.top < downY && rect.bottom > downY) {
                            draggingItem = textItem;
                            textItem.isDragging = true;
                            invalidate();
                            if (textDraggingCallback != null) {
                                textDraggingCallback.onTextStartDragging();
                            }
                            scaleGestureDetector.onTouchEvent(event);
                            return true;
                        }
                    }
                }
                draggingItem = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (draggingItem != null) {
                    float offsetX = (event.getRawX() - lastX) / imgScale / draggingItem.textScale;
                    float offsetY = (rawY - lastY) / imgScale / draggingItem.textScale;
                    draggingItem.matrix.postTranslate(offsetX, offsetY);

                    lastX = event.getRawX();
                    lastY = rawY;

                    scaleGestureDetector.onTouchEvent(event);
                    rotateGestureDetector.onTouchEvent(event);

                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (draggingItem != null) {
                    boolean canDelete = false;
                    if (textDraggingCallback != null) {
                        canDelete = textDraggingCallback.onTextDraggingRelease(draggingItem);
                    }
                    if (canDelete) {
                        textItems.remove(draggingItem);
                    }
                    //之后再做延时消失
                    draggingItem.isDragging = false;
                    draggingItem = null;
                    invalidate();
                    scaleGestureDetector.onTouchEvent(event);
                    return true;
                }
                break;
            default:
                break;

        }
        return false;
    }

    /**
     * 获取该文本的真实位置
     *
     * @param textItem
     * @return
     */
    public RectF getRealTouchRect(TextItem textItem) {
        RectF rect = textItem.touchRect;
        touchRect.left = rect.left;
        touchRect.right = rect.right;
        touchRect.top = rect.top;
        touchRect.bottom = rect.bottom;

        matrix.reset();
        matrix.set(textItem.matrix);
        matrix.postConcat(imgMatrix);

        matrix.mapRect(touchRect);

        return touchRect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (TextItem textItem : textItems) {
            canvas.save();
            matrix.reset();
            matrix.set(textItem.matrix);
            matrix.postConcat(imgMatrix);
            canvas.setMatrix(matrix);

            textPaint.setColor(textItem.textColor);
            int width = (int) (textPaint.measureText(textItem.text) + paddingHorizontal * 2);
            int maxSize = (int) (UiUtil.getScreenWidth(targetView.getContext()) - margin * 2);
            width = (int) (Math.min(width, maxSize) - paddingHorizontal * 2);
            @SuppressLint("DrawAllocation")
            StaticLayout staticLayout = new StaticLayout(textItem.text, textPaint, width, Layout.Alignment.ALIGN_NORMAL,
                    1, 0, false);

            drawBg(canvas, textItem, staticLayout);
            drawText(canvas, textItem, staticLayout);
            drawTouchBox(canvas, textItem);

            canvas.restore();
        }

        if (draggingItem != null && textDraggingCallback != null) {
            textDraggingCallback.onTextDragging(draggingItem);
        }
    }

    private void drawBg(Canvas canvas, TextItem textItem, StaticLayout staticLayout) {
        int lineCount = staticLayout.getLineCount();
        boolean isShowAllLine = calculateRect(textItem, staticLayout, lineCount);
        if (!textItem.hasBg) {
            return;
        }

        calculateRadii(lineCount, isShowAllLine);

        addPath(lineCount, isShowAllLine);

        bgPaint.setColor(textItem.bgColor);
        canvas.drawPath(path, bgPaint);
    }

    private boolean calculateRect(TextItem textItem, Layout layout, int lineCount) {
        roundRect.setEmpty();
        lastLinRect.setEmpty();
        int screenHeight = UiUtil.getScreenHeight(targetView.getContext());
        int screenWidth = UiUtil.getScreenWidth(targetView.getContext());
        int top = (screenHeight - layout.getHeight()) / 2;
        int left = (int) ((screenWidth - layout.getWidth()) / 2 - paddingHorizontal);

        if (lineCount > 0) {
            for (int i = 0; i < lineCount; i++) {
                boolean isLast = i == lineCount - 1;
                if (i == 0) {
                    roundRect.left = left + layout.getLineLeft(0);
                    roundRect.top = top + layout.getLineTop(0);
                    roundRect.right = left + paddingHorizontal + paddingHorizontal + layout.getLineRight(0);
                    roundRect.bottom = top + paddingVertical + (isLast ? paddingVertical : 0) + layout.getLineBottom(0);
                } else if (i < lineCount - 1) {
                    roundRect.bottom = roundRect.bottom + layout.getLineBottom(i) - layout.getLineTop(i);
                }
                roundRect.right = Math.max(roundRect.right, left + paddingHorizontal + paddingHorizontal + layout.getLineRight(i));

                if (i == lineCount - 1) {
                    lastLinRect.left = left + layout.getLineLeft(lineCount - 1);
                    lastLinRect.right = left + paddingHorizontal + paddingHorizontal + layout.getLineRight(lineCount - 1);
                    lastLinRect.top = top + paddingVertical + layout.getLineTop(lineCount - 1);
                    lastLinRect.bottom = top + layout.getHeight() + paddingVertical * 2;
                }
            }
        }

        boolean isShowAllLine = lastLinRect.width() + radius * 2 >= roundRect.width();
        if (isShowAllLine) {
            roundRect.bottom = top + layout.getHeight() + paddingVertical * 2;
        } else {
            roundRect.bottom = lastLinRect.top;
        }
        textItem.setRect(roundRect, isShowAllLine, lastLinRect, touchSize);

        return isShowAllLine;
    }

    private void calculateRadii(int lineCount, boolean isShowAllLine) {
        if (lineCount > 1) {
            if (isShowAllLine) {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = radius;
            } else {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = 0;
            }
        } else if (lineCount == 1) {
            if (isShowAllLine) {
                radii[0] = radii[1] = radius;
                radii[2] = radii[3] = radius;
                radii[4] = radii[5] = radius;
                radii[6] = radii[7] = radius;
            }
        }

        lastRadii[0] = lastRadii[1] = 0;
        lastRadii[2] = lastRadii[3] = 0;
        lastRadii[4] = lastRadii[5] = radius;
        lastRadii[6] = lastRadii[7] = radius;
    }

    private void addPath(int lineCount, boolean isShowAllLine) {
        path.reset();
        if (lineCount > 0) {
            if (lineCount > 1 || isShowAllLine) {
                path.addRoundRect(roundRect, radii, Path.Direction.CW);
            }
            if (!isShowAllLine) {
                path.addRoundRect(lastLinRect, lastRadii, Path.Direction.CW);

                arcRect.left = lastLinRect.right;
                arcRect.top = roundRect.bottom;
                arcRect.right = arcRect.left + radius * 2;
                arcRect.bottom = arcRect.top + radius * 2;
                path.addArc(arcRect, 180, 90);
                path.lineTo(lastLinRect.right, roundRect.bottom);
                path.lineTo(lastLinRect.right, arcRect.bottom);
                path.close();
            }
        }
    }

    private void drawText(Canvas canvas, TextItem textItem, StaticLayout layout) {
        int count = canvas.save();
        canvas.translate(textItem.bgRect.left + paddingHorizontal, textItem.bgRect.top + paddingVertical);
        layout.draw(canvas);
        canvas.restoreToCount(count);
    }

    private void drawTouchBox(Canvas canvas, TextItem textItem) {
        if (!textItem.isDragging) {
            return;
        }
        rectPaint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(textItem.touchRect, rectPaint);

        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(textItem.touchRect.left - rectSpotRadius, textItem.touchRect.top - rectSpotRadius,
                textItem.touchRect.left + rectSpotRadius, textItem.touchRect.top + rectSpotRadius, rectPaint);

        canvas.drawRect(textItem.touchRect.right - rectSpotRadius, textItem.touchRect.top - rectSpotRadius,
                textItem.touchRect.right + rectSpotRadius, textItem.touchRect.top + rectSpotRadius, rectPaint);

        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(textItem.touchRect.left - rectSpotRadius, textItem.touchRect.bottom - rectSpotRadius,
                textItem.touchRect.left + rectSpotRadius, textItem.touchRect.bottom + rectSpotRadius, rectPaint);

        canvas.drawRect(textItem.touchRect.right - rectSpotRadius, textItem.touchRect.bottom - rectSpotRadius,
                textItem.touchRect.right + rectSpotRadius, textItem.touchRect.bottom + rectSpotRadius, rectPaint);
    }

    public static class TextItem {
        public String text;
        public int textColor;
        public int bgColor;
        public boolean hasBg;

        public Matrix matrix;
        public RectF touchRect;
        public RectF bgRect;
        public boolean isDragging;

        public float textScale;

        private TextItem(String text, int textColor, boolean hasBg) {
            this.text = text;
            this.textColor = textColor;
            this.hasBg = hasBg;
            this.textScale = 1f;
            this.matrix = new Matrix();
        }

        public static TextItem createTextItem(String text, int textColor, int bgColor, boolean hasBg) {
            TextItem item = new TextItem(text, textColor, hasBg);
            if (hasBg) {
                item.bgColor = bgColor;
            }
            return item;
        }

        public void init() {
            touchRect = new RectF();

            bgRect = new RectF();
        }

        public void setRect(RectF roundRect, boolean isShowAllLine, RectF lastLineRect, float touchSize) {
            bgRect.left = roundRect.left;
            bgRect.right = roundRect.right;
            bgRect.top = roundRect.top;
            bgRect.bottom = roundRect.bottom;

            touchRect.left = bgRect.left - touchSize;
            touchRect.right = bgRect.right + touchSize;
            touchRect.top = bgRect.top - touchSize;
            touchRect.bottom = bgRect.bottom + touchSize;

            if (!isShowAllLine) {
                touchRect.bottom = lastLineRect.bottom + touchSize;
            }
        }
    }

    public interface TextDraggingCallback {
        void onTextStartDragging();

        void onTextDragging(TextItem draggingItem);

        boolean onTextDraggingRelease(TextItem draggingItem);
    }
}
