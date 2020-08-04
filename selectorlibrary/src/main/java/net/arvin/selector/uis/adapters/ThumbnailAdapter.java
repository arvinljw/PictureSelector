package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/22 17:37
 * Function：
 * Desc：
 */
public class ThumbnailAdapter extends BaseAdapter<Media, ThumbnailAdapter.ThumbnailHolder> {

    private OnThumbnailClickListener onThumbnailClickListener;
    private Media currItem;

    public ThumbnailAdapter(Context context, List<Media> items) {
        super(context, items);
    }

    public void setOnThumbnailClickListener(OnThumbnailClickListener onThumbnailClickListener) {
        this.onThumbnailClickListener = onThumbnailClickListener;
    }

    public void setCurrItem(Media item) {
        if (currItem != null && currItem.getUri().equals(item.getUri())) {
            return;
        }
        currItem = item;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ThumbnailHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThumbnailHolder(LayoutInflater.from(context).inflate(R.layout.ps_item_thumbnail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailHolder holder, int position) {
        final Media item = items.get(position);
        boolean isSelected = false;
        if (currItem != null) {
            isSelected = item.getUri().equals(currItem.getUri());
        }
        holder.setData(item, isSelected);
        holder.imgThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onThumbnailClickListener != null) {
                    onThumbnailClickListener.onThumbnailClicked(v, item);
                }
            }
        });
    }

    static class ThumbnailHolder extends RecyclerView.ViewHolder {
        private ImageView imgThumbnail;
        private View viewSelected;

        public ThumbnailHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.ps_img_thumbnail);
            viewSelected = itemView.findViewById(R.id.ps_v_selected);
        }

        public void setData(Media item, boolean isSelected) {
            viewSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            SelectorHelper.imageEngine.loadImage(imgThumbnail, item.getUri());
        }
    }

    public interface OnThumbnailClickListener {
        void onThumbnailClicked(View v, Media item);
    }
}
