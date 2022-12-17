package net.arvin.selectordemo;

import android.app.Application;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.arvin.selector.SelectorHelper;
import net.arvin.selector.engines.ImageEngine;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SelectorHelper.init(new ImageEngine() {
            @Override
            public void loadImage(ImageView imageView, Uri uri) {
                Glide.with(imageView)
                        .load(uri)
                        .into(imageView);
            }
        });
    }
}
