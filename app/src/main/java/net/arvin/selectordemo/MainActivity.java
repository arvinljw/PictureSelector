package net.arvin.selectordemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.arvin.selector.SelectorHelper;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.uis.views.photoview.PhotoView;
import net.arvin.selector.utils.PSGlideUtil;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private RadioButton mRbSingleYes;
    private RadioButton mRbCropYes;
    private RadioButton mRbCameraYes;

    private EditText mEdMaxCount;

    private PhotoView mImage;

    private ArrayList<String> selectedPictures = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImage = findViewById(R.id.image);

        PSGlideUtil.loadImage(this, R.drawable.ps_icon, mImage);

        mEdMaxCount = findViewById(R.id.ed_max_count);
        mRbSingleYes = findViewById(R.id.rb_single_yes);
        mRbCropYes = findViewById(R.id.rb_crop_yes);
        mRbCameraYes = findViewById(R.id.rb_camera_yes);

        RadioGroup rgSingle = findViewById(R.id.rg_single);
        final RadioGroup rgCrop = findViewById(R.id.rg_crop);
        rgSingle.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_single_no) {
                    mEdMaxCount.setText("");
                    mEdMaxCount.setVisibility(View.VISIBLE);
                    rgCrop.setVisibility(View.GONE);
                    rgCrop.check(R.id.rb_crop_no);
                } else {
                    mEdMaxCount.setVisibility(View.GONE);
                    rgCrop.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void selectPicture(View v) {
        checkPermission(new CheckPermListener() {
            @Override
            public void agreeAllPermission() {
                if (mRbSingleYes.isChecked()) {
                    SelectorHelper.selectPicture(MainActivity.this, mRbCropYes.isChecked(),
                            mRbCameraYes.isChecked(), 1001);
                } else {
                    int maxCount;
                    try {
                        maxCount = Integer.valueOf(mEdMaxCount.getText().toString().trim());
                    } catch (Exception e) {
                        maxCount = 9;
                    }
                    SelectorHelper.selectPictures(MainActivity.this, maxCount,
                            mRbCameraYes.isChecked(), selectedPictures, 1001);
                }


            }
        }, "需要拍照和读取文件权限", Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public void takePhoto(View v) {
        checkPermission(new CheckPermListener() {
            @Override
            public void agreeAllPermission() {
                SelectorHelper.takePhoto(MainActivity.this, mRbCropYes.isChecked(), 1001);
            }
        }, "需要拍照和读取文件权限", Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1001) {
                ArrayList<String> backPics = data.getStringArrayListExtra(ConstantData.KEY_BACK_PICTURES);
                if (backPics != null && backPics.size() > 0) {
                    selectedPictures.clear();
                    selectedPictures.addAll(backPics);
                    PSGlideUtil.loadImage(this, backPics.get(0), mImage);
                }
            }
        }
    }

    @Override
    protected void backFromSetting() {
        selectPicture(null);
    }
}
