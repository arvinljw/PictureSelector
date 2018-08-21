package net.arvin.selector.uis.fragments;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.data.FileData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.entities.FolderEntity;
import net.arvin.selector.listeners.OnItemClickListener;
import net.arvin.selector.listeners.OnItemSelectListener;
import net.arvin.selector.uis.adapters.SelectorAdapter;
import net.arvin.selector.uis.views.DividerGridItemDecoration;
import net.arvin.selector.uis.views.FolderDialog;
import net.arvin.selector.utils.PSUtil;

import java.util.ArrayList;

/**
 * Created by arvinljw on 17/12/25 14:17
 * Function：
 * Desc：
 */
public class SelectorFragment extends BaseFragment implements OnItemClickListener, OnItemSelectListener, FolderDialog.OnFolderSelectedListener {
    private TextView mTvFolderName;
    private TextView mTvReview;

    private SelectorAdapter mAdapter;

    private ArrayList<FolderEntity> mData;
    private ArrayList<FileEntity> mItems;
    private FolderDialog mFolderDialog;

    @Override
    protected int getLayout() {
        return R.layout.ps_fragment_selector;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mItems = new ArrayList<>();
        RecyclerView recyclerView = mRoot.findViewById(R.id.ps_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), ConstantData.VALUE_SPAN_COUNT));
        recyclerView.addItemDecoration(new DividerGridItemDecoration(getActivity()));
        mAdapter = new SelectorAdapter(getActivity(), mItems);
        mAdapter.setItemClickListener(this);
        mAdapter.setItemSelectListener(this);
        recyclerView.setAdapter(mAdapter);

        mTvFolderName = mRoot.findViewById(R.id.ps_tv_folder_name);
        mTvReview = mRoot.findViewById(R.id.ps_tv_review);
        mTvFolderName.setOnClickListener(getChangeFolderListener());
        mTvReview.setOnClickListener(getReviewListener());

        Bundle bundle = getArguments();
        if (bundle != null) {
            initSelectorInfo(bundle);
            ConstantData.setSelectedItems(bundle.getStringArrayList(ConstantData.KEY_SELECTED_PICTURES));
        }
        loadData();
    }

    private void loadData() {
        FileData.getImageFolderData(getActivity(), new FileData.DataCallback() {
            @Override
            public void dataCallback(ArrayList<FolderEntity> data) {
                mData = data;
                if (mData.size() > 0) {
                    ConstantData.setFolders(mData);

                    mItems.clear();
                    FolderEntity folderEntity = mData.get(0);
                    folderEntity.setSelected(true);
                    mItems.addAll(folderEntity.getImages());
                    mAdapter.notifyDataSetChanged();
                    mTvFolderName.setText(folderEntity.getFolderName());

                    int selectedCount = ConstantData.getSelectedItems().size();
                    setEnsure(selectedCount);
                    setReviewCount(selectedCount);
                    mAdapter.setSelectedCount(selectedCount);
                }
            }
        });
    }

    private void initSelectorInfo(Bundle bundle) {
        initBaseInfo(bundle);

        mAdapter.setWithCamera(mWithCamera);
        mAdapter.setMaxCount(mSingleSelection ? ConstantData.VALUE_COUNT_SINGLE : mMaxCount);
        mAdapter.notifyDataSetChanged();

        setEnsureEnable(mSingleSelection);
        if (mSingleSelection) {
            mTvEnsure.setVisibility(View.GONE);
        }
        setReviewCount(0);
    }

    @Override
    public void update(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        int fromPos = bundle.getInt(ConstantData.KEY_FROM_POS, ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR);
        if (fromPos == ConstantData.VALUE_CHANGE_FRAGMENT_TAKE_PHOTO) {
            mItems.add(0, new FileEntity(bundle.getString(ConstantData.KEY_NEW_PIC)));
            mAdapter.notifyDataSetChanged();
        } else if (fromPos == ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW) {
            int selectedCount = getSelectedPictures().size();
            setEnsure(selectedCount);
            setReviewCount(selectedCount);
            mAdapter.setSelectedCount(selectedCount);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected ArrayList<String> getSelectedPictures() {
        return ConstantData.getSelectedItems();
    }

    @Override
    protected ArrayList<String> getSelectedVideos() {
        return PSUtil.getSelectedVideos(mData);
    }

    @Override
    public void onItemClick(View v, int position) {
        if (position == -1) {
            if (mWithCamera) {
                if (mTransactionListener != null) {
                    mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_TAKE_PHOTO,
                            ConstantData.toTakePhotoBundle(getArguments()));
                }
            }
            return;
        }
        if (mTransactionListener != null) {
            if (mSingleSelection) {
                mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_CROP,
                        ConstantData.toCropBundle(getArguments(), mItems.get(position)));
            } else {//预览
                mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW,
                        ConstantData.toReviewBundle(getArguments(), mItems, position));
            }
        }
    }

    @Override
    public void onItemSelected(View v, int selectedCount) {
        setEnsure(selectedCount);
        setReviewCount(selectedCount);
    }

    private void setReviewCount(int selectedCount) {
        if (mTvReview == null) {
            return;
        }
        if (mSingleSelection) {
            mTvReview.setVisibility(View.GONE);
            return;
        }
        if (selectedCount == 0) {
            mTvReview.setText(R.string.ps_review);
            mTvReview.setEnabled(false);
            return;
        }
        mTvReview.setEnabled(true);
        mTvReview.setText(getString(R.string.ps_review_count, selectedCount));
    }

    private View.OnClickListener getChangeFolderListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFolderDialog == null) {
                    mFolderDialog = new FolderDialog(getActivity(), mData);
                    mFolderDialog.setOnFolderSelectedListener(SelectorFragment.this);
                }
                mFolderDialog.show();
            }
        };
    }

    private View.OnClickListener getReviewListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTransactionListener != null) {
                    mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_REVIEW,
                            ConstantData.toReviewBundle(getArguments(), mItems));
                }
            }
        };
    }

    @Override
    protected void onBackClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onFolderSelected(View v, int position) {
        FolderEntity folderEntity = mData.get(position);
        if (folderEntity != null) {
            mItems.clear();
            mItems.addAll(folderEntity.getImages());
            mAdapter.notifyDataSetChanged();
            mTvFolderName.setText(folderEntity.getFolderName());
        }
    }

}
