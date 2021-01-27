package com.ucab.onphoto.service;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.ucab.onphoto.request.ImageUploadRequest;

public class StorageService {

    private static final String KEY = "2a3f2feb414dc78b16675c2afdbbd709";
    private static final String URL = "https://api.imgbb.com/1/upload";
    private static final String EXPIRATION = "600";

    /* Timeout in ms 180.000 ms = 3 min */
    protected final static int TIMEOUT = 180000;

    RequestQueue requestQueue;

    public StorageService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void upload(byte[] image,
                       Response.Listener<NetworkResponse> responseCallback,
                       Response.ErrorListener errorCallback) {

        ImageUploadRequest request = new ImageUploadRequest(
                URL,
                EXPIRATION,
                KEY,
                image,
                responseCallback,
                errorCallback
        );

        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return TIMEOUT;
            }

            @Override
            public int getCurrentRetryCount() {
                return TIMEOUT;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                // empty by default
            }
        });

        Log.i("StorageRequest", request.toString());
        requestQueue.add(request);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
