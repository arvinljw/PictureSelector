package net.arvin.selector.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by arvinljw on 2020/7/14 17:34
 * Function：
 * Desc：
 */
public class SelectorParams implements Parcelable {
    public static final int SPAN_COUNT = 4;
    public static final int MAX_SIZE = 9;

    //选择数量
    public int chooseSize;
    //选择的媒体类型
    public MediaType mediaType;
    //样式
    public int style;

    public MediaStorageStrategy storageStrategy;

    public void setChooseSize(int chooseSize) {
        this.chooseSize = chooseSize;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setStorageStrategy(MediaStorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    public SelectorParams() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.chooseSize);
        dest.writeInt(this.mediaType == null ? -1 : this.mediaType.ordinal());
        dest.writeInt(this.style);
        dest.writeParcelable(this.storageStrategy, flags);
    }

    protected SelectorParams(Parcel in) {
        this.chooseSize = in.readInt();
        int tmpMediaType = in.readInt();
        this.mediaType = tmpMediaType == -1 ? null : MediaType.values()[tmpMediaType];
        this.style = in.readInt();
        this.storageStrategy = in.readParcelable(MediaStorageStrategy.class.getClassLoader());
    }

    public static final Creator<SelectorParams> CREATOR = new Creator<SelectorParams>() {
        @Override
        public SelectorParams createFromParcel(Parcel source) {
            return new SelectorParams(source);
        }

        @Override
        public SelectorParams[] newArray(int size) {
            return new SelectorParams[size];
        }
    };
}
