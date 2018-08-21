package net.arvin.selector.uis.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.listeners.TransactionListener;

import java.util.ArrayList;

/**
 * Created by arvinljw on 17/12/25 14:17
 * Function：
 * Desc：
 */
public abstract class BaseFragment extends Fragment {
    protected TransactionListener mTransactionListener;
    protected View mRoot;

    protected ImageView mImgBack;
    protected TextView mTvTitle;
    protected TextView mTvEnsure;

    protected Bundle baseInfo;
    protected int mSelectType;
    protected boolean mSingleSelection;
    protected int mMaxCount;
    protected boolean mCanCrop;
    protected boolean mWithCamera;
    protected String mAuthorities;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TransactionListener) {
            mTransactionListener = (TransactionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mTransactionListener != null) {
            mTransactionListener = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            mRoot = inflater.inflate(getLayout(), null);
        }
        initHeader();
        init(savedInstanceState);
        return mRoot;
    }

    protected void initHeader() {
        try {
            mImgBack = mRoot.findViewById(R.id.ps_img_back);
            mTvTitle = mRoot.findViewById(R.id.ps_tv_title);
            mTvEnsure = mRoot.findViewById(R.id.ps_tv_ensure);

            mImgBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackClicked();
                }
            });

            mTvEnsure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEnsureClicked();
                }
            });
        } catch (Exception e) {
            Log.d("HeaderLayout", "Do not have Header.");
        }
    }

    protected void onBackClicked() {
        if (mTransactionListener != null) {
            mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR, getArguments());
        }
    }

    protected void onEnsureClicked() {
        if (mTransactionListener != null) {
            Intent data = new Intent();
            data.putStringArrayListExtra(ConstantData.KEY_BACK_PICTURES, getSelectedPictures());
            data.putStringArrayListExtra(ConstantData.KEY_BACK_VIDEOS, getSelectedVideos());
            mTransactionListener.exchangeData(data);
        }
    }

    protected void initBaseInfo(Bundle bundle) {
        this.baseInfo = bundle;
        mSelectType = bundle.getInt(ConstantData.KEY_TYPE_SELECT, ConstantData.VALUE_TYPE_PICTURE);
        mSingleSelection = bundle.getBoolean(ConstantData.KEY_SINGLE_SELECTION, ConstantData.VALUE_SINGLE_SELECTION_TRUE);
        mMaxCount = bundle.getInt(ConstantData.KEY_MAX_COUNT, ConstantData.VALUE_COUNT_SINGLE);
        mCanCrop = bundle.getBoolean(ConstantData.KEY_CAN_CROP, ConstantData.VALUE_CAN_CROP_FALSE);
        mWithCamera = bundle.getBoolean(ConstantData.KEY_WITH_CAMERA, ConstantData.VALUE_WITH_CAMERA_TRUE);
        mAuthorities = bundle.getString(ConstantData.KEY_AUTHORITIES);
    }

    protected void setTitle(int currPos, int totalCount) {
        if (mTvTitle == null) {
            return;
        }
        mTvTitle.setText(getString(R.string.ps_title_pic_count, ++currPos, totalCount));
    }

    protected void setEnsure() {
        setEnsure(getSelectedPictures().size());
    }

    protected void setEnsure(int selectedCount) {
        if (mTvEnsure == null) {
            return;
        }
        if (mSingleSelection) {
            mTvEnsure.setText(R.string.ps_ensure_single);
            setEnsureEnable(true);
            return;
        }
        if (selectedCount == 0) {
            mTvEnsure.setText(R.string.ps_ensure);
            setEnsureEnable(false);
            return;
        }
        setEnsureEnable(true);
        mTvEnsure.setText(getString(R.string.ps_ensure_count, selectedCount, mMaxCount));
    }

    protected void setEnsureEnable(boolean enable) {
        if (mTvEnsure == null) {
            return;
        }
        mTvEnsure.setEnabled(enable);
    }

    public void update(Bundle bundle) {
    }

    protected abstract int getLayout();

    protected abstract void init(Bundle savedInstanceState);

    protected abstract ArrayList<String> getSelectedPictures();

    protected abstract ArrayList<String> getSelectedVideos();
}
