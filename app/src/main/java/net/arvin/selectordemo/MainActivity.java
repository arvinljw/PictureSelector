package net.arvin.selectordemo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.Media;
import net.arvin.selector.data.MediaStorageStrategy;
import net.arvin.selector.data.MediaType;
import net.arvin.selector.engines.ImageEngine;
import net.arvin.selector.utils.MediaScanner;
import net.arvin.selector.utils.TakePhotoUtil;
import net.arvin.selectordemo.permission.DefaultResourceProvider;
import net.arvin.selectordemo.permission.PermissionUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PermissionUtil permissionUtil;
    private ImageEngine imageEngine;
    private Uri photoUri;
    private ImageView imgPreview;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initImageEngine();

        initPermissionConfig();
    }

    private void initView() {
        imgPreview = findViewById(R.id.img_preview);
        findViewById(R.id.btnChoosePic).setOnClickListener(this);
        findViewById(R.id.btnTakePhoto).setOnClickListener(this);
    }

    private void initImageEngine() {
        imageEngine = new ImageEngine() {
            @Override
            public void loadImage(ImageView imageView, Uri uri) {
                Glide.with(imageView)
                        .load(uri)
                        .into(imageView);
            }
        };
        SelectorHelper.init(imageEngine);
    }

    private void initPermissionConfig() {
        PermissionUtil.setExtraResourceProvider(new DefaultResourceProvider());
        permissionUtil = new PermissionUtil.Builder().with(this).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChoosePic:
                permissionUtil.request("需要文件读写权限", Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionUtil.RequestPermissionListener() {
                    @Override
                    public void callback(boolean granted, boolean isAlwaysDenied) {
                        if (granted) {
                            new SelectorHelper.Builder()
                                    .setChooseSize(9)
                                    .setMediaType(MediaType.IMAGE)
                                    .setStyle(R.style.PS_Customer)
                                    .build()
                                    .forResult(MainActivity.this, 1001);
                        }
                    }
                });
                break;
            case R.id.btnTakePhoto:
                permissionUtil.request("需要拍照权限", Manifest.permission.CAMERA, new PermissionUtil.RequestPermissionListener() {
                    @Override
                    public void callback(boolean granted, boolean isAlwaysDenied) {
                        if (granted) {
                            photoUri =
                                    TakePhotoUtil.takePhoto(MainActivity.this,
                                    MediaStorageStrategy.publicStrategy("net.arvin.selectordemo.ps.provider", "psdemo"),
                                    1002);
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001 && data != null) {
                List<Media> medias = SelectorHelper.getMediaDataFromIntent(data);
                boolean isOriginalImage = SelectorHelper.getMediaIsOriginalImage(data);
                Log.d("ljw >>>", "isOriginalImage: " + isOriginalImage);
                if (medias != null && medias.size() > 0) {
                    imageEngine.loadImage(imgPreview, medias.get(0).getUri());
                    for (Media media : medias) {
                        Log.d("ljw >>>", "onActivityResult: " + media.getUri());
                    }
                }
            } else if (requestCode == 1002) {
                if (photoUri != null) {
                    TakePhotoUtil.scanPath(this, TakePhotoUtil.getPhotoPath(), new MediaScanner.ScanCompletedCallback() {
                        @Override
                        public void onScanCompleted(String path, final Uri uri) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageEngine.loadImage(imgPreview, uri);
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}