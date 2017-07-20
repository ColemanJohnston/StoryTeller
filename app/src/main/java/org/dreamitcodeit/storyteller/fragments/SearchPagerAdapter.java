package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by neeharmb on 7/20/17.
 */

public class SearchPagerAdapter extends SmartFragmentStatePagerAdapter{

    private String tabTitles[] = new String[]{"By Title","By User"};
    Context context;
    String query;

    public SearchPagerAdapter(FragmentManager fm, Context context, String query){
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
        return 2;
    }

    //return the fragment to use depending on the position
    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            SearchStoriesListFragment fragment = new SearchStoriesListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
            return fragment;
        }
        if(position == 1){
            SearchUsersListFragment fragment = new SearchUsersListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
           // Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();
            return fragment; //TODO: replace with User when feature is added
        }
        return null;
    }



    public String getQuery() {
        return query;
    }
}
