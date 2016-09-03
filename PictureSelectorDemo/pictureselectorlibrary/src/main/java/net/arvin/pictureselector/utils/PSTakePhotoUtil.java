package net.arvin.pictureselector.utils;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            } catch (NullPointerException e) {
                fileDir = Environment.getRootDirectory().getAbsolutePath();
            }
        } else {
            fileDir = Environment.getRootDirectory().getAbsolutePath();
        }
        fileDir += "/" + PSConstanceUtil.FOLDER_NAME + "/" + PSConstanceUtil.FROM_CAMERA;
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

    private Intent getCameraIntent(File f) {
        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    public interface OnTakePhotoSuccessListener {
        void onTakePhotoSuccess(String path);
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
}
