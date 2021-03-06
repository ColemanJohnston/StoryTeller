package org.dreamitcodeit.storyteller;


import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.dreamitcodeit.storyteller.fragments.DatePickerFragment;
import org.dreamitcodeit.storyteller.models.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//import static org.dreamitcodeit.storyteller.R.id.editor;

public class AuthorActivity extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
    public static final int CAMERA_REQUEST = 10;
    public static final int CAMERA_REQUEST_CODE = 111;
    Story story;
    Firebase ref;
    private FirebaseAuth mAuth;


    private EditText etTitle;
    private EditText etStoryBody;
    private Button btSave;
    private ImageButton btTakePhoto;
    private ImageButton btImportPhoto;
    private ImageView ivPreview;
    private String title;
    private TextView tvImages;
    private TextView tvCategories;

    private boolean isPersonal = false;
    private boolean isHistorical = false;
    private boolean isFictional = false;


    // for taking photos
    public Uri file;
    Uri photoURI;
    String mCurrentPhotoPath;
    InputStream inputStream;
    Bitmap bity;
    private DatePicker dpCompose;
    private ImageButton ibCalendar;
    private ImageButton ibPersonal;
    private ImageButton ibHistorical;
    private ImageButton ibFictional;
    private ListView lvContainer;
    DatePickerFragment datePickerFragment;
    private FirebaseUser currentUser;

    private Switch swAnonymous;
    String userName;

    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);

        Firebase.setAndroidContext(this);

        btSave = (Button) findViewById(R.id.btSave);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");

        btSave.setTypeface(typeface);
        etStoryBody = (EditText) findViewById(R.id.etStoryBody);
        etTitle = (EditText) findViewById(R.id.etTitle);

        etTitle.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etTitle, InputMethodManager.SHOW_IMPLICIT);
        btTakePhoto = (ImageButton) findViewById(R.id.btTakePhoto);
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        btImportPhoto = (ImageButton) findViewById(R.id.btImportPhoto);
        ibCalendar = (ImageButton) findViewById(R.id.ibCalendar);
        tvDate = (TextView) findViewById(R.id.tvDate);
        ibPersonal = (ImageButton) findViewById(R.id.ibPersonal);
        int notPressed = ContextCompat.getColor(this, R.color.colorWhite);
        ibPersonal.setColorFilter(notPressed, PorterDuff.Mode.SRC_IN);
        ibHistorical = (ImageButton) findViewById(R.id.ibHistorical);
        ibHistorical.setColorFilter(notPressed, PorterDuff.Mode.SRC_IN);
        ibFictional = (ImageButton) findViewById(R.id.ibFictional);
        ibFictional.setColorFilter(notPressed, PorterDuff.Mode.SRC_IN);

        swAnonymous = (Switch) findViewById(R.id.swAnonymous);
        swAnonymous.setTypeface(typeface);

        tvImages = (TextView) findViewById(R.id.tvImages);
        tvCategories = (TextView) findViewById(R.id.tvCategories);
        tvImages.setTypeface(typeface);
        tvCategories.setTypeface(typeface);

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy");
        String today = dateFormat.format(Calendar.getInstance().getTime());
        tvDate.setText(today);

        ivPreview.setVisibility(View.INVISIBLE);

        ibCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userName = currentUser.getDisplayName();

        //swAnonymous.setChecked(false);
        swAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    userName = "";
                }
            }
        });

        ibPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pressed = ContextCompat.getColor(AuthorActivity.this, R.color.color5);
                ibPersonal.setColorFilter(pressed, PorterDuff.Mode.SRC_IN);
                isPersonal = true;
            }
        });

        ibHistorical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pressed = ContextCompat.getColor(AuthorActivity.this, R.color.color5);
                ibHistorical.setColorFilter(pressed, PorterDuff.Mode.SRC_IN);
                isHistorical = true;
            }
        });

        ibFictional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pressed = ContextCompat.getColor(AuthorActivity.this, R.color.color5);
                ibFictional.setColorFilter(pressed, PorterDuff.Mode.SRC_IN);
                isFictional = true;
            }
        });
                                   //   }

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
                title = etTitle.getText().toString().trim();

                double latitude = getIntent().getDoubleExtra("lat", 0);
                double longitude = getIntent().getDoubleExtra("long", 0);
                boolean isCheckedIn = getIntent().getBooleanExtra("isCheckedIn",false);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                Firebase newStoryRef = ref.child("stories").push(); //generate new spot in database
                story = new Story(title,storyBody, uid,latitude, longitude, tvDate.getText().toString(), isCheckedIn, 0, isPersonal, isHistorical, isFictional, newStoryRef.getKey(), userName );//zero for no favorites
                newStoryRef.setValue(story);//send new story to its spot in the database
                addToUserStoriesList(story.getStoryId());//give the id from the story to the user for easy access.

                Intent data = new Intent();

                // TODO - we might want to pass info about the new story to something ... maybe can be removed
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response

                finish(); // closes the activity, pass data to parent
            }
        });


        // Open the camera and take a picture.
        // Save the image to be later inputted into Firebase storage
        btTakePhoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                
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

            // we are getting an input stream based on the Uri of the image
            try {
                inputStream = getContentResolver().openInputStream(imageUri);

                // get a bitmap from the stream
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                bity = image;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
                String imageType = options.outMimeType;

                ivPreview.setVisibility(View.VISIBLE);

                // show the preview image to the user
                ivPreview.setImageBitmap(image);

                // Now store the image in Firebase Cloud Storage
                storeImageCloud();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
            }
        }

        // For taking a photo in app
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK)
        {
            Bundle extras;
            Uri qualityImageUri;
            // Now we have the image from the camera
            if (data != null){
                extras = data.getExtras();
                Bitmap cameraImage = (Bitmap) extras.get("data");
            }

            try {
                ivPreview.setVisibility(View.VISIBLE);
                ivPreview.setImageURI(photoURI);
                bity = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            // Now store the image in Firebase Cloud Storage
            storeImageCloud();
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public void takePhoto()
    {
        // out with the old
//        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

        // in with the new lol
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "org.dreamitcodeit.storyteller.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Stores image in our Firebase Cloud Storage, backed by Google. Thanks Google!
    public void storeImageCloud()
    {
        // get a reference to the storage bucket!
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a child reference
        // imagesRef now points to "images"
        // TODO - bring up an alert dialogue saying "Hey, after this you won't be able to change your title anymore"

        title = etTitle.getText().toString().trim();
        StorageReference imagesRef = storageRef.child("images/" + title + "/");

        // Uploading as a compressed bitmap

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bity.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] dataBAOS = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        });
    }

    private void addToUserStoriesList(final String newStoryId){
       final String uid = currentUser.getUid();

        final Firebase hello = ref.child("users").child(uid);

        ref.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    User userObject = dataSnapshot.getValue(User.class);
                    List<String> storyList = userObject.getUserStoryIDs();
                    if(storyList == null){
                        storyList = new ArrayList<String>();
                        storyList.add(newStoryId);
                    }
                    else{
                        storyList.add(0,newStoryId);
                    }
                    ref.child("users").child(currentUser.getUid()).child("userStoryIDs").setValue(storyList);

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

}
