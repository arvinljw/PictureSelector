package net.arvin.pictureselector.uis.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.entities.PageChangeEntity;
import net.arvin.pictureselector.uis.adapters.ReviewAdapter;
import net.arvin.pictureselector.utils.PSAnimUtil;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSConstanceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * created by arvin on 16/8/28 00:02
 * email：1035407623@qq.com
 */
public class ReviewFragment extends BaseFragment implements ViewPager.OnPageChangeListener {
    private ArrayList<ImageEntity> mItems;
    private int currentPos;

    private ViewPager vpImages;
    private ImageView imgSelector;
    private boolean isSelected;
    private boolean isShow = true;

    @Override
    protected int setContentViewId() {
        return R.layout.ps_fragment_review;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        LinearLayout layoutSelector = getView(R.id.layout_selector);
        imgSelector = getView(R.id.img_selector);
        layoutSelector.setOnClickListener(this);

        initPager();
    }

    private void initPager() {
        mItems = new ArrayList<>();
        mSelectedImages = new ArrayList<>();
        vpImages = getView(R.id.vp_images);

        update(getArguments());
    }

    @SuppressWarnings("deprecation")
    @Override
    public void update(Bundle data) {
        //因为每次都会调用这个方法,所以在这里注册,在返回时反注册
        EventBus.getDefault().register(this);
        mItems.clear();
        mSelectedImages.clear();

        List<ImageEntity> showData = data.getParcelableArrayList(PSConstanceUtil.PASS_SHOW);
        List<ImageEntity> selectedData = data.getParcelableArrayList(PSConstanceUtil.PASS_SELECTED);
        int currentPos = data.getInt(PSConstanceUtil.PASS_CURRENT_POS);

        mItems.addAll(showData);
        mSelectedImages.addAll(selectedData);
        this.currentPos = currentPos;

        ReviewAdapter mAdapter = new ReviewAdapter(getChildFragmentManager(), mItems);
        vpImages.setAdapter(mAdapter);
//        vpImages.setPageTransformer(false, new DepthPageTransformer());
        vpImages.setCurrentItem(currentPos);
        vpImages.setOnPageChangeListener(this);

        asyncShow(mSelectedImages.size());

        if (!isShow) {
            onEvent(null);
        }
    }

    /**
     * Page中的图片被点击
     */
    @SuppressWarnings("UnusedParameters")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ScaleImageFragment.OnImageClicked onImageClicked) {
        if (isShow) {
            PSAnimUtil.startDownHideAnim(getView(R.id.layout_bottom));
            PSAnimUtil.startUpHideAnim(getView(R.id.layout_header));
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isShow = false;
            return;
        }
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PSAnimUtil.startDownShowAnim(getView(R.id.layout_bottom));
        PSAnimUtil.startUpShowAnim(getView(R.id.layout_header));
        isShow = true;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.layout_selector) {
            onItemSelect(currentPos, !isSelected);
        }
    }

    /**
     * @param currentPos 操作的pos
     * @param isSelected 是否选中
     */
    private void onItemSelect(int currentPos, boolean isSelected) {
        ImageEntity item = mItems.get(currentPos);
        if (!PSConfigUtil.getInstance().canAdd() && !item.isSelected()) {
            Toast.makeText(getActivity(), getString(R.string.ps_max_count_tips, "" + PSConfigUtil.getInstance().getMaxCount()),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        item.setSelected(isSelected);

        int selectedCount;
        if (isSelected) {
            selectedCount = PSConfigUtil.getInstance().addSelectedCount(1);
            mSelectedImages.add(item);
        } else {
            selectedCount = PSConfigUtil.getInstance().addSelectedCount(-1);
            mSelectedImages.remove(item);
        }
        asyncShow(selectedCount);

    }

    private void asyncShow(int selectedCount) {
        asyncSelected();

        boolean enabled = selectedCount > 0;
        tvEnsure.setText(getEnsureText(selectedCount));
        tvEnsure.setEnabled(enabled);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        currentPos = position;
        asyncSelected();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    private void asyncSelected() {
        tvTitle.setText(getTitleText());

        isSelected = mItems.get(currentPos).isSelected();
        imgSelector.setSelected(isSelected);
    }

    private String getTitleText() {
        return (currentPos + 1) + "/" + mItems.size();
    }

    @Override
    public void onBackClicked() {
        EventBus.getDefault().unregister(this);
        Bundle data = new Bundle();
        //这是已选中的数据
        data.putParcelableArrayList(PSConstanceUtil.PASS_SELECTED, mSelectedImages);
        PageChangeEntity entity = new PageChangeEntity(PageChangeEntity.PageId.PictureSelector, data);
        EventBus.getDefault().post(entity);
    }
}
