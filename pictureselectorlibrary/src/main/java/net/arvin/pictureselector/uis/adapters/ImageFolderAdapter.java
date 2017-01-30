package net.arvin.pictureselector.uis.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.arvin.pictureselector.R;
import net.arvin.pictureselector.entities.ImageFolderEntity;
import net.arvin.pictureselector.listeners.OnClickListener;
import net.arvin.pictureselector.listeners.OnItemClickListener;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSGlideUtil;

import java.util.List;

/**
 * created by arvin on 16/9/3 12:05
 * email：1035407623@qq.com
 */
public class ImageFolderAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<ImageFolderEntity> mItems;
    private OnItemClickListener onItemClickListener;

    public ImageFolderAdapter(Context context, List<ImageFolderEntity> items) {
        this.mContext = context;
        this.mItems = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ImageFolderHolder(LayoutInflater.from(mContext).inflate(R.layout.ps_item_image_folder, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(new OnClickListener(position) {
            @Override
            public void onClick(View v, int position) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, position);
                }
            }
        });
        ((ImageFolderHolder) holder).setData(position);
    }

    class ImageFolderHolder extends RecyclerView.ViewHolder {
        private ImageView imgContent;
        private TextView tvFileName;
        private TextView tvFileCount;
        private ImageView imgSelected;

        public ImageFolderHolder(View itemView) {
            super(itemView);
            imgContent = (ImageView) itemView.findViewById(R.id.img_first);
            imgSelected = (ImageView) itemView.findViewById(R.id.img_selected);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tvFileCount = (TextView) itemView.findViewById(R.id.tv_file_count);
        }

        public void setData(int position) {
            ImageFolderEntity item = mItems.get(position);
            PSGlideUtil.loadImage(mContext, "file://" + item.getFirstImagePath(), imgContent);
            tvFileName.setText(item.getFolderName());
            tvFileCount.setText(item.getCount() + "张");

            if (position == PSConfigUtil.getInstance().getSelectedFolderPos()) {
                imgSelected.setVisibility(View.VISIBLE);
                PSGlideUtil.loadLocalImage(mContext, R.drawable.ps_checked, imgSelected);
            } else {
                imgSelected.setVisibility(View.GONE);
            }
        }
    }

}
