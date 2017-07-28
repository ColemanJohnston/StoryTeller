package org.dreamitcodeit.storyteller;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
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
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import android.graphics.PorterDuff.Mode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static org.dreamitcodeit.storyteller.R.menu.search_menu;

@RuntimePermissions
public class MapActivity extends AppCompatActivity implements GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener{

    HashMap<LatLng,Marker> latLngMarkerHashMap;
    Firebase ref;

    AllStoriesPagerAdapter adapterViewPager;

    TabLayout tablayout;
    EditText etLocation;
    Button btSearch;

    // for tab layout image icons
    // configure icons
    private int[] imageResId = {
            R.drawable.all_stories,
            R.drawable.personal,
            R.drawable.historical,
            R.drawable.fictional};

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private LocationRequest mLocationRequest;
    Location mCurrentLocation;
    private Switch sMapList;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */
    String TAG = "DatabaseRefresh";
    boolean flag = false;
    boolean locFlag = false;
    int loopCounter = 0;
    private HashMap<String, Story> personalStories;
    private HashMap<String, Story> historicalStories;
    private HashMap<String, Story> fictionalStories;
    private HashMap<String, Story> miscStories;
    private ArrayList<LatLng> storyLocations;

   // GestureDetector gestureScanner;
    private SlidingUpPanelLayout mLayout;

    // for searching by location
    String zoomLocationFlag;
    ArrayList<Address> locationToZoomTo;


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

        personalStories = new HashMap<>();
        historicalStories = new HashMap<>();
        fictionalStories = new HashMap<>();
        miscStories = new HashMap<>();
        latLngMarkerHashMap = new HashMap<>();
        storyLocations = new ArrayList<>();

        sMapList = (Switch) findViewById(R.id.sMapList);

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);

        adapterViewPager = new AllStoriesPagerAdapter(getSupportFragmentManager(), MapActivity.this, "");

        vPager.setAdapter(adapterViewPager);
        tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
        tablayout.setupWithViewPager(vPager);


        // set the tab icons for the tags
        for (int i = 0; i < imageResId.length; i++) {
            tablayout.getTabAt(i).setIcon(imageResId[i]);
            int tabIconColor = ContextCompat.getColor(this, R.color.color1);
            tablayout.getTabAt(i).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        }


        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                clearMap();
                int pos = tab.getPosition();
                int selectedTabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color5);
                tablayout.getTabAt(pos).getIcon().setColorFilter(selectedTabIconColor, PorterDuff.Mode.SRC_IN);


                if(pos == AllStoriesPagerAdapter.ALL){//add all stories to map
                    for(Map.Entry<String,Story> e : personalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                    for(Map.Entry<String,Story> e : historicalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                    for(Map.Entry<String,Story> e : fictionalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                    for(Map.Entry<String,Story> e : miscStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                }
                else if(pos == AllStoriesPagerAdapter.PERSONAL){//add personal stories to map
                    for(Map.Entry<String,Story> e : personalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                }
                else if(pos == AllStoriesPagerAdapter.HISTORICAL){//add historical stories to map
                    for(Map.Entry<String,Story> e : historicalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                }
                else if(pos == AllStoriesPagerAdapter.FICTIONAL){// add fictional stories to map
                    for(Map.Entry<String,Story> e : fictionalStories.entrySet()){
                        dropMarker(e.getValue(), e.getKey());
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                //do nothing for now
                int pos = tab.getPosition();
                int selectedTabIconColor = ContextCompat.getColor(getApplicationContext(), R.color.color1);
                tablayout.getTabAt(pos).getIcon().setColorFilter(selectedTabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                //do nothing for now
            }
        });

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mLayout.setAnchorPoint(0.3f);

        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

        mLayout.addPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                String s = "hello";
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                String s = "hello";
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
                    setMarkerClickListener(map);
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    zoomToSearchedLocation();
                    //map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }


        sMapList.setChecked(false);
        sMapList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Intent intent = new Intent(MapActivity.this, AllStoriesActivity.class);//TODO: check if this is used still
                        startActivity(intent);
                    }
                    else {
                        sMapList.setChecked(false);
                    }
                }
        });

        Firebase.setAndroidContext(this);

        ref = new Firebase(Config.FIREBASE_URl);
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

    public void onProfileIconClick(MenuItem mi){
        this.startActivity(new Intent(this, ProfileActivity.class));
    }

    private void dropMarker(Story story, String key){//TODO: check method for setting things redundantly, and review if storing things in the markers is efficient
        LatLng location = new LatLng(story.getLatitude(),story.getLongitude());
        Marker marker = latLngMarkerHashMap.get(location);

        if(marker == null){
            marker = map.addMarker(new MarkerOptions()
                    .position(location)
                    //.icon(BitmapDescriptorFactory.fromResource(R.color.colorAccent)));
                    .icon(BitmapDescriptorFactory.defaultMarker(43)));
            HashMap<String,Story> tag = new HashMap<>();
            tag.put(key,story);
            marker.setTag( tag );//put an array with a story in the new marker
        }
        else {
            HashMap<String,Story> stories = (HashMap<String,Story>) marker.getTag();
            stories.put(key,story);
            marker.setTag(stories);
        }
        if(story.getIsCheckedIn()){
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(95));
        }

        latLngMarkerHashMap.put(location,marker);
    }

    private void clearMap(){
        map.clear();
        latLngMarkerHashMap.clear();

    }

    private boolean isInCurrentTab(Story s){//check if a story is in the currently displayed tab
        if(tablayout.getSelectedTabPosition() == AllStoriesPagerAdapter.ALL){
            return true;
        }
        if(tablayout.getSelectedTabPosition() == AllStoriesPagerAdapter.PERSONAL && s.isPersonal()){
            return true;
        }
        if(tablayout.getSelectedTabPosition() == AllStoriesPagerAdapter.HISTORICAL && s.isHistorical()){
            return true;
        }
        if(tablayout.getSelectedTabPosition() == AllStoriesPagerAdapter.FICTIONAL && s.isFictional()){
            return true;
        }
        return false;
    }

    private void recordStory(Story story, String key){//TODO: decide if stories can be in multiple categories
        storyLocations.add(new LatLng(story.getLatitude(),story.getLongitude()));//for notifications

        if(isInCurrentTab(story)){//for live updates and first time app is opened
            dropMarker(story,key);
        }

        if(story.isPersonal()){
            personalStories.put(key,story);
        }
        else if(story.isHistorical()){
            historicalStories.put(key,story);
        }
        else if(story.isFictional()){
            fictionalStories.put(key,story);
        }
        else{
            miscStories.put(key,story);
        }
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

        Query queryRef = ref.child("stories");

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//This function gets called like crazy all the time....
                try{//check in case there are some objects in the db that don't match my current story object
                    Story story = dataSnapshot.getValue(Story.class);
                    recordStory(story,dataSnapshot.getKey());
                    closeToStory(mCurrentLocation);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO: support live updates here
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
            return false;//
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


    // checks if you are close to a story and sends you a push notification if you are
    // should get called when location is changed TODO - AND when a new story is added or modified.
    public void closeToStory(Location location)
    {
        // Get all the locations from the hash map
        List<LatLng> listy = storyLocations;

        if (listy.size() == 0) // not efficient TODO: fix this sketchy circle call to populate map
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

            // TODO - maybe make listy global and remove from listy to prevent lots of spam notifications
        }

        Intent intent = new Intent(this, MapActivity.class);
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        intent.putExtra("notification", "zoom_to_current_location");
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        // send a push notification saying so!
        if (closeStories > 0)
        {
            NotificationManager notif =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            flag = false;
            Notification notify=new Notification.Builder
                    (getApplicationContext())
                    .setContentTitle("There are " + closeStories + " stories near your location!")
                    .setContentText("Click here to see and read them.")
                    .setContentIntent(pIntent)
                    .setSmallIcon(R.drawable.com_facebook_button_icon) // TODO - replace once we have a logo
                    .build();

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notify.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.notify(0, notify);
        }

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                //.icon(BitmapDescriptorFactory.fromResource(R.color.colorAccent)));
                .icon(BitmapDescriptorFactory.defaultMarker(55)));


        if(isCloseToCurrentLocation(latLng)){//drop verified color if marker is close enough to the current location
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(165));
        }
        dropPinEffect(marker);//author activity is started here
        //startAuthorActivity(latLng);
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
    public void onInfoWindowClick(Marker marker) {//I Don't think this function is being used anymore.
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
            final LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
            map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    onMapLongClick(latLng);// do same as long click when animation is finished
                }

                @Override
                public void onCancel() {
                    Toast.makeText(MapActivity.this,"Author action canceled",Toast.LENGTH_SHORT).show();//TODO: think about if we want to still take them to the AuthorActivity if they scroll away while the camera is updating
                }
            });

            //startAuthorActivity(latLng);
        } else {
            Toast.makeText(this, "Current location unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MapActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        locFlag = true;
        getMyLocation();
    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void getMyLocation() {

        flag = true;

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

    /*
 * Called when the Activity becomes visible.
*/
    @Override
    protected void onStart() {
        super.onStart();

    }

    public void zoomToSearchedLocation(){
        zoomLocationFlag = getIntent().getStringExtra("zoom-in-to-searched-location");
        locationToZoomTo = getIntent().getParcelableArrayListExtra("location-to-zoom-in-to");

        if (zoomLocationFlag != null && locationToZoomTo != null) // todo map is null when this is called
        {
            if (zoomLocationFlag.equals("true"))
            {
                Address address = locationToZoomTo.get(0);
                LatLng latLng2 = new LatLng(address.getLatitude(), address.getLongitude());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng2, 17);
                map.animateCamera(cameraUpdate);
                zoomLocationFlag = null; // todo - this doesn't work :( duh
            }
        }
    }

    @Override
    protected void onResume() { // TODO - I should make a method for zooming since I do it a bunch now. oops. problem for future Maria. Variable names could also be improved upon. And this comment is for sure over 80 characters. oops.
        super.onResume();

        // this only happens if you are coming from a notification
        String intentResult = getIntent().getStringExtra("notification");

        // I think it will be null at the start
        if (intentResult != null)
        {
            if (getIntent().getStringExtra("notification").equals("zoom_to_current_location") && !flag)
            {

                MapActivityPermissionsDispatcher.startLocationUpdatesWithCheck(this); //Pretty sure this is needed, but still don't know
                // MapActivityPermissionsDispatcher.getMyLocationWithCheck(this); // TODO I need this but it's making everything crash
                flag = true;
                if (mCurrentLocation != null) {
                    Toast.makeText(this, "Zooming in to stories near you!", Toast.LENGTH_SHORT).show();
                    LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                    map.animateCamera(cameraUpdate);
                } else {
                    Toast.makeText(this, "Oh oh! Your GPS is not working!", Toast.LENGTH_SHORT).show();
                }
            }
        }

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

                        zoomIn(locationResult.getLastLocation());

                        onLocationChanged(locationResult.getLastLocation());

                        loopCounter++;
                    }
                },
                Looper.myLooper());
    }

    public void zoomIn(Location currLoc){

        if (loopCounter == 1)
        {
            // this only happens if you are coming from a notification
            String intentResult = getIntent().getStringExtra("notification");

            // I think it will be null at the start
            if (intentResult != null)
            {
                if (getIntent().getStringExtra("notification").equals("zoom_to_current_location") && !flag)
                {
                    getIntent().putExtra("notification", "nope");

                    MapActivityPermissionsDispatcher.startLocationUpdatesWithCheck(MapActivity.this); //Pretty sure this is needed, but still don't know
                    // MapActivityPermissionsDispatcher.getMyLocationWithCheck(this); // TODO I need this but it's making everything crash
                    flag = true;
                    if (currLoc != null) {
                        Toast.makeText(MapActivity.this, "Zooming in to stories near you!", Toast.LENGTH_SHORT).show();
                        LatLng latLng = new LatLng(currLoc.getLatitude(), currLoc.getLongitude());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
                        map.animateCamera(cameraUpdate);
                    } else {
                        Toast.makeText(MapActivity.this, "Oh oh! Your GPS is not working!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

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

    public ArrayList<Address> getSearchedLocations(String query) {
        List<Address> addressList = null;
        ArrayList<Address> locations = new ArrayList<Address>();

        // time to search for a location!!!
        if (query != null && !query.equals("")) {
            Geocoder geocoder = new Geocoder(MapActivity.this);
            try
            {
                addressList = geocoder.getFromLocationName(query, 5);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (addressList != null)
            {
                for (int i = 0; i < addressList.size(); i++)
                {
                    // locations.add(i, addressList.get(i).toString());
                    locations.add(i, addressList.get(i));
                }
            }
            return locations;
        }

        return null; // should never happen
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(search_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                // perform query here

                Intent intent = new Intent(MapActivity.this, SearchActivity.class);
                intent.putExtra("query", query);

                // get the list of possible locations
                ArrayList<Address> locations = getSearchedLocations(query);

                if (locations != null)
                {
                    // intent.putStringArrayListExtra("locations", locations);
                    intent.putParcelableArrayListExtra("locations", locations);
                }

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

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new LinearInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            boolean flag1 = true;
            boolean flag2 = true;

            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;

                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 15);
                }
                else if(flag1){//delay after pin finishes dropping (for satisfaction)
                    Vibrator v = (Vibrator) MapActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(1);//tiny vibration (1 millisecond) //TODO does it need to be longer??
                    flag1 = false;
                    handler.postDelayed(this,300);
                }
                else if(flag2){ //start next activity and delay before removing marker
                    flag2 = false;
                    startAuthorActivity(marker.getPosition());
                    handler.postDelayed(this, 100);
                }
                else{
                    marker.remove();
                }
            }
        });
    }
}