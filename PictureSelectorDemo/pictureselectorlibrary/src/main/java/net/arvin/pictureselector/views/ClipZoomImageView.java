package net.arvin.pictureselector.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ClipZoomImageView extends ImageView implements
		OnScaleGestureListener, OnTouchListener,
		ViewTreeObserver.OnGlobalLayoutListener {
	public float SCALE_MAX = 4.0f;
	private float SCALE_MID = 2.0f;

	private float initScale = 1.0f;
	private boolean once = true;

	private final float[] matrixValues = new float[9];

	/**
	 * 缩放的手势检查
	 */
	private ScaleGestureDetector mScaleGestureDetector = null;
	private final Matrix mScaleMatrix = new Matrix();

	private GestureDetector mGestureDetector;
	private boolean isAutoScale;

	private int mTouchSlop;

	private float mLastX;
	private float mLastY;

	private boolean isCanDrag;
	private int lastPointerCount;
	/**
	 * 水平方向与View的边距
	 */
	private int mHorizontalPadding;

	public ClipZoomImageView(Context context) {
		this(context, null);
	}

	public ClipZoomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setScaleType(ScaleType.MATRIX);
		mGestureDetector = new GestureDetector(context,
				new SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale == true)
							return true;

						float x = e.getX();
						float y = e.getY();
						if (getScale() < SCALE_MID) {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(SCALE_MID, x, y), 16);
							isAutoScale = true;
						} else {
							ClipZoomImageView.this.postDelayed(
									new AutoScaleRunnable(initScale, x, y), 16);
							isAutoScale = true;
						}

						return true;
					}
				});
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		this.setOnTouchListener(this);
	}

	private class AutoScaleRunnable implements Runnable {
		static final float BIGGER = 1.07f;
		static final float SMALLER = 0.93f;
		private float mTargetScale;
		private float tmpScale;

		private float x;
		private float y;

		/**
		 * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
		 * 
		 * @param targetScale
		 */
		public AutoScaleRunnable(float targetScale, float x, float y) {
			this.mTargetScale = targetScale;
			this.x = x;
			this.y = y;
			if (getScale() < mTargetScale) {
				tmpScale = BIGGER;
			} else {
				tmpScale = SMALLER;
			}

		}

		@Override
		public void run() {
			// 进行缩放
			mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorder();
			setImageMatrix(mScaleMatrix);

			final float currentScale = getScale();
			// 如果值在合法范围内，继续缩放
			if (((tmpScale > 1f) && (currentScale < mTargetScale))
					|| ((tmpScale < 1f) && (mTargetScale < currentScale))) {
				ClipZoomImageView.this.postDelayed(this, 16);
			} else
			// 设置为目标的缩放比例
			{
				final float deltaScale = mTargetScale / currentScale;
				mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
				checkBorder();
				setImageMatrix(mScaleMatrix);
				isAutoScale = false;
			}

		}
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null)
			return true;

		if ((scale < SCALE_MAX && scaleFactor > 1.0f)
				|| (scale > initScale && scaleFactor < 1.0f)) {
			if (scaleFactor * scale < initScale) {
				scaleFactor = initScale / scale;
			}
			if (scaleFactor * scale > SCALE_MAX) {
				scaleFactor = SCALE_MAX / scale;
			}
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());
			checkBorder();
			setImageMatrix(mScaleMatrix);
		}
		return true;
	}

	/**
	 * 根据当前图片的Matrix获得图片的范围
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rect = new RectF();
		Drawable d = getDrawable();
		if (null != d) {
			rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rect);
		}
		return rect;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mGestureDetector.onTouchEvent(event))
			return true;
		mScaleGestureDetector.onTouchEvent(event);

		float x = 0, y = 0;
		// 拿到触摸点的个数
		final int pointerCount = event.getPointerCount();
		// 得到多个触摸点的x与y
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		x = x / pointerCount;
		y = y / pointerCount;

		/**
		 * 每当触摸点发生变化时，重置mLasX , mLastY
		 */
		if (pointerCount != lastPointerCount) {
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}

		lastPointerCount = pointerCount;
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				isCanDrag = isCanDrag(dx, dy);
			}
			if (isCanDrag) {
				if (getDrawable() != null) {

					RectF rectF = getMatrixRectF();
					// 如果宽度小于屏幕宽度，则禁止左右移动
					if (rectF.width() <= getWidth() - mHorizontalPadding * 2) {
						dx = 0;
					}

					// 如果高度小雨屏幕高度，则禁止上下移动
					if (rectF.height() <= getHeight() - getHVerticalPadding()
							* 2) {
						dy = 0;
					}
					mScaleMatrix.postTranslate(dx, dy);
					checkBorder();
					setImageMatrix(mScaleMatrix);
				}
			}
			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			lastPointerCount = 0;
			break;
		}

		return true;
	}

	/**
	 * 获得当前的缩放比例
	 * 
	 * @return
	 */
	public final float getScale() {
		mScaleMatrix.getValues(matrixValues);
		return matrixValues[Matrix.MSCALE_X];
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		if (once) {
			Drawable d = getDrawable();
			if (d == null)
				return;
			// getHVerticalPadding() = (getHeight() - (getWidth() - 2 *
			// mHorizontalPadding)) / 2;

			int width = getWidth();
			int height = getHeight();
			// 拿到图片的宽和高
			int drawableW = d.getIntrinsicWidth();
			int drawableH = d.getIntrinsicHeight();
			float scale = 1.0f;

			int frameSize = getWidth() - mHorizontalPadding * 2;

			// 大图
			if (drawableW > frameSize && drawableH < frameSize) {
				scale = 1.0f * frameSize / drawableH;
			} else if (drawableH > frameSize && drawableW < frameSize) {
				scale = 1.0f * frameSize / drawableW;
			} else if (drawableW > frameSize && drawableH > frameSize) {
				float scaleW = frameSize * 1.0f / drawableW;
				float scaleH = frameSize * 1.0f / drawableH;
				scale = Math.max(scaleW, scaleH);
			}

			// 太小的图片放大处理
			if (drawableW < frameSize && drawableH > frameSize) {
				scale = 1.0f * frameSize / drawableW;
			} else if (drawableH < frameSize && drawableW > frameSize) {
				scale = 1.0f * frameSize / drawableH;
			} else if (drawableW < frameSize && drawableH < frameSize) {
				float scaleW = 1.0f * frameSize / drawableW;
				float scaleH = 1.0f * frameSize / drawableH;
				scale = Math.max(scaleW, scaleH);
			}

			initScale = scale;
			SCALE_MID = initScale * 2;
			SCALE_MAX = initScale * 4;
			mScaleMatrix.postTranslate((width - drawableW) / 2,
					(height - drawableH) / 2);
			mScaleMatrix.postScale(scale, scale, getWidth() / 2,
					getHeight() / 2);

			// 图片移动至屏幕中央
			setImageMatrix(mScaleMatrix);
			once = false;
		}
	}

	/**
	 * 剪切图片，返回剪切后的bitmap对象
	 * 
	 * @return
	 */
	public Bitmap clip() {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		draw(canvas);
		return Bitmap.createBitmap(bitmap, mHorizontalPadding,
				getHVerticalPadding(), getWidth() - 2 * mHorizontalPadding,
				getWidth() - 2 * mHorizontalPadding);
	}

	private void checkBorder() {
		RectF rect = getMatrixRectF();
		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		// 如果宽或高大于屏幕，则控制范�?; 这里�?.001是因为精度丢失会产生问题，但是误差一般很小，�?��我们直接加了�?��0.01
		if (rect.width() + 0.01 >= width - 2 * mHorizontalPadding) {
			if (rect.left > mHorizontalPadding) {
				deltaX = -rect.left + mHorizontalPadding;
			}

			if (rect.right < width - mHorizontalPadding) {
				deltaX = width - mHorizontalPadding - rect.right;
			}
		}

		if (rect.height() + 0.01 >= height - 2 * getHVerticalPadding()) {
			if (rect.top > getHVerticalPadding()) {
				deltaY = -rect.top + getHVerticalPadding();
			}

			if (rect.bottom < height - getHVerticalPadding()) {
				deltaY = height - getHVerticalPadding() - rect.bottom;
			}
		}

		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	private boolean isCanDrag(float dx, float dy) {
		return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
	}

	public void setHorizontalPadding(int mHorizontalPadding) {
		this.mHorizontalPadding = mHorizontalPadding;
	}

	private int getHVerticalPadding() {
		return (getHeight() - (getWidth() - 2 * mHorizontalPadding)) / 2;
	}
}
