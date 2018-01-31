package net.arvin.selector.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import net.arvin.selector.data.ConstantData;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.entities.FolderEntity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 17/12/27 15:45
 * Function：
 * Desc：
 */
public class PSUtil {
    public static String createFileUrl(String path) {
        return "file://" + path;
    }

    public static ArrayList<FileEntity> getSelectedPictureFiles(List<FolderEntity> folders) {
        ArrayList<FileEntity> selectedPictures = new ArrayList<>();
        for (int i = 0; i < folders.size(); i++) {
            if (i == 0) {//跳过全部图片，避免重复
                continue;
            }
            FolderEntity folder = folders.get(i);
            for (FileEntity item : folder.getImages()) {
                if (item.isSelected()) {//之后再加一个类型判断
                    selectedPictures.add(item);
                }
            }
        }
        return selectedPictures;
    }

    public static ArrayList<String> getSelectedVideos(List<FolderEntity> folders) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        ArrayList<String> selectedVideos = new ArrayList<>();
        return selectedVideos;
    }

    public static String getAuthorities(Activity activity) {
        return activity.getPackageName() + ConstantData.VALUE_AUTHORITIES;
    }

    public static String getRootDir() {
        String rootDir;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            } catch (NullPointerException e) {
                rootDir = Environment.getRootDirectory().getAbsolutePath();
            }
        } else {
            rootDir = Environment.getRootDirectory().getAbsolutePath();
        }
        return rootDir + "/" + ConstantData.FOLDER_NAME + "/";
    }

    public static void saveBitmap(Bitmap bitmap, String path, String imagePath) {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        BufferedOutputStream bos = null;
        try {
            File myCaptureFile = new File(imagePath);
            bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos)) {
                bos.flush();
                bos.close();
                Log.i("TAG", "保存成功~");
            }
            if (bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void scanFile(Context context, String path) {
        MediaScannerConnection.scanFile(context, new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }
}
