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

    // Additional information
    public String mediaUrl;
    public List<User> userMentions; //todo: if done properly
    // todo: hashtags
    public long id;


    // Empty constructor needed for Parceler library
    public Tweet() {}

    // Given a JSON object (representing the tweet), return a Tweet object
    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.timestamp = tweet.getRelativeTimeAgo(jsonObject.getString("created_at"));
        tweet.id = jsonObject.getLong("id");

        JSONObject entities = jsonObject.getJSONObject("entities");
        try {
            tweet.mediaUrl = entities.getJSONArray("media").getJSONObject(0).getString("media_url_https");
            Log.i(TAG, "Media URL is: " + tweet.mediaUrl);
        } catch (JSONException e) {
            tweet.mediaUrl = null;
            Log.i(TAG, "No media URL is available", e);
        }


//        tweet.userMentions = getUserMentions(entities.getJSONArray("user_mentions"));

        return tweet;
    }

    // Get the relative timestamp for each Tweet
    public String getRelativeTimeAgo(String rawJsonDate) {
//        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
//        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
//        sf.setLenient(true);
//
//        String relativeDate = "";
//        try {
//            long dateMillis = sf.parse(rawJsonDate).getTime();
//            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
//                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        return relativeDate;
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

    // Get list of userMentions from the json array user_mention
//    public static List<User> getUserMentions(JSONArray jsonArray) throws JSONException {
//        List<User> userMentions = new ArrayList<>();
//        for (int i = 0; i < jsonArray.length(); i++) {
//            User user = User.fromJson(jsonArray.getJSONObject(i));
//            userMentions.add(user);
//        }
//        return userMentions;
//    }
}
