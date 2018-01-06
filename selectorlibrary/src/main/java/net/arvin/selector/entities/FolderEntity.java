package net.arvin.selector.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by arvinljw on 17/12/25 16:53
 * Function：文件夹实体
 * Desc：
 */
public class FolderEntity implements Parcelable {
    //该文件夹的名字
    private String folderName;
    //该文件夹下图片的数量
    private int count;
    //第一张图片的路径
    private String firstImagePath;
    //该文件夹下的图片列表
    private ArrayList<FileEntity> images;
    private boolean isSelected;

    public FolderEntity(String folderName, int count, String firstImagePath, ArrayList<FileEntity> images) {
        this.folderName = folderName;
        this.count = count;
        this.firstImagePath = firstImagePath;
        this.images = images;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<FileEntity> getImages() {
        return images;
    }

    public void setImages(ArrayList<FileEntity> images) {
        this.images = images;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public FolderEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.folderName);
        dest.writeInt(this.count);
        dest.writeString(this.firstImagePath);
        dest.writeTypedList(this.images);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    protected FolderEntity(Parcel in) {
        this.folderName = in.readString();
        this.count = in.readInt();
        this.firstImagePath = in.readString();
        this.images = in.createTypedArrayList(FileEntity.CREATOR);
        this.isSelected = in.readByte() != 0;
    }

    public static final Creator<FolderEntity> CREATOR = new Creator<FolderEntity>() {
        @Override
        public FolderEntity createFromParcel(Parcel source) {
            return new FolderEntity(source);
        }

        @Override
        public FolderEntity[] newArray(int size) {
            return new FolderEntity[size];
        }
    };
}
