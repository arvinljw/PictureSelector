package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.entities.FolderEntity;
import net.arvin.selector.listeners.OnItemClickListener;
import net.arvin.selector.listeners.OnItemSelectListener;
import net.arvin.selector.utils.PSGlideUtil;
import net.arvin.selector.utils.PSScreenUtil;
import net.arvin.selector.utils.PSToastUtil;
import net.arvin.selector.utils.PSUtil;

import java.util.List;

/**
 * Created by arvinljw on 17/12/25 17:35
 * Function：
 * Desc：
 */
public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.ViewHolder> {
    private Context mContext;
    private List<FolderEntity> mItems;

    private OnItemClickListener mItemClickListener;
    private int mCurrPos = 0;

    public FolderAdapter(Context context, List<FolderEntity> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public FolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(final FolderAdapter.ViewHolder holder, int position) {
        FolderEntity entity = mItems.get(position);
        holder.mTvName.setText(entity.getFolderName());
        holder.mTvCount.setText(mContext.getResources().getString(R.string.ps_image_count, entity.getImages().size()));
        PSGlideUtil.loadImage(mContext, entity.getFirstImagePath(), holder.mImgFirst);
        holder.mImgSelector.setVisibility(entity.isSelected() ? View.VISIBLE : View.GONE);
        setListener(holder, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private void setListener(final ViewHolder holder, final int pos) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems.get(mCurrPos).setSelected(false);
                mItems.get(pos).setSelected(true);
                mCurrPos = pos;
                notifyDataSetChanged();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, pos);
                }
            }
        });
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImgFirst;
        TextView mTvName;
        TextView mTvCount;
        ImageView mImgSelector;

        ViewHolder(View itemView) {
            super(itemView);
            mImgFirst = itemView.findViewById(R.id.ps_img_first);
            mImgSelector = itemView.findViewById(R.id.ps_img_selector);

            mTvName = itemView.findViewById(R.id.ps_tv_folder_name);
            mTvCount = itemView.findViewById(R.id.ps_tv_folder_count);
        }
    }
}
