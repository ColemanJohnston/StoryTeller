package org.dreamitcodeit.storyteller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.firebase.client.utilities.Base64;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.InputStream;
import java.net.URL;

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

        // get a reference to the storage bucket!
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name of title.
        // This is hopefully where your file will be found in cloud storage
        // final StorageReference pathReference = storageRef.child("images/" + tvTitle.getText().toString().trim());

        // temp path just to test
        // final StorageReference pathReference = storageRef.child("images/" + "please work or ill leave");
        //pathReference = storageRef.child("images/" + "download.jpg");
        pathReference = storageRef.child("images/" + "fin");



        if (pathReference != null)
        {

//            pathReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    // Use the bytes to display the image
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                }
//            });
//
//            // Get the info stream from the path reference
//            StreamDownloadTask stream = pathReference.getStream();
//
//            // Convert the info stream to a bitmap
//            //Bitmap bitmap = BitmapFactory.decodeStream(stream);
//
//            // set the image view to that stories image if it has one
//            //ivImage.setImageBitmap(bitmap);
//
//            pathReference.getDownloadUrl().addOnSuccessListener(this, new OnSuccessListener<Uri>() {
//                @Override
//                public void onSuccess(Uri uri) {
//                    Log.i(TAG, "Download URL : " + uri);
//                    Glide.with(ViewStoryActivity.this)
//                            .load(uri)
//                            .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                            .into(ivImage);
//                }
//            });


//            // Load the image using Glide
            Glide.with(this /* context */).using(new FirebaseImageLoader())
                    .load(pathReference)
                    .into(ivImage);

//            // defines the specific size of your image
//            final long ONE_MEGABYTE = 1024 * 1024;
//            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    // Data for "yourImage.jpg" is returns, use this as needed
//                    Glide.with(ViewStoryActivity.this)
//                            .load(bytes)
//                            .asBitmap()
//                            .into(ivImage);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    // Handle any errors
//                    return;
//                }
//            });

        }
    }
}
