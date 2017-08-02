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
 * Created by neeharmb on 7/21/17.
 */

public class SearchAllStoriesFragment extends StoryListFragment {

    private String tag;
    private Firebase ref;
    private ChildEventListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        tag = getArguments().getString("tag");
        ref = new Firebase(Config.FIREBASE_URl);
        fetchUserData();
        return v;
    }

    public void fetchUserData(){
        listener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Story story = dataSnapshot.getValue(Story.class);
                storyAdapter.add(0, story);//TODO: make sure this is the best way to add these
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO should probably update ui here
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
        };

        if (!tag.equals("all")) {

            ref.child("stories").orderByChild(tag).equalTo(true).addChildEventListener(listener);

//            ref.child("stories").orderByChild(tag).equalTo(true).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                    Story story = dataSnapshot.getValue(Story.class);
//                    storyAdapter.add(0, story);//TODO: make sure this is the best way to add these
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//
//
//            });
        }
        else {
            ref.child("stories").orderByChild("title").addChildEventListener(listener);
//            ref.child("stories").orderByChild("title").addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
//                    try {
//                        Story story = dataSnapshot.getValue(Story.class);
//                        storyAdapter.add(0, story);//TODO: make sure this is the best way to add these
//                    }
//                    catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                }
//
//                @Override
//                public void onCancelled(FirebaseError firebaseError) {
//
//                }
//            });
        }

    }

    @Override
    public void onDestroyView() {
        ref.removeEventListener(listener);//Hopefully this fixes some of the leakage.
        listener = null;//idk if this helps
        ref = null;//idk if this helps
        super.onDestroyView();
    }
}