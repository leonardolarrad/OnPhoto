package com.ucab.onphoto.request;

import androidx.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class ImageReverseRequest extends JsonObjectRequest {

    private String url;
    private String key;
    private String imageUrl;
    private String country;
    private String languague;

    public ImageReverseRequest(String url,
                               String key,
                               String imageUrl,
                               String country,
                               String languague,
                               Response.Listener<JSONObject> listener,
                               @Nullable Response.ErrorListener errorListener) {

        super(
                Method.GET,
                url + "?apikey="+key +"&image_url="+imageUrl+"&gl="+country+"&hl="+languague,
                null,
                listener,
                errorListener
        );

        this.url = url;
        this.key = key;
        this.imageUrl = imageUrl;
        this.country = country;
        this.languague = languague;
    }

    public String getKey() {
        return key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguague() {
        return languague;
    }

    public String getUrlExtended() {
        return url + "?apikey="+key +"&image_url="+imageUrl+"&gl="+country+"&hl"+languague;
    }
}
