package net.arvin.pictureselector.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.entities.PageChangeEntity;
import net.arvin.pictureselector.uis.fragments.BaseFragment;
import net.arvin.pictureselector.uis.fragments.CropFragment;
import net.arvin.pictureselector.uis.fragments.PictureSelectorFragment;
import net.arvin.pictureselector.uis.fragments.ReviewFragment;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSConstanceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import static net.arvin.pictureselector.entities.PageChangeEntity.PageId.*;

/**
 * created by arvin on 16/8/27 23:44
 * email：1035407623@qq.com
 */
public class PictureSelectorActivity extends AppCompatActivity {

    private PictureSelectorFragment mPictureSelectorFragment;
    private ReviewFragment mReviewFragment;
    private CropFragment mCropFragment;
    private FragmentManager mFragmentManager;

    private BaseFragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_activity_picture_selector);
        init();

        showFragmentById(PictureSelector, getIntent().getExtras());
    }

    private void init() {
        EventBus.getDefault().register(this);
        mFragmentManager = getSupportFragmentManager();
    }

    /**
     * @param pageChange 控制fragment的显示和隐藏
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PageChangeEntity pageChange) {
        showFragmentById(pageChange.getPage(), pageChange.getData());
    }

    /**
     * @param selectedImage 返回已选择的图片
     */
    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArrayList<ImageEntity> selectedImage) {
        Intent data = new Intent();
        data.putParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED, selectedImage);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * @param pageId 显示Fragment
     * @param bundle 数据s
     */
    public void showFragmentById(PageChangeEntity.PageId pageId, Bundle bundle) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (pageId) {
            case PictureSelector:
                mCurrentFragment = showPictureSelector(bundle, transaction);
                break;
            case Review:
                mCurrentFragment = showReview(bundle, transaction);
                break;
            case Crop:
                mCurrentFragment = showCrop(bundle, transaction);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mPictureSelectorFragment != null) {
            transaction.hide(mPictureSelectorFragment);
        }
        if (mReviewFragment != null) {
            transaction.hide(mReviewFragment);
        }
        if (mCropFragment != null) {
            transaction.hide(mCropFragment);
        }
    }

    private BaseFragment showPictureSelector(Bundle bundle, FragmentTransaction transaction) {
        if (mPictureSelectorFragment == null) {
            mPictureSelectorFragment = new PictureSelectorFragment();
            mPictureSelectorFragment.setArguments(bundle);
            transaction.add(R.id.ps_content, mPictureSelectorFragment);
            return mPictureSelectorFragment;
        }
        mPictureSelectorFragment.update(bundle);
        transaction.show(mPictureSelectorFragment);
        return mPictureSelectorFragment;
    }

    private BaseFragment showReview(Bundle bundle, FragmentTransaction transaction) {
        if (mReviewFragment == null) {
            mReviewFragment = new ReviewFragment();
            mReviewFragment.setArguments(bundle);
            transaction.add(R.id.ps_content, mReviewFragment);
            return mReviewFragment;
        }
        mReviewFragment.update(bundle);
        transaction.show(mReviewFragment);
        return mReviewFragment;
    }

    private BaseFragment showCrop(Bundle bundle, FragmentTransaction transaction) {
        if (mCropFragment != null) {
            transaction.remove(mCropFragment);
        }
        mCropFragment = new CropFragment();
        mCropFragment.setArguments(bundle);
        transaction.add(R.id.ps_content, mCropFragment);
        return mCropFragment;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        mCurrentFragment.onBackClicked();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
