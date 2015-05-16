package com.diandian.coolco.emilie.model;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Size;

public class Image implements Parcelable {
    private String name;
    private String description;
    private String downloadUrl;
    private String shoppingUrl;

    private Point size;

    public Point getSize() {
        return size;
    }

    public void setSize(Point size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getShoppingUrl() {
        return shoppingUrl;
    }

    public void setShoppingUrl(String shoppingUrl) {
        this.shoppingUrl = shoppingUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.shoppingUrl);
        dest.writeParcelable(this.size, 0);
    }

    public Image() {
    }

    private Image(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.downloadUrl = in.readString();
        this.shoppingUrl = in.readString();
        this.size = in.readParcelable(Point.class.getClassLoader());
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
