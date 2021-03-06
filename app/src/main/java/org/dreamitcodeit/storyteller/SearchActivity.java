package org.dreamitcodeit.storyteller;

import android.location.Address;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

import org.dreamitcodeit.storyteller.fragments.SearchPagerAdapter;

import java.util.ArrayList;


/**
 * Created by neeharmb on 7/18/17.
 */

public class SearchActivity extends AppCompatActivity {

    SearchPagerAdapter adapterViewPager;
    TabLayout tablayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);

        ArrayList<Address> locations = getIntent().getParcelableArrayListExtra("locations");

        adapterViewPager = new SearchPagerAdapter(getSupportFragmentManager(), this, getIntent().getStringExtra("query"), locations);
        vPager.setAdapter(adapterViewPager);
        tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
        tablayout.setupWithViewPager(vPager);

    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchActivity.this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }*/

}
