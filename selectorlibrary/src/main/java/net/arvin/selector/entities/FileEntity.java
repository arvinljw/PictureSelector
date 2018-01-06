package net.arvin.selector.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arvinljw on 17/12/25 16:49
 * Function：文件实体
 * Desc：
 */
public class FileEntity implements Parcelable {
    //文件路径
    private String path;
    //文件被加入到Media中的时间
    private long time;
    //文件的大小
    private long size;
    //是否被选中
    private boolean isSelected;

    public FileEntity(String path) {
        this.path = path;
        this.isSelected = false;
    }

    public FileEntity(String path, boolean isSelected) {
        this.path = path;
        this.isSelected = isSelected;
    }

    public FileEntity(String path, long time, long size) {
        this.path = path;
        this.time = time;
        this.size = size;
        this.isSelected = false;
    }

    public FileEntity(String path, long time, long size, boolean isSelected) {
        this.path = path;
        this.time = time;
        this.size = size;
        this.isSelected = isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeLong(this.time);
        dest.writeLong(this.size);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected FileEntity(Parcel in) {
        this.path = in.readString();
        this.time = in.readLong();
        this.size = in.readLong();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<FileEntity> CREATOR = new Creator<FileEntity>() {
        @Override
        public FileEntity createFromParcel(Parcel source) {
            return new FileEntity(source);
        }

        @Override
        public FileEntity[] newArray(int size) {
            return new FileEntity[size];
        }
    };
}
