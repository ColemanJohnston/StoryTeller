package org.dreamitcodeit.storyteller.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

/**
 * Created by colemanmav on 7/17/17.
 */

public class StoriesPagerAdapter extends SmartFragmentStatePagerAdapter{

    private String tabTitles[] = new String[]{"My Stories","Favorites"};
    Context context;
    private String uid;

    public StoriesPagerAdapter(FragmentManager fm, Context context, String uid){
        super(fm);
        this.context = context;
        this.uid = uid;
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
            return new YourStoriesListFragment().setUid(uid);
        }
        if(position == 1){
            return new FavoriteStoriesListFragment().setUid(uid);
        }
        return null;
    }
}
