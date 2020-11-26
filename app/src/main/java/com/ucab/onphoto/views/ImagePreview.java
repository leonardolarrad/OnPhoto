package com.ucab.onphoto.views;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ucab.onphoto.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImagePreview#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagePreview extends Fragment {

    private static final String ARG_IMAGE_PATH = "imagePath";
    private String imagePath;
    private ImageView preview;

    public ImagePreview() {
        // Required empty public constructor
    }

    public static ImagePreview newInstance(String imagePath) {
        ImagePreview fragment = new ImagePreview();
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

        if (getArguments() != null) {
            imagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_preview, container, false);
        preview = view.findViewById(R.id.ivPreview);

        setPreviewContent(Uri.parse(imagePath));
        return view;
    }
}