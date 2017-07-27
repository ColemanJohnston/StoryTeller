package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import org.dreamitcodeit.storyteller.fragments.AllStoriesPagerAdapter;

public class AllStoriesActivity extends AppCompatActivity {

    AllStoriesPagerAdapter adapterViewPager;

    TabLayout tablayout;


    @Override//TODO: get rid of this class
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        Firebase.setAndroidContext(this);
//
//        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
//
//        adapterViewPager = new AllStoriesPagerAdapter(getSupportFragmentManager(), this, "");
//        vPager.setAdapter(adapterViewPager);
//        tablayout = (TabLayout) findViewById(R.id.sliding_tabs_all);
//        tablayout.setupWithViewPager(vPager);
//
//        vPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                String hello;
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                String hello;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                String hello;
//            }
//        });
//
//        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                String hello;
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                String hi;
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//                String yo;
//            }
//        });
    }
}
