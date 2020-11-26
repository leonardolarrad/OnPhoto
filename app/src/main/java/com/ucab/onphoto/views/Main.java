package com.ucab.onphoto.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ucab.onphoto.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends Fragment {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE = 103;

    ImageView display;
    Button camera;
    Button gallery;

    String currentImagePath;

    public Main() {
        // Required empty public constructor
    }

    public static Main newInstance() {
        Main fragment = new Main();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        display = view.findViewById(R.id.ivCapture);
        camera = view.findViewById(R.id.btnCamera);
        gallery = view.findViewById(R.id.btnGallery);

        camera.setOnClickListener((View v) -> onCameraClicked(v));
        gallery.setOnClickListener((View v) -> onGalleryClicked(v));

        return view;
    }


    private void onCameraClicked(View v) {

        requestOpenCamera();
    }

    private void onGalleryClicked(View v) {
        openGallery();
    }

    private void askGalleryPermission() {

    }

    private void askCameraPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[] { Manifest.permission.CAMERA },
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    private boolean requestOpenCamera() {
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);

        if (cameraPermission == PackageManager.PERMISSION_GRANTED)
            openCamera();
        else
            askCameraPermission();

        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {
        System.out.println("A1");
        Intent nativeCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (nativeCamera.resolveActivity(getActivity().getPackageManager()) == null)
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
                getActivity(),
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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
                    getActivity().getContentResolver(),
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
        bundle.putString("imagePath", currentImagePath);
        Navigation.findNavController(getView()).navigate(R.id.action_preview_image, bundle);
    }

    private void openGallery() {
        Intent nativeGallery = new Intent();
        nativeGallery.setType("image/*");
        nativeGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(nativeGallery, "Selecciona una imagen"), GALLERY_REQUEST_CODE);
    }

    private void onGalleryResult(int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return; // @error

        Uri imageUri = data.getData();
        currentImagePath = imageUri.getPath();

        // Navigate to preview
        Bundle bundle = new Bundle();
        bundle.putString("imagePath", currentImagePath);
        Navigation.findNavController(getView()).navigate(R.id.action_preview_image, bundle);
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                onCameraResult(resultCode, data);
                break;

            case GALLERY_REQUEST_CODE:
                onGalleryResult(resultCode, data);
        }

    }
}