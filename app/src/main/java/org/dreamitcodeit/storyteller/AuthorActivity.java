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
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.LatLng;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import permissions.dispatcher.NeedsPermission;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static java.security.AccessController.getContext;

public class AuthorActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
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
    private ImageView ivPreview;

    // for taking photos
    public final String APP_TAG = "StoryTeller";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
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

                // stuff
                // onLaunchCamera(v);
//                try {
//                   takePicture(v);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                // invoke the image gallery using an implicit intent
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);

                // where do we want to find the data?
                File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String pictureDirectoryPath = pictureDirectory.getPath();

                // get a URI representation
                Uri data = Uri.parse(pictureDirectoryPath);

                // set the data and type. Get all image types
                photoPickerIntent.setDataAndType(data, "image/*");

                startActivityForResult(photoPickerIntent, IMAGE_GALLERY_REQUEST);


            }
        });

    }

    public void takePicture(View view) throws IOException {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //File f = createImageFile();
        //File f = getOutputMediaFile();
        // file = Uri.fromFile(f); Not supported if your target version is above 24 shit

        // File f = new File(getExternalFilesDir((Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png"));
        //File f = new File(Environment.getExternalStorageDirectory(), "images");


       intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

//        if (f != null){
//            try{
//                file = FileProvider.getUriForFile(AuthorActivity.this, "org.dreamitcodeit.fileprovider", f);
//            }
//            catch (IllegalArgumentException e)
//            {
//                e.printStackTrace();
//            }
//
//        }

//        if (hasPermissionInManifest(getApplicationContext(), "CAMERA"))
//        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri("hi.jpg"));

        if (intent.resolveActivity(getPackageManager())!= null)
        {
            startActivityForResult(intent, 100);
        }
//        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            return FileProvider.getUriForFile(AuthorActivity.this, "org.dreamitcodeit.fileprovider", file);
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }


    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        if (requestCode == 100 && resultCode == RESULT_OK)
//        {
//            ivPreview.setImageURI(file);
//        }
//    }

//    private static File getOutputMediaFile()
//    {
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera");
//
//        if (!mediaStorageDir.exists())
//        {
//            if (!mediaStorageDir.mkdirs())
//            {
//                return null;
//            }
//        }
//
//        // Name the file something different each time so you can't have overlaps
//        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//
//        return new File (mediaStorageDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg");
//
//    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DCIM), "Camera");
        //File storageDir = Environment.getExternalStorageDirectory();
        File storageDir = this.getFilesDir(); // TODO - is this the problem?
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    // START CAMERA CODE

//    public void onLaunchCamera(View view) {
//        // create Intent to take a picture and return control to the calling application
////        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////        //intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name
////
////        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
////        // So as long as the result is not null, it's safe to use the intent.
////        if (intent.resolveActivity(getPackageManager()) != null) {
////            // Start the image capture intent to take photo
////            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
////        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
////                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
////                // by this point we have the camera photo on disk
////                Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
////                // RESIZE BITMAP, see section below
////                // Load the taken image into a preview
////                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
////                ivPreview.setImageBitmap(takenImage);
//            } else { // Result was a failure
//                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    // Returns the Uri for a photo stored on disk given the fileName
//    public Uri getPhotoFileUri(String fileName) {
//        // Only continue if the SD Card is mounted
//        if (isExternalStorageAvailable()) {
//            // Get safe storage directory for photos
//            // Use `getExternalFilesDir` on Context to access package-specific directories.
//            // This way, we don't need to request external read/write runtime permissions.
//            File mediaStorageDir = new File(
//                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
//
//            // Create the storage directory if it does not exist
//            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
//                Log.d(APP_TAG, "failed to create directory");
//            }
//
//            // Return the file target for the photo based on filename
//            File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
//
//            // wrap File object into a content provider
//            // required for API >= 24
//            // See https://guides.codepath.com/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
//
//            Uri uri = FileProvider.getUriForFile(AuthorActivity.this, this.getPackageName() + ".share", file);
//            return uri;
//
//            //return FileProvider.getUriForFile(AuthorActivity.this, "org.dreamitcodeit.fileprovider", file);
//        }
//        return null;
//    }

//    // Returns true if external storage for photos is available
//    private boolean isExternalStorageAvailable() {
//        String state = Environment.getExternalStorageState();
//        return state.equals(Environment.MEDIA_MOUNTED);
//    }

    // END CAMERA CODE

}
