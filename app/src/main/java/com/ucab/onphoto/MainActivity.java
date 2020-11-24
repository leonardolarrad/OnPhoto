package com.ucab.onphoto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    ImageView display;
    Button camera;
    Button gallery;

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
        askCameraPermissions();
    }

    private void onGalleryClicked(View v) {

    }

    private void askCameraPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST_CODE);
        }
        else {
            openCamera();
        }
    }

    private void askGalleryPermissions() {

    }

    private void openCamera() {
        Intent nativeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(nativeCamera, CAMERA_REQUEST_CODE);
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
            case CAMERA_REQUEST_CODE:

                if (data == null)
                    return;

                Bitmap capture = (Bitmap) data.getExtras().get("data");
                display.setImageBitmap(capture);

                break;
        }

    }



}