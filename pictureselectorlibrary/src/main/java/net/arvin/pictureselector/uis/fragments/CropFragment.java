package net.arvin.pictureselector.uis.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.entities.PageChangeEntity;
import net.arvin.pictureselector.utils.PSConstanceUtil;
import net.arvin.pictureselector.utils.PSCropUtil;
import net.arvin.pictureselector.views.ClipImageLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import rx.functions.Action1;

/**
 * created by arvin on 16/8/28 00:02
 * email：1035407623@qq.com
 */
public class CropFragment extends BaseFragment {
    private ClipImageLayout imgClip;

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
        ImageEntity item = data.getParcelable(PSConstanceUtil.PASS_SHOW);
        if (item == null) {
            onBackClicked();
            return;
        }
        mSelectedImages.clear();
        mSelectedImages.add(item);
        Bitmap bitmap = PSCropUtil.createBitmap(item.getPath());
        if (bitmap != null) {
            imgClip.setImageBitmap(PSCropUtil.rotateBitmap(PSCropUtil.readBitmapDegree(item.getPath()), bitmap));
            if (bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } else {
            Toast.makeText(getActivity(), "没有找到该图片~", Toast.LENGTH_SHORT).show();
            onBackClicked();
        }
    }

    @Override
    protected void onEnsureClicked() {
        PSCropUtil.crop(imgClip).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                EventBus.getDefault().post(mSelectedImages);
            }
        });
    }

    @Override
    public void onBackClicked() {
        EventBus.getDefault().post(new PageChangeEntity(PageChangeEntity.PageId.PictureSelector, null));
    }
}
