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
    protected final static String KEY = "f38d38e0-650e-11eb-a258-674e1f223519";
    protected final static String GL = "VE";
    protected final static String HL = "es";

    protected final static String[] KEY_POOL = {
            "f38d38e0-650e-11eb-a258-674e1f223519",
            "6807dd30-6512-11eb-aa41-41812a1ef7d9",
            "cd7602b0-6512-11eb-8726-354d7792d9bd"
    };

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
        int max = KEY_POOL.length;
        int min = 1;

        String key = KEY_POOL[((int)(Math.random() * (max - min + 1) + min)) - 1];
        Log.e("KeyPool", key);

        return key;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
