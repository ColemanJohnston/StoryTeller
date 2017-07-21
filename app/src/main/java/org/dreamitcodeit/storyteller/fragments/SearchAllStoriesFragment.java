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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.dreamitcodeit.storyteller.Config;
import org.dreamitcodeit.storyteller.Story;

/**
 * Created by neeharmb on 7/21/17.
 */

public class SearchAllStoriesFragment extends StoryListFragment {

    private String tag;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        tag = getArguments().getString("tag");

        fetchUserData();
        return v;
    }

    public void fetchUserData(){

        Firebase ref = new Firebase(Config.FIREBASE_URl);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userName = currentUser.getEmail();

        // TODO: FIX POPULATING ALL TAB once database is restored

        ref.orderByChild(tag).equalTo(true).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Story story = dataSnapshot.getValue(Story.class);
                storyAdapter.add(0,story);//TODO: make sure this is the best way to add these
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