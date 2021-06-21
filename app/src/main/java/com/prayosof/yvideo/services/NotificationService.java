package com.prayosof.yvideo.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.Constants;
import com.prayosof.yvideo.view.activity.PlayerActivity;

/**
 * Created by Yogesh Y. Nikam on 01/08/20.
 */
public class NotificationService extends Service {

    NotificationManager mNotificationManager;
    public static final String ACTION_PREVIUOS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";
    public static final String ACTION_ON_CLICK = "action_on_click";

    Notification status;

    RemoteViews bigViews;

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            showNotification();

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {


        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.exo_controls_play);

        } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
            bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.exo_controls_pause);

        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

        } else if (intent.getAction().equals(
                Constants.ACTION.STOPFOREGROUND_ACTION)) {
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification

        bigViews = new RemoteViews(getPackageName(),
                R.layout.status_bar_expanded);

        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, PlayerActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        PendingIntent pendingIntentPrevious;
        Intent intentPrevious = new Intent(this, NotificationActionService.class)
                .setAction(ACTION_PREVIUOS);
        pendingIntentPrevious = PendingIntent.getBroadcast(this, 0,
                intentPrevious, PendingIntent.FLAG_UPDATE_CURRENT);


//        Intent previousIntent = new Intent(this, NotificationService.class);
//        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
//        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
//                previousIntent, 0);


        Intent intentPlay = new Intent(this, NotificationActionService.class)
                .setAction(ACTION_PLAY);
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 0,
                intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent playIntent = new Intent(this, NotificationService.class);
//        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
//        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
//                playIntent, 0);


        PendingIntent pendingIntentNext;
        Intent intentNext = new Intent(this, NotificationActionService.class)
                .setAction(ACTION_NEXT);
        pendingIntentNext = PendingIntent.getBroadcast(this, 0,
                intentNext, PendingIntent.FLAG_UPDATE_CURRENT);

//        Intent nextIntent = new Intent(this, NotificationService.class);
//        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
//        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
//                nextIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pendingIntentPlay);

        bigViews.setOnClickPendingIntent(R.id.notification_btn_next, pendingIntentNext);

        bigViews.setOnClickPendingIntent(R.id.notification_btn_prev, pendingIntentPrevious);

        bigViews.setOnClickPendingIntent(R.id.closeBtn, pcloseIntent);

        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.exo_controls_pause);

        bigViews.setTextViewText(R.id.track_name, NewMediaPlayerService.getSongTitle());


        mNotificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        String channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = createChannel();
        else {
            channel = "";
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channel).setSmallIcon(android.R.drawable.ic_menu_mylocation).setContentTitle("pappu player");

        mBuilder.setContent(bigViews);
        mBuilder.setSmallIcon(R.drawable.ic_baseline_music_note_24);
        mBuilder.setContentIntent(pendingIntent);
        status = mBuilder.build();
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    @NonNull
    @TargetApi(26)
    private synchronized String createChannel() {

        String name = "snap map fake location ";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel mChannel = new NotificationChannel("snap map channel", name, importance);

        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        if (mNotificationManager != null) {
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
            stopSelf();
        }
        return "snap map channel";
    }

    private void playPauseBtnClicked() {
        if (PlayerActivity.mediaPlayer.isPlaying()) {
            bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.exo_controls_play);
            status.contentView = bigViews;
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
            PlayerActivity.mediaPlayer.pause();

        } else {


            bigViews.setImageViewResource(R.id.status_bar_play, R.drawable.exo_controls_pause);
            status.contentView = bigViews;
            mNotificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);

            PlayerActivity.mediaPlayer.start();
        }
    }


}
