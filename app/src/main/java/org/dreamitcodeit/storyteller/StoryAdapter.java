package org.dreamitcodeit.storyteller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by colemanmav on 7/17/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private List<Story> stories;

    StorageReference pathReference;

    Context context;
    private Context resources;

    int currPosition = 0;

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
        holder.currentStory = story; //Possible privacy leak TODO: make copy constructor for story class.
        holder.tvTitle.setText(story.getTitle());
        holder.tvStoryBody.setText(story.getStoryBody());
        holder.tvAuthorName.setText(story.getUserName());
        holder.tvDate.setText(story.getDate());
        holder.tvFavorites.setText(String.format("%d", story.getFavCount()));
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");

        holder.tvTitle.setTypeface(typefaceBold);
        holder.tvAuthorName.setTypeface(typeface);
        holder.tvDate.setTypeface(typeface);
        holder.tvFavorites.setTypeface(typeface);
//        holder.tvIsCheckedIn.setTypeface(typeface);

        if (story.getIsCheckedIn()) {
            holder.ivIsCheckedIn.setVisibility(View.VISIBLE);
          //  holder.tvIsCheckedIn.setVisibility(View.VISIBLE);
        } else {
            holder.ivIsCheckedIn.setVisibility(View.INVISIBLE);
            //holder.tvIsCheckedIn.setVisibility(View.INVISIBLE);
        }

        if (position%5 == 1) holder.ivStoryImage.setBackgroundResource(R.drawable.color1);
        if (position%5 == 2) holder.ivStoryImage.setBackgroundResource(R.drawable.color2);
        if (position%5 == 3) holder.ivStoryImage.setBackgroundResource(R.drawable.color3);
        if (position%5 == 4) holder.ivStoryImage.setBackgroundResource(R.drawable.color4);
        if (position%5 == 0) holder.ivStoryImage.setBackgroundResource(R.drawable.color5);

        try {
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
                    .bitmapTransform(new CenterCrop(context), new RoundedCornersTransformation(context, 15, 0))
                    //.error(R.color.color1)
                    .into(holder.ivStoryImage);
        }
        // an error will be thrown when a story has no picture
        // a little jank but good for now I guess
        catch (Exception e){
                //   holder.ivStoryImage.setImageResource(R.drawable.ocean);
                //  holder.ivStoryImage.setBackgroundResource(R.drawable.round_outline);

                //   holder.ivStoryImage.setScaleType(scaleXY);
            Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
        }

        //holder.ivGradient

        holder.tvStoryBody.bringToFront();
        holder.tvAuthorName.bringToFront();
        holder.tvTitle.bringToFront();
    }

    @Override
    public int getItemCount() {
        return stories.size();

    }

    public void clearList(){
        stories.clear();
        notifyDataSetChanged();
    }

    public void add(int index, Story story){
        stories.add(index, story);
        notifyItemInserted(index);//TODO: test for crashes
    }

    public Context getResources() {
        return resources;
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
        public ImageView ivGradient;
        public TextView tvIsCheckedIn;
        public Story currentStory;

        public ViewHolder(View itemView) {
            super(itemView);
            ivStoryImage = (ImageView) itemView.findViewById(R.id.ivStoryImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvAuthorName = (TextView) itemView.findViewById(R.id.tvAuthorName);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvStoryBody = (TextView) itemView.findViewById(R.id.tvStoryBody);
            tvFavorites = (TextView) itemView.findViewById(R.id.tvFavorites);
            ivFavoriteIcon = (ImageView) itemView.findViewById(R.id.ivFavoriteIcon);
            ivIsCheckedIn = (ImageView) itemView.findViewById(R.id.ivIsCheckedIn);
            ivGradient = (ImageView) itemView.findViewById(R.id.ivGradient);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ViewStoryActivity.class);
                    intent.putExtra("story", Parcels.wrap(currentStory));
                    intent.putExtra("position", currPosition);
                    v.getContext().startActivity(intent);
                }
            });

        }
    }
}