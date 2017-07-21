package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

import org.dreamitcodeit.storyteller.fragments.AllStoriesPagerAdapter;

public class AllStoriesActivity extends AppCompatActivity {

    Firebase ref;
    AllStoriesPagerAdapter adapterViewPager;

    TabLayout tablayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stories);

        Firebase.setAndroidContext(this);

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);

        adapterViewPager = new AllStoriesPagerAdapter(getSupportFragmentManager(), this, "");
        //adapterViewPager.
        vPager.setAdapter(adapterViewPager);
        tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
        tablayout.setupWithViewPager(vPager);

    }
}
