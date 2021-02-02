package com.ucab.onphoto.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;

import com.ucab.onphoto.R;

public class GalleryProvider {

    public static final int GALLERY_PERMISSION_REQUEST_CODE = 103;
    public static final int GALLERY_REQUEST_CODE = 104;

    private Activity activity;
    private String currentImagePath;

    private void onGalleryClicked(View v) {
        requestOpenGallery();
    }

    private void askGalleryPermission() {
        activity.requestPermissions(
                new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                GALLERY_PERMISSION_REQUEST_CODE
        );
    }

    private boolean requestOpenGallery() {
        int galleryPermission = ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (galleryPermission == PackageManager.PERMISSION_GRANTED)
            openGallery();
        else
            askGalleryPermission();

        return galleryPermission == PackageManager.PERMISSION_GRANTED;
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, new  String[] { MediaStore.Audio.Media.DATA }, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void openGallery() {
        Intent nativeGallery = new Intent();
        nativeGallery.setType("image/*");
        nativeGallery.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(nativeGallery, "Selecciona una imagen"), GALLERY_REQUEST_CODE);
    }

    private void onGalleryResult(int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return; // @error

        Uri imageUri = data.getData();
        currentImagePath = imageUri.getPath();

        // System.out.println(imageUri.getPath());
        // System.out.println(imageUri.getEncodedPath());
        // System.out.println(getRealPathFromURI(imageUri));

        currentImagePath = getRealPathFromURI(imageUri);

        // Navigate to preview
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", imageUri.toString());
        //Navigation.findNavController(getView()).navigate(R.id.action_preview_image, bundle);
    }

}
