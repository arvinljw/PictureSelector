package net.arvin.selector.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

/**
 * Created by arvinljw on 2020/8/3 10:38
 * Function：
 * Desc：
 */
public class MediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private String path;
    private MediaScannerConnection msc;

    private ScanCompletedCallback callback;

    public MediaScanner(Context context, String path, ScanCompletedCallback callback) {
        this.path = path;
        this.callback = callback;
        msc = new MediaScannerConnection(context, this);
        msc.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        msc.scanFile(path, null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        msc.disconnect();
        if (callback != null) {
            callback.onScanCompleted(path, uri);
        }
        Log.d("MediaScanner", "scanCompleted path is " + path + ";uri is " + uri);
    }


    public interface ScanCompletedCallback {
        void onScanCompleted(String path, Uri uri);
    }
}
