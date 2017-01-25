package net.arvin.pictureselector.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.FinishEntity;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.uis.fragments.CropFragment;
import net.arvin.pictureselector.utils.PSConstanceUtil;
import net.arvin.pictureselector.utils.PSTakePhotoUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * created by arvin on 17/1/5 16:23
 * email：1035407623@qq.com
 */
public class TakePhotoAndCropActivity extends AppCompatActivity implements PSTakePhotoUtil.OnTakePhotoSuccessListener {
    private PSTakePhotoUtil takePhotoUtil;
    private CropFragment mCropFragment;
    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_activity_take_photo_crop);
        init();
    }

    private void init() {
        takePhotoUtil = new PSTakePhotoUtil(this, this);
        EventBus.getDefault().register(this);
        mFragmentManager = getSupportFragmentManager();

        takePhotoUtil.choosePhotoFromCamera();
    }

    /**
     * @param selectedImage 返回已选择的图片
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArrayList<ImageEntity> selectedImage) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED, selectedImage);
        setResult(RESULT_OK, data);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FinishEntity entity) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            takePhotoUtil.onActivityResult(requestCode, resultCode, data, this);
            return;
        }
        onBackPressed();
    }

    @Override
    public void onTakePhotoSuccess(String path) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(PSConstanceUtil.PASS_SHOW, new ImageEntity(path, true));
        bundle.putBoolean(PSConstanceUtil.PASS_EXTRA, true);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (mCropFragment != null) {
            transaction.remove(mCropFragment);
        }
        mCropFragment = new CropFragment();
        mCropFragment.setArguments(bundle);
        transaction.add(R.id.ps_content, mCropFragment).commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
