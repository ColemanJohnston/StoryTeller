package org.dreamitcodeit.storyteller;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by colemanmav on 7/12/17.
 */

class MarkerWindowAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public MarkerWindowAdapter(LayoutInflater i){
        mInflater = i;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.marker_info_window, null);
        // Populate fields
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//TODO: fix onclick that doesn't seem to be firing
                v.getContext().startActivity(new Intent(v.getContext(),ViewStoryActivity.class));
            }
        });
        TextView title = (TextView) v.findViewById(R.id.tv_info_window_title);
        title.setText(marker.getTitle());

        TextView description = (TextView) v.findViewById(R.id.tv_info_window_description);
        description.setText(marker.getSnippet());
        // Return info window contents
        return v;
    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}
