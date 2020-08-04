package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.R;
import net.arvin.selector.data.Media;
import net.arvin.selector.uis.widgets.MediaView;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/17 15:13
 * Function：
 * Desc：
 */
public class MediaAdapter extends BaseAdapter<Media, MediaAdapter.MediaHolder> {
    private DiffCallback diffCallback;
    private int chooseSize;

    private MediaView.OnMediaViewClickListener onMediaViewClickListener;

    public MediaAdapter(Context context, List<Media> items) {
        super(context, items);
    }

    public void setChooseSize(int chooseSize) {
        this.chooseSize = chooseSize;
    }

    public void setOnMediaViewClickListener(MediaView.OnMediaViewClickListener onMediaViewClickListener) {
        this.onMediaViewClickListener = onMediaViewClickListener;
    }

    public void setData(List<Media> oldList, List<Media> newList, boolean reset) {
        if (oldList == null) {
            oldList = items;
        }
        if (diffCallback == null) {
            diffCallback = new DiffCallback(oldList, newList);
        } else {
            diffCallback.setList(oldList, newList);
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
        items = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MediaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MediaHolder(LayoutInflater.from(context).inflate(R.layout.ps_item_media, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MediaHolder holder, int position) {
        holder.mediaView.setData(items.get(position), position, chooseSize);
        holder.mediaView.setOnMediaViewClickListener(new MediaView.OnMediaViewClickListener() {
            @Override
            public void onMediaViewClicked(View v, int type, Media item, int pos) {
                if (onMediaViewClickListener != null) {
                    onMediaViewClickListener.onMediaViewClicked(v, type, item, pos);
                }
            }
        });
    }

    static class MediaHolder extends RecyclerView.ViewHolder {
        MediaView mediaView;

        public MediaHolder(@NonNull View itemView) {
            super(itemView);
            mediaView = itemView.findViewById(R.id.ps_media_view);
        }
    }

    static class DiffCallback extends DiffUtil.Callback {
        private List<Media> oldList;
        private List<Media> newList;

        public DiffCallback(List<Media> oldList, List<Media> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        public void setList(List<Media> oldList, List<Media> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Media old = oldList.get(oldItemPosition);
            Media newItem = newList.get(newItemPosition);
            return oldItemPosition == newItemPosition && old.getUri().equals(newItem.getUri());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            //uri相同只有choseNum会发生改变
            Media old = oldList.get(oldItemPosition);
            Media newItem = newList.get(newItemPosition);
            return old.getChoseNum() == newItem.getChoseNum();
        }
    }
}
