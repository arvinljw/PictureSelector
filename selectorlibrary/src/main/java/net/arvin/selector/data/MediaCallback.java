package net.arvin.selector.data;

import java.util.List;

/**
 * Created by arvinljw on 2020/7/16 16:47
 * Function：
 * Desc：
 */
public interface MediaCallback {
    void mediaFolderCallback(List<MediaFolder> folders);

    void mediasCallback(long bucketId, int page, List<Media> medias);
}
