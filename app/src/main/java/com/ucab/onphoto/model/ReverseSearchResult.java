package com.ucab.onphoto.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ReverseSearchResult implements Parcelable {

    public final ReverseSearchObject[] match;
    public final ReverseSearchObject[] lowMatch;
    public final ReverseSearchImage[] similarImages;

    public ReverseSearchResult(ReverseSearchObject[] match,
                               ReverseSearchObject[] lowMatch,
                               ReverseSearchImage[] similarImages) {
        this.match = match;
        this.lowMatch = lowMatch;
        this.similarImages = similarImages;
    }

    public ReverseSearchResult(Parcel source) {
        this.match = (ReverseSearchObject[])
                source.readParcelableArray(ReverseSearchObject.class.getClassLoader());
        this.lowMatch = (ReverseSearchObject[])
                source.readParcelableArray(ReverseSearchObject.class.getClassLoader());
        this.similarImages = (ReverseSearchImage[])
                source.readParcelableArray(ReverseSearchImage.class.getClassLoader());
    }

    public static final Creator<ReverseSearchResult> CREATOR = new Creator<ReverseSearchResult>() {
        @Override
        public ReverseSearchResult createFromParcel(Parcel source) {
            return new ReverseSearchResult(source);
        }

        @Override
        public ReverseSearchResult[] newArray(int size) {
            return new ReverseSearchResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(match, flags);
        dest.writeParcelableArray(lowMatch, flags);
        dest.writeParcelableArray(similarImages, flags);
    }

    public boolean hasMatches() {
        return match.length > 0;
    }

    public boolean hasLowMatches() {
        return lowMatch.length > 0;
    }

    public boolean hasSimilarImages() {
        return similarImages.length > 0;
    }
}
