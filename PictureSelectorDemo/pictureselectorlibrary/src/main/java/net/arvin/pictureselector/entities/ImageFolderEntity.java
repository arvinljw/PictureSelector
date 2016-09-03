package net.arvin.pictureselector.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * created by arvin on 16/8/28 22:29
 * email：1035407623@qq.com
 */
public class ImageFolderEntity implements Parcelable {
    //该文件夹的名字
    private String folderName;
    //该文件夹下图片的数量
    private int count;
    //第一张图片的路径
    private String firstImagePath;
    //该文件夹下的图片列表
    private List<ImageEntity> images;

    public ImageFolderEntity(String folderName, int count, String firstImagePath, List<ImageEntity> images) {
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

    public List<ImageEntity> getImages() {
        return images;
    }

    public void setImages(List<ImageEntity> images) {
        this.images = images;
    }


    public ImageFolderEntity() {
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
    }

    protected ImageFolderEntity(Parcel in) {
        this.folderName = in.readString();
        this.count = in.readInt();
        this.firstImagePath = in.readString();
        this.images = in.createTypedArrayList(ImageEntity.CREATOR);
    }

    public static final Creator<ImageFolderEntity> CREATOR = new Creator<ImageFolderEntity>() {
        @Override
        public ImageFolderEntity createFromParcel(Parcel source) {
            return new ImageFolderEntity(source);
        }

        @Override
        public ImageFolderEntity[] newArray(int size) {
            return new ImageFolderEntity[size];
        }
    };
}
