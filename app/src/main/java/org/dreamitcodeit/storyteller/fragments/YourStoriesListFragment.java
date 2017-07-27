package org.dreamitcodeit.storyteller.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;

import org.dreamitcodeit.storyteller.Config;
import org.dreamitcodeit.storyteller.Story;

import java.util.List;

/**
 * Created by colemanmav on 7/18/17.
 */

public class YourStoriesListFragment extends StoryListFragment {
    private String uid;
    Firebase ref;
    String currentUserID;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
//        fetchUserData();
        return v;
    }

    @Override
    public void onResume() {//TODO: Change to live updates. This is a bit of a hack that is not efficient, but it should work
        super.onResume();
        storyAdapter.clearList();
        fetchUserData();
    }

    public YourStoriesListFragment setUid(String uid){
        this.uid = uid;
        return this;//allow for method chaining
    }


    public void fetchUserData(){
        ref = new Firebase(Config.FIREBASE_URl);

        ref.child("users").child(uid).child("userStoryIDs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
                List<String> storyIDs = dataSnapshot.getValue(t);
                if( storyIDs == null ) {
                    Log.d("YourStoriesListFragment", "No Stories to show");//TODO: display this to the user
                }
                else {
                    for(int i = 0; i < storyIDs.size(); ++i){
                        ref.child("stories").child(storyIDs.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Story story = dataSnapshot.getValue(Story.class);
                                story.setStoryId(dataSnapshot.getKey());

                                if( !(story.getUserName() == null || story.getUserName().isEmpty() ) || story.getuID().equals(currentUserID)){
                                    storyAdapter.add(0,story);//TODO: make sure this is the best way to add these
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
