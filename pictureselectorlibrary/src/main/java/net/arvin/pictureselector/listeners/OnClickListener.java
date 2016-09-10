package net.arvin.pictureselector.listeners;

import android.view.View;

/**
 * created by arvin on 16/8/29 22:30
 * email：1035407623@qq.com
 */
public abstract class OnClickListener implements View.OnClickListener {
    private int position;

    public OnClickListener(int position) {
        this.position = position;
    }

    @Override
    public void onClick(View v) {
        onClick(v, position);
    }

    public abstract void onClick(View v, int position);
}
