package net.arvin.selector.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import net.arvin.selector.R;

/**
 * Created by arvinljw on 17/12/27 14:16
 * Function：
 * Desc：
 */
public class PSGlideUtil {
    private static RequestOptions defaultOptions;

    public static void loadImage(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(getDefaultOptions())
                .into(imageView);
    }


    public static void loadImage(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .into(imageView);
    }

    public static RequestOptions getDefaultOptions() {
        if (defaultOptions == null) {
            defaultOptions = new RequestOptions()
                    .placeholder(R.drawable.ps_img_loading)
                    .error(R.drawable.ps_img_loading);
        }
        return defaultOptions;
    }
}
