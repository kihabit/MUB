package com.prayosof.yvideo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSessionManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.CreateNotification;
import com.prayosof.yvideo.helper.PlaybackStatus;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.interfaces.Playable;
import com.prayosof.yvideo.view.activity.AudioPlayerActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import androidx.core.app.NotificationManagerCompat;
import denis.musicplayer.data.media.model.Track;

/**
 * Created by Yogesh Y. Nikam on 01/09/20.
 */
public class NewMediaPlayerService extends Service implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,

        AudioManager.OnAudioFocusChangeListener {
    NotificationManager notificationManager;

   // BroadcastReceiver broadcastReceiver;
   private boolean musicTag = false;

    public static final String ACTION_PLAY = "ACTION_PLAY";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "ACTION_NEXT";
    public static final String ACTION_STOP = "ACTION_STOP";
    private final String MEDIA_CHANNEL_ID = "media_playback_channel";

    private MediaPlayer mediaPlayer;

    private static int sessionId = 0;

    int dr = 0;
    //MediaSession
    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    SessionManager sessionManager;
    //AudioPlayer notification ID
    private static final int NOTIFICATION_ID = 101;

    //Used to pause/resume MediaPlayer
    private int resumePosition;

    //AudioFocus
    private AudioManager audioManager;

    // Binder given to clients
    private final IBinder iBinder = new LocalBinder();

    //List of available Audio files
    private ArrayList<Track> audioList;
    private int audioIndex = -1;
    private Track activeAudio; //an object on the currently playing audio


    //Handle incoming phone calls
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private static boolean isRunning = false;
    private static boolean isplaying = false;
    private static boolean isPrepared = false;
    private static boolean isBroadcastRegistered = false;


    public static String songTitle = "";

    private Playable myCallback;
    /**
     * Service lifecycle methods
     */
    @Override
    public IBinder onBind(Intent intent) {
        //showCustomToast("on bind");
        return iBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        showCustomToast("on re bind");

    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    //The system calls this method when an activity, requests the service be started
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        sessionManager = new SessionManager(this);
        try {
            // You only need to create the channel on API 26+ devices
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannel();
            }
            //audioList.clear();
            if(Objects.requireNonNull(intent.getExtras()).containsKey("selectedTrackData")){
                audioList = (ArrayList<Track>) intent.getExtras().getSerializable("selectedTrackData");
                audioIndex = intent.getIntExtra("position",-1);
                //MUSIC_PATH = audioList.get(position).getData();
            }


            Log.e("audioList",audioList+"");
            Log.e("audioIndex",audioIndex+"");


            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        //Request audio focus
        if (requestAudioFocus() == false) {
            //Could not gain focus
            stopSelf();
        }

        if (mediaSessionManager == null) {
            try {
                initMediaSession();
                initMediaPlayer();
            } catch (RemoteException e) {
                e.printStackTrace();
                stopSelf();
            }
          //  buildNotification(PlaybackStatus.PLAYING);
        }

        //Handle Intent action from MediaSession.TransportControls
        handleIncomingActions(intent);

        // Perform one-time setup procedures

        // Manage incoming phone calls during playback.
        // Pause MediaPlayer on incoming call,
        // Resume on hangup.
        callStateListener();
        //ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs -- BroadcastReceiver
        registerBecomingNoisyReceiver();
        //Listen for new Audio to play -- BroadcastReceiver
        register_playNewAudio();
        register_notification_listner();
        register_screenlock_broadcast();
        isBroadcastRegistered = true;
        return super.onStartCommand(intent, flags, startId);
    }



    /**
     * Service Binder
     */
    public class LocalBinder extends Binder {
        public NewMediaPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return NewMediaPlayerService.this;
        }
    }


    /**
     * MediaPlayer callback methods
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        //Invoked indicating buffering status of
        //a media resource being streamed over the network.
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

        if (sessionManager.getRepeatMode()) {
            transportControls.play();
        }
        else {
            transportControls.skipToNext();
        }
        //Invoked when playback of a media source has completed.
        //stopMedia();

        //removeNotification();
        //stop the service

        //stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //Invoked when there has been an error during an asynchronous operation
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED " + extra);
                break;
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN " + extra);
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //Invoked to communicate some info
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        sessionId = mediaPlayer.getAudioSessionId();
        dr = mediaPlayer.getDuration();
        isPrepared = true;
        //Invoked when the media source is ready for playback.
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        //Invoked indicating the completion of a seek operation.
    }

//    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
//            new AudioManager.OnAudioFocusChangeListener() {
//                public void onAudioFocusChange(int focusChange) {
//                    AudioManager am =(AudioManager)getSystemService(Context.AUDIO_SERVICE);
//                    switch (focusChange) {
//
//                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) :
//                            Log.e("CAN_DUCK","CAN_DUCK");
//                            break;
//                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) :
//                            Log.e("LOSS_TRANSIENT","LOSS_TRANSIENT");
//
//                            break;
//
//                        case (AudioManager.AUDIOFOCUS_LOSS) :
//                            Log.e("AUDIOFOCUS_LOSS","AUDIOFOCUS_LOSS");
//
//                            break;
//
//                        case (AudioManager.AUDIOFOCUS_GAIN) :
//                            Log.e("AUDIOFOCUS_GAIN","AUDIOFOCUS_GAIN");
//
//                            break;
//                        default: break;
//                    }
//                }
//            };

    @Override
    public void onAudioFocusChange(int focusState) {

        //Invoked when the audio focus of the system is updated.
        switch (focusState) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.e("AUDIOFOCUS_GAIN","AUDIOFOCUS_GAIN");

                // resume playback
                if (mediaPlayer == null) initMediaPlayer();
                else if (!mediaPlayer.isPlaying()) mediaPlayer.start();
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.e("AUDIOFOCUS_LOSS","AUDIOFOCUS_LOSS");

                // Lost focus for an unbounded amount of time: stop playback and release media player
                if (mediaPlayer.isPlaying())
                //mediaPlayer.pause();
                transportControls.pause();
//                mediaPlayer.reset();
//                mediaPlayer.release();
//                mediaPlayer = null;

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.e("LOSS_TRANSIENT","LOSS_TRANSIENT");

                // Lost focus for a short time, but we have to stop
                // playback. We don't release the media player because playback
                // is likely to resume
                if (mediaPlayer.isPlaying()) mediaPlayer.pause();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                Log.e("CAN_DUCK","CAN_DUCK");
                // Lost focus for a short time, but it's ok to keep playing
                // at an attenuated level
                if (mediaPlayer.isPlaying()) mediaPlayer.setVolume(0.1f, 0.1f);
                break;
            default:
                Log.e("focusState",focusState+"");
                break;
        }
    }


    /**
     * AudioFocus
     */
    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e("REQUEST_GRANTED","REQUEST_GRANTED");
            //Focus gained
            return true;
        }
        //Could not gain focus
        return false;
    }

    private boolean removeAudioFocus() {
        Log.e("abandonAudioFocus","abandonAudioFocus");
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                audioManager.abandonAudioFocus(this);
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static boolean isplaying() {
        return isplaying;
    }




    /**
     * MediaPlayer actions
     */
    private void initMediaPlayer() {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();//new MediaPlayer instance

        //Set up MediaPlayer event listeners
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        //Reset so that the MediaPlayer is not pointing to another data source
        mediaPlayer.reset();


        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // Set the data source to the mediaFile location
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            e.printStackTrace();

            stopSelf();
        }
        mediaPlayer.prepareAsync();
//        isPrepared= false;
    }

    public void playMedia() {
        isRunning = true;
        isplaying = true;
        songTitle  = audioList.get(audioIndex).getTitle();
        sendBroadcast(new Intent(AudioPlayerActivity.UPDATE_NEXT_SONG));

        if (myCallback != null) {
            myCallback.onTrackPlay(activeAudio.getTitle());
        } else {
            Log.e("Playable", "Null");
        }

        if (!mediaPlayer.isPlaying()) {
            CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                    R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);
            mediaPlayer.start();
        }
    }

    public void stopMedia() {
        myCallback.onTrackPause();

        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                R.drawable.exo_controls_play, audioIndex, audioList.size()-1);
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    public void pauseMedia() {
        isplaying = false;

        myCallback.onTrackPause();

        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                R.drawable.exo_controls_play, audioIndex, audioList.size()-1);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    public void resumeMedia() {
        isRunning = true;
        isplaying = true;

        myCallback.onTrackResume();
        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    public void playNewAudio(int mAudioIndex) {

            audioIndex = mAudioIndex;
        activeAudio = audioList.get(mAudioIndex);
            Log.e("nowaudioIndex", mAudioIndex+"");
            Log.e("datas",audioList.get(audioIndex)+"");

        stopMedia();
        //reset mediaPlayer
        //mediaPlayer.reset();
        initMediaPlayer();
//        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
//                R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);

    }

    public void skipToNext() {
        myCallback.onTrackNext(audioList.get(audioIndex));
        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);

        if (audioIndex == audioList.size() - 1) {
            //if last in playlist
            audioIndex = 0;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get next in playlist
            if (sessionManager.getShuffleToggle()) {

                final int random = new Random().nextInt((audioList.size() - 1 - 0) + 1) + 0;
                activeAudio = audioList.get(random);

            } else {
                activeAudio = audioList.get(++audioIndex);
            }

        }


        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }

    public void skipToPrevious() {

        myCallback.onTrackPrevious();

        CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);
        if (audioIndex == 0) {
            //if first in playlist
            //set index to the last of audioList
            audioIndex = audioList.size() - 1;
            activeAudio = audioList.get(audioIndex);
        } else {
            //get previous in playlist
            activeAudio = audioList.get(--audioIndex);
        }


        stopMedia();
        //reset mediaPlayer
        mediaPlayer.reset();
        initMediaPlayer();
    }


    /**
     * ACTION_AUDIO_BECOMING_NOISY -- change in audio outputs
     */
    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //pause audio on ACTION_AUDIO_BECOMING_NOISY
            pauseMedia();
           // buildNotification(PlaybackStatus.PAUSED);
        }
    };

    private void registerBecomingNoisyReceiver() {
        //register after getting audio focus
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    /**
     * Handle PhoneState changes
     */
    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Phone idle. Start playing.
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                resumeMedia();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    /**
     * MediaSession and Notification actions
     */
    private void initMediaSession() throws RemoteException {
        if (mediaSessionManager != null) return; //mediaSessionManager exists

        mediaSessionManager = (MediaSessionManager) getSystemService(Context.MEDIA_SESSION_SERVICE);
        // Create a new MediaSession
        mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
        //Get MediaSessions transport controls
        transportControls = mediaSession.getController().getTransportControls();
        //set MediaSession -> ready to receive media commands
        mediaSession.setActive(true);
        //indicate that the MediaSession handles transport control commands
        // through its MediaSessionCompat.Callback.
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        //Set mediaSession's MetaData
        updateMetaData();

        // Attach Callback to receive MediaSession updates
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            // Implement callbacks
            @Override
            public void onPlay() {
                super.onPlay();

                resumeMedia();
              //  buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onPause() {
                super.onPause();

                pauseMedia();
                CreateNotification.createNotification(getApplicationContext(),mediaSession, audioList.get(audioIndex),
                        R.drawable.exo_controls_play, audioIndex, audioList.size()-1);
                //buildNotification(PlaybackStatus.PAUSED);
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();

                skipToNext();
                updateMetaData();
                CreateNotification.createNotification(getApplicationContext(),mediaSession, audioList.get(audioIndex),
                        R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);
               // buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();

                skipToPrevious();
                updateMetaData();
                //buildNotification(PlaybackStatus.PLAYING);
            }

            @Override
            public void onStop() {
                super.onStop();
                isRunning = false;

                removeNotification();
                //Stop the service
                stopSelf();
            }

            @Override
            public void onSeekTo(long position) {
                super.onSeekTo(position);
            }
        });
    }

    private void updateMetaData() {
        if (activeAudio != null) {
            Bitmap albumArt = BitmapFactory.decodeResource(getResources(),
                    R.drawable.brand_logo); //replace with medias albumArt
            // Update the current metadata
            mediaSession.setMetadata(new MediaMetadataCompat.Builder()
                    .putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, activeAudio.getArtist())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, activeAudio.getArtist())
                    .putString(MediaMetadataCompat.METADATA_KEY_TITLE, activeAudio.getTitle())
                    .build());
        }

    }

   /* private void buildNotification(PlaybackStatus playbackStatus) {

        *//**
         * Notification actions -> playbackAction()
         *  0 -> Play
         *  1 -> Pause
         *  2 -> Next track
         *  3 -> Previous track
         *//*

        int notificationAction = android.R.drawable.ic_media_pause;//needs to be initialized
        PendingIntent play_pauseAction = null;

        //Build a new notification according to the current state of the MediaPlayer
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            //create the pause action
            play_pauseAction = playbackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            //create the play action
            play_pauseAction = playbackAction(0);
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.brand_logo); //replace with your own image

        // Create a new Notification
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this, MEDIA_CHANNEL_ID)
                // Hide the timestamp
                .setShowWhen(false)
                // Set the Notification style
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        // Attach our MediaSession token
                        .setMediaSession(mediaSession.getSessionToken())
                        // Show our playback controls in the compat view
                        .setShowActionsInCompactView(0, 1, 2))
                // Set the Notification color
                .setColor(getResources().getColor(R.color.colorAccent))
                // Set the large and small icons
                .setLargeIcon(largeIcon)
                .setSmallIcon(android.R.drawable.stat_sys_headset)
                // Set Notification content information
                .setContentText(activeAudio.getArtist())
                .setContentTitle(activeAudio.getArtist())
                .setContentInfo(activeAudio.getTitle())
                // Add playback actions
                .addAction(android.R.drawable.ic_media_previous, "previous", playbackAction(3))
                .addAction(notificationAction, "pause", play_pauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", playbackAction(2));

        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notificationBuilder.build());
    }*/


    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, NewMediaPlayerService.class);
        switch (actionNumber) {
            case 0:
                // Play
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                // Pause
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                // Next track
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                // Previous track
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void removeNotification() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.cancelAll();
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;

        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    public void showCustomToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
    /**
     * Play new Audio
     */
    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(Objects.requireNonNull(intent.getExtras()).containsKey("selectedTrackData")){
                audioList.clear();
                audioList = (ArrayList<Track>) intent.getExtras().getSerializable("selectedTrackData");
                audioIndex = intent.getIntExtra("position",-1);
                //MUSIC_PATH = audioList.get(position).getData();
            }


            Log.e("audioList",audioList+"");
            Log.e("audioIndex",audioIndex+"");
            //Get the new media index form SharedPreferences
            //audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                //index is in a valid range
                activeAudio = audioList.get(audioIndex);
            } else {

                stopSelf();
            }

            //A PLAY_NEW_AUDIO action received
            //reset mediaPlayer to play the new Audio
            stopMedia();
            mediaPlayer.reset();
            initMediaPlayer();
            updateMetaData();
           // buildNotification(PlaybackStatus.PLAYING);
        }
    };

    public void playOrPause() {
        if (mediaPlayer ==null) {
            showCustomToast("Invalid operation");
        } else {
            try{
                if (mediaPlayer.isPlaying()){
                    transportControls.pause();
                } else {
                    transportControls.play();


                }
            } catch (IllegalStateException e){
                e.printStackTrace();
            }

        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("pappu_notification");

            assert action != null;
            switch (action){
                case CreateNotification.ACTION_PREVIUOS:
                    //myCallback.onTrackPrevious();
                   // onTrackPrevious();
                    //skipToPrevious();
                        transportControls.skipToPrevious();

                    break;
                case CreateNotification.ACTION_PLAY:

                   playOrPause();
                    //transportControls.play();


                    break;

                case CreateNotification.ACTION_NEXT:
                    transportControls.skipToNext();
                    //myCallback.onTrackNext(selectedTrackData.get(position));
                    //onTrackNext(selectedTrackData.get(position));
                    break;

                case CreateNotification.ACTION_CLOSE_NOTIFICATION:
                    AudioPlayerActivity.fa.finish();
                    onDestroy();
                    //transportControls.skipToNext();
                    //myCallback.onTrackNext(selectedTrackData.get(position));
                    //onTrackNext(selectedTrackData.get(position));
                    break;
            }
        }
    };

    private BroadcastReceiver screenlock_broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
            {

                showNotification();
                Log.v("$$$$$$", "In Method:  ACTION_SCREEN_OFF");
                // onPause() will be called.
            }
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON))
            {
                showNotification();

                Log.v("$$$$$$", "In Method:  ACTION_SCREEN_ON");
//onResume() will be called.

//Better check for whether the screen was already locked
// if locked, do not take any resuming action in onResume()

                //Suggest you, not to take any resuming action here.
            }
            else if(intent.getAction().equals(Intent.ACTION_USER_PRESENT))
            {                showNotification();

                Log.v("$$$$$$", "In Method:  ACTION_USER_PRESENT");
//Handle resuming events
            }

        }
    };

    public void registerCallBack(Playable myCallback){
        this.myCallback= myCallback;
    }

    private void register_notification_listner() {
        registerReceiver(broadcastReceiver, new IntentFilter("PAPPU_TRACKS"));
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
    }


    private void register_screenlock_broadcast() {

        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);

        registerReceiver(screenlock_broadcast, filter);
    }




    private void register_playNewAudio() {
        //Register playNewMedia receiver
        IntentFilter filter = new IntentFilter(AudioPlayerActivity.Broadcast_PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }


 private void showNotification() {
     if (isplaying) {

             CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                     R.drawable.exo_controls_pause, audioIndex, audioList.size()-1);
             //mediaPlayer.start();

     } else {
         CreateNotification.createNotification(this, mediaSession, audioList.get(audioIndex),
                 R.drawable.exo_controls_play, audioIndex, audioList.size()-1);
     }
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

    public int getDuration() {
        try {
            if (mediaPlayer != null) {
                return dr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null) {

                    try {
                        return mediaPlayer.getCurrentPosition();
                    }
                    catch(IllegalStateException e){
                        e.printStackTrace();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
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

    public static int getSessionId() {
        return sessionId;
    }

    public static String getSongTitle() {
        return songTitle;
    }

//    private void createChannel() {
//        // The id of the channel.
//        String id = MEDIA_CHANNEL_ID;
//        // The user-visible name of the channel.
//        CharSequence name = "Media playback";
//        // The user-visible description of the channel.
//        String description = "Media playback controls";
//        int importance;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            importance = NotificationManager.IMPORTANCE_LOW;
//        } else {
//            importance = 0;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
//            // Configure the notification channel.
//            mChannel.setDescription(description);
//            mChannel.setShowBadge(false);
//            mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(
//                    mChannel
//            );
//
//        }
//    }



    @Override
    public boolean onUnbind(Intent intent) {
        if(mediaSession!=null)
        mediaSession.release();
        removeNotification();
        return super.onUnbind(intent);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        isplaying = false;
        if (myCallback != null) {
            myCallback.onServiceStoped();
        }

        if (isBroadcastRegistered) {
            //unregister BroadcastReceivers
            if (becomingNoisyReceiver != null) {

                try {
                    unregisterReceiver(becomingNoisyReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }
            if (playNewAudio != null) {
                try {
                    unregisterReceiver(playNewAudio);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            if (broadcastReceiver != null) {
                try {
                    unregisterReceiver(broadcastReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }

            if (screenlock_broadcast != null) {
                try {
                    unregisterReceiver(screenlock_broadcast);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }

            }

        }

        Log.w("Service Media Player" , "Stopper");
        if (mediaPlayer != null) {
//            if (mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//            }
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        removeAudioFocus();
        //Disable the PhoneStateListener
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        removeNotification();


        //clear cached playlist
        audioList.clear();
        mediaSessionManager = null;
        stopSelf();

    }
}
