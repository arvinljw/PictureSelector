package net.arvin.selector.uis.widgets.editable;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.arvin.selector.uis.widgets.photoview.OnMatrixChangedListener;
import net.arvin.selector.uis.widgets.photoview.PhotoView;
import net.arvin.selector.utils.UiUtil;

/**
 * Created by arvinljw on 2020/8/8 23:55
 * Function：
 * Desc：
 */
public class EditableView extends PhotoView implements OnMatrixChangedListener {
    //是否打开画笔
    private boolean isOpenPainting;
    private PaintingHelper paintingHelper;
    private OnEditTouchListener paintingTouchListener;

    private TextHelper textHelper;
    private OnEditTouchListener textTouchListener;
    private Matrix matrix;
    private Matrix baseMatrix;
    private Matrix imgMatrix;
    private RectF cropRect;
    private RectF backendRect;
    private RectF clipRect;

    public EditableView(Context context) {
        super(context);
    }

    public EditableView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public EditableView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
    }

    @Override
    protected void init() {
        super.init();
        paintingHelper = new PaintingHelper(this);
        textHelper = new TextHelper(this);
        initListener();
        matrix = new Matrix();
        baseMatrix = new Matrix();
        imgMatrix = new Matrix();
        clipRect = new RectF();
        addOnMatrixChangeListener(this);
    }

    private void initListener() {
        paintingTouchListener = new OnEditTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return paintingHelper.onTouch(v, event);
            }
        };

        textTouchListener = new OnEditTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return textHelper.onTouch(v, event);
            }
        };
        textTouchListener.setIntercept(true);
        setTextTouchListener(textTouchListener);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        getSuppMatrix(imgMatrix);
        imgMatrix.invert(baseMatrix);
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        getSuppMatrix(imgMatrix);
        imgMatrix.invert(baseMatrix);
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        getSuppMatrix(imgMatrix);
        imgMatrix.invert(baseMatrix);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        getSuppMatrix(imgMatrix);
        imgMatrix.invert(baseMatrix);
        return changed;
    }

    public RectF calculateCropRect(float degree) {
        if (degree % 180 != 0) {
            return calculateCropRectDegree90();
        } else {
            return calculateCropRectDegree0();
        }
    }

    private RectF calculateCropRectDegree0() {
        Drawable drawable = getDrawable();

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        Context context = getContext();
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        //paddingBottom
        float dp80 = UiUtil.dp2px(context, 80);
        //paddingTop
        float dp24 = UiUtil.dp2px(context, 24);
        //paddingLeft 和paddingRight
        float dp16 = UiUtil.dp2px(context, 16);

        float left = dp16;
        float right = viewWidth - dp16;
        float top = dp24;
        float bottom = viewHeight - dp80;

        RectF maxRect = new RectF(left, top, right, bottom);

        float widthScale = maxRect.width() / drawableWidth;
        float heightScale = maxRect.height() / drawableHeight;

        float scaleDrawableHeight = drawableHeight * widthScale;
        if (drawableWidth > drawableHeight) {
            //横着的图
            maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
            maxRect.bottom = maxRect.top + scaleDrawableHeight;
        } else {
            //竖着的或者方的图
            if (widthScale > heightScale) {
                if (scaleDrawableHeight > maxRect.height()) {
                    float scaleDrawableWidth = drawableWidth * heightScale;
                    maxRect.left = (viewWidth - scaleDrawableWidth) / 2;
                    maxRect.right = maxRect.left + scaleDrawableWidth;
                } else {
                    maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                    maxRect.bottom = maxRect.top + scaleDrawableHeight;
                }
            } else {
                maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                maxRect.bottom = maxRect.top + scaleDrawableHeight;
            }
        }
        return maxRect;
    }

    private RectF calculateCropRectDegree90() {
        Drawable drawable = getDrawable();

        final int drawableWidth = drawable.getIntrinsicHeight();
        final int drawableHeight = drawable.getIntrinsicWidth();

        Context context = getContext();
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        //paddingBottom
        float dp80 = UiUtil.dp2px(context, 80);
        //paddingTop
        float dp24 = UiUtil.dp2px(context, 24);
        //paddingLeft 和paddingRight
        float dp16 = UiUtil.dp2px(context, 16);

        float left = dp16;
        float right = viewWidth - dp16;
        float top = dp24;
        float bottom = viewHeight - dp80;

        RectF maxRect = new RectF(left, top, right, bottom);

        float widthScale = maxRect.width() / drawableWidth;
        float heightScale = maxRect.height() / drawableHeight;

        float scaleDrawableHeight = drawableHeight * widthScale;
        if (drawableWidth > drawableHeight) {
            //横着的图
            maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
            maxRect.bottom = maxRect.top + scaleDrawableHeight;
        } else {
            //竖着的或者方的图
            if (widthScale > heightScale) {
                if (scaleDrawableHeight > maxRect.height()) {
                    float scaleDrawableWidth = drawableWidth * heightScale;
                    maxRect.left = (viewWidth - scaleDrawableWidth) / 2;
                    maxRect.right = maxRect.left + scaleDrawableWidth;
                } else {
                    maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                    maxRect.bottom = maxRect.top + scaleDrawableHeight;
                }
            } else {
                maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                maxRect.bottom = maxRect.top + scaleDrawableHeight;
            }
        }
        return maxRect;
    }

    private RectF calculateCropRectDegree180() {
        Drawable drawable = getDrawable();

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        Context context = getContext();
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        //paddingBottom
        float dp80 = UiUtil.dp2px(context, 80);
        //paddingTop
        float dp24 = UiUtil.dp2px(context, 24);
        //paddingLeft 和paddingRight
        float dp16 = UiUtil.dp2px(context, 16);

        float left = dp16;
        float right = viewWidth - dp16;
        float top = dp24;
        float bottom = viewHeight - dp80;

        RectF maxRect = new RectF(left, top, right, bottom);

        float widthScale = maxRect.width() / drawableWidth;
        float heightScale = maxRect.height() / drawableHeight;

        float scaleDrawableHeight = drawableHeight * widthScale;
        if (drawableWidth > drawableHeight) {
            //横着的图
            maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
            maxRect.bottom = maxRect.top + scaleDrawableHeight;
        } else {
            //竖着的或者方的图
            if (widthScale > heightScale) {
                if (scaleDrawableHeight > maxRect.height()) {
                    float scaleDrawableWidth = drawableWidth * heightScale;
                    maxRect.left = (viewWidth - scaleDrawableWidth) / 2;
                    maxRect.right = maxRect.left + scaleDrawableWidth;
                } else {
                    maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                    maxRect.bottom = maxRect.top + scaleDrawableHeight;
                }
            } else {
                maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                maxRect.bottom = maxRect.top + scaleDrawableHeight;
            }
        }
        return maxRect;
    }

    private RectF calculateCropRectDegree270() {
        Drawable drawable = getDrawable();

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        Context context = getContext();
        float viewWidth = getWidth();
        float viewHeight = getHeight();

        //paddingBottom
        float dp80 = UiUtil.dp2px(context, 80);
        //paddingTop
        float dp24 = UiUtil.dp2px(context, 24);
        //paddingLeft 和paddingRight
        float dp16 = UiUtil.dp2px(context, 16);

        float left = dp16;
        float right = viewWidth - dp16;
        float top = dp24;
        float bottom = viewHeight - dp80;

        RectF maxRect = new RectF(left, top, right, bottom);

        float widthScale = maxRect.width() / drawableWidth;
        float heightScale = maxRect.height() / drawableHeight;

        float scaleDrawableHeight = drawableHeight * widthScale;
        if (drawableWidth > drawableHeight) {
            //横着的图
            maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
            maxRect.bottom = maxRect.top + scaleDrawableHeight;
        } else {
            //竖着的或者方的图
            if (widthScale > heightScale) {
                if (scaleDrawableHeight > maxRect.height()) {
                    float scaleDrawableWidth = drawableWidth * heightScale;
                    maxRect.left = (viewWidth - scaleDrawableWidth) / 2;
                    maxRect.right = maxRect.left + scaleDrawableWidth;
                } else {
                    maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                    maxRect.bottom = maxRect.top + scaleDrawableHeight;
                }
            } else {
                maxRect.top = top + (maxRect.height() - scaleDrawableHeight) / 2;
                maxRect.bottom = maxRect.top + scaleDrawableHeight;
            }
        }
        return maxRect;
    }

    public void setCropRect(RectF cropRect) {
        if (cropRect == null) {
            if (this.cropRect != null) {
                backendRect = new RectF();
                backendRect.set(this.cropRect);
            }
        }
        this.cropRect = cropRect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //有裁剪的时候才裁剪
        if (cropRect != null) {
            calculateClipRect();
            canvas.save();
            canvas.clipRect(clipRect);
        }

        super.onDraw(canvas);
        paintingHelper.onDraw(canvas);
        textHelper.onDraw(canvas);

        if (cropRect != null) {
            canvas.restore();
        }
    }

    private void calculateClipRect() {
        clipRect.setEmpty();
        clipRect.set(cropRect);
        matrix.reset();
        matrix.set(baseMatrix);
        matrix.postConcat(imgMatrix);
        matrix.mapRect(clipRect);
    }

    public void rollback() {
        if (backendRect != null) {
            cropRect = new RectF();
            cropRect.set(backendRect);
            backendRect = null;
        }
        getAttacher().rollback();
    }

    public PaintingHelper getPaintingHelper() {
        return paintingHelper;
    }

    public void setOpenPainting(boolean openPainting) {
        isOpenPainting = openPainting;
        if (isOpenPainting) {
            paintingTouchListener.setIntercept(true);
            setOnEditTouchListener(paintingTouchListener);
        } else {
            setOnEditTouchListener(null);
        }
    }

    public void addText(String result, int textColor, int bgColor, boolean hasBg) {
        textHelper.addText(result, textColor, bgColor, hasBg);
    }

    public void setTextDraggingCallback(TextHelper.TextDraggingCallback callback) {
        textHelper.setTextDraggingCallback(callback);
    }

    public RectF getRealTouchRect(TextHelper.TextItem draggingItem) {
        return textHelper.getRealTouchRect(draggingItem);
    }

    @Override
    public void onMatrixChanged(RectF rect) {
        getSuppMatrix(imgMatrix);
    }

}
