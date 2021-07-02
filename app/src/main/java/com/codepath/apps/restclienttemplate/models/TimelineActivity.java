package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.EndlessRecyclerViewScrollListener;
import com.codepath.apps.restclienttemplate.LoginActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetsAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;

    // Endless Scrolling
    private EndlessRecyclerViewScrollListener scrollListener;

    // Instance of the progress action-view
    MenuItem miActionProgressItem;

    // For SwipeLayoutRefresh
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    ImageView ivLogout;
    ImageView ivCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        // Remove default title text
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        client = TwitterApp.getRestClient(this);

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Set up recycler view:
            // Layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);
            // Adapter
        rvTweets.setAdapter(adapter);

        // Define Logout ImageView in Menu
        ivLogout = findViewById(R.id.ivLogout);
        // Define compose icon to log out
        ivCompose = findViewById(R.id.ivCompose);

        populateHomeTimeline();

        // When someone has clicked on the Logout button
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.clearAccessToken(); // forget who's logged in
                finish(); // navigate backwards to Login screen
                Toast.makeText(getApplicationContext(), "You've been logged out", Toast.LENGTH_SHORT).show();
            }
        });

        // When someone has clicked on the Logout button
        ivCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle intent to take user to ComposeActivity
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                // TODO: request code
                startActivityForResult(intent, REQUEST_CODE);
                Log.i(TAG, "Compose menu item has been selected");
            }
        });

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.twitter_blue,
                R.color.twitter_blue_30,
                R.color.twitter_blue,
                R.color.twitter_blue_30);

    // Endless Scrolling
        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Always want to update the arraylist rather than replace it
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // Always want to notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + response, throwable);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.miCompose:
//                // Handle intent to take user to ComposeActivity
//                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
//                this.startActivityForResult(intent, REQUEST_CODE);
//                Log.i(TAG, "onOptionsItem Selected - Compose menu item has been selected");
//                return true;
//            // another case - when a different menu item is selected
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    // Posting new tweet
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        showProgressBar();

        // verify first
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get data from ComposeActivity intent (aka a tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
        // update the recycler view with the tweet
            // modify data source of tweets
            tweets.add(0, tweet);
            // update adapter
            adapter.notifyItemInserted(0);

            // want to scroll to the top of recyclerview after each new tweet
            rvTweets.smoothScrollToPosition(0);

            hideProgressBar();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                // Remember to CLEAR OUT old items before appending in the new ones
                adapter.clear();

                // Re-add items to the adapter
                Log.i(TAG, "Refresh onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Always want to update the arraylist rather than replace it
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // Always want to notify the adapter that the data set has changed
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "Fetch timeline error: " + throwable);
            }
        });
    }

    //  Progress Indicator - methods get reference to menu item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgressItem = menu.findItem(R.id.miActionProgress);

        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    // Progress Indicator - 2 methods to change the visibility of the menu item to show and hide it
    public void showProgressBar() {
        miActionProgressItem.setVisible(true);
    }

    public void hideProgressBar() {
        miActionProgressItem.setVisible(false);
    }

// Endless Scrolling
    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        showProgressBar();

        // Send an API request to retrieve appropriate paginated data
        final Tweet tweet = tweets.get(tweets.size() - 1);
        long tweetID = tweet.id;

        client.updateHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "Homeline updated!" + json.toString());
                Log.i(TAG, "Plus 50!");
                JSONArray jsonArray = json.jsonArray;
                try {
                    // Always want to update the arraylist rather than replace it
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    // Notify the adapter new items have been added
//                    adapter.notifyItemRangeInserted(tweets.size() - 26, tweets.size() - 1);
                    adapter.notifyDataSetChanged();

                    hideProgressBar();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Homeline update has failed!" + response, throwable);
            }
        }, 50, tweetID);
    }
}