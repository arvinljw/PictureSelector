package net.arvin.selector.uis.fragments;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import net.arvin.selector.uis.views.CropImageLayout;
import net.arvin.selector.uis.views.subscaleview.ImageSource;
import net.arvin.selector.uis.views.subscaleview.ImageViewState;
import net.arvin.selector.uis.views.subscaleview.SubsamplingScaleImageView;

import net.arvin.selector.R;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.uis.views.photoview.PhotoView;
import net.arvin.selector.utils.PSGlideUtil;
import net.arvin.selector.utils.PSUtil;

/**
 * Created by arvinljw on 17/12/27 15:32
 * Function：
 * Desc：
 */
public class ImageFragment extends Fragment {
    private View mRoot;
    private CropImageLayout mLayoutCrop;

    private FileEntity mEntity;

    public ImageFragment() {
    }

    @SuppressLint("ValidFragment")
    public ImageFragment(FileEntity entity) {
        this.mEntity = entity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(R.layout.ps_fragment_image, null);
        }
        init();
        return mRoot;
    }

    private void init() {
        mLayoutCrop = mRoot.findViewById(R.id.ps_layout_crop);
        mLayoutCrop.setCrop(false);
        mLayoutCrop.setImage(mEntity.getPath());
    }
}
