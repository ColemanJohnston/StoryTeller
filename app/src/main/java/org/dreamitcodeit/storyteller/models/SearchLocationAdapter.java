package org.dreamitcodeit.storyteller.models;

import android.content.Context;
import android.content.Intent;
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

import org.dreamitcodeit.storyteller.R;
import org.dreamitcodeit.storyteller.Story;
import org.dreamitcodeit.storyteller.ViewStoryActivity;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by mariadeangelis on 7/26/17.
 */

public class SearchLocationAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private List<Story> locations;
    Context context;

    public StoryAdapter(List<Story> locations){
        this.locations = locations;
    }


    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View storyView = inflater.inflate(R.layout.item_story, parent, false); // TODO - change this
        SearchLocationAdapter.ViewHolder viewHolder = new SearchLocationAdapter.ViewHolder(storyView);
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

    public void add(int index, Story story){
        locations.add(index, story);
        notifyItemInserted(index); //TODO: test for crashes
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
        public Story currentStory;

        public ViewHolder(View itemView) {
            super(itemView);
            ivStoryImage = (ImageView) itemView.findViewById(R.id.ivStoryImage);
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

                    Intent intent i = 
                }
            });

        }


    }
}

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{



    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {


        Story story = stories.get(position);
        holder.currentStory = story; //Possible privacy leak TODO: make copy constructor for story class.
        holder.tvTitle.setText(story.getTitle());
        holder.tvStoryBody.setText(story.getStoryBody());
        holder.tvAuthorName.setText(story.getUserName());//(String.format("By %s","Neehar"));//TODO: optimize for i18n with string resource
        holder.tvDate.setText(story.getDate());
        holder.tvFavorites.setText(String.format("%d",story.getFavCount()));

        if(story.getIsCheckedIn()){
            holder.ivIsCheckedIn.setVisibility(View.VISIBLE);
            holder.tvIsCheckedIn.setVisibility(View.VISIBLE);
        }
        else{
            holder.ivIsCheckedIn.setVisibility(View.INVISIBLE);
            holder.tvIsCheckedIn.setVisibility(View.INVISIBLE);
        }

        try
        {
            // get a reference to the storage bucket!
            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference with an initial file path and name of title.
            // This is hopefully where your file will be found in cloud storage
            pathReference = storageRef.child("images/" + story.getTitle());

            // temp path just to test
            //pathReference = storageRef.child("images/" + "download.jpg");


            // Load the image using Glide
            Glide.with(context /* context*/).using(new FirebaseImageLoader())
                    .load(pathReference)
                    .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                    .error(R.drawable.ocean)
                    .centerCrop()
                    .into(holder.ivStoryImage);
        }
        // an error will be thrown when a story has no picture
        // a little jank but good for now I guess
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }

}