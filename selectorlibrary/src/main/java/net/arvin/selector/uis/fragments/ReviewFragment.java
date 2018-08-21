package net.arvin.selector.uis.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.uis.adapters.ReviewAdapter;
import net.arvin.selector.utils.PSToastUtil;
import net.arvin.selector.utils.PSUtil;

import java.util.ArrayList;

/**
 * Created by arvinljw on 17/12/25 14:24
 * Function：
 * Desc：
 */
public class ReviewFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    private TextView mTvEdit;
    private View mLayoutSelector;
    private ImageView mImgSelector;

    private ReviewAdapter mAdapter;
    private ArrayList<FileEntity> mItems;
    private int mCurrPosition;
    private int mSelectedCount;

    @Override
    protected int getLayout() {
        return R.layout.ps_fragment_review;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mViewPager = mRoot.findViewById(R.id.ps_view_pager);
        mTvEdit = mRoot.findViewById(R.id.ps_tv_edit);
        mLayoutSelector = mRoot.findViewById(R.id.ps_layout_selector);
        mImgSelector = mRoot.findViewById(R.id.ps_img_selector);

        mTvEdit.setOnClickListener(getEditClickedListener());
        mLayoutSelector.setOnClickListener(getSelectListener());

        update(getArguments());
    }

    private View.OnClickListener getEditClickedListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTransactionListener != null) {
                    mTransactionListener.showFragment(ConstantData.VALUE_CHANGE_FRAGMENT_EDIT,
                            ConstantData.toEditBundle(getArguments(), mItems.get(mCurrPosition)));
                }
            }
        };
    }

    private View.OnClickListener getSelectListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileEntity entity = mItems.get(mCurrPosition);
                if (!entity.isSelected()) {
                    if (mSelectedCount >= mMaxCount) {
                        PSToastUtil.showToast(R.string.ps_cant_add);
                        return;
                    }
                    ConstantData.addSelectedItem(entity);
                    mSelectedCount++;
                } else {
                    ConstantData.removeItem(entity);
                    mSelectedCount--;
                }
                entity.setSelected(!entity.isSelected());
                mImgSelector.setSelected(entity.isSelected());
                setEnsure();
            }
        };
    }

    @Override
    public void update(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        initBaseInfo(bundle);

        boolean mOnlyShowSelectedPic = bundle.getBoolean(ConstantData.KEY_ONLY_SHOW_SELECTED_PIC, ConstantData.VALUE_ONLY_SHOW_SELECTED_PIC_DEFAULT);
        mCurrPosition = bundle.getInt(ConstantData.KEY_CURR_POSITION, 0);

        if (mOnlyShowSelectedPic) {
            mItems = PSUtil.getSelectedPictureFiles(ConstantData.getFolders());
        } else {
            mItems = ConstantData.getCurrItems();
        }

        if (mItems == null) {
            mItems = new ArrayList<>();
        }

        mAdapter = new ReviewAdapter(getChildFragmentManager(), mItems);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrPosition);
        mViewPager.addOnPageChangeListener(this);

        setTitle(mCurrPosition, mItems.size());
        mSelectedCount = getSelectedPictures().size();
        setEnsure(mSelectedCount);
        mImgSelector.setSelected(mItems.get(mCurrPosition).isSelected());
        if (mSingleSelection) {
            mLayoutSelector.setVisibility(View.GONE);
        }
    }

    @Override
    protected ArrayList<String> getSelectedPictures() {
        return ConstantData.getSelectedItems();
    }

    @Override
    protected ArrayList<String> getSelectedVideos() {
        return PSUtil.getSelectedVideos(ConstantData.getFolders());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrPosition = position;
        setTitle(position, mItems.size());
        mImgSelector.setSelected(mItems.get(mCurrPosition).isSelected());
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    protected void onBackClicked() {
        if (mTransactionListener != null) {
            mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR,
                    ConstantData.toSelectorBundle(getArguments(), null, ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW));
        }
    }
}
