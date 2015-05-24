package com.diandian.coolco.emilie.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "image")
public class Image implements Parcelable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String description;
    @DatabaseField
    private String downloadUrl;
    @DatabaseField
    private String shoppingUrl;
    @DatabaseField
    private int width;
    @DatabaseField
    private int height;

    private boolean collected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.downloadUrl);
        dest.writeString(this.shoppingUrl);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeByte(collected ? (byte) 1 : (byte) 0);
    }

    public Image() {
    }

    private Image(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.downloadUrl = in.readString();
        this.shoppingUrl = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
        this.collected = in.readByte() != 0;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (id != image.id) return false;
        if (width != image.width) return false;
        if (height != image.height) return false;
        if (collected != image.collected) return false;
        if (name != null ? !name.equals(image.name) : image.name != null) return false;
        if (description != null ? !description.equals(image.description) : image.description != null)
            return false;
        if (downloadUrl != null ? !downloadUrl.equals(image.downloadUrl) : image.downloadUrl != null)
            return false;
        return !(shoppingUrl != null ? !shoppingUrl.equals(image.shoppingUrl) : image.shoppingUrl != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
        result = 31 * result + (shoppingUrl != null ? shoppingUrl.hashCode() : 0);
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + (collected ? 1 : 0);
        return result;
    }
}
