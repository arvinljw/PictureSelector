package net.arvin.pictureselector.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.ImageViewState;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import net.arvin.pictureselector.utils.PSGlideUtil;

public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView mZoomImageView;
    private SubsamplingScaleImageView mZoomBigImageView;

    private ClipImageBorderView mClipImageView;

    private int mHorizontalPadding = 0;// 框左右的边距，这里左右边距为0，为手机屏幕宽度的正方形

    private boolean isBigImage = false;

    public ClipImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        mZoomImageView = new ClipZoomImageView(context);
        mZoomBigImageView = new SubsamplingScaleImageView(context);
        mZoomBigImageView.setCrop(true);
        mClipImageView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(mZoomImageView, lp);
        this.addView(mZoomBigImageView, lp);
        mZoomBigImageView.setVisibility(View.GONE);
        this.addView(mClipImageView, lp);

        // 计算padding的px
        mHorizontalPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
                        .getDisplayMetrics());
//        mZoomImageView.setHorizontalPadding(mHorizontalPadding);
        mClipImageView.setHorizontalPadding(mHorizontalPadding);
    }

    public void setImageBitmap(Bitmap bitmap) {
        isBigImage = bitmap.getWidth() * 3 <= bitmap.getHeight();
        if (isBigImage) {
            mZoomImageView.setVisibility(View.GONE);
            mZoomBigImageView.setVisibility(View.VISIBLE);
            mZoomBigImageView.setImage(ImageSource.bitmap(bitmap), new ImageViewState(0.0f, new PointF(0, 0), 0));
            mZoomBigImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
            mZoomBigImageView.setZoomEnabled(true);
            return;
        }
        mZoomImageView.setVisibility(View.VISIBLE);
        mZoomBigImageView.setVisibility(View.GONE);
        mZoomImageView.setImageBitmap(bitmap);
    }

    /**
     * 对外公布设置边距的方框单位为dp
     */
    public void setHorizontalPadding(int mHorizontalPadding) {
        this.mHorizontalPadding = mHorizontalPadding;
    }

    /**
     * 裁切图片
     */
    public Bitmap clip() {
        if (isBigImage) {
            return mZoomBigImageView.clip();
        }
        return mZoomImageView.clip();
    }
}
