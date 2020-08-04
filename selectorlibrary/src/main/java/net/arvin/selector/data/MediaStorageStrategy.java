package net.arvin.selector.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arvinljw on 2020/8/1 18:23
 * Function：
 * Desc：媒体存储策略
 * 目前只是图片存储
 */
public class MediaStorageStrategy implements Parcelable {
    public boolean isPublic;
    public String authority;
    public String directory;

    public static MediaStorageStrategy publicStrategy(String authority, String directory) {
        return new MediaStorageStrategy(true, authority, directory);
    }

    public static MediaStorageStrategy privateStrategy(String authority) {
        return new MediaStorageStrategy(false, authority, null);
    }

    public static MediaStorageStrategy privateStrategy(String authority, String directory) {
        return new MediaStorageStrategy(false, authority, directory);
    }

    private MediaStorageStrategy(boolean isPublic, String authority, String directory) {
        this.isPublic = isPublic;
        this.authority = authority;
        this.directory = directory;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isPublic ? (byte) 1 : (byte) 0);
        dest.writeString(this.authority);
        dest.writeString(this.directory);
    }

    protected MediaStorageStrategy(Parcel in) {
        this.isPublic = in.readByte() != 0;
        this.authority = in.readString();
        this.directory = in.readString();
    }

    public static final Parcelable.Creator<MediaStorageStrategy> CREATOR = new Parcelable.Creator<MediaStorageStrategy>() {
        @Override
        public MediaStorageStrategy createFromParcel(Parcel source) {
            return new MediaStorageStrategy(source);
        }

        @Override
        public MediaStorageStrategy[] newArray(int size) {
            return new MediaStorageStrategy[size];
        }
    };
}
