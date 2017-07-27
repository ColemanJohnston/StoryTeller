package org.dreamitcodeit.storyteller.fragments;

        import android.location.Location;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;

        import com.firebase.client.ChildEventListener;
        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;

        import org.dreamitcodeit.storyteller.Config;
        import org.dreamitcodeit.storyteller.Story;
        import org.dreamitcodeit.storyteller.models.SearchLocationAdapter;

        import java.util.ArrayList;
        import java.util.List;


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

        // unpack the array of possible locations and add them to our new adapter.

        for (int i = 0; i < locations.size(); i++)
        {
            searchLocationAdapter.add(0, locations.get(i));
        }
    }
}