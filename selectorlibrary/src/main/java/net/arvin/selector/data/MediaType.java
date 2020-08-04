package net.arvin.selector.data;

/**
 * Created by arvinljw on 2020/7/14 16:31
 * Function：
 * Desc：
 */
public enum MediaType {
    IMAGE(true), IMAGE_NO_GIF(false), VIDEO, ALL;

    /**
     * 是否有gif标识，对VIDEO和ALL无效，VIDEO就不会有gif，all是包含有gif
     */
    private boolean hasGif;

    MediaType() {
    }

    MediaType(boolean hasGif) {
        this.hasGif = hasGif;
    }

    public boolean hasGif() {
        return hasGif;
    }

    public static boolean isGif(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals("image/gif");
    }

    public static boolean isImage(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("image");
    }

    public static boolean isVideo(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("video");
    }
}
