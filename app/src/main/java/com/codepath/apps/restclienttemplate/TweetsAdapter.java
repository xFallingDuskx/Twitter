package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    // Pass in the context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    // For each row, inflate the layout for a tweet
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // Inflating the xml document to be displayed for each tweet (item_tweet.xml)
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // Bind values based on the position of the element in the list
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // Get data at the position
        Tweet tweet = tweets.get(position);

        // Bind the tweet with the view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void addOnScrollListener() {

    }

    // Define a view holder - DO THIS FIRST
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvName;
        TextView tvTimestamp;
        ImageView ivMedia;
        TextView tvReplies;
        TextView tvRetweets;
        TextView tvFavorites;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivMedia = itemView.findViewById(R.id.ivMedia);
            tvReplies = itemView.findViewById(R.id.tvReplies);
            tvRetweets = itemView.findViewById(R.id.tvRetweets);
            tvFavorites = itemView.findViewById(R.id.tvFavorites);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvName.setText(tweet.user.name);
            tvScreenName.setText("@" + tweet.user.screenName);
            Glide.with(context)
                    .load(tweet.user.profileImageURL)
                    .centerInside()
                    .transform(new CircleCrop())
                    .into(ivProfileImage);
            tvTimestamp.setText(tweet.timestamp);
            tvReplies.setText(String.valueOf(tweet.replyCount));
            tvRetweets.setText(String.valueOf(tweet.retweetCount));
            tvFavorites.setText(String.valueOf(tweet.favoriteCount));

            if (tweet.mediaUrl != null) {
                ivMedia.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(tweet.mediaUrl)
                        .into(ivMedia);
            } else {
                ivMedia.setVisibility(View.GONE);
            }
        }
    }


    // For SwipeRefreshLayout (aka Infinite Scroll)
    // Clean all elements of the recycler
    // TODO: replaced 'items' with 'tweets'
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
