package org.dreamitcodeit.storyteller;

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

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by colemanmav on 7/17/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private List<Story> stories;

    StorageReference pathReference;

    Context context;


    public StoryAdapter(List<Story> stories){
        this.stories = stories;
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View storyView = inflater.inflate(R.layout.item_story, parent, false);
        ViewHolder viewHolder = new ViewHolder(storyView);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.tvStoryBody.setText(story.getStoryBody());
        holder.tvAuthorName.setText(String.format("By %s",story.getScreenName()));//TODO: optimize for i18n with string resource
        holder.tvDate.setText(story.getDate());

        if(story.getIsCheckedIn()){
            holder.ivIsCheckedIn.setVisibility(View.VISIBLE);
            holder.tvIsCheckedIn.setVisibility(View.VISIBLE);
        }
        else{
            holder.ivIsCheckedIn.setVisibility(View.INVISIBLE);
            holder.tvIsCheckedIn.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();

    }

    public void add(int index, Story story){
        stories.add(index, story);
        notifyItemInserted(index);//TODO: test for crashes
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


            try
            {
                // get a reference to the storage bucket!
                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();

                // Create a reference with an initial file path and name of title.
                // This is hopefully where your file will be found in cloud storage
                pathReference = storageRef.child("images/" + tvTitle.getText().toString().trim());

                // temp path just to test
                //pathReference = storageRef.child("images/" + "download.jpg");


                // Load the image using Glide
                Glide.with(context /* context */).using(new FirebaseImageLoader())
                        .load(pathReference)
                        .error(R.drawable.ocean)
                        .bitmapTransform(new RoundedCornersTransformation(context, 15, 0))
                        .centerCrop()
                        .into(ivStoryImage);
            }
            // an error will be thrown when a story has no picture
            // a little jank but good for now I guess
            catch (Exception e)
            {
                e.printStackTrace();
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ViewStoryActivity.class);
                    intent.putExtra("title", tvTitle.getText().toString());
                    intent.putExtra("storyBody", tvStoryBody.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }


    }
}