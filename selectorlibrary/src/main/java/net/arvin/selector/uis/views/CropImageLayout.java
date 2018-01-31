package net.arvin.selector.uis.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import net.arvin.selector.R;
import net.arvin.selector.uis.views.photoview.PhotoView;
import net.arvin.selector.uis.views.subscaleview.ImageSource;
import net.arvin.selector.uis.views.subscaleview.ImageViewState;
import net.arvin.selector.uis.views.subscaleview.SubsamplingScaleImageView;

/**
 * Created by arvinljw on 18/1/5 18:22
 * Function：
 * Desc：
 */
public class CropImageLayout extends FrameLayout {
    private PhotoView mImgReview;
    private SubsamplingScaleImageView mImgReviewBig;
    private CropRectView mClipRectView;
    private TextView mTvLoading;

    private int mSpacing;
    private boolean mCanCrop = true;

    public CropImageLayout(@NonNull Context context) {
        this(context, null);
    }

    public CropImageLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.ps_layout_image, this, true);
        mImgReview = view.findViewById(R.id.ps_img_review);
        mImgReviewBig = view.findViewById(R.id.ps_img_review_big);

        mClipRectView = new CropRectView(getContext());
        addView(mClipRectView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        addLoadingText();

        mSpacing = mClipRectView.getHorizontalSpacing();
        mImgReview.setPadding(mSpacing, mSpacing, mSpacing, mSpacing);
        mImgReviewBig.setPadding(mSpacing, mSpacing, mSpacing, mSpacing);
    }

    private void addLoadingText() {
        mTvLoading = new TextView(getContext());
        mTvLoading.setText(R.string.ps_loading);
        mTvLoading.setTextSize(15);
        mTvLoading.setTextColor(getResources().getColor(R.color.ps_white_secondary));
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        addView(mTvLoading, params);
    }

    public void setImage(final String path) {
        mImgReview.setVisibility(View.GONE);
        mImgReviewBig.setVisibility(View.GONE);
        mTvLoading.setVisibility(View.VISIBLE);
        mImgReviewBig.setCheckBounds(true);
        mImgReview.getAttacher().setCheckBounds(!mCanCrop);
        Glide.with(getContext())
                .load(path)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        boolean isBigImage = resource.getIntrinsicWidth() <= resource.getIntrinsicHeight() / 3 ||
                                resource.getIntrinsicWidth() >= resource.getIntrinsicHeight() * 2;
                        if (isBigImage) {
                            mImgReviewBig.setVisibility(View.VISIBLE);
                            mImgReviewBig.setImage(ImageSource.uri(path), new ImageViewState(0.0f, new PointF(0, 0), 0));
                            mImgReviewBig.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
                            mImgReviewBig.setOnImageEventListener(new SubsamplingScaleImageView.DefaultOnImageEventListener() {
                                @Override
                                public void onReady() {
                                    mImgReviewBig.setCheckBounds(!mCanCrop);
                                    mTvLoading.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            mTvLoading.setVisibility(View.GONE);
                            mImgReview.setVisibility(View.VISIBLE);
                            Glide.with(getContext())
                                    .load(path)
                                    .into(mImgReview);
                        }
                    }
                });
    }

    public Bitmap cropBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        if (mImgReview.getVisibility() == View.VISIBLE) {
            mImgReview.draw(canvas);
        } else if (mImgReviewBig.getVisibility() == View.VISIBLE) {
            mImgReviewBig.draw(canvas);
        } else {
            return null;
        }
        int centerSize = mClipRectView.getCenterSize();
        return Bitmap.createBitmap(bitmap, mSpacing, (mClipRectView.getHeight() - centerSize) / 2, centerSize, centerSize);
    }

    public void setCrop(boolean canCrop) {
        this.mCanCrop = canCrop;
        mImgReview.getAttacher().setCheckBounds(!mCanCrop);
        mImgReviewBig.setCheckBounds(!mCanCrop);
        mClipRectView.setVisibility(canCrop ? View.VISIBLE : View.GONE);
        if (!mCanCrop) {
            mImgReview.setPadding(0, 0, 0, 0);
            mImgReviewBig.setPadding(0, 0, 0, 0);
        }
    }
}
