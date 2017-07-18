package org.dreamitcodeit.storyteller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by colemanmav on 7/17/17.
 */

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    private List<Story> stories;

    public StoryAdapter(List<Story> stories){
        this.stories = stories;
    }

    @Override
    public StoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View storyView = inflater.inflate(R.layout.item_story, parent, false);
        ViewHolder viewHolder = new ViewHolder(storyView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoryAdapter.ViewHolder holder, int position) {
        Story story = stories.get(position);
        holder.tvTitle.setText(story.getTitle());
        holder.tvStoryBody.setText(story.getStoryBody());
        holder.tvAuthorName.setText(String.format("By %s",story.getScreenName()));//TODO: optimize for i18n with string resource
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
        }
    }
}