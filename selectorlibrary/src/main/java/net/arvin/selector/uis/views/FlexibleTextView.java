package net.arvin.selector.uis.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import net.arvin.selector.uis.views.photoview.Compat;
import net.arvin.selector.uis.views.photoview.CustomGestureDetector;
import net.arvin.selector.uis.views.photoview.OnGestureListener;
import net.arvin.selector.uis.views.photoview.OnMatrixChangedListener;
import net.arvin.selector.utils.PSScreenUtil;

/**
 * Created by arvinljw on 18/1/9 16:48
 * Function：
 * Desc：
 */
public class FlexibleTextView extends View implements OnGestureListener, OnMatrixChangedListener {
    private GestureDetector mGestureDetector;
    private CustomGestureDetector mScaleDragDetector;
    private View.OnClickListener mOnClickListener;
    private OnLongClickListener mLongClickListener;

    private Matrix mCurrMatrix;
    private Matrix mImageMatrix;
    private Matrix mImageInverseMatrix;
    private Matrix mDrawMatrix;
    private FlingRunnable mCurrentFlingRunnable;
    private Paint mPaint;
    private int mPaintColor = Color.BLACK;
    private String mText = "";
    private boolean mCanTouch = false;

    public FlexibleTextView(Context context) {
        this(context, null);
    }

    public FlexibleTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlexibleTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCurrMatrix = new Matrix();
        mImageMatrix = new Matrix();
        mImageInverseMatrix = new Matrix();
        mDrawMatrix = new Matrix();
        mCurrMatrix.reset();
        mImageMatrix.reset();
        mImageInverseMatrix.reset();
        initPaint();

        mScaleDragDetector = new CustomGestureDetector(getContext(), this);

        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

            // forward long click listener
            @Override
            public void onLongPress(MotionEvent e) {
                if (mLongClickListener != null) {
                    mLongClickListener.onLongClick(FlexibleTextView.this);
                }
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(FlexibleTextView.this);
                }
                return false;
            }
        });
    }

    private boolean isInContentRect(MotionEvent e) {
        return true;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextSize(PSScreenUtil.sp2px(24));
        mPaint.setColor(mPaintColor);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (!mCanTouch) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelFling();
                break;
        }
        boolean handle = mScaleDragDetector.onTouchEvent(event);
        mGestureDetector.onTouchEvent(event);
        return handle;
    }

    @Override
    public void onDrag(float dx, float dy) {
        if (mScaleDragDetector.isScaling()) {
            return; // Do not drag if we are already scaling
        }
        mCurrMatrix.postTranslate(dx, dy);
        invalidate();
    }

    @Override
    public void onFling(float startX, float startY, float velocityX, float velocityY) {
        mCurrentFlingRunnable = new FlingRunnable(getContext());
        mCurrentFlingRunnable.fling((int) velocityX, (int) velocityY);
        post(mCurrentFlingRunnable);
    }

    @Override
    public void onScale(float scaleFactor, float focusX, float focusY) {
        mCurrMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        mDrawMatrix.set(mCurrMatrix);
        mDrawMatrix.postConcat(mImageInverseMatrix);
        mDrawMatrix.postConcat(mImageMatrix);
        canvas.setMatrix(mDrawMatrix);
        canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mPaint);
        canvas.restore();
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    private void cancelFling() {
        if (mCurrentFlingRunnable != null) {
            mCurrentFlingRunnable.cancelFling();
            mCurrentFlingRunnable = null;
        }
    }

    public void setText(String msg) {
        mText = msg;
        invalidate();
    }

    public void setCanTouch(boolean canTouch) {
        this.mCanTouch = canTouch;
        mCurrMatrix.set(mDrawMatrix);
        mImageMatrix.invert(mImageInverseMatrix);
    }

    public void setColor(@ColorInt int color) {
        this.mPaintColor = color;
        mPaint.setColor(mPaintColor);
    }

    @Override
    public void onMatrixChanged(RectF rect, Matrix matrix) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        mImageMatrix.set(matrix);
        invalidate();
    }

    public String getText() {
        return mText;
    }

    private class FlingRunnable implements Runnable {

        private final OverScroller mScroller;
        private int mCurrentX, mCurrentY;

        FlingRunnable(Context context) {
            mScroller = new OverScroller(context);
        }

        public void cancelFling() {
            mScroller.forceFinished(true);
        }

        void fling(int velocityX,
                   int velocityY) {

            final int startX = Math.round(getLeft());
            final int startY = Math.round(getTop());

            final int minX, maxX, minY, maxY;
            minX = maxX = startX;
            minY = maxY = startY;

            mCurrentX = startX;
            mCurrentY = startY;

            // If we actually can move, fling the scroller
            mScroller.fling(startX, startY, velocityX, velocityY, minX,
                    maxX, minY, maxY, 0, 0);
        }

        @Override
        public void run() {
            if (mScroller.isFinished()) {
                return; // remaining post that should not be handled
            }

            if (mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

                mCurrMatrix.postTranslate(mCurrentX - newX, mCurrentY - newY);
                invalidate();

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                Compat.postOnAnimation(FlexibleTextView.this, this);
            }
        }
    }
}
