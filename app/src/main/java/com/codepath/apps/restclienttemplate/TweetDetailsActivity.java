package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {

    Tweet tweet;
    public static final String TAG = "TweetDetailsActivity";

    ImageView ivProfileImage_Details;
    TextView tvName_Details;
    TextView tvScreenName_Details;
    TextView tvBody_Details;
    ImageView ivComments_Details;
    ImageView ivRetweets_Details;
    ImageView ivFavorites_Details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ivProfileImage_Details = findViewById(R.id.ivProfileImage_Details);
        tvName_Details = findViewById(R.id.tvName_Details);
        tvScreenName_Details = findViewById(R.id.tvScreenName_Details);
        tvBody_Details = findViewById(R.id.tvBody_Details);
        ivComments_Details = findViewById(R.id.ivComments_Details);
        ivRetweets_Details = findViewById(R.id.ivRetweets_Details);
        ivFavorites_Details = findViewById(R.id.ivFavorites_Details);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for the user '%s'", tweet.user));

        // Setting TextViews
        tvName_Details.setText(tweet.getUser().getName());
        tvScreenName_Details.setText("@" + tweet.getUser().getScreenName());
        tvBody_Details.setText(tweet.getBody());

        // Defining ImageView
        Glide.with(this)
                .load(tweet.getUser().getProfileImageURL())
                .centerCrop() // scale image to fill the entire ImageView
                .transform(new CircleCrop())
                .into(ivProfileImage_Details);
    }
}