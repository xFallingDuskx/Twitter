package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Parcel
public class Tweet {

    public static final String TAG = "Tweet";

    // Timestamp abbrev formating
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public String body;
    public String createdAt;
    public User user;
    public String timestamp;
    public String mediaUrl;
    public List<User> userMentions;
    public long id;
    public int replyCount;
    public int retweetCount;
    public int favoriteCount;

    // Empty constructor needed for Parceler library
    public Tweet() {}

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    // Given a JSON object (representing the tweet), return a Tweet object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // Some tweets have "full_text" while others have "text"
        if (jsonObject.has("full_text")) {
            tweet.body = jsonObject.getString("full_text");
        } else {
            tweet.body = jsonObject.getString("text");
        }

        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.timestamp = tweet.getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.id = jsonObject.getLong("id");
        tweet.replyCount = 0; //jsonObject.getInt("reply_count");
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favoriteCount = jsonObject.getInt("favorite_count");

        // To randomize my own tweet 'mock' data
        boolean randomizeAll = tweet.retweetCount == 0 && tweet.favoriteCount == 0;
        if (randomizeAll) {
            tweet.retweetCount = ThreadLocalRandom.current().nextInt(0, 5);
            tweet.favoriteCount = ThreadLocalRandom.current().nextInt(5, 20);
        }

        // # of replies for each Tweet seems to be unavailable
        // Want a reasonable # of replies to match the # of favorites
        boolean randomizeReplies = tweet.retweetCount != 0 && tweet.favoriteCount != 0;
        if (randomizeReplies) {
            if (tweet.favoriteCount > 75) {
                tweet.replyCount = ThreadLocalRandom.current().nextInt(60, 150);
            } else if (tweet.favoriteCount > 50) {
                tweet.replyCount = ThreadLocalRandom.current().nextInt(45, 100);
            } else if (tweet.favoriteCount > 25) {
                tweet.replyCount = ThreadLocalRandom.current().nextInt(10, 50);
            } else {
                tweet.replyCount = ThreadLocalRandom.current().nextInt(5, 20);
            }
        }

        JSONObject entities = jsonObject.getJSONObject("entities");
        try {
            tweet.mediaUrl = entities.getJSONArray("media").getJSONObject(0).getString("media_url_https");
            Log.i(TAG, "Media URL is: " + tweet.mediaUrl);
        } catch (JSONException e) {
            tweet.mediaUrl = null;
            Log.i(TAG, "No media URL is available", e);
        }

        return tweet;
    }

    // Get the relative timestamp for each Tweet
    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        try {
            long time = sf.parse(rawJsonDate).getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (ParseException e) {
            Log.i("Tweet", "getRelativeTimeAgo failed");
            e.printStackTrace();
        }

        return "";

    }

    // Get a list of Tweets from a json array
    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }
}
