package com.ucab.onphoto.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReverseSearchObject implements Parcelable {

    public final String title;
    public final String description;
    public final String url;

    public ReverseSearchObject(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public ReverseSearchObject(Parcel source) {
        this.title = source.readString();
        this.description = source.readString();
        this.url = source.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public ReverseSearchObject createFromParcel(Parcel source) {
            return new ReverseSearchObject(source);
        }

        public ReverseSearchObject[] newArray(int size) {
            return new ReverseSearchObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
    }
}
