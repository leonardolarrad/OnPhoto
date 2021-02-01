package com.ucab.onphoto.views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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



    private void onEditButtonClick(View v) {
        tryEdit();
    }

    private void tryEdit() {
        Intent editIntent = new Intent(Intent.ACTION_EDIT);
        editIntent.setDataAndType(Uri.parse(imagePath), "image/*");
        editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //startActivity(editIntent);
        startActivity(Intent.createChooser(editIntent, null));
    }

    private void onProcessButtonClick(View v)  {
        try {
            Bitmap image =
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