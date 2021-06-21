package com.prayosof.yvideo.view.activity.audioEffects;

import android.content.SharedPreferences;

/**
 * Created by Yogesh Y. Nikam on 26/07/20.
 */
public class AudioPreferences {
    public static SharedPreferences preferences;
    public static SharedPreferences.Editor edit_preferences;
    static AudioPreferences singleton;
    public String app = "Ultra_HD_Preference";

    public AudioPreferences() {
    }

    public static AudioPreferences getInstance() {
        if (singleton == null) {
            singleton = new AudioPreferences();
        }

        return singleton;
    }

    public void setAppTheme(String themeColor, String statusBarColor) {
        edit_preferences.putString("actionBar_color", themeColor).commit();
        edit_preferences.putString("statusBar_color", statusBarColor).commit();
    }
}

