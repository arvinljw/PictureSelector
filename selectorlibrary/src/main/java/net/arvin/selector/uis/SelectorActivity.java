package net.arvin.selector.uis;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaCallback;
import net.arvin.selector.data.MediaFolder;
import net.arvin.selector.data.MediaManager;
import net.arvin.selector.data.SelectorParams;
import net.arvin.selector.uis.adapters.MediaAdapter;
import net.arvin.selector.uis.adapters.OnAdapterItemClickListener;
import net.arvin.selector.uis.widgets.FolderHelper;
import net.arvin.selector.uis.widgets.GridDivider;
import net.arvin.selector.uis.widgets.MediaView;
import net.arvin.selector.utils.AnimUtil;
import net.arvin.selector.utils.PSUtil;
import net.arvin.selector.utils.UiUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by arvinljw on 2020/7/9 17:05
 * Function：
 * Desc：媒体库界面
 */
public class SelectorActivity extends AppCompatActivity implements View.OnClickListener, MediaCallback,
        MediaView.OnMediaViewClickListener, OnAdapterItemClickListener<MediaFolder> {
    public static String KEY_PARAMS = "params";
    public static final int INVALIDATE_POS = -1;
    private SelectorParams params;

    private ImageView imgArrow;
    private TextView tvTitle;
    private TextView tvEnsure;
    private TextView tvPreview;
    private TextView tvUseTime;
    private TextView tvOriginalImage;

    private MediaAdapter adapter;
    private List<MediaFolder> folders;

    private int choseCount = 0;
    private Map<Uri, Integer> choseMedias;
    //该列表的choseNum不正确，所以不能使用该列表的choseNum
    private ArrayList<Media> choseMediaList;

    private FolderHelper folderHelper;

    private int currFolderPos = 0;
    private boolean isLoading;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_PARAMS, params);
    }

    private void configParams(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            params = savedInstanceState.getParcelable(KEY_PARAMS);
        }
        if (params == null) {
            params = getIntent().getParcelableExtra(KEY_PARAMS);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        configParams(savedInstanceState);
        if (params != null) {
            setTheme(params.style);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ps_activity_selector);

        init();
    }

    private void init() {
        initView();
        initListener();
        initText();
    }

    private void initView() {
        imgArrow = findViewById(R.id.ps_img_title_arrow);
        tvTitle = findViewById(R.id.ps_tv_title);
        tvEnsure = findViewById(R.id.ps_btn_ensure);
        tvUseTime = findViewById(R.id.ps_tv_use_time);
        tvPreview = findViewById(R.id.ps_tv_preview);
        tvOriginalImage = findViewById(R.id.ps_tv_original_image);

        initList();
    }

    private void initList() {
        RecyclerView mediaList = findViewById(R.id.ps_media_list);
        mediaList.setLayoutManager(new GridLayoutManager(this, SelectorParams.SPAN_COUNT));
        adapter = new MediaAdapter(this, new ArrayList<Media>());
        adapter.setChooseSize(params.chooseSize);
        adapter.setOnMediaViewClickListener(this);
        AnimUtil.removeChangeAnim(mediaList);
        mediaList.setAdapter(adapter);
        mediaList.addItemDecoration(new GridDivider(getResources().getDimensionPixelSize(R.dimen.dp_2)));

        mediaList.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    LinearLayoutManager gridLayoutManager = (LinearLayoutManager) layoutManager;
                    int mLastChildPosition = gridLayoutManager.findLastVisibleItemPosition();
                    int itemTotalCount = gridLayoutManager.getItemCount();

                    if (mLastChildPosition == itemTotalCount - 1 && !isLoading) {
                        if (folders != null && folders.get(currFolderPos).hasMore()) {
                            isLoading = true;
                            MediaManager.loadMedia(SelectorActivity.this, folders.get(currFolderPos).getBucketId(),
                                    folders.get(currFolderPos).getPage(), params, SelectorActivity.this);
                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    AnimUtil.fadeHide(tvUseTime);
                } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager == null) {
                        return;
                    }
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    Media media = adapter.getItems().get(firstVisibleItemPosition);
                    long dateTaken = media.getDateTaken();
                    tvUseTime.setText(PSUtil.getDate(SelectorActivity.this, dateTaken));
                    AnimUtil.fadeShow(tvUseTime);
                }
            }
        });

        choseMedias = new HashMap<>();
        choseMediaList = new ArrayList<>();
        MediaManager.loadFolder(this, params, this);
    }

    private void initListener() {
        findViewById(R.id.ps_img_close).setOnClickListener(this);
        findViewById(R.id.ps_layout_title).setOnClickListener(this);
        findViewById(R.id.ps_layout_media_folder).setOnClickListener(this);
        tvEnsure.setOnClickListener(this);
        tvPreview.setOnClickListener(this);
        tvOriginalImage.setOnClickListener(this);
    }

    private void initText() {
        UiUtil.dealEnsureText(tvEnsure, choseCount, params.chooseSize);
        UiUtil.dealPreviewText(tvPreview, choseCount);
    }

    @Override
    public void mediaFolderCallback(List<MediaFolder> folders) {
        if (folders == null || folders.size() == 0) {
            return;
        }
        this.folders = folders;
        initMediaFolder(folders.get(0));
    }

    private void initMediaFolder(MediaFolder folder) {
        isLoading = false;
        tvTitle.setText(folder.getBucketName());
        if (folder.getMedias().size() == 0) {
            MediaManager.loadMedia(this, folder.getBucketId(), folder.getPage(), params, this);
            return;
        }
        adapter.setData(adapter.getItems(), PSUtil.generateNewList(folder, choseMedias), true);
    }

    @Override
    public void mediasCallback(long bucketId, int page, List<Media> medias) {
        isLoading = false;
        if (medias == null) {
            return;
        }
        MediaFolder currFolder = null;
        for (MediaFolder folder : folders) {
            if (folder.getBucketId() == bucketId) {
                if (folder.getPage() == MediaManager.FIRST_PAGE) {
                    folder.getMedias().clear();
                }
                folder.setPage(page + 1);
                folder.getMedias().addAll(medias);
                currFolder = folder;
                break;
            }
        }
        if (currFolder != null) {
            adapter.setData(adapter.getItems(), PSUtil.generateNewList(currFolder, choseMedias), true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ps_layout_title) {
            showHideFolders();
        } else if (v == tvEnsure) {
            ensure();
        } else if (v.getId() == R.id.ps_img_close) {
            onBackPressed();
        } else if (v.getId() == R.id.ps_layout_media_folder) {
            folderHelper.hide();
        } else if (v == tvPreview) {
            UiUtil.showPreviewFragment(this, false, 0);
        } else if (v == tvOriginalImage) {
            originalImageClicked();
        }
    }

    private void showHideFolders() {
        if (folders == null) {
            return;
        }
        if (PSUtil.isQuickClick(AnimUtil.DURATION_200)) {
            return;
        }
        if (folderHelper == null) {
            RecyclerView folderList = findViewById(R.id.ps_media_folder_list);
            folderHelper = new FolderHelper(this, findViewById(R.id.ps_layout_media_folder),
                    folderList, folders);
        }
        boolean isOpen = !folderHelper.isOpen();
        AnimUtil.rotation(imgArrow, isOpen);
        folderHelper.setOnAdapterItemClickListener(this);
        if (isOpen) {
            folderHelper.show();
        } else {
            folderHelper.hide();
        }
    }

    @Override
    public void onMediaViewClicked(View v, int type, Media item, int pos) {
        if (type == MediaView.OnMediaViewClickListener.TYPE_IMAGE) {
            UiUtil.showPreviewFragment(this, true, pos);
        } else if (type == MediaView.OnMediaViewClickListener.TYPE_SELECT) {
            itemSelect(item, pos);
        }
    }

    private boolean itemSelect(Media item, int pos) {
        if (!isItemChose(item)) {
            //正常情况只有非chooseSize>1才会触发
            if (choseCount >= params.chooseSize) {
                Toast.makeText(this, SelectorHelper.textEngine.alreadyMaxCount(this, params.chooseSize), Toast.LENGTH_SHORT).show();
                return false;
            }
            choseCount++;
            PSUtil.chooseItem(adapter, choseMedias, choseMediaList, pos, choseCount);
        } else {
            choseCount--;
            PSUtil.cancelItem(adapter, choseMedias, choseMediaList, pos);
        }
        UiUtil.dealEnsureText(tvEnsure, choseCount, params.chooseSize);
        UiUtil.dealPreviewText(tvPreview, choseCount);
        return true;
    }

    @Override
    public void onAdapterItemClicked(View v, MediaFolder item, int pos) {
        showHideFolders();
        currFolderPos = pos;
        initMediaFolder(item);
    }

    public List<Media> getPreviewMedias(boolean previewAll) {
        if (previewAll) {
            return adapter.getItems();
        }
        return getChoseMedias();
    }

    public List<Media> getChoseMedias() {
        List<Media> items = adapter.getItems();
        List<Media> choseItems = new ArrayList<>();
        for (Media item : items) {
            if (choseMedias.containsKey(item.getUri())) {
                choseItems.add(item);
            }
        }
        return choseItems;
    }

    public boolean choseItem(Media item, int pos) {
        if (pos == INVALIDATE_POS) {
            List<Media> items = adapter.getItems();
            int i = 0;
            for (Media media : items) {
                if (item.getUri().equals(media.getUri())) {
                    pos = i;
                    break;
                }
                i++;
            }
        }
        return itemSelect(item, pos);
    }

    public boolean isItemChose(Media item) {
        return choseMedias.containsKey(item.getUri());
    }

    public boolean isSelectOriginalImage() {
        return tvOriginalImage.isSelected();
    }

    public void originalImageClicked() {
        tvOriginalImage.setSelected(!tvOriginalImage.isSelected());
        if (tvOriginalImage.isSelected()) {
            UiUtil.setDrawableLeft(tvOriginalImage, R.drawable.ps_ic_radio_selected);
        } else {
            UiUtil.setDrawableLeft(tvOriginalImage, R.drawable.ps_ic_radio_transparent);
        }
    }

    public SelectorParams getParams() {
        return params;
    }

    public void ensure() {
        int size = choseMediaList.size();
        if (size == 0) {
            return;
        }
        //重新整理一下choseNum
        for (int i = 0; i < choseMediaList.size(); i++) {
            choseMediaList.get(i).setChoseNum(i + 1);
        }
        Intent data = new Intent();
        data.putParcelableArrayListExtra(SelectorHelper.KEY_BACK_MEDIA, choseMediaList);
        data.putExtra(SelectorHelper.KEY_ORIGINAL_IMAGE, tvOriginalImage.isSelected());
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (folderHelper != null && folderHelper.isOpen()) {
            showHideFolders();
            return;
        }
        if (UiUtil.hasEditFragment(this)) {
            return;
        }
        if (UiUtil.isShowPreviewFragment(this)) {
            return;
        }
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}