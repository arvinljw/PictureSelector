package net.arvin.pictureselector.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.util.Calendar;

public class PSTakePhotoUtil {
    private Activity mActivity;
    private String fileDir;
    private String imagePath;
    private File mCurrentFile;
    private OnTakePhotoSuccessListener imageSuccess;
    public static final int RESULT_OK = -1;

    public PSTakePhotoUtil(Activity mActivity, OnTakePhotoSuccessListener imageSuccess) {
        this.mActivity = mActivity;
        this.imageSuccess = imageSuccess;
        initFileDir();
    }

    @SuppressWarnings("ConstantConditions")
    public void initFileDir() {
        fileDir = getTakePhotoDir();
    }

    private static String getTakePhotoDir() {
        return PSConstanceUtil.getRootDIr() + PSConstanceUtil.FROM_CAMERA;
    }

    public void choosePhotoFromCamera() {
        File photoDir = new File(fileDir);
        if (!photoDir.exists()) {
            photoDir.mkdirs();
        }
        mCurrentFile = new File(fileDir, getPhotoName());
        Log.i("URI", mCurrentFile.toString());
        imagePath = mCurrentFile.toString();
        final Intent intent = getCameraIntent(mCurrentFile);
        mActivity.startActivityForResult(intent, PSConstanceUtil.IMAGE_REQUEST_TAKE_PHOTO);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data, Activity mActivity) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PSConstanceUtil.IMAGE_REQUEST_TAKE_PHOTO:
                    imageSuccess.onTakePhotoSuccess(imagePath);
                    scanFile(imagePath);
                    break;
            }
        }
    }

    private String getPhotoName() {
        return Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    /**
     * 在res目录下创建xml文件夹，再在该目录下创建file_paths.xml，内容如下：
     * <?xml version="1.0" encoding="utf-8"?>
     * <resources>
     * <paths>
     * <external-path name="camera_photos" path="" />
     * </paths>
     * </resources>
     * 在manifest的application标签中加入配置
     * <provider
     * android:name="android.support.v4.content.FileProvider"
     * android:authorities="net.arvin.takephoto.fileprovider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_paths" />
     * </provider>
     * <p>
     * 上边的authorities属性值，可以自定义，需要与下面的FileProvider.getUriForFile(String,String,File)第二个参数一致
     */
    private Intent getCameraIntent(File f) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri imageUri = FileProvider.getUriForFile(mActivity, "net.arvin.takephoto.fileprovider", f);//通过FileProvider创建一个content类型的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI

        } else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        }
        return intent;
    }

    protected void scanFile(String path) {
        MediaScannerConnection.scanFile(mActivity, new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.i("scanFile", "刷新成功");
                    }
                });
    }

    public static void clear() {
        PSConstanceUtil.clear(getTakePhotoDir());
    }

    public interface OnTakePhotoSuccessListener {
        void onTakePhotoSuccess(String path);
    }
}
