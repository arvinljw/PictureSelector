package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.MediaFolder;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/19 20:22
 * Function：
 * Desc：
 */
public class FolderAdapter extends BaseAdapter<MediaFolder, FolderAdapter.FolderViewHolder> {

    private OnAdapterItemClickListener<MediaFolder> callback;

    public FolderAdapter(Context context, List<MediaFolder> items) {
        super(context, items);
    }

    public void setCallback(OnAdapterItemClickListener<MediaFolder> callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FolderViewHolder(LayoutInflater.from(context).inflate(R.layout.ps_item_media_folder, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, final int position) {
        final MediaFolder item = items.get(position);
        holder.setData(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAdapterItemClicked(v, item, position);
            }
        });
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private ImageView imgSelected;
        private TextView tvFolderName;
        private TextView tvFolderItemCount;

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.ps_img_thumbnail);
            imgSelected = itemView.findViewById(R.id.ps_img_selected);
            tvFolderName = itemView.findViewById(R.id.ps_tv_folder_name);
            tvFolderItemCount = itemView.findViewById(R.id.ps_tv_folder_item_count);
        }

        public void setData(MediaFolder item) {
            if (item.getCount() > 0) {
                SelectorHelper.imageEngine.loadImage(imgThumbnail, item.getFirstMedia().getUri());
            }
            tvFolderName.setText(item.getBucketName());
            tvFolderItemCount.setText("（" + item.getCount() + "）");
            imgSelected.setVisibility(item.isSelected() ? View.VISIBLE : View.GONE);

        }
    }
}
