package net.arvin.selector.uis.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import net.arvin.selector.R;
import net.arvin.selector.entities.FolderEntity;
import net.arvin.selector.listeners.OnItemClickListener;
import net.arvin.selector.uis.adapters.FolderAdapter;
import net.arvin.selector.utils.PSScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 18/1/6 17:54
 * Function：
 * Desc：
 */
public class FolderDialog extends Dialog implements OnItemClickListener {
    private List<FolderEntity> mImageFolders;
    private FolderAdapter mAdapter;
    private OnFolderSelectedListener mFolderSelectedListener;

    public FolderDialog(Context context, List<FolderEntity> imageFolders) {
        super(context, R.style.normal_dialog_white);
        setContentView(R.layout.ps_dialog_folder);
        setImageFolders(imageFolders);
        layoutDialog(context);
    }

    @SuppressWarnings("ConstantConditions")
    private void layoutDialog(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.x = 0;
        lp.y = PSScreenUtil.dp2px(48);
        lp.width = metrics.widthPixels;
        //这个高度去掉了底部，2*标题以及状态栏
        lp.height = metrics.heightPixels - PSScreenUtil.dp2px(168);
        getWindow().setAttributes(lp);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView mRcvFolder = getWindow().findViewById(R.id.ps_rcv_folder);
        mRcvFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new FolderAdapter(getContext(), mImageFolders);
        mRcvFolder.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
    }

    public void setOnFolderSelectedListener(OnFolderSelectedListener folderSelectedListener) {
        this.mFolderSelectedListener = folderSelectedListener;
    }

    private void setImageFolders(List<FolderEntity> imageFolders) {
        if (mImageFolders == null) {
            mImageFolders = new ArrayList<>();
        } else {
            mImageFolders.clear();
        }
        mImageFolders.addAll(imageFolders);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View v, int position) {
        if (mFolderSelectedListener != null) {
            mFolderSelectedListener.onFolderSelected(v, position);
        }
        dismiss();
    }

    public interface OnFolderSelectedListener {
        void onFolderSelected(View v, int position);
    }
}
