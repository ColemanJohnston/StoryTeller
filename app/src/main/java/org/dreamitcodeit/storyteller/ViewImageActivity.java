package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ViewImageActivity extends AppCompatActivity {

    private ImageView ivStoryImage;
    StorageReference pathReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ivStoryImage = (ImageView) findViewById(R.id.ivStoryImage);

        try
        {
            // get a reference to the storage bucket!
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference with an initial file path and name of title.
            // This is hopefully where your file will be found in cloud storage
            pathReference = storageRef.child("images/" + getIntent().getStringExtra("title"));

            // temp path just to test
            //pathReference = storageRef.child("images/" + "download.jpg");


            // Load the image using Glide
            Glide.with(this /* context */).using(new FirebaseImageLoader())
                    .load(pathReference)
                    .bitmapTransform(new RoundedCornersTransformation(this, 15, 0))
                    .error(R.drawable.ocean)
                    .centerCrop()
                    .into(ivStoryImage);
        }
        // an error will be thrown when a story has no picture
        // a little jank but good for now I guess
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
