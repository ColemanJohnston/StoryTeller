package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class ComposeStoryActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etStoryBody;
    private EditText etLatitude;
    private EditText etLongitude;
    private TextView tvStories;
    private Button btSave;
    private Button btFetch;

    private String currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_story);

        Firebase.setAndroidContext(this);

        btSave = (Button) findViewById(R.id.btSave);
        btFetch = (Button) findViewById(R.id.btFetch);
        etTitle = (EditText) findViewById(R.id.etTitle);
        etStoryBody = (EditText) findViewById(R.id.etStoryBody);
        etLatitude = (EditText) findViewById(R.id.etLatitude);
        etLongitude = (EditText) findViewById(R.id.etLongitude);
        tvStories = (TextView) findViewById(R.id.tvStories);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // creating Firebase object
                Firebase ref = new Firebase(Config.FIREBASE_URl);

                // getting values to store
                String title = etTitle.getText().toString().trim();
                String storyBody = etStoryBody.getText().toString().trim();
                Integer latitude = Integer.valueOf(etLatitude.getText().toString().trim());
                Integer longitude = Integer.valueOf(etLongitude.getText().toString().trim());
                String username = getIntent().getStringExtra("userName");

                // creating Story object
                Story story = new Story();

                // adding values
                story.setTitle(title);
                story.setStoryBody(storyBody);
                story.setLatitude(latitude);
                story.setLongitude(longitude);

                //if( ref.getAuth().getUid())
                //userRef.child(title).setValue(story);

              //.  FirebaseUser user = ref.getAuth().getUid();

                ref.child(title).setValue(story);

            }
        });
    }
}
