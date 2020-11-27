package net.arvin.selector.uis.widgets.editable;

import android.view.View;

/**
 * Created by arvinljw on 2020/8/9 00:05
 * Function：
 * Desc：
 */
public abstract class OnEditTouchListener implements View.OnTouchListener {
    private boolean intercept;

    public boolean isIntercept() {
        return intercept;
    }

    public void setIntercept(boolean intercept) {
        this.intercept = intercept;
    }
}
