package net.arvin.selector.uis.adapters;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.arvin.selector.data.Media;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/22 17:06
 * Function：
 * Desc：
 */
public abstract class BaseAdapter<T, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {
    protected Context context;
    protected List<T> items;
    protected OnAdapterItemClickListener<T> onItemClickListener;

    public BaseAdapter(Context context, List<T> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnAdapterItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<T> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
