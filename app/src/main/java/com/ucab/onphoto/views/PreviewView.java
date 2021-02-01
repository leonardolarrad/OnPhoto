package com.ucab.onphoto.views;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.ucab.onphoto.R;
import com.ucab.onphoto.model.ReverseSearchObject;
import com.ucab.onphoto.service.ProcessorService;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreviewView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreviewView extends Fragment {

    /* Args bundle */
    private static final String ARG_IMAGE_PATH = "imagePath";
    private String imagePath;

    private Bitmap bitmap;

    /* Intent request codes */
    private static final int EDITOR_REQUEST_CODE = 105;
    private boolean edited = false;

    /* View buttons and picture box */
    private ImageView preview;
    private Button edit;
    private Button process;

    private ProgressDialog progressDialog;

    /* Services */
    ProcessorService processorService;

    public PreviewView() {
        // Required empty public constructor
    }

    public static PreviewView newInstance(String imagePath) {
        PreviewView fragment = new PreviewView();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_PATH, imagePath);
        fragment.setArguments(args);
        return fragment;
    }

    private void setPreviewContent(Bitmap bitmap) {
        preview.setImageBitmap(bitmap);
    }

    private void setPreviewContent(String imagePath) {
        if (preview == null)
            return;

        preview.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    private void setPreviewContent(Uri uri) {
        preview.setImageURI(uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        processorService = new ProcessorService(Volley.newRequestQueue(getActivity()), this::onProcessResponse);
        progressDialog = new ProgressDialog(getActivity());

        if (getArguments() != null) {
            imagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        preview = view.findViewById(R.id.ivPreview);
        edit = view.findViewById(R.id.btnEdit);
        process = view.findViewById(R.id.btnProcess);

        edit.setOnClickListener(this::onEditButtonClick);
        process.setOnClickListener(this::onProcessButtonClick);

        setPreviewContent(Uri.parse(imagePath));
        return view;
    }

    /* IMAGE EDITOR */

    private void onEditButtonClick(View v) {
        tryEdit();
    }

    private void tryEdit() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        edited = true;

        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(Uri.parse(imagePath), "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(editIntent, null));
    }

    private void onEditorResult( int resultCode, @Nullable Intent data) {
        Log.i("EditorResult", "Has result");

        if (resultCode != Activity.RESULT_OK && data == null)
            return; // @error

        Bitmap image = (Bitmap) data.getExtras().get("data");
        setPreviewContent(image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EDITOR_REQUEST_CODE)
            onEditorResult(resultCode, data);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getActivity().getContentResolver().
                query(contentURI, new  String[] { MediaStore.Audio.Media.DATA }, null, null, null);

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

    public void lastPhoto() {
        Log.i("LastPhoto", "Taking last photo");
        // Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_ADDED,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getActivity().getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_ADDED + " DESC");

        // Put it in the image view
        if (cursor.moveToFirst()) {

            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);

            if (imageFile.exists()) {
                //Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                //setPreviewContent(bm);
                //imagePath = imageFile.getPath();
                Log.e("Path", imagePath);
                Log.e("Path2", Uri.fromFile(imageFile).getPath());
                Log.e("Path3", getRealPathFromURI(Uri.fromFile(imageFile)));
                Log.e("Path4", imageFile.getAbsolutePath());
                Log.e("Path5", "file://" + imageFile.getAbsolutePath());
                Log.e("Path6", imageLocation);


                setPreviewContent(BitmapFactory.decodeFile(imageLocation));
                imagePath = "file://" + imageFile.getAbsolutePath();
            }
        }

        edited = false;
        cursor.close();
    }

    @Override
    public void onStart() {
        Log.e("onStart", "onResume of LoginFragment. Edited: " + edited);
        super.onStart();

        int galleryPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE);

        Log.i("Permissions", "Has sotrage permission: " + (galleryPermission == PackageManager.PERMISSION_GRANTED));

        if (edited)
            lastPhoto();
    }

    /* IMAGE PROCESSOR */

    private void onProcessButtonClick(View v)  {
        try {
            Bitmap image = (bitmap != null) ? bitmap :
                    MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(imagePath));

            processorService.process(image);

            progressDialog.setMessage("Procesando ...");
            progressDialog.show();

        } catch (Exception e) {
            Log.e("Bitmap Error", "Bad content resolver ?");
        }
    }

    private void onProcessResponse(ProcessorService.Response response) {
        progressDialog.dismiss();

        if (!response.success()) {
            Toast.makeText(getActivity(), response.message, Toast.LENGTH_LONG).show();
            return;
        }

        /* Navigate to Result View */
        Bundle bundle = new Bundle();
        bundle.putParcelable("searchResult", response.searchResult);

        Navigation.findNavController(getView()).navigate(R.id.action_process_image, bundle);
    }
}