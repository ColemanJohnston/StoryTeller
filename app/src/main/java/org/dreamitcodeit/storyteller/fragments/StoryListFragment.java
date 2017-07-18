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

import java.util.ArrayList;

/**
 * Created by colemanmav on 7/17/17.
 */

public class StoryListFragment extends Fragment {

    StoryAdapter storyAdapter;
    ArrayList<Story> stories;
    RecyclerView rvStories;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories_list, container, false);
        rvStories = (RecyclerView) v.findViewById(R.id.rvStories);
        stories = new ArrayList<>();
        storyAdapter = new StoryAdapter(stories);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStories.setAdapter(storyAdapter);

        return v;
    }
}
