package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by neeharmb on 7/20/17.
 */

public class SearchPagerAdapter extends SmartFragmentStatePagerAdapter{

    private String tabTitles[] = new String[]{"By Title","By User", "By Location"};
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
            AllStoriesPagerAdapter.SearchUsersListFragment fragment = new AllStoriesPagerAdapter.SearchUsersListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
            return fragment;
        }

        if (position == 2)
        {
            AllStoriesPagerAdapter.SearchUsersListFragment fragment = new AllStoriesPagerAdapter.SearchUsersListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("query", query);
            fragment.setArguments(bundle);
        }

        // search fails
        return null;
    }

}
