package net.arvin.selector.uis.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.data.SelectorParams;
import net.arvin.selector.uis.SelectorActivity;
import net.arvin.selector.uis.adapters.OnAdapterItemClickListener;
import net.arvin.selector.uis.adapters.PreviewAdapter;
import net.arvin.selector.uis.adapters.ThumbnailAdapter;
import net.arvin.selector.utils.AnimUtil;
import net.arvin.selector.utils.PSUtil;
import net.arvin.selector.utils.UiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2020/7/22 16:19
 * Function：
 * Desc：预览界面
 */
public class PreviewFragment extends BaseFragment implements View.OnClickListener, ThumbnailAdapter.OnThumbnailClickListener, OnAdapterItemClickListener<Media> {
    public static final String KEY_PREVIEW_ALL = "preview_all";
    public static final String KEY_PREVIEW_POS = "preview_pos";

    private TextView tvTitle;
    private TextView tvEnsure;
    private TextView tvOriginalImage;
    private TextView tvChoose;

    private RecyclerView mediaList;
    private PreviewAdapter adapter;
    private boolean previewAll;
    private int currentPos;

    private RecyclerView choseList;
    private ThumbnailAdapter thumbnailAdapter;
    private View layoutHeader;
    private View layoutBottom;
    private boolean isShowBar;

    @Override
    protected int getLayoutId() {
        return R.layout.ps_fragment_preview;
    }

    @Override
    protected void init() {
        initView();

        initListener();

        initMediaList();

        initChoseList();

        initData();
    }

    public void update(Bundle args) {
        List<Media> items = adapter.getItems();
        items.clear();
        items.addAll(getItems(args));
        adapter.notifyDataSetChanged();

        List<Media> thumbnailAdapterItems = thumbnailAdapter.getItems();
        thumbnailAdapterItems.clear();
        thumbnailAdapterItems.addAll(getChoseItems());
        thumbnailAdapter.notifyDataSetChanged();
        showHideChoseList(thumbnailAdapterItems.size());

        initData();
    }

    private void initData() {
        dealOriginalImageState();
        dealEnsureText();
        scrollToMediaPosition(true);
        showBars(true);
    }

    private void dealOriginalImageState() {
        if (getActivity() == null) {
            return;
        }
        boolean selectOriginalImage = ((SelectorActivity) getActivity()).isSelectOriginalImage();
        tvOriginalImage.setSelected(selectOriginalImage);
        if (selectOriginalImage) {
            UiUtil.setDrawableLeft(tvOriginalImage, R.drawable.ps_ic_radio_selected);
        } else {
            UiUtil.setDrawableLeft(tvOriginalImage, R.drawable.ps_ic_radio_transparent);
        }
    }

    private void initView() {
        layoutHeader = findViewById(R.id.ps_layout_header);
        layoutBottom = findViewById(R.id.ps_layout_bottom);
        tvTitle = findViewById(R.id.ps_tv_title);
        tvEnsure = findViewById(R.id.ps_btn_ensure);
        tvOriginalImage = findViewById(R.id.ps_tv_original_image);
        tvChoose = findViewById(R.id.ps_tv_choose);

        mediaList = findViewById(R.id.ps_media_list);
        choseList = findViewById(R.id.ps_chose_list);
    }

    private void initListener() {
        findViewById(R.id.ps_img_back).setOnClickListener(this);
        tvEnsure.setOnClickListener(this);
        tvOriginalImage.setOnClickListener(this);
        tvChoose.setOnClickListener(this);
    }

    private void setTitle() {
        tvTitle.setText(SelectorHelper.textEngine.previewTitle(getActivity(), currentPos + 1, adapter.getItemCount()));
    }

    private void initMediaList() {
        mediaList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        AnimUtil.removeChangeAnim(mediaList);

        final PagerSnapHelper snapHelper = new PagerSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                if (targetPos >= adapter.getItemCount()) {
                    return targetPos;
                }
                currentPos = targetPos;
                scrollToMediaPosition(false);
                return targetPos;
            }
        };
        snapHelper.attachToRecyclerView(mediaList);
        List<Media> items = getItems(getArguments());
        adapter = new PreviewAdapter(getActivity(), items);
        adapter.setOnItemClickListener(this);
        mediaList.setAdapter(adapter);
    }

    private void scrollToMediaPosition(boolean isExecute) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mediaList.getLayoutManager();
        LinearLayoutManager choseLayoutManager = (LinearLayoutManager) choseList.getLayoutManager();
        if (layoutManager == null || choseLayoutManager == null) {
            return;
        }
        Media currItem = adapter.getItems().get(currentPos);
        if (thumbnailAdapter != null) {
            thumbnailAdapter.setCurrItem(currItem);
        }
        if (isExecute) {
            layoutManager.scrollToPosition(currentPos);
        }
        setTitle();
        if (isItemChose(currItem)) {
            if (!isExecute) {
                choseLayoutManager.smoothScrollToPosition(choseList, null, getChosePos(currItem));
            }
            UiUtil.setDrawableLeft(tvChoose, R.drawable.ps_ic_radio_selected);
        } else {
            UiUtil.setDrawableLeft(tvChoose, R.drawable.ps_ic_radio_transparent);
        }
        if (MediaType.isVideo(currItem.getMimeType())) {
            tvOriginalImage.setVisibility(View.GONE);
        } else {
            tvOriginalImage.setVisibility(View.VISIBLE);
        }
    }

    private int getChosePos(Media currItem) {
        List<Media> items = thumbnailAdapter.getItems();
        int i = 0;
        for (Media item : items) {
            if (item.getUri().equals(currItem.getUri())) {
                break;
            }
            i++;
        }
        return i;
    }

    private List<Media> getItems(Bundle args) {
        List<Media> items = new ArrayList<>();
        previewAll = true;
        if (args != null) {
            previewAll = args.getBoolean(KEY_PREVIEW_ALL, true);
            currentPos = args.getInt(KEY_PREVIEW_POS, 0);
        }

        List<Media> allList = null;
        if (getActivity() != null) {
            allList = ((SelectorActivity) getActivity()).getPreviewMedias(previewAll);
        }
        if (allList != null) {
            items.addAll(allList);
        }
        return items;
    }

    private boolean isItemChose(Media item) {
        if (getActivity() == null) {
            return false;
        }
        return ((SelectorActivity) getActivity()).isItemChose(item);
    }

    private void initChoseList() {
        choseList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        List<Media> items = getChoseItems();
        showHideChoseList(items.size());
        AnimUtil.removeChangeAnim(choseList);
        thumbnailAdapter = new ThumbnailAdapter(getActivity(), items);
        thumbnailAdapter.setCurrItem(adapter.getItems().get(currentPos));
        choseList.setAdapter(thumbnailAdapter);
        thumbnailAdapter.setOnThumbnailClickListener(this);
    }

    private void showHideChoseList(int size) {
        if (size > 0) {
            choseList.setVisibility(View.VISIBLE);
        } else {
            choseList.setVisibility(View.GONE);
        }
    }

    private List<Media> getChoseItems() {
        if (getActivity() == null) {
            return new ArrayList<>();
        }
        return ((SelectorActivity) getActivity()).getChoseMedias();
    }

    @Override
    public void onThumbnailClicked(View v, Media item) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mediaList.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        List<Media> items = adapter.getItems();
        int pos = 0;
        for (Media media : items) {
            if (media.getUri().equals(item.getUri())) {
                break;
            }
            pos++;
        }
        if (currentPos == pos) {
            return;
        }
        currentPos = pos;
        scrollToMediaPosition(true);
    }

    @Override
    public void onClick(View v) {
        if (getActivity() == null) {
            return;
        }
        if (v.getId() == R.id.ps_img_back) {
            UiUtil.hideFragment(getActivity(), this);
        } else if (v == tvEnsure) {
            ((SelectorActivity) getActivity()).ensure();
        } else if (v == tvChoose) {
            Media item = adapter.getItems().get(currentPos);
            SelectorActivity selectorActivity = (SelectorActivity) getActivity();
            boolean chooseSuccess = selectorActivity.choseItem(item, previewAll ? currentPos : SelectorActivity.INVALIDATE_POS);
            //超过可选数量就会失败
            if (!chooseSuccess) {
                return;
            }
            if (isItemChose(item)) {
                UiUtil.setDrawableLeft(tvChoose, R.drawable.ps_ic_radio_selected);
                addThumbnail(item);
            } else {
                UiUtil.setDrawableLeft(tvChoose, R.drawable.ps_ic_radio_transparent);
                removeThumbnail(item);
            }
            dealEnsureText();
            showHideChoseList(thumbnailAdapter.getItemCount());
        } else if (v == tvOriginalImage) {
            SelectorActivity selectorActivity = (SelectorActivity) getActivity();
            selectorActivity.originalImageClicked();
            dealOriginalImageState();
        }
    }

    private void removeThumbnail(Media item) {
        List<Media> items = thumbnailAdapter.getItems();
        if (items.size() == 0) {
            return;
        }
        int i = 0;
        for (Media media : items) {
            if (media.getUri().equals(item.getUri())) {
                break;
            }
            i++;
        }
        items.remove(i);
        thumbnailAdapter.notifyItemRemoved(i);
    }

    private void addThumbnail(Media item) {
        thumbnailAdapter.getItems().add(item);
        int position = thumbnailAdapter.getItemCount() - 1;
        thumbnailAdapter.notifyItemInserted(position);
        LinearLayoutManager choseLayoutManager = (LinearLayoutManager) choseList.getLayoutManager();
        if (choseLayoutManager != null) {
            int firstVisibleItemPosition = choseLayoutManager.findFirstVisibleItemPosition();
            int lastVisibleItemPosition = choseLayoutManager.findLastVisibleItemPosition();
            if (position < firstVisibleItemPosition || position > lastVisibleItemPosition) {
                choseLayoutManager.smoothScrollToPosition(choseList, null, position);
            }
        }
    }

    private void dealEnsureText() {
        if (getActivity() == null) {
            return;
        }
        SelectorParams params = ((SelectorActivity) getActivity()).getParams();
        UiUtil.dealPreviewEnsureText(tvEnsure, thumbnailAdapter.getItemCount(), params.chooseSize);
    }

    @Override
    public void onAdapterItemClicked(View v, Media item, int pos) {
        showHideBars();
    }

    private void showHideBars() {
        if (isShowBar) {
            hideBars();
            return;
        }
        showBars(false);
    }

    private void showBars(boolean showNow) {
        isShowBar = true;
        AnimUtil.fadeShowBar(layoutBottom, showNow);
        AnimUtil.fadeShowBar(choseList, showNow);

        if (showNow) {
            layoutHeader.setTranslationY(0);
            layoutHeader.setVisibility(View.VISIBLE);
            return;
        }

        AnimUtil.translationY(layoutHeader, true, layoutHeader.getHeight(), null);
    }

    private void hideBars() {
        isShowBar = false;
        AnimUtil.fadeHideBar(layoutBottom);
        AnimUtil.fadeHideBar(choseList);

        AnimUtil.translationY(layoutHeader, false, layoutHeader.getHeight(), new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                layoutHeader.setVisibility(View.GONE);
            }
        });
    }
}
