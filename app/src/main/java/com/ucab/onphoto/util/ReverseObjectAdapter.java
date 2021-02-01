package com.ucab.onphoto.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ucab.onphoto.R;
import com.ucab.onphoto.model.ReverseSearchObject;

import java.util.List;

public class ReverseObjectAdapter extends ArrayAdapter<ReverseSearchObject> {

    private final Context context;
    private final List<ReverseSearchObject> matches;

    public ReverseObjectAdapter(Context context,
                                List<ReverseSearchObject> matches) {

        super(context, R.layout.cardview_reverse_object, matches);
        this.context = context;
        this.matches = matches;
    }

    @NonNull
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.cardview_reverse_object, parent, false);
        }

        ReverseSearchObject item = matches.get(position);

        TextView match = (TextView) view.findViewById(R.id.match);
        TextView description = (TextView) view.findViewById(R.id.description);

        match.setText(item.title);
        description.setText(item.description);

        return view;
    }
}
