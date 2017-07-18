package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.dreamitcodeit.storyteller.fragments.StoriesPagerAdapter;

public class ProfileActivity extends AppCompatActivity {

    Firebase ref;
    StoriesPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Firebase.setAndroidContext(this);
//        fetchUserData();

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new StoriesPagerAdapter(getSupportFragmentManager(), this);
        vPager.setAdapter(adapterViewPager);
        TabLayout tablayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tablayout.setupWithViewPager(vPager);
    }



    public void fetchUserData(){
        ref = new Firebase(Config.FIREBASE_URl);

        ref.orderByChild("uID").equalTo("Neehar").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                Story story = dataSnapshot.getValue(Story.class);
                Toast.makeText(ProfileActivity.this,story.getStoryBody(),Toast.LENGTH_LONG).show();
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



}
