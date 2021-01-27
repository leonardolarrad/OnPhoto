package com.ucab.onphoto.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class ImageUploadRequest extends MultipartRequest {

    private String key;
    private String expiration;
    private byte[] image;

    public ImageUploadRequest(String url,
                              String expiration,
                              String key,
                              byte[] image,
                              Response.Listener<NetworkResponse> listener,
                              Response.ErrorListener errorListener) {

        super(
                Request.Method.POST,
                url + "?expiration=" + expiration + "&key=" + key,
                listener,
                errorListener
        );

        this.expiration = expiration;
        this.key = key;
        this.image = image;
    }

    public String getExtendedUrl()    {
        return getUrl() + "?expiration=" + expiration + "&key=" + key;
    }

    @Override
    protected Map<String, DataPart> getByteData() {

        Map<String, DataPart> params = new HashMap<>();
        long name = System.currentTimeMillis();
        params.put("image", new DataPart(name + ".jpeg", image));

        return params;
    }
}

