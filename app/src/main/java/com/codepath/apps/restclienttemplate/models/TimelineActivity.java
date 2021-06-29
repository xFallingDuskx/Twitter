package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;

    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);

        // Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        // Set up recycler view:
            // Layout manager
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
            // Adapter
        rvTweets.setAdapter(adapter);

        // Define button
        btnLogout = findViewById(R.id.btnLogout);

        populateHomeTimeline();

        // When someone has clicked on the Logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.clearAccessToken(); // forget who's logged in
                finish(); // navigate backwards to Login screen
                Toast.makeText(getApplicationContext(), "You've been logged out", Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                // Handle intent to take user to ComposeActivity
                Intent intent = new Intent(TimelineActivity.this, ComposeActivity.class);
                // TODO: request code
                this.startActivityForResult(intent, REQUEST_CODE);
                Log.i(TAG, "onOptionsItem Selected - Compose menu item has been selected");
                return true;
            // another case - when a different menu item is selected
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    // request code is what we are getting back
    // resultCode is from Android
    //data is what the child activity is sending back
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
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
        }

        super.onActivityResult(requestCode, resultCode, data);


    }
}