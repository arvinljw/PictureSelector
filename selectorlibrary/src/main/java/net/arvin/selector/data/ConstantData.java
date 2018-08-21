package net.arvin.selector.data;

import android.os.Bundle;

import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.entities.FolderEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 17/12/25 09:44
 * Function：Key或者一些路径对的参数
 * Desc：
 */
public class ConstantData {
    public static final int REQUEST_CODE_TAKE_PHOTO = 10001;

    //回传的图片
    public static final String KEY_BACK_PICTURES = "key_back_pictures";
    public static final String KEY_BACK_VIDEOS = "key_back_videos";

    //选择类型
    public static final String KEY_TYPE_SELECT = "key_select_type";
    //选图片
    public static final int VALUE_TYPE_PICTURE = 0;
    //选视频
    public static final int VALUE_TYPE_VIDEO = 1;
    //选图片或视频
    public static final int VALUE_TYPE_PICTURE_VIDEO = 2;
    //直接打开相机
    public static final int VALUE_TYPE_CAMERA = 3;

    public static final int VALUE_SPAN_COUNT = 4;

    //是否单选
    public static final String KEY_SINGLE_SELECTION = "key_single_selection";
    public static final boolean VALUE_SINGLE_SELECTION_TRUE = true;
    public static final boolean VALUE_SINGLE_SELECTION_FALSE = false;

    //多选最大数量
    public static final String KEY_MAX_COUNT = "key_max_count";
    public static final int VALUE_COUNT_SINGLE = 1;

    //能否裁剪
    public static final String KEY_CAN_CROP = "key_can_crop";
    public static final boolean VALUE_CAN_CROP_FALSE = false;

    //是否带相机
    public static final String KEY_WITH_CAMERA = "key_with_camera";
    public static final boolean VALUE_WITH_CAMERA_TRUE = true;

    //已选中图片
    public static final String KEY_SELECTED_PICTURES = "key_selected_pictures";
    public static final ArrayList<String> VALUE_SELECTED_PICTURES_NULL = null;

    //已选中视频
    public static final String KEY_SELECTED_VIDEOS = "key_selected_videos";
    public static final ArrayList<String> VALUE_SELECTED_VIDEOS_NULL = null;

    //切换界面
    public static final String KEY_FROM_POS = "key_from_pos";
    public static final int VALUE_CHANGE_FRAGMENT_SELECTOR = 0;
    public static final int VALUE_CHANGE_FRAGMENT_REVIEW = 1;
    public static final int VALUE_CHANGE_FRAGMENT_CROP = 2;
    public static final int VALUE_CHANGE_FRAGMENT_TAKE_PHOTO = 3;
    public static final int VALUE_CHANGE_FRAGMENT_EDIT = 4;

    public static final String KEY_ONLY_SHOW_SELECTED_PIC = "key_only_show_selected_pic";
    public static final boolean VALUE_ONLY_SHOW_SELECTED_PIC_DEFAULT = false;

    public static final String KEY_CURR_ITEM = "key_curr_item";
    public static final String KEY_CURR_POSITION = "key_curr_position";

    //7.0权限授权字符串
    public static final String KEY_AUTHORITIES = "key_authorities";
    public static final String VALUE_AUTHORITIES = ".selector.provider";

    //文件夹名字
    public static final String FOLDER_NAME = "selector";
    public static final String FOLDER_CAMERA = "camera";
    public static final String FOLDER_CROP = "crop";

    //拍照新加的图片
    public static final String KEY_NEW_PIC = "key_new_pic";

    private static ArrayList<FolderEntity> sFolders;
    private static ArrayList<FileEntity> sCurrItems;
    private static ArrayList<String> sSelectedItems = new ArrayList<>();

    public static ArrayList<FolderEntity> getFolders() {
        return sFolders;
    }

    public static void setFolders(ArrayList<FolderEntity> folders) {
        ConstantData.sFolders = folders;
    }

    private static void setCurrItems(ArrayList<FileEntity> currItems) {
        ConstantData.sCurrItems = currItems;
    }

    public static ArrayList<FileEntity> getCurrItems() {
        return sCurrItems;
    }

    public static ArrayList<String> getSelectedItems() {
        return sSelectedItems;
    }

    public static void setSelectedItems(List<String> items) {
        if (items != null) {
            sSelectedItems.clear();
            sSelectedItems.addAll(items);
        }
    }

    public static void addSelectedItem(FileEntity item) {
        ConstantData.sSelectedItems.add(item.getPath());
    }

    public static void removeItem(FileEntity item) {
        int removePos = 0;
        for (int i = 0, size = ConstantData.sSelectedItems.size(); i < size; i++) {
            if (item.getPath().equals(ConstantData.sSelectedItems.get(i))) {
                removePos = i;
                break;
            }
        }
        ConstantData.sSelectedItems.remove(removePos);
    }

    public static Bundle toSelectorBundle(Bundle baseBundle, String newPic, int fromPos) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        bundle.putString(ConstantData.KEY_NEW_PIC, newPic);
        bundle.putInt(ConstantData.KEY_FROM_POS, fromPos);
        return bundle;
    }

    public static Bundle toReviewBundle(Bundle baseBundle, ArrayList<FileEntity> items, int currPos) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        setCurrItems(items);
        bundle.putInt(KEY_CURR_POSITION, currPos);
        return bundle;
    }

    public static Bundle toReviewBundle(Bundle baseBundle, ArrayList<FileEntity> items) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        bundle.putBoolean(KEY_ONLY_SHOW_SELECTED_PIC, true);
        setCurrItems(items);
        return bundle;
    }

    public static Bundle toCropBundle(Bundle baseBundle, FileEntity item) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        bundle.putParcelable(KEY_CURR_ITEM, item);
        return bundle;
    }

    public static Bundle toTakePhotoBundle(Bundle baseBundle) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        return bundle;
    }

    public static Bundle toEditBundle(Bundle baseBundle, FileEntity item) {
        Bundle bundle = new Bundle();
        bundle.putAll(baseBundle);
        bundle.putParcelable(KEY_CURR_ITEM, item);
        return bundle;
    }
}
