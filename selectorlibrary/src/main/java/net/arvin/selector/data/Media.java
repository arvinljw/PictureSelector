package net.arvin.selector.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Arrays;

/**
 * Created by arvinljw on 2020/7/16 16:13
 * Function：
 * Desc：
 */
@SuppressWarnings({"unused", "deprecation"})
public class Media implements Parcelable, Cloneable {
    private long id;
    private String mimeType;
    private long bucketId;
    private String bucketName;
    private Uri uri;
    private long size;
    private long duration;
    private int width;
    private int height;
    private long dateTaken;
    /**
     * copy from {@link android.provider.MediaStore.Files.FileColumns#DATA}
     * Absolute filesystem path to the media item on disk.
     * <p>
     * Note that apps may not have filesystem permissions to directly access
     * this path. Instead of trying to open this path directly, apps should
     * use {@link android.content.ContentResolver#openFileDescriptor(Uri, String)} to gain
     * access.
     *
     * @deprecated Apps may not have filesystem permissions to directly
     * access this path. Instead of trying to open this path
     * directly, apps should use
     * {@link android.content.ContentResolver#openFileDescriptor(Uri, String)}
     * to gain access.
     */
    @Deprecated
    private String path;

    private int choseNum;

    public static Media createMedia(long id, String mimeType, long bucketId, String bucketName, Uri uri, long size,
                                    long duration, int width, int height, long dateTaken, String path) {
        Media media = new Media();
        media.id = id;
        media.mimeType = mimeType;
        media.bucketId = bucketId;
        media.bucketName = bucketName;
        media.uri = uri;
        media.size = size;
        media.duration = duration;
        media.width = width;
        media.height = height;
        media.dateTaken = dateTaken;
        media.path = path;
        return media;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getBucketId() {
        return bucketId;
    }

    public void setBucketId(long bucketId) {
        this.bucketId = bucketId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
    }

    /**
     * copy from {@link android.provider.MediaStore.Files.FileColumns#DATA}
     * Absolute filesystem path to the media item on disk.
     * <p>
     * Note that apps may not have filesystem permissions to directly access
     * this path. Instead of trying to open this path directly, apps should
     * use {@link android.content.ContentResolver#openFileDescriptor(Uri, String)} to gain
     * access.
     *
     * @deprecated Apps may not have filesystem permissions to directly
     * access this path. Instead of trying to open this path
     * directly, apps should use
     * {@link android.content.ContentResolver#openFileDescriptor(Uri, String)}
     * to gain access.
     */
    @Deprecated
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getChoseNum() {
        return choseNum;
    }

    public void setChoseNum(int choseNum) {
        this.choseNum = choseNum;
    }

    @NonNull
    @Override
    public String toString() {
        return "Media{" +
                "bucketId=" + bucketId +
                ", bucketName='" + bucketName + '\'' +
                ", uri='" + uri + '\'' +
                ", size=" + size +
                ", dateTaken=" + dateTaken +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Media media = (Media) o;
        return id == media.id &&
                uri.equals(media.uri);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{id, uri});
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.mimeType);
        dest.writeLong(this.bucketId);
        dest.writeString(this.bucketName);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.size);
        dest.writeLong(this.duration);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.dateTaken);
    }

    public Media() {
    }

    protected Media(Parcel in) {
        this.id = in.readLong();
        this.mimeType = in.readString();
        this.bucketId = in.readLong();
        this.bucketName = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.size = in.readLong();
        this.duration = in.readLong();
        this.width = in.readInt();
        this.height = in.readInt();
        this.dateTaken = in.readLong();
    }

    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel source) {
            return new Media(source);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };

    @NonNull
    @Override
    public Media clone() {
        Media media = null;
        try {
            media = (Media) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (media == null) {
            media = new Media();
        }
        media.id = id;
        media.mimeType = mimeType;
        media.bucketId = bucketId;
        media.bucketName = bucketName;
        media.uri = uri;
        media.size = size;
        media.duration = duration;
        media.width = width;
        media.height = height;
        media.dateTaken = dateTaken;
        return media;
    }
}
