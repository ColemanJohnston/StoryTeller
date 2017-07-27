package org.dreamitcodeit.storyteller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.dreamitcodeit.storyteller.R;
import org.dreamitcodeit.storyteller.Story;
import org.dreamitcodeit.storyteller.StoryAdapter;
import org.dreamitcodeit.storyteller.models.SearchLocationAdapter;

import java.util.ArrayList;

/**
 * Created by mariadeangelis on 7/26/17.
 */

public class LocationListFragment extends Fragment {

    SearchLocationAdapter searchLocationAdapter;
    ArrayList<String> locations;
    RecyclerView rvStories;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories_list, container, false);
        rvStories = (RecyclerView) v.findViewById(R.id.rvStories);
        locations = new ArrayList<>();
        searchLocationAdapter = new SearchLocationAdapter(locations);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStories.setAdapter(searchLocationAdapter);

        for(int i = 0;i < locations.size(); ++i){

        }
        return v;
    }
}
