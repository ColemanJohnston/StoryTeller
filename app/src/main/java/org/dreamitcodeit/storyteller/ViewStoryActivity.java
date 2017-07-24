package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewStoryActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStoryBody;
    private ImageView ivImage;
    String TAG = "LoadImage";
    StorageReference pathReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvStoryBody = (TextView) findViewById(R.id.tvStoryBody);
        ivImage = (ImageView) findViewById(R.id.ivImage);

        tvTitle.setText(getIntent().getStringExtra("title"));
        tvStoryBody.setText(getIntent().getStringExtra("storyBody"));
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
    }


}
