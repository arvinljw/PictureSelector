package net.arvin.pictureselector.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * created by arvin on 16/8/28 22:22
 * email：1035407623@qq.com
 */
public class ImageEntity implements Parcelable {
    //文件路径
    private String path;
    //文件被加入到Media中的时间
    private long time;
    //文件的大小
    private long size;
    //是否被选中
    private boolean isSelected;

    public ImageEntity(String path) {
        this.path = path;
        this.isSelected = false;
    }

    public ImageEntity(String path, boolean isSelected) {
        this.path = path;
        this.isSelected = isSelected;
    }

    public ImageEntity(String path, long time, long size) {
        this.path = path;
        this.time = time;
        this.size = size;
        this.isSelected = false;
    }

    public ImageEntity(String path, long time, long size, boolean isSelected) {
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

    protected ImageEntity(Parcel in) {
        this.path = in.readString();
        this.time = in.readLong();
        this.size = in.readLong();
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {
        @Override
        public ImageEntity createFromParcel(Parcel source) {
            return new ImageEntity(source);
        }

        @Override
        public ImageEntity[] newArray(int size) {
            return new ImageEntity[size];
        }
    };
}
