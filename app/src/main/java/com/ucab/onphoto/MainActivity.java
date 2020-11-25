package com.ucab.onphoto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    ImageView display;
    Button camera;
    Button gallery;

    String currentImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen

        setContentView(R.layout.activity_main);

        display = findViewById(R.id.ivCapture);
        camera = findViewById(R.id.btnCamera);
        gallery = findViewById(R.id.btnGallery);

        camera.setOnClickListener((View v) -> onCameraClicked(v));
        gallery.setOnClickListener((View v) -> onGalleryClicked(v));
    }

    private void onCameraClicked(View v) {
        requestOpenCamera();
    }

    private void onGalleryClicked(View v) {

    }

    private void askGalleryPermissions() {

    }

    private void askCameraPermission() {
        ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA },
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private boolean requestOpenCamera() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (cameraPermission == PackageManager.PERMISSION_GRANTED)
            openCamera();
        else
            askCameraPermission();

        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        System.out.println("A1");
        Intent nativeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (nativeCamera.resolveActivity(getPackageManager()) == null)
            return; // @error

        File imageFile = null;
        try {
            imageFile = createImageFile();
        } catch (IOException ex) {
            // @error
        }

        if (imageFile == null)
            return; // @error

        System.out.println("A2");
        Uri imageURI = FileProvider.getUriForFile(
                this,
                "com.ucab.onphoto.android.fileprovider",
                imageFile
        );

        System.out.println("A3");
        nativeCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(nativeCamera, CAMERA_REQUEST_CODE);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "ONPHOTO_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
                    getContentResolver(),
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

        // Move to preview
    }

    private void openGallery() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: onCameraResult(resultCode, data); break;
        }

    }



}