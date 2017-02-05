package net.arvin.pictureselector.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import net.arvin.pictureselector.entities.ImageEntity;
import net.arvin.pictureselector.uis.PictureSelectorActivity;
import net.arvin.pictureselector.uis.TakePhotoAndCropActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * created by arvin on 16/8/30 22:36
 * email：1035407623@qq.com
 */
public class PSConfigUtil {
    private static final String endName = ".takephoto.fileprovider";
    private static String sAuthorities;

    private static PSConfigUtil sInstance;
    /**
     * 多选图片上限
     */
    private int mMaxCount;
    /**
     * 是否能拍照
     */
    private boolean canTakePhoto;
    /**
     * 是否能裁剪
     */
    private boolean canCrop;
    /**
     * 选中的数量
     */
    private int mSelectedCount;

    /**
     * 选中的文件夹的位置
     */
    private int mSelectedFolderPos;

    private PSConfigUtil() {
        this.mMaxCount = 9;
        this.canTakePhoto = true;
        this.canCrop = false;
        this.mSelectedCount = 0;
        this.mSelectedFolderPos = 0;
    }

    public static PSConfigUtil getInstance() {
        if (sInstance == null) {
            sInstance = new PSConfigUtil();
        }
        return sInstance;
    }

    public static String getAuthorities() {
        return sAuthorities;
    }

    public static void setAuthorities(String sAuthorities) {
        PSConfigUtil.sAuthorities = sAuthorities;
    }

    /**
     * @return 是否能继续选中图片
     */
    public boolean canAdd() {
        return mMaxCount > mSelectedCount;
    }

    public boolean canReview() {
        return mMaxCount != 1;
    }

    /**
     * 清除本次数据
     */
    public void clear() {
        sInstance = null;
    }

    public int getMaxCount() {
        return mMaxCount;
    }

    public PSConfigUtil setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
        return getInstance();
    }

    public boolean isCanTakePhoto() {
        return canTakePhoto;
    }

    public PSConfigUtil setCanTakePhoto(boolean canTakePhoto) {
        this.canTakePhoto = canTakePhoto;
        return getInstance();
    }

    public boolean isCanCrop() {
        return canCrop;
    }

    /**
     * @param canCrop 只有当maxCount == 1时有效
     */
    public PSConfigUtil setCanCrop(boolean canCrop) {
        this.canCrop = canCrop;
        return getInstance();
    }

    public int getSelectedCount() {
        return mSelectedCount;
    }

    public int addSelectedCount(int count) {
        mSelectedCount += count;
        return mSelectedCount;
    }

    public PSConfigUtil setSelectedCount(int count) {
        mSelectedCount = count;
        return getInstance();
    }

    public int getSelectedFolderPos() {
        return mSelectedFolderPos;
    }

    public void setSelectedFolderPos(int selectedFolderPos) {
        this.mSelectedFolderPos = selectedFolderPos;
    }

    public void showSelector(Activity activity, int requestCode, ArrayList<ImageEntity> selectedImages) {
        initAuthorities(activity);
        Intent intent = new Intent(activity, PictureSelectorActivity.class);
        if (selectedImages != null) {
//            超过了也可以这样处理,截取某个区间的图片作为选择图片,例如[selectedImages.size() - getMaxCount(),selectedImages.size())
//            if (selectedImages.size() > getMaxCount()) {
//                for (int i = selectedImages.size() - getMaxCount(); i < selectedImages.size(); i++) {
//                    realSelectedImages.add(selectedImages.get(i));
//                }
//            }else{
//                realSelectedImages.addAll(selectedImages);
//            }
            if (selectedImages.size() > getMaxCount()){
                throw new RuntimeException("selectedImages' size can not more than maxCount!");
            }
        }
        intent.putParcelableArrayListExtra(PSConstanceUtil.PASS_SELECTED, selectedImages);
        activity.startActivityForResult(intent, requestCode);
    }

    public void showSelector(Activity activity, int requestCode) {
        showSelector(activity, requestCode, null);
    }

    public void showTakePhotoAndCrop(Activity activity, int requestCode) {
        initAuthorities(activity);
        Intent intent = new Intent(activity, TakePhotoAndCropActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    private void initAuthorities(Activity activity) {
        if (TextUtils.isEmpty(PSConfigUtil.sAuthorities)) {
            sAuthorities = activity.getPackageName() + endName;
        }
    }

    /**
     * 清除缓存:包含裁剪的图片和通过PictureSelector拍照所得的图片
     */
    public static void clearCache() {
        PSCropUtil.clear();
        PSTakePhotoUtil.clear();
    }
}
