package net.arvin.selector.data;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.LongSparseArray;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import net.arvin.selector.SelectorHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 2020/7/14 16:15
 * Function：
 * Desc：
 */
public final class MediaManager {
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    public static final String BUCKET_ID = "bucket_id";
    public static final String BUCKET_DISPLAY_NAME = "bucket_display_name";
    public static final String DURATION = "duration";
    public static final String DATE_TAKEN = "datetaken";
    public static final String DATE_TAKEN_ORDER_BY = "datetaken DESC";

    public static final long All_BUCKET_ID = Long.MIN_VALUE;
    public static final long All_VIDEO_BUCKET_ID = Long.MIN_VALUE + 1;

    public static final int FIRST_PAGE = 0;
    private static final int PAGE_SIZE = 100;

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.MIME_TYPE,
            BUCKET_ID,
            BUCKET_DISPLAY_NAME,
            MediaStore.Files.FileColumns.SIZE,
            DURATION,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            DATE_TAKEN,
            MediaStore.Files.FileColumns.DATA
    };

    private static final String SELECTION_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? or " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) and " +
            MediaStore.Files.FileColumns.SIZE + ">0";

    private static final String SELECTION_PAGE_ALL = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? or " +
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?) and " +
            BUCKET_ID + "=? and " +
            MediaStore.Files.FileColumns.SIZE + ">0";

    private static final String SELECTION_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? and " +
            MediaStore.Files.FileColumns.SIZE + ">0";

    private static final String SELECTION_PAGE_SINGLE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? and " +
            BUCKET_ID + "=? and " +
            MediaStore.Files.FileColumns.SIZE + ">0";

    private static final String SELECTION_SINGLE_NO_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? and " +
            MediaStore.Files.FileColumns.SIZE + ">0 and " +
            MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'";

    private static final String SELECTION_PAGE_SINGLE_NO_GIF = MediaStore.Files.FileColumns.MEDIA_TYPE + "=? and " +
            BUCKET_ID + "=? and " +
            MediaStore.Files.FileColumns.SIZE + ">0 and " +
            MediaStore.MediaColumns.MIME_TYPE + "!='image/gif'";

    public static void loadFolder(final Activity activity, final SelectorParams params, final MediaCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity == null) {
                    return;
                }
                Cursor cursor = activity.getContentResolver().query(QUERY_URI, PROJECTION,
                        getSelection(params, All_BUCKET_ID), getSelectionArgs(params, All_BUCKET_ID), DATE_TAKEN_ORDER_BY);
                if (cursor == null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.mediaFolderCallback(null);
                        }
                    });
                    return;
                }
                final LongSparseArray<MediaFolder> folders = new LongSparseArray<>();
                MediaFolder allMediaFolder = new MediaFolder(All_BUCKET_ID, SelectorHelper.textEngine.titleAll(activity, params.mediaType));
                folders.put(allMediaFolder.getBucketId(), allMediaFolder);
                MediaFolder allVideoFolder = null;
                if (params.mediaType == MediaType.ALL) {
                    allVideoFolder = new MediaFolder(Long.MIN_VALUE + 1, SelectorHelper.textEngine.titleAllVideo(activity));
                    folders.put(allVideoFolder.getBucketId(), allVideoFolder);
                }
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[0]));
                    String mimeType = cursor.getString(cursor.getColumnIndex(PROJECTION[1]));
                    long bucketId = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[2]));
                    String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[3]));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[4]));
                    long during = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[5]));
                    int width = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[6]));
                    int height = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[7]));
                    long dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[8]));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[9]));

                    MediaFolder folder = folders.get(bucketId);
                    if (folder == null) {
                        folder = new MediaFolder(bucketId, bucketName);
                        folders.put(bucketId, folder);
                    }

                    if (folder.getCount() == 0) {
                        Media media = Media.createMedia(id, mimeType, bucketId, bucketName,
                                getContentUri(id, mimeType), size, during, width, height, dateTaken, path);
                        folder.setFirstMedia(media);
                    }
                    folder.setCount(folder.getCount() + 1);

                    if (allMediaFolder.getCount() == 0) {
                        Media media = Media.createMedia(id, mimeType, bucketId, bucketName,
                                getContentUri(id, mimeType), size, during, width, height, dateTaken, path);
                        allMediaFolder.setFirstMedia(media);
                    }
                    allMediaFolder.setCount(allMediaFolder.getCount() + 1);

                    if (MediaType.isVideo(mimeType) && allVideoFolder != null) {
                        if (allVideoFolder.getCount() == 0) {
                            Media media = Media.createMedia(id, mimeType, bucketId, bucketName,
                                    getContentUri(id, mimeType), size, during, width, height, dateTaken, path);
                            allVideoFolder.setFirstMedia(media);
                        }
                        allVideoFolder.setCount(allVideoFolder.getCount() + 1);
                    }
                }
                cursor.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.mediaFolderCallback(getFolders(folders));
                    }
                });
            }
        }).start();
    }

    public static void loadMedia(final Activity activity, final long inBucketId, final int page, final SelectorParams params, final MediaCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (activity == null) {
                    return;
                }

                Cursor cursor;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    Bundle queryArgs = createSqlQueryBundle(getSelection(params, inBucketId), getSelectionArgs(params, inBucketId), DATE_TAKEN_ORDER_BY, getLimit(page));
                    cursor = activity.getContentResolver().query(QUERY_URI, PROJECTION, queryArgs, null);
                } else {
                    cursor = activity.getContentResolver().query(QUERY_URI, PROJECTION,
                            getSelection(params, inBucketId), getSelectionArgs(params, inBucketId), getOrderAndLimit(page));
                }
                if (cursor == null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.mediasCallback(inBucketId, page, null);
                        }
                    });
                    return;
                }
                final List<Media> medias = new ArrayList<>();
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[0]));
                    String mimeType = cursor.getString(cursor.getColumnIndex(PROJECTION[1]));
                    long bucketId = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[2]));
                    String bucketName = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[3]));
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[4]));
                    long during = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[5]));
                    int width = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[6]));
                    int height = cursor.getInt(cursor.getColumnIndexOrThrow(PROJECTION[7]));
                    long dateTaken = cursor.getLong(cursor.getColumnIndexOrThrow(PROJECTION[8]));
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(PROJECTION[9]));

                    Media media = Media.createMedia(id, mimeType, bucketId, bucketName,
                            getContentUri(id, mimeType), size, during, width, height, dateTaken, path);

                    if (inBucketId == All_VIDEO_BUCKET_ID) {
                        if (MediaType.isVideo(mimeType)) {
                            medias.add(media);
                        }
                    } else {
                        medias.add(media);
                    }
                }
                cursor.close();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.mediasCallback(inBucketId, page, medias);
                    }
                });
            }
        }).start();
    }

    private static String getSelection(SelectorParams params, long bucketId) {
        boolean isAll = bucketId == All_BUCKET_ID || bucketId == All_VIDEO_BUCKET_ID;
        MediaType type = params.mediaType;
        if (type == MediaType.ALL) {
            if (isAll) {
                return SELECTION_ALL;
            }
            return SELECTION_PAGE_ALL;
        }
        if (isAll) {
            return type.hasGif() ? SELECTION_SINGLE : SELECTION_SINGLE_NO_GIF;
        }
        return type.hasGif() ? SELECTION_PAGE_SINGLE : SELECTION_PAGE_SINGLE_NO_GIF;
    }

    private static String[] getSelectionArgs(SelectorParams params, long bucketId) {
        boolean isAll = bucketId == All_BUCKET_ID || bucketId == All_VIDEO_BUCKET_ID;
        MediaType type = params.mediaType;
        if (type == MediaType.ALL) {
            if (isAll) {
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                        String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
            }
            return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                    String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                    String.valueOf(bucketId)};
        } else if (type == MediaType.IMAGE || type == MediaType.IMAGE_NO_GIF) {
            if (isAll) {
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)};
            }
            return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE), String.valueOf(bucketId)};
        } else {
            if (isAll) {
                return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)};
            }
            return new String[]{String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO), String.valueOf(bucketId)};
        }
    }

    private static List<MediaFolder> getFolders(LongSparseArray<MediaFolder> map) {
        List<MediaFolder> folders = new ArrayList<>();
        for (int i = 0; i < map.size(); i++) {
            MediaFolder folder = map.get(map.keyAt(i));
            if (folder.getCount() > 0) {
                folders.add(folder);
            }
        }
        return folders;
    }

    private static String getOrderAndLimit(int page) {
        return DATE_TAKEN_ORDER_BY + " limit " + PAGE_SIZE + " offset " + (page * PAGE_SIZE);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private static Bundle createSqlQueryBundle(String selection, String[] selectionArgs,
                                               String sortOrder, String limit) {
        Bundle queryArgs = new Bundle();
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection);
        queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, sortOrder);
        queryArgs.putString(ContentResolver.QUERY_ARG_SQL_LIMIT, limit);
        return queryArgs;
    }

    private static String getLimit(int page) {
        return PAGE_SIZE + " offset " + (page * PAGE_SIZE);
    }

    private static Uri getContentUri(long id, String mimeType) {
        Uri contentUri;
        if (MediaType.isImage(mimeType)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (MediaType.isVideo(mimeType)) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = QUERY_URI;
        }
        return ContentUris.withAppendedId(contentUri, id);
    }
}
