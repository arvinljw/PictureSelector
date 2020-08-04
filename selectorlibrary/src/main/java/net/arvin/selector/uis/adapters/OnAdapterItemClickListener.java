package net.arvin.selector.uis.adapters;

import android.view.View;

/**
 * Created by arvinljw on 2020/7/31 18:35
 * Function：
 * Desc：
 */
public interface OnAdapterItemClickListener<T> {
    void onAdapterItemClicked(View v, T item, int pos);
}
