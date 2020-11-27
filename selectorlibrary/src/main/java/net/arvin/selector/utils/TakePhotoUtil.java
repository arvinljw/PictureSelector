package net.arvin.selector.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import net.arvin.selector.data.MediaStorageStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by arvinljw on 2020/8/1 17:29
 * Function：
 * Desc：
 */
public class TakePhotoUtil {
    private static String photoPath;

    public static Uri takePhoto(Activity activity, MediaStorageStrategy storageStrategy, int requestCode) {
        Intent captureIntent = getCaptureIntent();
        if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {
            Uri photoUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                photoUri = createImageUri(activity);
            } else {
                File photoFile = null;
                try {
                    photoFile = createImageFile(activity, storageStrategy);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (photoFile == null) {
                    return null;
                }
                photoPath = photoFile.getAbsolutePath();
                photoUri = FileProvider.getUriForFile(activity, storageStrategy.authority, photoFile);
            }
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ResolveInfo> resInfoList = activity.getPackageManager()
                        .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    activity.grantUriPermission(packageName, photoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
            activity.startActivityForResult(captureIntent, requestCode);
            return photoUri;
        }
        return null;
    }

    private static Uri createImageUri(Activity activity) {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return activity.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    private static Intent getCaptureIntent() {
        return new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    }

    private static File createImageFile(Context context, MediaStorageStrategy storageStrategy) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);

        File storageDir;
        if (storageStrategy != null && storageStrategy.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) storageDir.mkdirs();
        } else {
            storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (storageStrategy != null && storageStrategy.directory != null) {
            storageDir = new File(storageDir, storageStrategy.directory);
            if (!storageDir.exists()) storageDir.mkdirs();
        }

        File tempFile = new File(storageDir, imageFileName);

        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    public static String getPhotoPath() {
        return photoPath;
    }

    public static void scanPath(Context context, Uri photoUri, String path, MediaScanner.ScanCompletedCallback callback) {
        if (path == null && photoUri == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            callback.onScanCompleted(path, photoUri);
            return;
        }
        boolean isApplication = context instanceof Application;
        if (!isApplication) {
            context = context.getApplicationContext();
        }
        new MediaScanner(context, path, callback);
    }
}
