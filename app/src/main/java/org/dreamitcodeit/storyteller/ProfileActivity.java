package org.dreamitcodeit.storyteller;


import android.net.Uri;

import android.content.pm.PackageManager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
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
import com.facebook.LoggingBehavior;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.dreamitcodeit.storyteller.fragments.StoriesPagerAdapter;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    Firebase ref;
    StoriesPagerAdapter adapterViewPager;
    private FirebaseAuth mAuth;
    private String userName = "";
    private TextView tvLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    // References to the xml
    private ImageView ivProfileImage;
    private TextView tvName;

    // Facebook user variables
    private String userId;
    private String fbName;
    private String fbLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Firebase.setAndroidContext(this);


        tvName = (TextView) findViewById(R.id.tvName);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        if (AccessToken.getCurrentAccessToken() != null)
        {
            // means that the user is actually logged in with FB and not just email
            fetchFacebookUserData();
        }
        else
        {
            tvName.setText(userName);
        }

        // fetchUserData();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userName = currentUser.getEmail();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new StoriesPagerAdapter(getSupportFragmentManager(), this);
        vPager.setAdapter(adapterViewPager);
        TabLayout tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
        tablayout.setupWithViewPager(vPager);


        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       /* mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            tvLocation.setText(location.convert(137.00, 3));
                        }
                    }
                });*/
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

                        // get stuff from the JSON object
                        try {
                            fbName = response.getJSONObject().get("name").toString();
                            userId = response.getJSONObject().get("id").toString();
                            //fbLocation = response.getJSONObject().get("location").toString();
                            tvName.setText(fbName);
                            //tvLocation.setText(fbLocation);
                            setFBProfileImage();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,location");
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

        ref.orderByChild("userName").equalTo(userName).addChildEventListener(new ChildEventListener() {
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
