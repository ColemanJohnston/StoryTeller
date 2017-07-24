package org.dreamitcodeit.storyteller;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import org.dreamitcodeit.storyteller.fragments.AllStoriesPagerAdapter;
import org.dreamitcodeit.storyteller.fragments.StoriesDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static org.dreamitcodeit.storyteller.R.menu.search_menu;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    HashMap<LatLng,Marker> latLngMarkerHashMap;
    Firebase ref;

    AllStoriesPagerAdapter adapterViewPager;

    TabLayout tablayout;


    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private Switch sMapList;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    String TAG = "DatabaseRefresh";

   // GestureDetector gestureScanner;
    private SlidingUpPanelLayout mLayout;


    private final static String KEY_LOCATION = "location";

    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Firebase.setAndroidContext(this);

        sMapList = (Switch) findViewById(R.id.sMapList);
        //gestureScanner = new GestureDetector(this);

        //setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));


        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);

        adapterViewPager = new AllStoriesPagerAdapter(getSupportFragmentManager(), MapActivity.this, "");
        //adapterViewPager.
        vPager.setAdapter(adapterViewPager);
        tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
        tablayout.setupWithViewPager(vPager);

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        //mLayout.setShadowDrawable(getResources().getDrawable(R.drawable.above_shadow));
        mLayout.setAnchorPoint(0.3f);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {


                /*Toast.makeText(MapActivity.this, "here", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if (slideOffset < 0.2) {
                    /*if (getActionBar().isShowing()) {
                        getActionBar().hide();
                    }
                    Toast.makeText(MapActivity.this, "here", Toast.LENGTH_SHORT).show();

                } else {
                   /* if (!getActionBar().isShowing()) {
                        getActionBar().show();
                    }
                    Toast.makeText(MapActivity.this, "here", Toast.LENGTH_SHORT).show();

                }*/
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                //Toast.makeText(MapActivity.this, "yo", Toast.LENGTH_SHORT).show();
               // Intent intent = new Intent(MapActivity.this, AllStoriesActivity.class);
                //startActivityForResult(intent, 20);

            }

            public void onPanelExpanded(View panel) {

                Log.i(TAG, "onPanelExpanded");

            }

            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");

            }

            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");

            }
        });


        latLngMarkerHashMap = new HashMap<>();
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        if (savedInstanceState != null && savedInstanceState.keySet().contains(KEY_LOCATION)) {
            // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
            // is not null.
            mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setInfoWindowAdapter(new MarkerWindowAdapter(getLayoutInflater()));
//                    populateMap();
                    setMarkerClickListener(map);
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


        // SWitCH STUFF

        sMapList.setChecked(false);
        sMapList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Intent intent = new Intent(MapActivity.this, AllStoriesActivity.class);
                        startActivity(intent);
                    }
                    else {
                        sMapList.setChecked(false);
                    }
                }
        });


        // TODO - code to listen for real time refresh
        // TODO - right now we will re-popoulate the map everytime anything is changed
        // TODO - in the future we should optimize this

        Firebase.setAndroidContext(this);



        ref = new Firebase(Config.FIREBASE_URl);
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        Firebase myRef = ref.getRoot().getRef();

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever anything in our database is changed

                // Update our UI to reflect these changes
                populateMap();
            }

            @Override
            public void onCancelled(FirebaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.");
            }
        });


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_map,menu);
//        return true;
//    }

    public void onProfileIconClick(MenuItem mi){
        this.startActivity(new Intent(this, ProfileActivity.class));
    }

    private void dropMarker(Story story, String key){ //TODO: check method for setting things redundantly, and review if storing things in the markers is efficient
        LatLng location = new LatLng(story.getLatitude(),story.getLongitude());
        Marker marker = latLngMarkerHashMap.get(location);
        if(marker == null){
            marker = map.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            HashMap<String,Story> tag = new HashMap<>();
            tag.put(key,story);
            marker.setTag( tag );//put an array with a story in the new marker
        }
        else {
            // add a story to an existing location
            HashMap<String,Story> stories = (HashMap<String,Story>) marker.getTag();
            stories.put(key,story);
            marker.setTag(stories);
        }
        if(story.getIsCheckedIn()){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }

        latLngMarkerHashMap.put(location,marker);
    }

    private void setMarkerClickListener(GoogleMap map){
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                HashMap<String,Story> tag = (HashMap<String, Story>) marker.getTag();
                FragmentManager fm = getSupportFragmentManager();
                StoriesDialogFragment storiesDialogFragment = StoriesDialogFragment.newInstance(new ArrayList<Story>(tag.values()));
                storiesDialogFragment.show(fm,"fragment_dialog_fragment");

                return true;
            }
        });
    }

    private void populateMap(){
        ref = new Firebase(Config.FIREBASE_URl);

        Query queryRef = ref.orderByChild("title");

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try{//check in case there are some objects in the db that don't match my current story object
                    Story story = dataSnapshot.getValue(Story.class);
                    dropMarker(story,dataSnapshot.getKey());
                    closeToStory(mCurrentLocation);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

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

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            //Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();
            map.setOnMapLongClickListener(this);
            map.setOnInfoWindowClickListener(this);
            MapActivityPermissionsDispatcher.getMyLocationWithCheck(this);
            MapActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isCloseToCurrentLocation(LatLng latLng){
        if(mCurrentLocation == null){
            Toast.makeText(this,"Could not verify location",Toast.LENGTH_SHORT).show();
            return false;
        }

        Location markerLocation = new Location(mCurrentLocation);//construct location with current location to make sure altitude and things are same
        markerLocation.setLatitude(latLng.latitude);
        markerLocation.setLongitude(latLng.longitude);//set lat and long to match marker

        if(mCurrentLocation.distanceTo(markerLocation) <= 300){
            return true;
        }

        return false;
    }

    public void startAuthorActivity(LatLng latLng){
        Intent i = new Intent(this, AuthorActivity.class);

        if(isCloseToCurrentLocation(latLng)){
            i.putExtra("isCheckedIn",true);// Not putting else because default value in AuthorActivity is false.
        }
        i.putExtra("lat", latLng.latitude);
        i.putExtra("long", latLng.longitude);
        startActivityForResult(i, 20);//TODO: figure out why we are using startAcitvityForResult instead of StartActivity
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        startAuthorActivity(latLng);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == 20) {

            // TODO - Grab story object and extract location from it
            // TODO - drop pin and fill it with story object
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) { // I Don't think this function is being used anymore. TODO - remove?
        Integer clickCount = (Integer) marker.getTag();

        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Intent intent = new Intent(this, ViewStoryActivity.class);
            intent.putExtra("title", marker.getTitle());
            intent.putExtra("storyBody", marker.getSnippet());
            startActivity(intent);
        }
    }

    public void onFabComposeCurrentLocationClick(View v){
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate);
            startAuthorActivity(latLng);
        } else {
            Toast.makeText(this, "Current location unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);

        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);
        //noinspection MissingPermission
        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            onLocationChanged(location);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Display the connection status

        //TODO: think about if this needs to happen, but we decided that it's annoying
//        if (mCurrentLocation != null) {
//            Toast.makeText(this, "GPS location was found!", Toast.LENGTH_SHORT).show();
//            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
//            map.animateCamera(cameraUpdate);
//        } else {
//            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
//        }
        MapActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this);//Pretty sure this is needed, but still don't know
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        //noinspection MissingPermission
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    public void onLocationChanged(Location location) {
        // GPS may be turned off
        if (location == null) {
            return;
        }

        // Report to the UI that the location was updated
        mCurrentLocation = location;
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());

        closeToStory(location);
    }

    // checks if you are close to a story and sends you a push notification if you are
    // should get called when location is changed TODO - AND when a new story is added or modified.
    public void closeToStory(Location location)
    {
        // Get all the locations from the hash map
        List<LatLng> listy = new ArrayList<LatLng>(latLngMarkerHashMap.keySet());

        if (listy.size() == 0) // not efficient
        {
            populateMap();
        }

        listy = new ArrayList<LatLng>(latLngMarkerHashMap.keySet());

        int closeStories = 0;

        // check if you are close to a story, if so, count the number of stories you are close to
        for (int i = 0; i< listy.size() ; i++)
        {
            float [] results = new float[3];
            Location.distanceBetween(location.getLatitude(), location.getLongitude(),
                    listy.get(i).latitude, listy.get(i).longitude, results);

            // check if distance between them is less than a threshold number of meters
            if (results[0] <= 300 && results[0] > 0) // TODO - think about this magic number!!!
            {
                closeStories++;
            }

            // TODO -maybe make listy global and remove from listy to prevent lots of spam notifications
        }

        // send a push notification saying so!
        if (closeStories > 0)
        {
            NotificationManager notif =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notify=new Notification.Builder
                    (getApplicationContext())
                    .setContentTitle("There are " + closeStories + " stories near your location!")
                    .setContentText("Click here to see and read them.")
                    .setSmallIcon(R.drawable.com_facebook_button_icon) // TODO - replace once we have a logo
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        }

    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends android.support.v4.app.DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Intent intent = new Intent(MapActivity.this, SearchActivity.class);

        //startActivity(intent);

        //this.startActivity(new Intent(this, SearchActivity.class));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                // perform query here

                Intent intent = new Intent(MapActivity.this, SearchActivity.class);
                intent.putExtra("query", query);

                startActivity(intent);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                searchItem.collapseActionView();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return gestureScanner.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        Log.i("Test", "On Fling");
        Intent intent = new Intent(MapActivity.this, AllStoriesActivity.class);
        startActivity(intent);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }



    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }*/

}