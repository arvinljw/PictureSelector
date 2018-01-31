package net.arvin.selector.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;

import net.arvin.selector.data.ConstantData;
import net.arvin.selector.R;
import net.arvin.selector.listeners.TransactionListener;
import net.arvin.selector.uis.fragments.BaseFragment;
import net.arvin.selector.uis.fragments.CropFragment;
import net.arvin.selector.uis.fragments.EditFragment;
import net.arvin.selector.uis.fragments.ReviewFragment;
import net.arvin.selector.uis.fragments.SelectorFragment;
import net.arvin.selector.uis.fragments.TakePhotoFragment;
import net.arvin.selector.utils.PSToastUtil;

/**
 * Created by arvinljw on 17/12/25 09:37
 * Function：选择图片或者视频界面入口
 * Desc：
 */
public class SelectorActivity extends AppCompatActivity implements TransactionListener {
    private final String TAG = SelectorActivity.class.getName();

    private FragmentManager mFragmentManager;

    private SparseArray<Class> mFragmentClasses;
    private SparseArray<BaseFragment> mFragments;
    private int mCurrPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_activity_selector);
        init();
    }

    private void init() {
        PSToastUtil.init(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            Log.w(TAG, "Please start this activity by PSSelectorHelper.");
            onBackPressed();
            return;
        }

        int type = bundle.getInt(ConstantData.KEY_TYPE_SELECT, ConstantData.VALUE_TYPE_PICTURE);
        if (type != ConstantData.VALUE_TYPE_PICTURE && type != ConstantData.VALUE_TYPE_CAMERA) {
            PSToastUtil.showToast(R.string.ps_not_support);
            onBackPressed();
            return;
        }

        initFragmentInfo();

        if (type == ConstantData.VALUE_TYPE_CAMERA) {
            switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_TAKE_PHOTO, bundle);
        } else {
            switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR, bundle);
        }
    }

    private void initFragmentInfo() {
        mFragmentManager = getSupportFragmentManager();

        mFragmentClasses = new SparseArray<>();
        mFragmentClasses.put(ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR, SelectorFragment.class);
        mFragmentClasses.put(ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW, ReviewFragment.class);
        mFragmentClasses.put(ConstantData.VALUE_CHANGE_FRAGMENT_CROP, CropFragment.class);
        mFragmentClasses.put(ConstantData.VALUE_CHANGE_FRAGMENT_TAKE_PHOTO, TakePhotoFragment.class);
        mFragmentClasses.put(ConstantData.VALUE_CHANGE_FRAGMENT_EDIT, EditFragment.class);

        mFragments = new SparseArray<>();
    }

    @Override
    public void exchangeData(Intent data) {
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    @Override
    public void switchFragment(int fragmentPos, Bundle bundle) {
        hideFragment(mCurrPos, fragmentPos);
        showFragment(fragmentPos, bundle);
    }

    @Override
    public void showFragment(int fragmentPos, Bundle bundle) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        mCurrPos = fragmentPos;

        BaseFragment toFragment = mFragments.get(fragmentPos);
        if (toFragment == null) {
            try {
                BaseFragment fragment = (BaseFragment) mFragmentClasses.get(fragmentPos).newInstance();
                transaction.add(R.id.ps_content, fragment);
                fragment.setArguments(bundle);
                mFragments.put(fragmentPos, fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toFragment.update(bundle);
            if (!(toFragment instanceof SelectorFragment)) {
                transaction.show(toFragment);
            }
        }

        transaction.commitAllowingStateLoss();
    }

    @Override
    public void hideFragment(int hidePos, int currPos) {
        if (mCurrPos != -1 && mCurrPos != ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR) {
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            BaseFragment fragment = mFragments.get(mCurrPos);
            if (fragment != null) {
                transaction.hide(fragment);
            }
            transaction.commitAllowingStateLoss();
        }
        mCurrPos = currPos;
    }

    @Override
    protected void onDestroy() {
        mFragments = null;
        mFragmentClasses = null;
        PSToastUtil.onDestroy();
        super.onDestroy();
    }
}
