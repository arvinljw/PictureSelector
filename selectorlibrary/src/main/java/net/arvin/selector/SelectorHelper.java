package net.arvin.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.fragment.app.Fragment;

import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaStorageStrategy;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.data.SelectorParams;
import net.arvin.selector.engines.ImageEngine;
import net.arvin.selector.engines.TextEngine;
import net.arvin.selector.engines.defaultimpl.DefaultTextEngine;
import net.arvin.selector.uis.SelectorActivity;

import java.util.List;

public final class SelectorHelper {
    public static final String KEY_BACK_MEDIA = "backMedia";
    public static final String KEY_ORIGINAL_IMAGE = "originalImage";

    //图片加载引擎
    public static ImageEngine imageEngine;
    //文本引擎
    public static TextEngine textEngine;
    private SelectorParams params;

    private SelectorHelper(Builder builder) {
        params = new SelectorParams();
        params.chooseSize = builder.chooseSize;
        params.mediaType = builder.mediaType;
        params.style = builder.style;
        params.storageStrategy = builder.storageStrategy;
    }

    public static void init(ImageEngine imageEngine) {
        init(imageEngine, null);
    }

    public static void init(ImageEngine imageEngine, TextEngine textEngine) {
        SelectorHelper.imageEngine = imageEngine;
        if (textEngine == null) {
            textEngine = new DefaultTextEngine();
        }
        SelectorHelper.textEngine = textEngine;
    }

    public void forResult(Activity activity, int requestCode) {
        forResult(activity, null, requestCode);
    }

    public void forResult(Fragment fragment, int requestCode) {
        forResult(null, fragment, requestCode);
    }

    private void forResult(Activity activity, Fragment fragment, int requestCode) {
        Context context;
        if (activity != null) {
            context = activity;
        } else {
            context = fragment.getActivity();
        }
        Intent intent = new Intent(context, SelectorActivity.class);
        intent.putExtra(SelectorActivity.KEY_PARAMS, params);
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    public static List<Media> getMediaDataFromIntent(Intent data) {
        if (data == null) {
            return null;
        }
        return data.getParcelableArrayListExtra(KEY_BACK_MEDIA);
    }

    public static boolean getMediaIsOriginalImage(Intent data) {
        if (data == null) {
            return true;
        }
        return data.getBooleanExtra(KEY_ORIGINAL_IMAGE, true);
    }

    public static class Builder {
        //选择数量
        private int chooseSize;
        //选择的媒体类型
        private MediaType mediaType;
        //样式
        private int style;
        //编辑图片时保存图片策略
        private MediaStorageStrategy storageStrategy;

        public Builder setChooseSize(int chooseSize) {
            this.chooseSize = chooseSize;
            return this;
        }

        public Builder setMediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public Builder setStyle(int style) {
            this.style = style;
            return this;
        }

        public Builder setStorageStrategy(MediaStorageStrategy storageStrategy) {
            this.storageStrategy = storageStrategy;
            return this;
        }

        public SelectorHelper build() {
            if (chooseSize == 0) {
                chooseSize = 1;
            }
            if (mediaType == null) {
                mediaType = MediaType.ALL;
            }
            if (style == 0) {
                style = R.style.PS_Default;
            }
            return new SelectorHelper(this);
        }
    }
}