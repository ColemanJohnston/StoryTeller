package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.dreamitcodeit.storyteller.Config;
import org.dreamitcodeit.storyteller.Story;

/**
 * Created by neeharmb on 7/21/17.
 */

public class AllStoriesPagerAdapter extends SmartFragmentStatePagerAdapter {

    private String tabTitles[] = new String[]{"All","Personal", "Historical"
    , "Fictional"};
    Context context;
    String query;

    public AllStoriesPagerAdapter(FragmentManager fm, Context context, String query){
        super(fm);
        this.context = context;
        this.query = query;
    }

    //return title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    //return the total number of fragments
    @Override
    public int getCount() {
        return 4;
    }

    //return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {

        // search "by title" tab is selected
        if(position == 0){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "all");
            fragment.setArguments(bundle);
            return fragment;
        }

        // search "by users" tab is selected
        if(position == 1){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "personal");
            fragment.setArguments(bundle);
            return fragment;
        }

        if(position == 2){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "historical");
            fragment.setArguments(bundle);
            return fragment;
        }

        if(position == 3){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "fictional");
            fragment.setArguments(bundle);
            return fragment;
        }

        // search fails
        return null;
    }

    /**
     * Created by neeharmb on 7/20/17.
     */

    public static class SearchUsersListFragment extends StoryListFragment  {

        private String query;

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View v = super.onCreateView(inflater, container, savedInstanceState);

            query = getArguments().getString("query");

            fetchUserData();
            return v;
        }

        public void fetchUserData(){

            Firebase ref = new Firebase(Config.FIREBASE_URl);

            //  String author_id = ref.child("users").equalTo(query);

            ref.child("stories").orderByChild("userName").equalTo(query).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                    Story story = dataSnapshot.getValue(Story.class);
                    storyAdapter.add(0,story); //TODO: make sure this is the best way to add these
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }


            });
        }
    }
}
