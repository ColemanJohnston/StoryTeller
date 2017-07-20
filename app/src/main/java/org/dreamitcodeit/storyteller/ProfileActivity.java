package org.dreamitcodeit.storyteller;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import org.dreamitcodeit.storyteller.fragments.StoriesPagerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

import static org.dreamitcodeit.storyteller.R.id.ivImage;

public class ProfileActivity extends AppCompatActivity {

    Firebase ref;
    StoriesPagerAdapter adapterViewPager;

    // References to the xml
    private ImageView ivProfileImage;
    private TextView tvName;

    // Facebook user variables
    private String userId;
    private String fbName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);
        // fetchUserData();

        if (AccessToken.getCurrentAccessToken() != null)
        {
            // means that the user is actually logged in with FB and not just email
            fetchFacebookUserData();
        }

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new StoriesPagerAdapter(getSupportFragmentManager(), this);
        vPager.setAdapter(adapterViewPager);
        TabLayout tablayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tablayout.setupWithViewPager(vPager);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvName = (TextView) findViewById(R.id.tvName);
    }


    public void fetchFacebookUserData()
    {
        // Line to let us see why it's not working
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);

        // calls the /user/me endpoint to fetch the user data for the given access token.
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), // TODO - eventually this won't be only our user, but the current user!
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {

                        // get the name  and the UID from the JSON object
                        try {
                            fbName = response.getJSONObject().get("name").toString();
                            userId = response.getJSONObject().get("id").toString();
                            tvName.setText(fbName);
                            setFBProfileImage();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void setFBProfileImage(){
        // get the profile image with the UID and load it into the profile image view
        try {

            //  get the url where the profile image should be
            String imageUrl = "https://graph.facebook.com/" + userId + "/picture?type=large";

            Uri uri = Uri.parse(imageUrl);

            // Load the image using Glide
            Glide.with(this /* context */)
                    .load(uri)
                    .into(ivProfileImage);

        }
        catch (Exception e){
            Log.e("ReadRdaJSONFeedTask", e.getLocalizedMessage() == null ? "ERROR IS NULL" : "ERROR IS NOT NULL AND IT IS:"+e.getLocalizedMessage());
        }
    }

    public void fetchUserData(){
        ref = new Firebase(Config.FIREBASE_URl);

        ref.orderByChild("uID").equalTo("Neehar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Story story = dataSnapshot.getValue(Story.class);
                Toast.makeText(ProfileActivity.this,story.getStoryBody(),Toast.LENGTH_LONG).show();
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
