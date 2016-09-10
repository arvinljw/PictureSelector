package net.arvin.pictureselector.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import net.arvin.pictureselector.views.ClipImageLayout;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * created by arvin on 16/9/4 15:24
 * email：1035407623@qq.com
 */
public class PSCropUtil {

    // 读取图像的旋转度
    public static int readBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * @param path 创建图片
     */
    public static Bitmap createBitmap(String path) {
        return getImage(path, 480, 480);
    }

    public static Bitmap getImage(String srcPath, float maxOutWidth, float maxOutHeight) {
        if (TextUtils.isEmpty(srcPath)) {
            return null;
        }
        if (srcPath.startsWith("file://")) {
            srcPath = srcPath.substring("file://".length());
        }
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        int be = 1;
        if (w > h && w > maxOutWidth) {
            be = (int) (newOpts.outWidth / maxOutWidth);
        } else if (w < h && h > maxOutHeight) {
            be = (int) (newOpts.outHeight / maxOutHeight);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        return BitmapFactory.decodeFile(srcPath, newOpts);
    }

    // 旋转图片
    public static Bitmap rotateBitmap(int angle, Bitmap bitmap) {
        if (angle == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
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

    @SuppressWarnings("ConstantConditions")
    public static String getCropFilePath() {
        return PSConstanceUtil.getRootDIr() + PSConstanceUtil.CROP_FILE_NAME;
    }

    public static Observable<String> crop(ClipImageLayout imgClip) {
        return Observable.just(imgClip).map(new Func1<ClipImageLayout, Bitmap>() {
            @Override
            public Bitmap call(ClipImageLayout clipImageLayout) {
                return clipImageLayout.clip();
            }
        }).flatMap(new Func1<Bitmap, Observable<String>>() {
            @Override
            public Observable<String> call(Bitmap bitmap) {
                String path = getCropFilePath();
                String imagePath = path + "/" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                PSCropUtil.saveBitmap(bitmap, path, imagePath);
                return Observable.just(imagePath);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 清除裁剪图片的缓存
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void clear() {
        PSConstanceUtil.clear(getCropFilePath());
    }

}
