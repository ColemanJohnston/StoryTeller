package org.dreamitcodeit.storyteller;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

/**
 * Created by mariadeangelis on 7/12/17.
 */

public class AuthorFragment extends DialogFragment {


    Story story;
    Firebase ref;

    private EditText etTitle;
    private EditText etStoryBody;
    private EditText etLatitude;
    private EditText etLongitude;
    private TextView tvStories;
    private Button btSave;
    private Button btFetch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_author, container, false);
        getDialog().setTitle("Author new story");

        Firebase.setAndroidContext(getActivity());

        //  Button cancel = (Button) rootView.findViewById(R.id.cancel);
        btSave = (Button) rootView.findViewById(R.id.btSave);
        btFetch = (Button) rootView.findViewById(R.id.btFetch);
        etStoryBody = (EditText) rootView.findViewById(R.id.etStoryBody);
        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        tvStories = (TextView) rootView.findViewById(R.id.tvStories);

        // save this story and close the window
        btSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO - grab the data from the edit text, create a new story object
                // TODO - populate the story object with location (passed in from the MapActivity which launches this fragment)
                // TODO - and send to our cloud based database

                ref = new Firebase(Config.FIREBASE_URl);

                // creating Story object

                story = new Story();


                String storyBody = etStoryBody.getText().toString().trim();
                String title = etTitle.getText().toString().trim();


                // adding values
                // story.setTitle(title);
                story.setStoryBody(storyBody);
                story.setTitle(title);
                story.setLongitude(1);
                story.setLatitude(1);
                story.setuID("neehar");
                //story.setLatitude(latitude); TODO - pass in from map activity
                //story.setLongitude(longitude);
                // story.setuID(uID); // TODO - pass in

                ref.child(title).setValue(story);

                dismiss();
            }
        });

        // discard this story and close the window
        btFetch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ref = new Firebase(Config.FIREBASE_URl);

                Query queryRef = ref.orderByChild("title");

                queryRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                        for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//                            Story currentStories = dataSnapshot.getValue(Story.class);

                            String string = "TITLE: " + dataSnapshot.getKey();
                            tvStories.setText(tvStories.getText() + " " + string );
                      //  }
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
//                dismiss();
            }
        });

        return rootView;
    }

}
