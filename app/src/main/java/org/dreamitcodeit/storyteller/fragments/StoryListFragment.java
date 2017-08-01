package org.dreamitcodeit.storyteller.fragments;

import android.graphics.Typeface;
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
    TextView tvNoResults;
    ImageView ivNoResults;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stories_list, container, false);
        rvStories = (RecyclerView) v.findViewById(R.id.rvStories);
        tvNoResults = (TextView) v.findViewById(R.id.tvNoResults);
        ivNoResults = (ImageView) v.findViewById(R.id.ivNoResults);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");

        tvNoResults.setTypeface(typefaceBold);

        stories = new ArrayList<>();
        storyAdapter = new StoryAdapter(stories);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStories.setAdapter(storyAdapter);
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
