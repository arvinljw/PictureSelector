package net.arvin.selector.uis.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.data.MediaFolder;
import net.arvin.selector.uis.adapters.FolderAdapter;
import net.arvin.selector.uis.adapters.OnAdapterItemClickListener;
import net.arvin.selector.utils.AnimUtil;
import net.arvin.selector.utils.PSUtil;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/19 20:20
 * Function：
 * Desc：
 */
public class FolderHelper implements OnAdapterItemClickListener<MediaFolder> {
    private Context context;
    private View layoutFolder;
    private RecyclerView folderList;
    private List<MediaFolder> folders;
    private FolderAdapter adapter;
    private int listHeight;
    private int selectedPos;
    private boolean isOpen;
    private OnAdapterItemClickListener<MediaFolder> callback;

    public FolderHelper(Context context, View layoutFolder, RecyclerView folderList, List<MediaFolder> folders) {
        this.context = context;
        this.layoutFolder = layoutFolder;
        this.folderList = folderList;
        this.folders = folders;
        selectedPos = 0;

        init();
    }

    private void init() {
        folderList.setLayoutManager(new LinearLayoutManager(context));
        folders.get(selectedPos).setSelected(true);
        adapter = new FolderAdapter(context, folders);
        adapter.setCallback(this);
        folderList.setAdapter(adapter);
        dealListHeight();
    }

    private void dealListHeight() {
        Resources resources = context.getResources();
        listHeight = folders.size() * resources.getDimensionPixelSize(R.dimen.dp_56);
        int maxHeight = resources.getDisplayMetrics().heightPixels * 4 / 5;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) folderList.getLayoutParams();
        if (listHeight > maxHeight) {
            listHeight = maxHeight;
            params.height = listHeight;
        } else {
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
        folderList.setLayoutParams(params);
    }

    public void show() {
        setOpen(true);
    }

    public void hide() {
        setOpen(false);
    }

    public void setOpen(boolean open) {
        isOpen = open;
        AnimUtil.folderOpenHide(layoutFolder, folderList, listHeight, isOpen);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener<MediaFolder> callback) {
        this.callback = callback;
    }

    @Override
    public void onAdapterItemClicked(View v, MediaFolder item, int pos) {
        folders.get(selectedPos).setSelected(false);
        folders.get(pos).setSelected(true);
        selectedPos = pos;
        adapter.notifyDataSetChanged();
        if (callback != null) {
            callback.onAdapterItemClicked(v, item, pos);
        }
    }

    public void setFolders(List<MediaFolder> folders) {
        this.folders.clear();
        this.folders.addAll(folders);
        adapter.notifyDataSetChanged();
    }
}
