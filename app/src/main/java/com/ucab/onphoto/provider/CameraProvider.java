package com.ucab.onphoto.provider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.ucab.onphoto.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraProvider {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    private Activity activity;
    private String currentImagePath;

    /*
        CAMERA
    */

    public CameraProvider(Activity activity) {
        this.activity = activity;
    }

    private void onCameraClicked(View v) {
        requestOpenCamera();
    }

    private void askCameraPermission() {
        activity.requestPermissions(new String[] { Manifest.permission.CAMERA },
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private boolean requestOpenCamera() {
        int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (cameraPermission == PackageManager.PERMISSION_GRANTED)
            openCamera();
        else
            askCameraPermission();

        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        Intent nativeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (nativeCamera.resolveActivity(activity.getPackageManager()) == null)
            return; // @error

        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException ex) {
            // @error
        }

        if (imageFile == null)
            return; // @error

        Uri imageURI = FileProvider.getUriForFile(
                activity,
                "com.ucab.onphoto.android.fileprovider",
                imageFile
        );

        nativeCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        nativeCamera.putExtra("android.intent.extras.CAMERA_FACING", 1);
        activity.startActivityForResult(nativeCamera, CAMERA_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ONPHOTO_" + timeStamp;
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentImagePath = image.getAbsolutePath();
        return image;
    }

    private void saveImage(String imagePath) {
        File file = new File(imagePath);

        try {
            MediaStore.Images.Media.insertImage(
                    activity.getContentResolver(),
                    file.getAbsolutePath(),
                    file.getName(),
                    "Photo taked by OnPhoto (c).");
        }
        catch (FileNotFoundException ex) {
            // @error
        }
    }

    private void onCameraResult(int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return; // @error

        //display.setImageURI(uri);
        saveImage(currentImagePath);

        // Navigate to preview
        Bundle bundle = new Bundle();
        //bundle.putString("imagePath", currentImagePath);
        bundle.putString("imagePath", Uri.fromFile(new File(currentImagePath)).toString());

        //Navigation.findNavController(.navigate(R.id.action_preview_image, bundle);
    }

}
