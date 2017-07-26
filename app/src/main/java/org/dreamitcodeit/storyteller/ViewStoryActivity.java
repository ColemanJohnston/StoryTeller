package org.dreamitcodeit.storyteller;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.dreamitcodeit.storyteller.models.User;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


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
        //tvStoryBody.setText(story.getStoryBody());

        final SpannableString spannableString = new SpannableString(story.getStoryBody());
        int position = 0;
        for (int i = 0, ei = story.getStoryBody().length(); i < ei; i++) {
            char c = story.getStoryBody().charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                position = i;
                break;
            }
        }
        spannableString.setSpan(new RelativeSizeSpan(3.0f), position, position + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvStoryBody.setText(spannableString, TextView.BufferType.SPANNABLE);

        tvStoryBody.setMovementMethod(new ScrollingMovementMethod());

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/QuattrocentoSans-Regular.ttf");

        tvStoryBody.setTypeface(typeface);

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
                    .bitmapTransform(new RoundedCornersTransformation(this, 15, 0))
                    .error(R.drawable.ocean)
                    .centerCrop()
                    .into(ivImage);
        }
        // an error will be thrown when a story has no picture
        // a little jank but good for now I guess
        catch (Exception e)
        {
            e.printStackTrace();
        }

        /*ivImage.setImageResource(R.drawable.ocean);
        ivImage.setAdjustViewBounds(true);
<<<<<<< HEAD
        ivImage.setScaleType(ImageView.ScaleType.FIT_XY);*/

       // ivImage.setScaleType(ImageView.ScaleType.FIT_XY);

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
                                incrementFavCount();//add one the favCount of the story
                            }
                            else{
                                if(favStoryList.contains(story.getStoryId())){
                                    //Do nothing or maybe un-favorite
                                }
                                else{
                                    favStoryList.add(story.getStoryId());
                                    incrementFavCount();//add one the favCount of the story
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


        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewStoryActivity.this, ViewImageActivity.class);
                intent.putExtra("title", tvTitle.getText().toString().trim());
                /*Bitmap bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                intent.putExtra("byteArray", bs.toByteArray());*/
                startActivity(intent);
            }
        });


    }

    public void incrementFavCount(){
        ref.child("stories").child(story.getStoryId()).runTransaction(new Transaction.Handler(){//TODO: check to see if multiple users can make a transaction at the same time
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Story s = mutableData.getValue(Story.class);
                if (s == null) {
                    return Transaction.success(mutableData);//TODO: check to see what this is for
                }
                s.favCount = s.favCount + 1;//increment the favorites for a story

                // Set value and report transaction success
                mutableData.setValue(s);
                return Transaction.success(mutableData);
            }

            public void onComplete(FirebaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }

}
