package org.dreamitcodeit.storyteller.models;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.dreamitcodeit.storyteller.MapActivity;
import org.dreamitcodeit.storyteller.R;

import java.util.ArrayList;

/**
 * Created by mariadeangelis on 7/26/17.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.ViewHolder>{

    private ArrayList<Address> locations;
    Context context;

    public SearchLocationAdapter(ArrayList<Address> locations){
        this.locations = locations;
    }


    public SearchLocationAdapter(){
         // empty public constructor does nothing hehe
        locations = new ArrayList<Address>();
    }
    @Override
    public SearchLocationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View storyView = inflater.inflate(R.layout.item_story, parent, false); // TODO - change this
        SearchLocationAdapter.ViewHolder viewHolder = new SearchLocationAdapter.ViewHolder(storyView); // TODO - what is storyView?
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return locations.size();

    }

    public void clearList(){
        locations.clear();
        notifyDataSetChanged();
    }

    public void add(int index, Address location){
        locations.add(index, location);
        notifyItemInserted(index); //TODO: test for crashes
    }

    @Override
    public void onBindViewHolder(SearchLocationAdapter.ViewHolder holder, int position) {


        Address currLoc = locations.get(position);
        holder.currentLoc = currLoc;

        // only the title is filled in with each location possibility

        holder.tvTitle.setText(currLoc.getAddressLine(0));
        holder.tvAuthorName.setText(currLoc.getCountryName());

        // The rest are either set to blank or invisible
        holder.tvDate.setText("");
        holder.tvFavorites.setText("");
        holder.tvStoryBody.setText("");
        holder.ivIsCheckedIn.setVisibility(View.INVISIBLE);
        holder.tvIsCheckedIn.setVisibility(View.INVISIBLE);
        holder.ivFavoriteIcon.setVisibility(View.INVISIBLE);
        }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivStoryImage;
        public TextView tvTitle;
        public TextView tvAuthorName;
        public TextView tvRating;
        public TextView tvDate;
        public TextView tvStoryBody;
        public TextView tvFavorites;
        public ImageView ivFavoriteIcon;
        public ImageView ivIsCheckedIn;
        public TextView tvIsCheckedIn;
        public Address currentLoc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            tvAuthorName = (TextView) itemView.findViewById(R.id.tvAuthorName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvStoryBody = (TextView) itemView.findViewById(R.id.tvStoryBody);
            tvFavorites = (TextView) itemView.findViewById(R.id.tvFavorites);

            ivFavoriteIcon = (ImageView) itemView.findViewById(R.id.ivFavoriteIcon);
            ivIsCheckedIn = (ImageView) itemView.findViewById(R.id.ivIsCheckedIn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // this is where you go back to the map view and pass in an intent saying to zoom in to that specific location you clicked on
                    // TODO - make sure to clear the intent in map activity
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("zoom-in-to-searched-location", "true");

                    ArrayList<Address> stupid = new ArrayList<Address>();
                    stupid.add(0, currentLoc);

                    // get the location you clicked on and pass it back to map activity
                    //intent.p("location-to-zoom-in-to", currentLoc);
                    intent.putParcelableArrayListExtra("location-to-zoom-in-to", stupid);
                    context.startActivity(intent);
                }
            });

        }


    }
}
