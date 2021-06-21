package com.prayosof.yvideo.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;

import java.util.HashMap;

import static com.prayosof.yvideo.helper.Constants.FOLDER_PATH;

/**
 * Created by Yogesh Y. Nikam on 05/07/20.
 */
public class SessionManager {

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "AndroidHivePref";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String KEY_BASSBOOST_VALUE = "bassboostValue";
    public static final String KEY_ASPECT_RAIO_VALUE = "aspectratioValue";
    public static final String KEY_VIRTUALIZER_VALUE = "virtualizerValue";


    // Email address (make variable public to access from outside)
    public static final String DARK_MODE = "is_dark_mode_on";
    public static final String PINCH_ZOOM = "is_pinch_zoom_on";
    public static final String BRIGHTNESS_ENABLED = "is_brightness_enabled";
    public static final String AUTO_PLAY_NEXT = "is_auto_play_next_enabled";
    public static final String MUSIC_SERVICE = "is_service_running";

    public static final String AUDIO_INDEX = "audio_index";

    public static final String SHUFFLE_TOGGLE = "shuffle_toggle";
    public static final String REPEAT_TOGGLE = "repeat_toggle";





    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setDarkThemeMode(Boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(DARK_MODE, value);


        // commit changes
        editor.commit();
    }

    // Get darkmode value
    public boolean getDarkModeValue() {
        return pref.getBoolean(DARK_MODE, false);
    }

    public void setPinchZoomEnabled(Boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(PINCH_ZOOM, value);
        // commit changes
        editor.commit();
    }


    public void setMusicServiceRunning(Boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(MUSIC_SERVICE, value);
        // commit changes
        editor.commit();
    }


    public boolean isMusicServiceRunning() {
        return pref.getBoolean(MUSIC_SERVICE, false);
    }


    public void setAudioIndex(int value) {
        // Storing login value as TRUE
        editor.putInt(AUDIO_INDEX, value);
        // commit changese
        editor.commit();
    }


    public boolean getAudioIndex() {
        return pref.getBoolean(SHUFFLE_TOGGLE, false);
    }

    public void setShuffleToggle(boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(SHUFFLE_TOGGLE, value);
        // commit changese
        editor.commit();
    }


    public boolean getRepeatMode() {
        return pref.getBoolean(REPEAT_TOGGLE, false);
    }

    public void setRepeatMode(boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(REPEAT_TOGGLE, value);
        // commit changese
        editor.commit();
    }

    public boolean getShuffleToggle() {
        return pref.getBoolean(SHUFFLE_TOGGLE, false);
    }



    public boolean isPinchZoomEnabled() {
        return pref.getBoolean(PINCH_ZOOM, false);
    }

    public void setBrightnessEnabled(Boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(BRIGHTNESS_ENABLED, value);
        // commit changes
        editor.commit();
    }


    public boolean isBrightnessEnabled() {
        return pref.getBoolean(BRIGHTNESS_ENABLED, false);
    }

    public void setAutoPlayNextEnabled(Boolean value) {
        // Storing login value as TRUE
        editor.putBoolean(AUTO_PLAY_NEXT, value);
        // commit changes
        editor.commit();
    }


    public boolean isAutoPlayNextEnabled() {
        return pref.getBoolean(AUTO_PLAY_NEXT, false);
    }


//    Player Configurations
    public void setKeyBassboostValue(int progress) {
        editor.putInt(KEY_BASSBOOST_VALUE, progress);
        editor.commit();
    }

    public int getKeyBassboostValue() {
        return pref.getInt(KEY_BASSBOOST_VALUE, 0);
    }

    public void setKeyAspectRatioValue(int aspectRatioValue) {
        editor.putInt(KEY_ASPECT_RAIO_VALUE, aspectRatioValue);
        editor.commit();
    }

    public int getKeyAspectRatioValue() {
        return pref.getInt(KEY_ASPECT_RAIO_VALUE, AspectRatioFrameLayout.RESIZE_MODE_FIT);
    }

    public void setKeyVirtualizerValue(int progress) {
        editor.putInt(KEY_VIRTUALIZER_VALUE, progress);
        editor.commit();
    }

    public int getKeyVirtualizerValue() {
        return pref.getInt(KEY_VIRTUALIZER_VALUE, 0);
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
//            // user is not logged in redirect him to Login Activity
//            Intent i = new Intent(_context, LoginActivity.class);
//            // Closing all the Activities
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//            // Add new Flag to start new Activity
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//            // Staring Login Activity
//            _context.startActivity(i);
        }

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
//
//        // After logout redirect user to Loing Activity
//        Intent i = new Intent(_context, LoginActivity.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        // Staring Login Activity
//        _context.startActivity(i);
    }

    /**
     * Quick check for login
     **/
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void clearSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getFolderPath(Context context) {
        return getSharedPreferences(context).getString(FOLDER_PATH, "");
    }

    public void storeFolderPath(Context context, String path) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(FOLDER_PATH, path);
        editor.commit();
    }
}