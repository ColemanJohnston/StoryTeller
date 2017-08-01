package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by neeharmb on 7/20/17.
 */

public class SearchPagerAdapter extends SmartFragmentStatePagerAdapter{

    private String tabTitles[] = new String[]{"By Title","By User", "By Location"};
    Context context;
    String query;
    ArrayList<Address> locations;

//    public SearchPagerAdapter(FragmentManager fm, Context context, String query){
//        super(fm);
//        this.context = context;
//        this.query = query;
//    }

    public SearchPagerAdapter(FragmentManager fm, Context context, String query, ArrayList<Address> locations){
        super(fm);
        this.context = context;
        this.query = query;
        this.locations = locations;
    }

    //return title
    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles[position];
    }

    //return the total number of fragments
    @Override
    public int getCount() {
        return 3;
    }

    //return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {

        // search "by title" tab is selected
        if(position == 0){
            SearchStoriesListFragment fragment = new SearchStoriesListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
            return fragment;
        }

        // search "by users" tab is selected
        if(position == 1){
            SearchUsersListFragment fragment = new SearchUsersListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
            return fragment;
        }

        // search "by location" tab is selected
        if (position == 2)
        {
            SearchLocationFragment fragment = new SearchLocationFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            bundle.putParcelableArrayList("locations", locations);
            fragment.setArguments(bundle);
            return fragment;
        }

        // search fails
        return null;
    }

}
