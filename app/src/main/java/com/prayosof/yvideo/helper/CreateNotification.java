package com.prayosof.yvideo.helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.services.NewMediaPlayerService;
import com.prayosof.yvideo.services.NotificationActionService;
import com.prayosof.yvideo.services.NotificationService;
import com.prayosof.yvideo.view.activity.AudioPlayerActivity;
import com.prayosof.yvideo.view.activity.PlayerActivity;

import denis.musicplayer.data.media.model.Track;

public class CreateNotification {

    public static final String CHANNEL_ID = "channel1";

    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_ON_CLICK = "action_on_click";
    public static final String ACTION_CLOSE_NOTIFICATION = "close_notification";


    public static Notification notification;

    public static void createNotification(Context context, MediaSessionCompat mediaSession, Track activeAudio, int playbutton, int pos, int size){

//           Intent serviceIntent = new Intent(context, NotificationService.class);
//
//           if (playbutton == R.drawable.exo_controls_play) {
//               serviceIntent.setAction(Constants.ACTION.PAUSE_ACTION);
//           } else {
//               serviceIntent.setAction(Constants.ACTION.PLAY_ACTION);
//           }
//
//           serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//           context.startService(serviceIntent);
        PendingIntent pendingIntentPrevious;
        Intent openActivityIntent = new Intent(context, AudioPlayerActivity.class);
        openActivityIntent.putExtra("is_track_adapter", "from_mini_player");

        PendingIntent conPendingIntent = PendingIntent.getActivity(context,0,openActivityIntent,PendingIntent.FLAG_UPDATE_CURRENT);


        int drw_previous;
        if (pos == 0){
            pendingIntentPrevious = null;
            drw_previous = 0;
        } else {
            Intent intentPrevious = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PREVIUOS);
            pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                    intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_previous = R.drawable.exo_controls_previous;
        }

        Intent intentPlay = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntentNext;
        int drw_next;
        if (pos == size){
            pendingIntentNext = null;
            drw_next = 0;
        } else {
            Intent intentNext = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_NEXT);
            pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                    intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
            drw_next = R.drawable.exo_controls_next;
        }

        Intent closeIntent = new Intent(context, NotificationActionService.class)
                .setAction(ACTION_CLOSE_NOTIFICATION);
        PendingIntent pendingIntentClose = PendingIntent.getBroadcast(context, 0,
                closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = CHANNEL_ID;
        else {
            channel = "";
        }
        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        String strPlayPause="Play";
        //Build a new notification according to the current state of the MediaPlayer
        if (playbutton == R.drawable.exo_controls_play) {
            notificationAction = android.R.drawable.ic_media_play;
            strPlayPause="Pause";
            //create the pause action

        } else  {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the play action

        }

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.brand_logo); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context,channel)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(pendingIntentClose)
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(context.getResources().getColor(R.color.colorAccent))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getTitle())
                .setContentInfo(activeAudio.getTitle())

        .setOngoing(true)
        .setContentIntent(conPendingIntent)
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "Previous", pendingIntentPrevious)
                .addAction(notificationAction, strPlayPause, pendingIntentPlay)
                .addAction(android.R.drawable.ic_media_next, "Next", pendingIntentNext)
                .addAction(R.drawable.ic_baseline_close_24, "Close", pendingIntentClose);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1, notificationBuilder.build());
        /*RemoteViews bigViews;


        bigViews = new RemoteViews(context.getPackageName(),
                R.layout.status_bar_expanded);

        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(context));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompat = new MediaSessionCompat( context, "newTag");

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_brand_logo);






        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay);

        bigViews.setOnClickPendingIntent(R.id.notification_btn_next, pendingIntentNext);

        bigViews.setOnClickPendingIntent(R.id.notification_btn_prev, pendingIntentPrevious);

        bigViews.setOnClickPendingIntent(R.id.notification_btn_close, pendingIntentClose);

        //bigViews.setOnClickPendingIntent(R.id.closeBtn, pcloseIntent);

        bigViews.setImageViewResource(R.id.status_bar_play,
                playbutton);

        bigViews.setTextViewText(R.id.track_name, NewMediaPlayerService.getSongTitle());



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channel);

        mBuilder.setContent(bigViews);
        mBuilder.setSmallIcon(R.drawable.brand_logo);
        mBuilder.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        mBuilder.setLargeIcon(icon);
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(conPendingIntent);

        mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        notification = mBuilder.build();

        notificationManagerCompat.notify(1, notification);*/


    }

    public static void cancelNotifications(Context context) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.cancelAll();
    }

            //create notification
//            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.brand_logo)
//                    .setContent(bigViews)
//                    .setContentTitle(track.getTitle())
//                    .setContentText(track.getArtist())
//                    .setLargeIcon(icon)
//                    .setOnlyAlertOnce(true)//show notification for only first time
//                    .setShowWhen(false)
//                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
//                    .addAction(playbutton, "Play", pendingIntentPlay)
//                    .addAction(drw_next, "Next", pendingIntentNext)
//                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
//                        .setShowActionsInCompactView(0, 1, 2)
//                        .setMediaSession(mediaSessionCompat.getSessionToken()))
//                    .setPriority(NotificationCompat.PRIORITY_MAX)
//                    .setContentIntent(conPendingIntent)
//                    .build();
//
//
//            notificationManagerCompat.notify(1, notification);


    }