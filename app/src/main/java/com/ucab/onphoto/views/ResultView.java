package com.ucab.onphoto.views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucab.onphoto.R;
import com.ucab.onphoto.model.ReverseSearchObject;
import com.ucab.onphoto.model.ReverseSearchResult;
import com.ucab.onphoto.util.ReverseObjectAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultView#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultView extends Fragment {

    /* Args bundle */
    private static final String ARG_SEARCH_RESULT = "searchResult";
    private ReverseSearchResult searchResult;

    /* Views */
    private ListView objectList;
    private TextView resultTitle;
    private Button btnHome;

    /* Adapters */
    private ReverseObjectAdapter objectAdapter;

    public ResultView() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param searchResult reverse search result.
     * @return A new instance of fragment ProcessView.
     */
    public static ResultView newInstance(ReverseSearchResult searchResult) {
        ResultView fragment = new ResultView();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SEARCH_RESULT, searchResult);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            searchResult = getArguments().getParcelable(ARG_SEARCH_RESULT);

        List<ReverseSearchObject> objects = new ArrayList<>();
        objects.addAll(Arrays.asList(searchResult.lowMatch));
        objects.addAll(Arrays.asList(searchResult.match));

        objectAdapter = new ReverseObjectAdapter(getContext(), objects);

        for (ReverseSearchObject match : searchResult.match)
            Log.i("Match", match.title);

        for (ReverseSearchObject lowMatch : searchResult.lowMatch)
            Log.i("LowMatch", lowMatch.title);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        objectList = view.findViewById(R.id.reverse_search_object);
        objectList.setAdapter(objectAdapter);

        View viewGroup = inflater.inflate(R.layout.cardview_footer, objectList, false);
        objectList.addFooterView(viewGroup);

        btnHome = viewGroup.findViewById(R.id.btnHome);
        resultTitle = view.findViewById(R.id.resultTitle);


        if (!searchResult.hasMatches() && !searchResult.hasLowMatches()) {
            resultTitle.setText("No se han encontrado resultados en la búsqueda.");
            Toast.makeText(getActivity(), "Compruebe que la imagen contenga un objeto de ferretería a identificar.", Toast.LENGTH_LONG).show();
        }

        btnHome.setOnClickListener(this::onHomeClick);
        return view;
    }

    private void onHomeClick(View v) {
        Navigation.findNavController(getView())
                .navigate(R.id.action_go_home);
    }
}