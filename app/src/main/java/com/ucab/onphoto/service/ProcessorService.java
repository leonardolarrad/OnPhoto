package com.ucab.onphoto.service;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.ucab.onphoto.model.ReverseSearchImage;
import com.ucab.onphoto.model.ReverseSearchObject;
import com.ucab.onphoto.model.ReverseSearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class ProcessorService {

    public static class Response {

        @Nullable
        public final ReverseSearchResult searchResult;

        public final String message;

        public Response(@Nullable ReverseSearchResult searchResult, String message) {
            this.searchResult = searchResult;
            this.message = message;
        }

        public boolean success() {
            return searchResult != null;
        }
    }

    public interface Callback {
        void onProcess(Response response);
    }

    private final RequestQueue requestQueue;
    private final StorageService storageService;
    private final ReverseService reverseService;
    private final Callback callback;

    public ProcessorService(RequestQueue requestQueue, Callback callback) {

        this.requestQueue = requestQueue;
        this.callback = callback;
        this.storageService = new StorageService(requestQueue);
        this.reverseService = new ReverseService(requestQueue);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public StorageService getStorageService() {
        return storageService;
    }

    public ReverseService getReverseService() {
        return reverseService;
    }

    public void process(Bitmap image) {
        byte[] imageData = compress(image);
        store(imageData); /* store first, then do reverse search */
    }

    protected byte[] compress(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    /* STORAGE SERVICE */

    private void store(byte[] data) {
        storageService.upload(
                data,
                this::onStorageResponse,
                this::onStorageError
        );
    }

    private void onStorageResponse(NetworkResponse response) {
        Log.i("StorageResponse", "" + new String(response.data));

        try {
            String imageUrl = buildStoreImage(new JSONObject(new String(response.data)));
            Log.i("JSON", "JSON object builded. URL: " + imageUrl);

            reverse(imageUrl);

        } catch (JSONException e) {
            Log.e("JSON Error", "Error parsing JSONObject");
        }
    }

    private void onStorageError(VolleyError error)    {
        Log.e("Storage Request Error", "" + error.getMessage());

        callback.onProcess(new Response(
                null,
                "No se pudo cargar la imagen al servidor. Compruebe su conexión a internet e intente nuevamente."
        ));
    }

    /* REVERSE SEARCH SERVICE */

    private void reverse(String imageUrl) {
        reverseService.reverse(
                imageUrl,
                this::onReverseResponse,
                this::onReverseError
        );
    }

    private void onReverseResponse(JSONObject response) {
        Log.i("ReverseResponse", response.toString());
        try {
            ReverseSearchResult result =
                    buildReverseSearchResult(response);

            callback.onProcess(new Response(result, "Busqueda exitosa"));

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onProcess(new Response(null,
                    "No se han encontrado resultados en la busqueda."));
        }

    }

    private void onReverseError(VolleyError error) {
        error.printStackTrace();
        callback.onProcess(new Response(null,
                "Ha ocurrido un error inesperado en la busqueda."));
    }

    protected String buildStoreImage(JSONObject json) throws JSONException     {
        return json.getJSONObject("data")
                   .getString("url");
    }

    protected ReverseSearchResult buildReverseSearchResult(JSONObject json) throws JSONException {

        JSONObject reverseResult = json.getJSONObject("reverse_image_results");

        /* Parse match objects */
        JSONArray jsonArr = reverseResult.getJSONArray("pages_with_matching_images");
        ReverseSearchObject[] match = new ReverseSearchObject[jsonArr.length()];

        for (int i = 0; i < jsonArr.length(); ++i) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);

            match[i] = new ReverseSearchObject(
                    jsonObj.getString("title"),
                    jsonObj.getString("description"),
                    jsonObj.getString("url")
            );
        }

        /* Parse low match objects */
        jsonArr = reverseResult.getJSONArray("organic");
        ReverseSearchObject[] lowMatch = new ReverseSearchObject[jsonArr.length()];

        for (int i = 0; i < jsonArr.length(); ++i) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);

            lowMatch[i] = new ReverseSearchObject(
                    jsonObj.getString("title"),
                    jsonObj.getString("description"),
                    jsonObj.getString("url")
            );
        }

        /* Parse similar images */
        jsonArr = reverseResult.getJSONArray("similar_images");
        ReverseSearchImage[] similarImages = new ReverseSearchImage[jsonArr.length()];

        for(int i = 0; i < jsonArr.length(); ++i) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);

            similarImages[i] = new ReverseSearchImage(
                    jsonObj.getString("url"),
                    jsonObj.getString("thumbnail")
            );
        }

        return new ReverseSearchResult(match, lowMatch, similarImages);
    }

}
