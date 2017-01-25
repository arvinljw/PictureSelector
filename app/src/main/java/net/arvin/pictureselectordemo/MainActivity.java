package net.arvin.pictureselectordemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;

import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.utils.PSConfigUtil;
import net.arvin.pictureselector.utils.PSConstanceUtil;
import net.arvin.pictureselector.utils.PSGlideUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private final String KEY_COUNT = "count";
    private final String KEY_CROP = "canCrop";
    private final int REQUEST_CODE_1 = 1001;
    private final int REQUEST_CODE_2 = 1002;

    private ImageView img;
    private TextView tvSwitch;

    private ArrayList<ImageEntity> selectedImages;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();

        initView();

        initEvent();
    }

    private void initData() {
        selectedImages = new ArrayList<>();

        PSConfigUtil.getInstance().setCanCrop(true)
                .setCanTakePhoto(true)
                .setMaxCount(1);
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.img_test);
        tvSwitch = (TextView) findViewById(R.id.tv_switch);

        tvSwitch.setSelected(PSConfigUtil.getInstance().getMaxCount() == 1);
        tvSwitch.setText(tvSwitch.isSelected() ? "单选" : "多选");
    }

    private void initEvent() {
        tvSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                tvSwitch.setText(v.isSelected() ? "单选" : "多选");
                PSConfigUtil.getInstance().setMaxCount(v.isSelected() ? 1 : 9);
            }
        });

        findViewById(R.id.tv_open_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPs();
            }
        });
        findViewById(R.id.tv_open_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //建议异步操作,如果文件过多则同样耗时
                PSConfigUtil.clearCache();
                Toast.makeText(MainActivity.this, "清除成功~", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPs() {
        String perms[] = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        checkPermission(new CheckPermListener() {
            @Override
            public void agreeAllPermission() {
                PSConfigUtil.getInstance().showSelector(MainActivity.this, REQUEST_CODE_1, selectedImages);
            }
        }, "获取相片需要使用相机和内存读写权限", perms);
    }

    private void openCamera() {
        String perms[] = {Manifest.permission.CAMERA};
        checkPermission(new CheckPermListener() {
            @Override
            public void agreeAllPermission() {
                PSConfigUtil.getInstance().showTakePhotoAndCrop(MainActivity.this, REQUEST_CODE_2);
            }
        }, "拍照需要使用相机权限", perms);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_1: {
                    selectedImages.clear();
                    List<ImageEntity> temp = data.getParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED);
                    selectedImages.addAll(temp);

                    PSGlideUtil.loadImage(this, "file://" + temp.get(0).getPath(), img);
                    for (ImageEntity selectedImage : selectedImages) {
                        Log.d("back_data", selectedImage.getPath());
                    }
                    break;
                }
                case REQUEST_CODE_2: {
                    selectedImages.clear();
                    List<ImageEntity> temp = data.getParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED);
                    selectedImages.addAll(temp);

                    PSGlideUtil.loadImage(this, "file://" + temp.get(0).getPath(), img);
                    for (ImageEntity selectedImage : selectedImages) {
                        Log.d("back_data", selectedImage.getPath());
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void backFromSetting() {
        super.backFromSetting();
        openPs();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(KEY_COUNT, PSConfigUtil.getInstance().getMaxCount());
        outState.putBoolean(KEY_CROP, PSConfigUtil.getInstance().isCanCrop());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        PSConfigUtil.getInstance()
                .setMaxCount(savedInstanceState.getInt(KEY_COUNT))
                .setCanCrop(savedInstanceState.getBoolean(KEY_CROP));
    }

    @Override
    protected void onDestroy() {
        PSConfigUtil.clearCache();
        super.onDestroy();
    }
}
