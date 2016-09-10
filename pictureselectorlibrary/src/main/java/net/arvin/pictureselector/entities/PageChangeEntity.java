package net.arvin.pictureselector.entities;

import android.os.Bundle;

/**
 * created by arvin on 16/8/28 17:52
 * email：1035407623@qq.com
 */
public class PageChangeEntity {
    private PageId page;
    private Bundle data;

    public PageChangeEntity(PageId page, Bundle data) {
        this.page = page;
        this.data = data;
    }

    public PageId getPage() {
        return page;
    }

    public void setPage(PageId page) {
        this.page = page;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    /**
     * PictureSelector表示首页，Review表示预览页，Crop表示裁剪页
     */
    public enum  PageId {
        PictureSelector,Crop,Review
    }
}
