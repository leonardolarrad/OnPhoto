package com.ucab.onphoto.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReverseSearchImage implements Parcelable {

    public final String url;
    public final String image; /* base 64 encoded */

    public ReverseSearchImage(String url, String image) {
        this.url = url;
        this.image = image;
    }

    public ReverseSearchImage(Parcel source) {
        this.url = source.readString();
        this.image = source.readString();
    }

    public static final Creator<ReverseSearchImage> CREATOR = new Creator<ReverseSearchImage>() {
        @Override
        public ReverseSearchImage createFromParcel(Parcel source) {
            return new ReverseSearchImage(source);
        }

        @Override
        public ReverseSearchImage[] newArray(int size) {
            return new ReverseSearchImage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString("url");
        dest.writeString("image");
    }
}
