package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.dreamitcodeit.storyteller.models.User;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;


public class ViewStoryActivity extends AppCompatActivity {

    private Story story;
    private Button btnFavorite;
    private TextView tvTitle;
    private TextView tvStoryBody;
    private ImageView ivImage;
    String TAG = "LoadImage";
    StorageReference pathReference;
    Firebase ref;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        story = Parcels.unwrap(getIntent().getParcelableExtra("story"));

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvStoryBody = (TextView) findViewById(R.id.tvStoryBody);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        btnFavorite = (Button) findViewById(R.id.btnFavorite);

        tvTitle.setText(story.getTitle());
        tvStoryBody.setText(story.getStoryBody());
        tvStoryBody.setMovementMethod(new ScrollingMovementMethod());



        try
        {
            // get a reference to the storage bucket!
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference with an initial file path and name of title.
            // This is hopefully where your file will be found in cloud storage
            pathReference = storageRef.child("images/" + tvTitle.getText().toString().trim());

            // temp path just to test
            //pathReference = storageRef.child("images/" + "download.jpg");


            // Load the image using Glide
            Glide.with(this /* context */).using(new FirebaseImageLoader())
                    .load(pathReference)
                    .into(ivImage);

        }
        // an error will be thrown when a story has no picture
        // a little jank but good for now I guess
        catch (Exception e)
        {
            e.printStackTrace();
        }

        ivImage.setImageResource(R.drawable.ocean);
        ivImage.setAdjustViewBounds(true);
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase.setAndroidContext(ViewStoryActivity.this);
                ref = new Firebase(Config.FIREBASE_URl);

                mAuth = FirebaseAuth.getInstance();
                String uid = mAuth.getCurrentUser().getUid();


                ref.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {//for some reason this is getting called twice. Maybe because I'm changing it??
                        try{
                            User userObject = dataSnapshot.getValue(User.class);
                            List<String> favStoryList = userObject.getFavoriteIDs();
                            if(favStoryList == null){
                                favStoryList = new ArrayList<String>();
                                favStoryList.add(story.getStoryId());
                            }
                            else{
                                if(favStoryList.contains(story.getStoryId())){
                                    //Do nothing or maybe un-favorite
                                }
                                else{
                                    favStoryList.add(story.getStoryId());
                                }
                            }
                            ref.child("users").child(mAuth.getCurrentUser().getUid()).child("favoriteIDs").setValue(favStoryList);

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        });
    }


}
