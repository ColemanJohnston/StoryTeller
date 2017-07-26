package org.dreamitcodeit.storyteller.models;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.dreamitcodeit.storyteller.MapActivity;
import org.dreamitcodeit.storyteller.R;
import org.dreamitcodeit.storyteller.Story;
import org.dreamitcodeit.storyteller.ViewStoryActivity;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mariadeangelis on 7/26/17.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<SearchLocationAdapter.ViewHolder>{

    private List<String> locations;
    Context context;

    public SearchLocationAdapter(List<String> locations){
        this.locations = locations;
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

    public void add(int index, String location){
        locations.add(index, location);
        notifyItemInserted(index); //TODO: test for crashes
    }

    @Override
    public void onBindViewHolder(SearchLocationAdapter.ViewHolder holder, int position) {


        String currLoc = locations.get(position);
        holder.currentLoc = currLoc;

        // only the title is filled in with each location possibility
        holder.tvTitle.setText(currLoc);

        // The rest are either set to blank or invisible
        holder.tvStoryBody.setText("");
        holder.tvAuthorName.setText("");
        holder.tvDate.setText("");
        holder.tvFavorites.setText("");

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
        public String currentLoc;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            tvAuthorName = (TextView) itemView.findViewById(R.id.tvAuthorName);
            tvRating = (TextView) itemView.findViewById(R.id.tvRating);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvStoryBody = (TextView) itemView.findViewById(R.id.tvStoryBody);
            tvFavorites = (TextView) itemView.findViewById(R.id.tvFavorites);

            ivFavoriteIcon = (ImageView) itemView.findViewById(R.id.ivFavoriteIcon);
            ivIsCheckedIn = (ImageView) itemView.findViewById(R.id.ivIsCheckedIn);
            tvIsCheckedIn = (TextView) itemView.findViewById(R.id.tvIsCheckedIn);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO - this is where you go back to the map view and pass in an intent saying to zoom in to that specific location you clicked on
                    // TODO - make sure to clear the intent in map activity
                    Intent intent = new Intent(context, MapActivity.class);
                    intent.putExtra("zoom-in-to-searched-location", "true");

                    // TODO - somehow get the location you clicked on and pass it back to map activity
                    // intent.putExtra("location-to-zoom-in-to", location);
                }
            });

        }


    }
}
