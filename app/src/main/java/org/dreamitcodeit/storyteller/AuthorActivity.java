package org.dreamitcodeit.storyteller;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import org.dreamitcodeit.storyteller.fragments.DatePickerFragment;

import java.util.Date;

public class AuthorActivity extends AppCompatActivity {

    Story story;
    Firebase ref;

    private EditText etTitle;
    private EditText etStoryBody;
    private EditText etLatitude;
    private EditText etLongitude;
    private TextView tvStories;
    private Button btSave;
    private Button btFetch;
    private DatePicker dpCompose;
    private ImageButton ibCalendar;
    private ListView lvContainer;
    DatePickerFragment datePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        Firebase.setAndroidContext(this);

        //  Button cancel = (Button) rootView.findViewById(R.id.cancel);
        btSave = (Button) findViewById(R.id.btSave);
        btFetch = (Button) findViewById(R.id.btFetch);
        etStoryBody = (EditText) findViewById(R.id.etStoryBody);
        etTitle = (EditText) findViewById(R.id.etTitle);
        tvStories = (TextView) findViewById(R.id.tvStories);
        dpCompose = (DatePicker) findViewById(R.id.dpCompose);
        ibCalendar = (ImageButton) findViewById(R.id.ibCalendar);
       // lvContainer = (ListView) findViewById(R.id.lvContainer);

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }

        });

        // save this story and return to MapView Activity
        btSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // grab the data from the edit text, create a new story object
                // populate the story object with location (passed in from the MapActivity which launches this fragment)
                // and send to our cloud based database

                ref = new Firebase(Config.FIREBASE_URl);

                // creating Story object

                String storyBody = etStoryBody.getText().toString().trim();
                etStoryBody.setMovementMethod(new ScrollingMovementMethod());
                String title = etTitle.getText().toString().trim();

                double latitude = getIntent().getDoubleExtra("lat", 0);
                double longitude = getIntent().getDoubleExtra("long", 0);

                Date now = new Date();

                now.setMonth(dpCompose.getMonth());
                now.setYear(dpCompose.getYear());
                now.setDate(dpCompose.getDayOfMonth());


                story = new Story(title,storyBody,"Neehar","Neehar","Neehar",latitude, longitude);
                story.setDate(now);
                story.setTimestamp(now.toString());

                ref.push().setValue(story);//send data to database with unique id

                // TODO - go back to the map activity
                // TODO - pass back parcelable story object

                Intent data = new Intent();

                // TODO - we might want to pass info about the new story to something ... maybe can be removed
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response

                finish(); // closes the activity, pass data to parent
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

                        String string = "TITLE: " + dataSnapshot.getKey();
                        tvStories.setText(tvStories.getText() + " " + string );
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

       // etStoryBody.setMovementMethod(new ScrollingMovementMethod());
    }
}
