package com.ucab.onphoto.service;

import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.ucab.onphoto.request.ImageReverseRequest;

import org.json.JSONObject;

public class ReverseService {

    protected final static String URL = "https://app.zenserp.com/api/v2/search";
    protected final static String KEY = "8cffa550-6062-11eb-9919-dd2ad9f58b6f";
    protected final static String GL = "VE";
    protected final static String HL = "es";

    protected final static String[] KEY_POOL = { "", "" };

    /* Timeout in ms 180.000 ms = 3 min */
    protected final static int TIMEOUT = 180000;

    RequestQueue requestQueue;

    public ReverseService(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void reverse(String imageUrl,
                        Response.Listener<JSONObject> responseCallback,
                        Response.ErrorListener errorCallback) {

        ImageReverseRequest request = new ImageReverseRequest(
                URL,
                generateKey(),
                imageUrl,
                GL,
                HL,
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

        Log.i("ReverseRequest", request.toString());
        requestQueue.add(request);
    }

    private String generateKey() {
        return KEY;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
