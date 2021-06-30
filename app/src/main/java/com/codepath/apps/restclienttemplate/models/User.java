package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User {

    public static final String TAG = "User";

    public String name;
    public String screenName; // aka the handle
    public String profileImageURL;

    // Empty constructor for Parceler library
    public User() {}

    public static User fromJson(JSONObject jsonObject) throws JSONException {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");

        try {
            user.profileImageURL = jsonObject.getString("profile_image_url_https");
            Log.i(TAG, "Profile image URL is: " + user.profileImageURL);
        } catch (JSONException e) {
            user.profileImageURL = null;
            Log.e(TAG, "JSON Exception - Failed to get media URL", e);
        }

        return user;
    }
}
