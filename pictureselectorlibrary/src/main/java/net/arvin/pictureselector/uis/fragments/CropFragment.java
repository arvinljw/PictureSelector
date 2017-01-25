package net.arvin.pictureselector.uis.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.FinishEntity;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.entities.PageChangeEntity;
import net.arvin.pictureselector.utils.PSConstanceUtil;
import net.arvin.pictureselector.utils.PSCropUtil;
import net.arvin.pictureselector.views.ClipImageLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.Subscriber;
import rx.functions.Action1;

/**
 * created by arvin on 16/8/28 00:02
 * email：1035407623@qq.com
 */
public class CropFragment extends BaseFragment {
    private ClipImageLayout imgClip;
    private boolean isFromTakePhotoCrop = false;

    @Override
    protected int setContentViewId() {
        return R.layout.ps_fragment_crop;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        imgClip = getView(R.id.img_clip);
        tvTitle.setText("裁剪");
        tvEnsure.setEnabled(true);
        mSelectedImages = new ArrayList<>();

        update(getArguments());
    }

    @Override
    public void update(Bundle data) {
        isFromTakePhotoCrop = data.getBoolean(PSConstanceUtil.PASS_EXTRA, false);

        ImageEntity item = data.getParcelable(PSConstanceUtil.PASS_SHOW);
        if (item == null) {
            onBackClicked();
            return;
        }
        mSelectedImages.clear();
        mSelectedImages.add(item);

        final long time = System.currentTimeMillis();
        Glide.with(this).load(item.getPath()).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                Log.i("加载耗时", (System.currentTimeMillis() - time) + "");
                if (resource != null) {
                    imgClip.setImageBitmap(resource);
                    Log.i("显示", (System.currentTimeMillis() - time) + "");
                } else {
                    Toast.makeText(getActivity(), "没有找到该图片~", Toast.LENGTH_SHORT).show();
                    onBackClicked();
                }
            }
        });
    }

    @Override
    protected void onEnsureClicked() {
        PSCropUtil.crop(imgClip).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                mSelectedImages.clear();
                mSelectedImages.add(new ImageEntity(s, true));
                EventBus.getDefault().post(mSelectedImages);
            }
        });
    }

    @Override
    public void onBackClicked() {
        if (isFromTakePhotoCrop) {
            EventBus.getDefault().post(new FinishEntity());
            return;
        }
        EventBus.getDefault().post(new PageChangeEntity(PageChangeEntity.PageId.PictureSelector, null));
    }
}
