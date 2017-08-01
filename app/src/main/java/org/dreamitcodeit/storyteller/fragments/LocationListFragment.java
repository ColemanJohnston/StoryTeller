package org.dreamitcodeit.storyteller.fragments;

import android.location.Address;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dreamitcodeit.storyteller.R;
import org.dreamitcodeit.storyteller.models.SearchLocationAdapter;

import java.util.ArrayList;

/**
 * Created by mariadeangelis on 7/26/17.
 */

public class LocationListFragment extends Fragment {

    SearchLocationAdapter searchLocationAdapter;
    ArrayList<Address> locations;
    RecyclerView rvStories;
    TextView tvNoResults;
    ImageView ivNoResults;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories_list, container, false);
        rvStories = (RecyclerView) v.findViewById(R.id.rvStories);
        tvNoResults = (TextView) v.findViewById(R.id.tvNoResults);
        ivNoResults = (ImageView) v.findViewById(R.id.ivNoResults);
        locations = new ArrayList<Address>();
        searchLocationAdapter = new SearchLocationAdapter(locations);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStories.setAdapter(searchLocationAdapter);

        return v;
    }

    public void showNoResults(){
        ivNoResults.setVisibility(View.VISIBLE);
        tvNoResults.setVisibility(View.VISIBLE);
    }

    public void hideNoResults(){
        ivNoResults.setVisibility(View.GONE);
        tvNoResults.setVisibility(View.GONE);
    }

}
