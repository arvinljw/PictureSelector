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
import net.arvin.selector.data.MediaManager;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.uis.widgets.photoview.PhotoView;
import net.arvin.selector.utils.PSUtil;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/22 17:03
 * Function：
 * Desc：
 */
public class PreviewAdapter extends BaseAdapter<Media, PreviewAdapter.PreviewHolder> {

    public PreviewAdapter(Context context, List<Media> items) {
        super(context, items);
    }

    @NonNull
    @Override
    public PreviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewHolder(LayoutInflater.from(context).inflate(R.layout.ps_item_preview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewHolder holder, final int position) {
        final Media item = items.get(position);
        holder.setData(item);
        holder.imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onAdapterItemClicked(v, item, position);
                }
            }
        });
        holder.imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PSUtil.playVideo(context, item);
            }
        });
    }

    static class PreviewHolder extends RecyclerView.ViewHolder {
        private PhotoView imgPreview;
        private ImageView imgPlay;

        public PreviewHolder(@NonNull View itemView) {
            super(itemView);
            imgPreview = itemView.findViewById(R.id.ps_img_preview);
            imgPlay = itemView.findViewById(R.id.ps_img_play);
        }

        public void setData(Media media) {
            SelectorHelper.imageEngine.loadImage(imgPreview, media.getUri());
            if (MediaType.isVideo(media.getMimeType())) {
                imgPlay.setVisibility(View.VISIBLE);
            } else {
                imgPlay.setVisibility(View.GONE);
            }
        }
    }
}
