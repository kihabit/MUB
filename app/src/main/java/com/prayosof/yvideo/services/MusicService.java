package com.prayosof.yvideo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.CreateNotification;
import com.prayosof.yvideo.interfaces.Playable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import denis.musicplayer.data.media.model.Track;

public class MusicService extends Service implements Playable, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = MusicService.class.getSimpleName();
    private static boolean isRunning = false;

    private static int sessionId = 0;



    public static  String MUSIC_PATH = "";

    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_REWIND = "action_rewind";
    public static final String ACTION_FAST_FORWARD = "action_fast_foward";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String NEW_AUDIO = "new_audio";


    static ArrayList<Track> selectedTrackData =null;
    //Environment.getExternalStorageDirectory().getPath() + File.separator + "music.mp3";

    private MediaPlayer mediaPlayer;
    private boolean musicTag = false;
    NotificationManager notificationManager;
    int position = 0;
    BroadcastReceiver broadcastReceiver;

    private Playable myCallback;

    private BroadcastReceiver receiver;

    public void registerCallBack(Playable myCallback){
        this.myCallback= myCallback;
    }

//    public MusicService() {
//
//        mediaPlayer = new MediaPlayer();
//    }

    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public MusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicService.this;
        }
    }

    private MyBinder binder = new MyBinder();

    public int getDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void seekTo(int progress) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(progress);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isTag() {
        return musicTag;
    }

    public void setTag(boolean tag) {
        this.musicTag = tag;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
//        if (mediaPlayer != null) {
//            mediaPlayer = MediaPlayer.create(this, Uri.parse(MUSIC_PATH));
//            mediaPlayer.start();
//        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onAudioFocusChange(int focusChange) {

    }

    @Override
    public void onTrackPrevious() {
        prevBtnClicked();
//        CreateNotification.createNotification(this, selectedTrackData.get(position),
//                R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);
    }

    @Override
    public void onTrackPlay(String songName) {
        mediaPlayer.start();
//        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mp.start();
//            }
//        });
//        CreateNotification.createNotification(this, selectedTrackData.get(position),
//                R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);

    }

    @Override
    public void onTrackPause() {
        mediaPlayer.pause();
//        CreateNotification.createNotification(this, selectedTrackData.get(position),
//                R.drawable.exo_controls_play, position, selectedTrackData.size()-1);
    }

    @Override
    public void onTrackNext(Track track) {
        nextBtnClicked();
//        CreateNotification.createNotification(this, selectedTrackData.get(position),
//                R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);
    }

    @Override
    public void onTrackResume() {

    }

    @Override
    public void onServiceStoped() {

    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playOrPause() {
        if (mediaPlayer.isPlaying()) {

            onTrackPause();
        } else {

            onTrackPlay("name");
        }
    }

    public void nextBtnClicked() {
        myCallback.onTrackNext(selectedTrackData.get(position));
        position++;
        MUSIC_PATH = selectedTrackData.get(position).getData();

        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(MUSIC_PATH);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prevBtnClicked() {
        if (position != 0) {
            position--;
            MUSIC_PATH = selectedTrackData.get(position).getData();

            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MUSIC_PATH);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "This is first song", Toast.LENGTH_LONG).show();
        }

    }
    public void stop() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MUSIC_PATH);
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("ServiceOnUnBind", "ServiceOnUnBind");

        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {


        if(Objects.requireNonNull(intent.getExtras()).containsKey("selectedTrackData")){
            selectedTrackData = (ArrayList<Track>) intent.getExtras().getSerializable("selectedTrackData");
            position = intent.getIntExtra("position",-1);
            MUSIC_PATH = selectedTrackData.get(position).getData();
        }

        if (mediaPlayer == null) {

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);

            try {

                mediaPlayer.setDataSource(MUSIC_PATH);
                mediaPlayer.prepare();
                playOrPause();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sessionId = mediaPlayer.getAudioSessionId();
        }

//        if (mediaPlayer == null) {
//
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnCompletionListener(this);
//
//            try {
//
//                mediaPlayer.setDataSource(MUSIC_PATH);
//                Log.e("ithe ala","ithe");
//                mediaPlayer.prepare();
//                playOrPause();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            sessionId = mediaPlayer.getAudioSessionId();
//        }

//        mediaPlayer.release();
//        mediaPlayer = null;
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.setOnCompletionListener(this);
//
//        try {
//
//            mediaPlayer.setDataSource(MUSIC_PATH);
//            mediaPlayer.prepare();
//            playOrPause();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        sessionId = mediaPlayer.getAudioSessionId();
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        isRunning = true;
        //register_playNewAudio();

//        mediaPlayer = new MediaPlayer();
//        try {
//
//                mediaPlayer.stop();
//
//            mediaPlayer.setDataSource(getApplicationContext(),Uri.parse(MUSIC_PATH));
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();

        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getExtras().getString("pappu_notification");

                assert action != null;
                switch (action){
                    case CreateNotification.ACTION_PREVIUOS:
                        myCallback.onTrackPrevious();
                        onTrackPrevious();
                        break;
                    case CreateNotification.ACTION_PLAY:
                        if (mediaPlayer ==null) {
                            showToast();
                        } else {
                            if (mediaPlayer.isPlaying()){
                                onTrackPause();
                                myCallback.onTrackPause();

                            } else {
                                onTrackPlay("name");
                                myCallback.onTrackPlay("song name");

                            }
                        }

                        break;

                    case CreateNotification.ACTION_NEXT:
                        myCallback.onTrackNext(selectedTrackData.get(position));
                        onTrackNext(selectedTrackData.get(position));
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("PAPPU_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {

                Bundle args = intent.getBundleExtra("DATA");
                selectedTrackData = (ArrayList<Track>) args.getSerializable("selectedTrackData");
                position = args.getInt("position",-1);
                MUSIC_PATH = selectedTrackData.get(position).getData();
//                if(Objects.requireNonNull(intent.getExtras()).containsKey("selectedTrackData")){
//                    selectedTrackData = (ArrayList<Track>) intent.getExtras().getSerializable("selectedTrackData");
//                    position = intent.getIntExtra("position",-1);
//                    MUSIC_PATH = selectedTrackData.get(position).getData();
//                }

                Log.e("position_2", position+"");

                try {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(MUSIC_PATH);
                    mediaPlayer.prepareAsync();
                    playOrPause();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("RECEIVER_FILTER"));

//        Log.d(TAG, "onCreate: SD card path = " + MUSIC_PATH);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            String channelId = "001";
//            String channelName = "myChannel";
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
//            channel.setLightColor(Color.BLUE);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            if (manager != null) {
//                manager.createNotificationChannel(channel);
//                Notification notification;
//
//                notification = new Notification.
//                        Builder(getApplicationContext(), channelId).setOngoing(true).setSmallIcon(R.mipmap.ic_launcher).setCategory(Notification.CATEGORY_SERVICE).build();
//
//                startForeground(101, notification);
//            }
//        } else {
//            startForeground(101, new Notification());
//        }
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "pplayer", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showToast()
    {
        Toast.makeText(this, "mediaplayer is null", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) mediaPlayer.release();

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            notificationManager.cancelAll();
//        }

        unregisterReceiver(broadcastReceiver);

    }


    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Toast.makeText(getApplicationContext(), "re binding", Toast.LENGTH_SHORT).show();



    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;



//        if(Objects.requireNonNull(intent.getExtras()).containsKey("selectedTrackData")){
//            selectedTrackData = (ArrayList<Track>) intent.getExtras().getSerializable("selectedTrackData");
//            position = intent.getIntExtra("position",-1);
//            MUSIC_PATH = selectedTrackData.get(position).getData();
//        }
//
//        if (mediaPlayer == null) {
//
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnCompletionListener(this);
//
//            try {
//
//                mediaPlayer.setDataSource(MUSIC_PATH);
//                mediaPlayer.prepare();
//                playOrPause();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            sessionId = mediaPlayer.getAudioSessionId();
//        }

//
//        mediaPlayer = MediaPlayer.create(this, Uri.parse(MUSIC_PATH));
//        mediaPlayer.setLooping(true);

        return super.onStartCommand(intent, flags, startId);

    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static int getSessionId() {
        return sessionId;
    }


    public void resetMediaPlayer() {

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepareAsync();
                mediaPlayer.reset();
                mediaPlayer.setDataSource(MUSIC_PATH);
                mediaPlayer.setLooping(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

        }

    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);

        mediaPlayer.stop();

    }


//    public void showToast(){
//        Toast.makeText(this, "Next playNewAudio BroadcastReceiver", Toast.LENGTH_LONG).show();
//    }
    /**
     * Play new Audio
     */
//    private BroadcastReceiver newPlayAudio = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            mediaPlayer.stop();
//            showToast();
//
//        }
//    };
//
//    private void register_playNewAudio() {
//        IntentFilter filter = new IntentFilter(AudioPlayerActivity.Broadcast_NEW_PLAY_AUDIO);
//        registerReceiver(newPlayAudio, filter);
//    }




}

