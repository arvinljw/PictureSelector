package net.arvin.selector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.arvin.selector.data.ConstantData;
import net.arvin.selector.uis.SelectorActivity;
import net.arvin.selector.utils.PSUtil;

import java.util.ArrayList;

/**
 * Created by arvinljw on 17/12/25 09:54
 * Function：
 * Desc：选择图片或者视频的外观类，可直接调用
 */
public class SelectorHelper extends ConstantData {

    /**
     * 拍照，是否裁剪
     */
    public static void takePhoto(Activity activity, boolean canCrop, int requestCode) {
        select(activity, VALUE_TYPE_CAMERA, VALUE_SINGLE_SELECTION_TRUE, canCrop, VALUE_COUNT_SINGLE,
                VALUE_WITH_CAMERA_TRUE, VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_PICTURES_NULL, requestCode);
    }

    /**
     * 单选图片，不裁剪，带相机
     */
    public static void selectPicture(Activity activity, int requestCode) {
        selectPicture(activity, VALUE_CAN_CROP_FALSE, VALUE_WITH_CAMERA_TRUE, requestCode);
    }

    /**
     * 单选图片
     *
     * @param canCrop    选择是否裁剪
     * @param withCamera 选择是否带相机
     */
    public static void selectPicture(Activity activity, boolean canCrop, boolean withCamera, int requestCode) {
        select(activity, VALUE_TYPE_PICTURE, VALUE_SINGLE_SELECTION_TRUE, canCrop, VALUE_COUNT_SINGLE, withCamera,
                VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 多选图片，带相机
     *
     * @param maxCount 多选的最大数量
     */
    public static void selectPictures(Activity activity, int maxCount, int requestCode) {
        selectPictures(activity, maxCount, VALUE_WITH_CAMERA_TRUE, VALUE_SELECTED_PICTURES_NULL, requestCode);
    }

    /**
     * 多选图片
     *
     * @param maxCount         多选的最大数量
     * @param withCamera       选择是否带相机
     * @param selectedPictures 已选中的图片
     */
    public static void selectPictures(Activity activity, int maxCount, boolean withCamera, ArrayList<String> selectedPictures, int requestCode) {
        select(activity, VALUE_TYPE_PICTURE, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, maxCount, withCamera,
                selectedPictures, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 单选视频
     *
     * @param withCamera 选择是否带相机
     */
    public static void selectVideo(Activity activity, boolean withCamera, int requestCode) {
        select(activity, VALUE_TYPE_VIDEO, VALUE_SINGLE_SELECTION_TRUE, VALUE_CAN_CROP_FALSE, VALUE_COUNT_SINGLE, withCamera,
                VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 多选视频，带相机
     *
     * @param maxCount 多选的最大数量
     */
    public static void selectVideos(Activity activity, int maxCount, int requestCode) {
        selectVideos(activity, maxCount, VALUE_WITH_CAMERA_TRUE, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 多选视频
     *
     * @param maxCount       多选的最大数量
     * @param withCamera     选择是否带相机
     * @param selectedVideos 已选中的视频
     */
    public static void selectVideos(Activity activity, int maxCount, boolean withCamera, ArrayList<String> selectedVideos, int requestCode) {
        select(activity, VALUE_TYPE_VIDEO, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, maxCount, withCamera,
                VALUE_SELECTED_PICTURES_NULL, selectedVideos, requestCode);
    }

    /**
     * 单选图片或视频，不能裁剪
     *
     * @param withCamera 选择是否带相机
     */
    public static void selectPictureAndVideo(Activity activity, boolean withCamera, int requestCode) {
        select(activity, VALUE_TYPE_PICTURE_VIDEO, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, VALUE_COUNT_SINGLE, withCamera,
                VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 多选图片或视频，带相机
     *
     * @param maxCount 多选的最大数量
     */
    public static void selectPicturesAndVideos(Activity activity, int maxCount, int requestCode) {
        select(activity, VALUE_TYPE_PICTURE_VIDEO, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, maxCount, VALUE_WITH_CAMERA_TRUE,
                VALUE_SELECTED_PICTURES_NULL, VALUE_SELECTED_VIDEOS_NULL, requestCode);
    }

    /**
     * 多选图片或视频
     *
     * @param maxCount         多选的最大数量
     * @param withCamera       选择是否带相机
     * @param selectedPictures 已选中的图片
     * @param selectedVideos   已选中的视频
     */
    public static void selectPicturesAndVideos(Activity activity, int maxCount, boolean withCamera, ArrayList<String> selectedPictures,
                                               ArrayList<String> selectedVideos, int requestCode) {
        select(activity, VALUE_TYPE_PICTURE_VIDEO, VALUE_SINGLE_SELECTION_FALSE, VALUE_CAN_CROP_FALSE, maxCount, withCamera,
                selectedPictures, selectedVideos, requestCode);
    }

    /**
     * 去选择图片或视频
     *
     * @param type             可选值{@link #VALUE_TYPE_PICTURE}{@link #VALUE_TYPE_VIDEO}{@link #VALUE_TYPE_PICTURE_VIDEO}{@link #VALUE_TYPE_CAMERA}
     * @param singleSelection  是否单选
     * @param canCrop          是否裁剪
     * @param maxCount         最大数量
     * @param withCamera       是否带相机
     * @param selectedPictures 已选中图片
     * @param selectedVideos   已选中视频
     */
    private static void select(Activity activity, int type, boolean singleSelection, boolean canCrop, int maxCount, boolean withCamera,
                               ArrayList<String> selectedPictures, ArrayList<String> selectedVideos, int requestCode) {
        Intent intent = new Intent(activity, SelectorActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE_SELECT, type);
        bundle.putBoolean(KEY_SINGLE_SELECTION, singleSelection);
        bundle.putBoolean(KEY_CAN_CROP, canCrop);
        bundle.putInt(KEY_MAX_COUNT, maxCount);
        bundle.putBoolean(KEY_WITH_CAMERA, withCamera);
        if (selectedPictures != VALUE_SELECTED_PICTURES_NULL) {
            bundle.putStringArrayList(KEY_SELECTED_PICTURES, selectedPictures);
        }
        if (selectedVideos != VALUE_SELECTED_VIDEOS_NULL) {
            bundle.putStringArrayList(KEY_SELECTED_VIDEOS, selectedVideos);
        }
        bundle.putString(KEY_AUTHORITIES, PSUtil.getAuthorities(activity));
        intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);
    }
}
