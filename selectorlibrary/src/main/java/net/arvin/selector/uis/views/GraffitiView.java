package net.arvin.selector.uis.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.arvin.selector.uis.views.photoview.OnMatrixChangedListener;
import net.arvin.selector.utils.PSScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 18/1/8 11:04
 * Function：
 * Desc：涂鸦View
 */
public class GraffitiView extends View implements OnMatrixChangedListener {
    private Paint mPaint;
    private Path mPath;
    private float mLastX, mLastY;
    private List<DrawPath> mDrawPathList;
    private List<DrawPath> mSavePathList;

    private boolean mCanDraw;
    private int mPaintColor = Color.BLACK;
    private Path mMatrixPath;
    private Matrix mMatrix;
    private Matrix mImageMatrix;

    public GraffitiView(Context context) {
        this(context, null);
    }

    public GraffitiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraffitiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDrawPathList = new ArrayList<>();
        mSavePathList = new ArrayList<>();
        mMatrixPath = new Path();
        mImageMatrix = new Matrix();
        mImageMatrix.reset();
        mMatrix = new Matrix();
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(PSScreenUtil.dp2px(5));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mPaintColor);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!mCanDraw) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getRawX();
                float downY = event.getRawY();
                Matrix matrix = new Matrix();
                mImageMatrix.invert(matrix);
                mPath = new Path();
                mPath.moveTo(downX, downY);
                mDrawPathList.add(new DrawPath(mPath, matrix, mPaint));
                invalidate();
                mLastX = downX;
                mLastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getRawX();
                float moveY = event.getRawY();
                mPath.quadTo(mLastX, mLastY, moveX, moveY);
                invalidate();
                mLastX = moveX;
                mLastY = moveY;
                break;
            case MotionEvent.ACTION_UP:
                initPaint();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawPathList != null && !mDrawPathList.isEmpty()) {
            for (DrawPath drawPath : mDrawPathList) {
                if (drawPath.mPath != null) {
                    canvas.save();
                    mMatrix.reset();
                    mMatrix.set(drawPath.mMatrix);
                    mMatrix.postConcat(mImageMatrix);
                    canvas.setMatrix(mMatrix);
                    mMatrixPath.set(drawPath.mPath);
                    canvas.drawPath(mMatrixPath, drawPath.mPaint);
                    canvas.restore();
                }
            }
        }
    }

    public void setCanDraw(boolean canDraw) {
        this.mCanDraw = canDraw;
    }

    public void setColor(@ColorInt int color) {
        this.mPaintColor = color;
        mPaint.setColor(mPaintColor);
    }

    public void undo() {
        if (mDrawPathList != null && mDrawPathList.size() >= 1) {
            mSavePathList.add(mDrawPathList.get(mDrawPathList.size() - 1));
            mDrawPathList.remove(mDrawPathList.size() - 1);
            invalidate();
        }
    }

    public void reUndo() {
        if (mSavePathList != null && !mSavePathList.isEmpty()) {
            mDrawPathList.add(mSavePathList.get(mSavePathList.size() - 1));
            mSavePathList.remove(mSavePathList.size() - 1);
            invalidate();
        }
    }

    public void clear() {
        mDrawPathList.clear();
        mSavePathList.clear();
    }

    @Override
    public void onMatrixChanged(RectF rect, Matrix matrix) {
        mImageMatrix.set(matrix);
        invalidate();
    }

    public class DrawPath {
        Path mPath;
        Matrix mMatrix;
        Paint mPaint;

        DrawPath(Path path, Matrix matrix, Paint paint) {
            this.mPath = path;
            this.mMatrix = matrix;
            this.mPaint = paint;
        }
    }
}
