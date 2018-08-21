package net.arvin.selector.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import net.arvin.selector.R;
import net.arvin.selector.entities.FileEntity;
import net.arvin.selector.entities.FolderEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by arvinljw on 17/12/25 16:58
 * Function：获取本地的图片视频数据
 * Desc：通过content provider获取
 */
public class FileData {

    public static void getImageFolderData(final Activity context, final DataCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final ArrayList<FolderEntity> folders = new ArrayList<>();

                //图集列表
                LinkedHashMap<String, ArrayList<FileEntity>> foldersMap = new LinkedHashMap<>();
                //所有图片列表
                String allKey = context.getResources().getString(R.string.ps_all_image);
                foldersMap.put(allKey, new ArrayList<FileEntity>());
                Cursor cursor = getImageCursor(context);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        //获取当前游标所指的图片信息
                        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        long time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        FileEntity entity = new FileEntity(path, time, size);

                        if (entity.getSize() / 1024 < 10) {
                            continue;
                        }

                        ArrayList<String> selectedItems = ConstantData.getSelectedItems();
                        for (String item : selectedItems) {
                            if (item.equals(path)) {
                                entity.setSelected(true);
                            }
                        }
                        
                        foldersMap.get(allKey).add(entity);

                        //将当前图片加入到相应的文件图集中
                        File parentFile = new File(path).getParentFile();
                        if (foldersMap.containsKey(parentFile.getName())) {
                            foldersMap.get(parentFile.getName()).add(entity);
                        } else {
                            ArrayList<FileEntity> images = new ArrayList<>();
                            images.add(entity);
                            foldersMap.put(parentFile.getName(), images);
                        }
                    }
                    cursor.close();
                }

                for (Map.Entry<String, ArrayList<FileEntity>> entry : foldersMap.entrySet()) {
                    ArrayList<FileEntity> files = entry.getValue();
                    if (files.size() > 0) {
                        folders.add(new FolderEntity(entry.getKey(), files.size(), files.get(0).getPath(), files));
                    }
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.dataCallback(folders);
                    }
                });
            }
        }).start();
    }

    private static Cursor getImageCursor(Context context) {
        String[] supportMineTypes = getSupportMineTypes();
        return context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                getSelection(supportMineTypes), supportMineTypes, MediaStore.Images.Media.DATE_MODIFIED + " desc");
    }

    private static String getSelection(String[] supportMineTypes) {
        StringBuilder selection = new StringBuilder();
        for (int i = 0; i < supportMineTypes.length; i++) {
            selection.append(MediaStore.Images.Media.MIME_TYPE + "=?");
            if (i != supportMineTypes.length - 1) {
                selection.append(" or ");
            }
        }
        return selection.toString();
    }

    private static String[] getSupportMineTypes() {
        return new String[]{"image/jpeg", "image/png"};
    }

    public interface DataCallback {
        void dataCallback(ArrayList<FolderEntity> data);
    }
}
