package net.arvin.selector.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2020/7/16 16:32
 * Function：
 * Desc：
 */
public class MediaFolder {
    private long bucketId;
    private String bucketName;
    private Media firstMedia;
    private List<Media> medias;
    private int count;
    private int page;
    private boolean isSelected;

    public MediaFolder(long bucketId, String bucketName) {
        this.bucketId = bucketId;
        this.bucketName = bucketName;
        this.medias = new ArrayList<>();
        this.isSelected = false;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Media getFirstMedia() {
        return firstMedia;
    }

    public void setFirstMedia(Media firstMedia) {
        this.firstMedia = firstMedia;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean hasMore() {
        return getMedias().size() < count;
    }
}
