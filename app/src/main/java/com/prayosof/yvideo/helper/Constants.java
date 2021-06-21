package com.prayosof.yvideo.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.prayosof.yvideo.R;

/**
 * Created by Yogesh Y. Nikam on 01/08/20.
 */


public class Constants {

    public static final String PREF_NAME = "video_downloader";
    public static final String FOLDER_PATH = "folderpath";
    public static final String BASE_URL = "http://suggestqueries.google.com/";
    public static final int INSTAGRAM = 0;
    public static final int FACEBOOK = 1;
    public static final int DAILYMOTION = 2;
    public static final int BROWSER = 3;
    public static final String FLAG = "flag";
    public static final String WORDS = "words";
    public static String download = "download";

    public interface ACTION {
        public static String MAIN_ACTION = "com.prayosof.yvideo.action.main";
        public static String INIT_ACTION = "com.prayosof.yvideo.action.init";
        public static String PREV_ACTION = "com.prayosof.yvideo.action.prev";
        public static String PLAY_ACTION = "com.prayosof.yvideo.action.play";
        public static String NEXT_ACTION = "com.prayosof.yvideo.action.next";
        public static String STARTFOREGROUND_ACTION = "com.prayosof.yvideo.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.prayosof.yvideo.action.stopforeground";

        public static String PAUSE_ACTION = "com.prayosof.yvideo.action.pause";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public static Bitmap getDefaultAlbumArt(Context context) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            bm = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.brand_logo, options);
        } catch (Error ee) {
        } catch (Exception e) {
        }
        return bm;
    }

}
