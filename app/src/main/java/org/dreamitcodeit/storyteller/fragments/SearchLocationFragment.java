package org.dreamitcodeit.storyteller.fragments;

        import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SearchLocationFragment extends LocationListFragment  {

    private String query;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);

        query = getArguments().getString("query");

        // get a string array of possible locations from the SearchActivity class
        locations = getArguments().getParcelableArrayList("locations");
        // adapter = new SearchLocationAdapter(locations);


        populateLocationResults();
        return v;
    }

    public void populateLocationResults(){
        if(locations.size() < 1){
            showNoResults();
        }
        // unpack the array of possible locations and add them to our new adapter.
        for (int i = 0; i < locations.size(); i++)
        {
            searchLocationAdapter.add(0, locations.get(i));
        }
    }
}