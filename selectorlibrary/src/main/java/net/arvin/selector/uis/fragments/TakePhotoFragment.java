package net.arvin.selector.uis.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.arvin.selector.R;
import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.utils.PSUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

/**
 * Created by arvinljw on 18/1/4 17:38
 * Function：
 * Desc：
 */
public class TakePhotoFragment extends BaseFragment {
    private String mImagePath;

    @Override
    protected int getLayout() {
        return R.layout.ps_fragment_take_photo;
    }

    @Override
    protected ArrayList<String> getSelectedPictures() {
        ArrayList<String> selectedPics = new ArrayList<>();
        selectedPics.add(mImagePath);
        return selectedPics;
    }

    @Override
    protected ArrayList<String> getSelectedVideos() {
        return null;
    }

    @Override
    public void update(Bundle bundle) {
        init(null);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        initBaseInfo(getArguments());
        choosePhotoFromCamera();
    }

    private void choosePhotoFromCamera() {
        String dir = getTakePhotoDir();
        File photoDir = new File(dir);
        if (!photoDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            photoDir.mkdirs();
        }
        File mCurrentFile = new File(dir, getPhotoName());
        mImagePath = mCurrentFile.toString();
        Intent intent = getCameraIntent(mCurrentFile);
        startActivityForResult(intent, ConstantData.REQUEST_CODE_TAKE_PHOTO);
        getActivity().overridePendingTransition(0, 0);
    }

    private String getTakePhotoDir() {
        return PSUtil.getRootDir() + ConstantData.FOLDER_CAMERA;
    }

    private String getPhotoName() {
        return Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    /**
     * 在manifest的application标签中加入配置
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="换成包名.selector.provider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/ps_file_paths" />
     * </provider>
     * <p>
     * 上边的authorities属性值，需要与下面的FileProvider.getUriForFile(String,String,File)第二个参数一致
     */
    private Intent getCameraIntent(File f) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri imageUri = FileProvider.getUriForFile(getActivity(), mAuthorities, f);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        }
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ConstantData.REQUEST_CODE_TAKE_PHOTO:
                    if (mSelectType == ConstantData.VALUE_TYPE_CAMERA) {
                        if (mCanCrop) {
                            if (mTransactionListener != null) {
                                mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_CROP,
                                        ConstantData.toCropBundle(getArguments(), new FileEntity(mImagePath)));
                            }
                        } else {
                            onEnsureClicked();
                        }
                    } else {
                        if (mTransactionListener != null) {
                            mTransactionListener.switchFragment(ConstantData.VALUE_CHANGE_FRAGMENT_SELECTOR,
                                    ConstantData.toSelectorBundle(getArguments(), mImagePath, ConstantData.VALUE_CHANGE_FRAGMENT_TAKE_PHOTO));
                        }
                    }
                    PSUtil.scanFile(getActivity(), mImagePath);
                    break;
            }
            return;
        }
        onBackClicked();
    }

    @Override
    protected void onBackClicked() {
        if (mSelectType == ConstantData.VALUE_TYPE_CAMERA) {
            getActivity().onBackPressed();
            return;
        }
        super.onBackClicked();
    }
}
