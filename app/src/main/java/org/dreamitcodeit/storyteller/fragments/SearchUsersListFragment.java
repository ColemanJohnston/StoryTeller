package org.dreamitcodeit.storyteller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by neeharmb on 7/20/17.
 */

public class SearchUsersListFragment extends StoryListFragment  {

    private String query;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        query = getArguments().getString("query");
        showNoResults();
        fetchUserData();
        return v;
    }

    public void fetchUserData(){

        Firebase ref = new Firebase(Config.FIREBASE_URl);

      //  String author_id = ref.child("users").equalTo(query);

        ref.child("stories").orderByChild("userName").equalTo(query).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                hideNoResults();
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