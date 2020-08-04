package net.arvin.selector.engines;

import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by arvinljw on 2020/7/14 16:17
 * Function：
 * Desc：
 */
public interface ImageEngine {
    void loadImage(ImageView imageView, Uri uri);
}
