package com.ucab.onphoto.model;

public class ReverseSearchImage {

    public final String url;
    public final String image; /* base 64 encoded */

    public ReverseSearchImage(String url, String image) {
        this.url = url;
        this.image = image;
    }
}
