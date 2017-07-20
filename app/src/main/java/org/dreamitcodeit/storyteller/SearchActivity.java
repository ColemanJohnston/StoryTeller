package org.dreamitcodeit.storyteller;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.firebase.client.Firebase;

import org.dreamitcodeit.storyteller.fragments.SearchPagerAdapter;


/**
 * Created by neeharmb on 7/18/17.
 */

public class SearchActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStoryBody;
    Firebase ref;
    SearchPagerAdapter adapterViewPager;
    TabLayout tablayout;

    String title;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Firebase.setAndroidContext(this);

        ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);



        //Bundle bundle = new Bundle();
        //bundle.putString("query", getIntent().getStringExtra("query"));

        adapterViewPager = new SearchPagerAdapter(getSupportFragmentManager(), this, getIntent().getStringExtra("query"));
        //adapterViewPager.
        vPager.setAdapter(adapterViewPager);
        tablayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tablayout.setupWithViewPager(vPager);

       /* tvStoryBody = (TextView) findViewById(R.id.tvStoryBody);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");

        tvTitle.setText(title);
        tvStoryBody.setText("hello" + body);*/
       // tvStoryBody.setMovementMethod(new ScrollingMovementMethod());

       /* ViewPager vPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new StoriesPagerAdapter(getSupportFragmentManager(), this);
        vPager.setAdapter(adapterViewPager);
        TabLayout tablayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tablayout.setupWithViewPager(vPager);*/
    }


    //tablayout.set



    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(SearchActivity.this, MapActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }*/

}
