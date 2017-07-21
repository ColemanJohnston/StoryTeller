package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by neeharmb on 7/21/17.
 */

public class AllStoriesPagerAdapter extends SmartFragmentStatePagerAdapter {

    private String tabTitles[] = new String[]{"All Stories","Personal Stories", "Historical Stories"
    , "Fictional Stories"};
    Context context;
    String query;

    public AllStoriesPagerAdapter(FragmentManager fm, Context context, String query){
        super(fm);
        this.context = context;
        this.query = query;
    }

    //return title
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    //return the total number of fragments
    @Override
    public int getCount() {
        return 4;
    }

    //return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {

        // search "by title" tab is selected
        if(position == 0){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "all");
            fragment.setArguments(bundle);
            return fragment;
        }

        // search "by users" tab is selected
        if(position == 1){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "personal");
            fragment.setArguments(bundle);
            return fragment;
        }

        if(position == 2){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "historical");
            fragment.setArguments(bundle);
            return fragment;
        }

        if(position == 3){
            SearchAllStoriesFragment fragment = new SearchAllStoriesFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tag", "fictional");
            fragment.setArguments(bundle);
            return fragment;
        }


        // search fails
        return null;
    }
}
