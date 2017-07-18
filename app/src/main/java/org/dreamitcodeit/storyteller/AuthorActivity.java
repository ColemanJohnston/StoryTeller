package org.dreamitcodeit.storyteller;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.utilities.Base64;
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.*;
import java.util.jar.Manifest;

import permissions.dispatcher.NeedsPermission;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.security.AccessController.getContext;

public class AuthorActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST = 10;
    public static final int CAMERA_REQUEST_CODE = 111;
    Story story;
    Firebase ref;

    private EditText etTitle;
    private EditText etStoryBody;
    private EditText etLatitude;
    private EditText etLongitude;
    private TextView tvStories;
    private Button btSave;
    private Button btFetch;
    private Button btTakePhoto;
    private Button btImportPhoto;
    private ImageView ivPreview;

    // for taking photos
    public Uri file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        Firebase.setAndroidContext(this);

        // Button cancel = (Button) rootView.findViewById(R.id.cancel);
        btSave = (Button) findViewById(R.id.btSave);
        btFetch = (Button) findViewById(R.id.btFetch);
        etStoryBody = (EditText) findViewById(R.id.etStoryBody);
        etTitle = (EditText) findViewById(R.id.etTitle);
        tvStories = (TextView) findViewById(R.id.tvStories);
        btTakePhoto = (Button) findViewById(R.id.bTakePhoto);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btImportPhoto = (Button) findViewById(R.id.btImportPhoto);

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

                story = new Story(title,storyBody,"Neehar","Neehar","Neehar",latitude, longitude);

                ref.child(title).setValue(story);

                // TODO - go back to the map activity
                // TODO - pass back parcelable story object

                Intent data = new Intent();

                // TODO - we might want to pass info about the new story to something ... maybe can be removed
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response

                finish(); // closes the activity, pass data to parent
            }
        });

        // fetch data for testing purposes
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
            }
        });

        // Open the camera and take a picture.
        // Save the image to be later inputted into Firebase storage
        btTakePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //onLaunchCamera(v);
//                try {
//                   takePicture(v);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // if we have permission, just go for it!
                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                {
                    takePhoto();
                }
                // means we have to first get permission
                else
                {
                    String [] permissionRequested = {android.Manifest.permission.CAMERA};
                    requestPermissions(permissionRequested, CAMERA_REQUEST_CODE);
                }
            }
        });

        // Open the gallery and chose a picture.
        // Save the image to be later inputted into Firebase storage
        btImportPhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // invoke the image gallery using an implicit intent
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // where do we want to find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                // get a URI representation
                Uri data = Uri.parse(pictureDirectoryPath);

                // set the data and type. Get all image types
                photoPickerIntent.setDataAndType(data, "image/*");

                // We will invoke this activity and get something back from it
                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                takePhoto();
            }
            else
            {
                Toast.makeText(this, "Unable to invoke camera without permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // for picking an image from the gallery
        if (requestCode == IMAGE_GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            // if we are here, we are hearing back from the image gallery.
            // we get back a Uri in a third parameter data

            // the address of the image on the SD card
            Uri imageUri = data.getData();

            // declare a stream to read the image data from the SD card
            InputStream inputStream;

            // we are gett ing an input stream based on the Uri of the image
            try {
                inputStream = getContentResolver().openInputStream(imageUri);

                // get a bitmap from the stream
                Bitmap image = BitmapFactory.decodeStream(inputStream);

                // show the preview image to the user
                ivPreview.setImageBitmap(image);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
            }
        }

        // For taking a photo in app
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            // Now we have the image from the camera
            Bitmap cameraImage = (Bitmap) data.getExtras().get("Data");
            ivPreview.setImageBitmap(cameraImage);
        }
    }

    public void takePhoto()
    {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

}
