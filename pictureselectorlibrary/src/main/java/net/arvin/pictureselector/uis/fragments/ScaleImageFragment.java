package net.arvin.pictureselector.uis.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.utils.PSGlideUtil;

import org.greenrobot.eventbus.EventBus;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * created by arvin on 16/9/3 17:16
 * email：1035407623@qq.com
 */
public class ScaleImageFragment extends Fragment implements PhotoViewAttacher.OnPhotoTapListener{
    private View mRoot;
    private ImageView imgScale;
    private PhotoViewAttacher mAttacher;
    private ImageEntity mItem;

    public ScaleImageFragment() {
    }

    @SuppressLint("ValidFragment")
    public ScaleImageFragment(ImageEntity item) {
        this.mItem = item;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRoot = LayoutInflater.from(getActivity()).inflate(R.layout.ps_fragment_scale_image, null);
        init(savedInstanceState);
        return mRoot;
    }

    private void init(Bundle savedInstanceState) {
        imgScale = (ImageView) mRoot.findViewById(R.id.img_scale);
        PSGlideUtil.loadImage(getActivity(), "file://" + mItem.getPath(), imgScale, new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                mAttacher = new PhotoViewAttacher(imgScale);
                mAttacher.setOnPhotoTapListener(ScaleImageFragment.this);
                return false;
            }
        });
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        EventBus.getDefault().post(new OnImageClicked());
    }

    @Override
    public void onOutsidePhotoTap() {
    }

    public class OnImageClicked{}
}
