package org.dreamitcodeit.storyteller.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import org.dreamitcodeit.storyteller.AuthorActivity;
import org.dreamitcodeit.storyteller.MapActivity;
import org.dreamitcodeit.storyteller.NarrowStoryAdapter;
import org.dreamitcodeit.storyteller.R;
import org.dreamitcodeit.storyteller.Story;

import java.util.List;

/**
 * Created by colemanmav on 7/18/17.
 */

public class StoriesDialogFragment extends DialogFragment {
    private Button btPlus;
    private RecyclerView rvStories;
    private List<Story> stories;
    private NarrowStoryAdapter storyAdapter;

    public StoriesDialogFragment(){}

    public static StoriesDialogFragment newInstance(List<Story> stories){
        StoriesDialogFragment frag = new StoriesDialogFragment();
        frag.stories = stories;
        Bundle args = new Bundle();
        args.putString("title", "title");
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stories_dialog, container);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvStories = (RecyclerView) view.findViewById(R.id.rvStories);
        btPlus = (Button) view.findViewById(R.id.btPlus);
        storyAdapter = new NarrowStoryAdapter(stories);
        rvStories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvStories.setAdapter(storyAdapter);

        if(getContext() instanceof MapActivity){
            MapActivity mapActivity = (MapActivity) getContext();
            mapActivity.setMapActivityListener(new MapActivity.MapActivityListener() {
                @Override
                public void onStoryAdded(Story story) {
                    Story element = stories.get(0);
                    if(story.getLatitude() == element.getLatitude() && story.getLongitude() == element.getLongitude()){
                        storyAdapter.add(0,story);
                    }
                }
            });
        }

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Bold.ttf");
        btPlus.setTypeface(typeface);

        btPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getContext() instanceof MapActivity){//making sure that this dialog box was started by mapActivity TODO: find out if this is bad style
                    MapActivity mapActivity = (MapActivity) getContext();
                    mapActivity.startAuthorActivity(new LatLng(stories.get(0).getLatitude(), stories.get(0).getLongitude()));
                }
                else{
                    //TODO: check if we still need this
                    Intent i = new Intent(getContext(), AuthorActivity.class);
                    i.putExtra("lat", stories.get(0).getLatitude());
                    i.putExtra("long", stories.get(0).getLongitude());
                    getContext().startActivity(i);
                }
            }
        });


    }

    @Override
    public void onDestroyView() {
        rvStories.setAdapter(null);//TODO: test if this lets the RecyclerView go. (I just did what stack overflow told me to)
        super.onDestroyView();
    }
}