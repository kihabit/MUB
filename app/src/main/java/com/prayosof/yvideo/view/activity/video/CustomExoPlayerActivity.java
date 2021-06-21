package com.prayosof.yvideo.view.activity.video;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.material.card.MaterialCardView;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.helper.ZoomableExoPlayerView;
import com.prayosof.yvideo.view.fragment.video.EqualizerFragment;

import java.io.File;
import java.util.ArrayList;

import ua.polohalo.zoomabletextureview.ZoomableTextureView;

public class CustomExoPlayerActivity extends AppCompatActivity implements VideoRendererEventListener, Player.EventListener, View.OnClickListener {
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "CustomExoPlayerActivity";
    private SimpleExoPlayer simpleExoPlayerView;
    private SimpleExoPlayer player;
    private TextView resolutionTextView;
    String videoURL= "";
    ProgressBar progressBar;
    ImageButton ibVolumeBtn, fullScrennButton, exo_lock_screen, exo_unlock_screen, exitFullScreen, exo_equalizer, closeEq;
    //private var playbackPosition = 0L
    ZoomableExoPlayerView playerView;
    AudioManager audioManager;
    SeekBar volumeSeekBar, brightnessSeekBar, bassboostSeekBar;

    DefaultTimeBar exo_progress;
    MaterialCardView cvEqulizer;
    ImageButton exoBrightness, brightnessCloseButton, exo_back_button, exo_bass_boost, bassBoostCloseButton;

    public static Equalizer mEqualizer;

    private TextView tv;
    private LinearLayout ll;
    private MaterialCardView cvVolume;
    private MaterialCardView cvBrightness, cvBassboost;
    int i =1;
    int brightness;
    BassBoost mBassBoost;
    int controllerVisibility = 1;
    SessionManager sessionManager;

    private boolean playWhenReady = true;
    private int currentWindow;
    int pos = 0;
    boolean isRepeat = false;
    boolean isShuffle = false;
    private ActivityInfo activityInfo;
    private long playbackPosition;
    static ArrayList<String> videosModelList = new ArrayList<>();
    private CustomExoPlayerActivity.ComponentListener componentListener;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exo_player);

        if(getIntent().getExtras() != null) {
            videoURL = getIntent().getStringExtra("videoPath");
            videosModelList = getIntent().getExtras().getStringArrayList("videosModelList");
            pos = getIntent().getExtras().getInt("current_video_pos");
        }
        sessionManager = new SessionManager(getApplicationContext());

        componentListener = new CustomExoPlayerActivity.ComponentListener();
        try {
            if (checkSystemWritePermission()) {

                Toast.makeText(getApplicationContext(), "Set as ringtoon successfully ", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.i("ringtoon",e.toString());
            Toast.makeText(getApplicationContext(), "unable to set as Ringtoon ", Toast.LENGTH_SHORT).show();
        }

        audioManager= (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        playerView = (ZoomableExoPlayerView) findViewById(R.id.player_view);

        ImageButton closeVolumeBar = findViewById(R.id.closeButton);
        closeVolumeBar.setOnClickListener(this);

        bassBoostCloseButton = findViewById(R.id.bassBoostCloseButton);
        bassBoostCloseButton.setOnClickListener(this);



        cvVolume = findViewById(R.id.cvVolume);
        cvBrightness = findViewById(R.id.cvBrightness);
        cvBassboost = findViewById(R.id.cvBassBoost);


        fullScrennButton = findViewById(R.id.exo_fullscreen_button);
        fullScrennButton.setOnClickListener(this);
        exitFullScreen = findViewById(R.id.exo_exit_fullscreen_button);
        exitFullScreen.setOnClickListener(this);


        cvEqulizer = findViewById(R.id.cvEqulizer);
        exo_equalizer = findViewById(R.id.exo_equalizer);
        exo_equalizer.setOnClickListener(this);

        tv=new TextView(this);
        ll= findViewById(R.id.lll);//new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tv);

        closeEq = findViewById(R.id.closeEq);
        closeEq.setOnClickListener(this);

        exoBrightness = findViewById(R.id.exo_brightness);
        exoBrightness.setOnClickListener(this);

        brightnessCloseButton = findViewById(R.id.brightnessCloseButton);
        brightnessCloseButton.setOnClickListener(this);
        if (sessionManager.isBrightnessEnabled()) {
            exoBrightness.setVisibility(View.VISIBLE);
        } else {
            exoBrightness.setVisibility(View.GONE);
        }


        volumeSeekBar = findViewById(R.id.volumeSeekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        bassboostSeekBar = findViewById(R.id.bassboostSeekBar);

        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightness =
                Settings.System.getInt(getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 0);
        brightnessSeekBar.setProgress(brightness);


        exo_progress = findViewById(R.id.exo_progress);

        exo_lock_screen = findViewById(R.id.exo_lock_screen);
        exo_lock_screen.setOnClickListener(this);
        exo_unlock_screen = findViewById(R.id.exo_unlock_screen);
        exo_unlock_screen.setOnClickListener(this);
        ibVolumeBtn = findViewById(R.id.exo_volume);
        ibVolumeBtn.setOnClickListener(this);



        exo_back_button = findViewById(R.id.exo_back_button);
        exo_back_button.setOnClickListener(this);

        exo_bass_boost = findViewById(R.id.exo_bass_boost);
        exo_bass_boost.setOnClickListener(this);

//        Context context = getApplicationContext();
//        player = new SimpleExoPlayer.Builder(context).build();
//        playerView.setPlayer(player);
//
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test
//
//        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
//        TrackSelector trackSelector =
//                new DefaultTrackSelector(videoTrackSelectionFactory);
//
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yvideo"), bandwidthMeter);
//        // This is the MediaSource representing the media to be played.
//        //MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(liveStreamUri);
//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//        Uri videoURI = Uri.parse(videoURL);
//        MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);
//
//        player.prepare(mediaSource);
//        player.setPlayWhenReady(true);




        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                audioManager.setStreamVolume(player.getAudioStreamType(), i, 0);
                try {
                    audioManager.setStreamVolume(player.getAudioStreamType(), i, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Settings.System.putInt(getApplicationContext().getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bassboostSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBassBoostStrength(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //PlaybackControlView playerControlView = findViewById(R.id.exo_controller);


//        playerControlView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (controllerVisibility==1) {
//                    controllerVisibility=0;
//                    playerView.hideController();
//                } else {
//                    controllerVisibility = 1;
//                    playerView.showController();
//                }
//            }
//        });


        playerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, @SuppressLint("ClickableViewAccessibility") MotionEvent event) {
                if (controllerVisibility==1) {
                    controllerVisibility=0;
                    playerView.hideController();
                } else {
                    controllerVisibility = 1;
                    playerView.showController();
                }
                return true;
            }
        });
        //// I. ADJUST HERE:
////CHOOSE CONTENT: LiveStream / SdCard
//
////LIVE STREAM SOURCE: * Livestream links may be out of date so find any m3u8 files online and replace:
//
////        Uri mp4VideoUri =Uri.parse("http://81.7.13.162/hls/ss1/index.m3u8"); //random 720p source
////        Uri mp4VideoUri =Uri.parse("http://54.255.155.24:1935//Live/_definst_/amlst:sweetbcha1novD235L240P/playlist.m3u8"); //Radnom 540p indian channel
//        Uri mp4VideoUri =Uri.parse("http://cbsnewshd-lh.akamaihd.net/i/CBSNHD_7@199302/index_700_av-p.m3u8"); //CNBC
        Uri mp4VideoUri =Uri.parse("http://live.field59.com/wwsb/ngrp:wwsb1_all/playlist.m3u8"); //ABC NEWS
////        Uri mp4VideoUri =Uri.parse("FIND A WORKING LINK ABD PLUg INTO HERE"); //PLUG INTO HERE<------------------------------------------
//
//
////VIDEO FROM SD CARD: (2 steps. set up file and path, then change videoSource to get the file)
////        String urimp4 = "path/FileName.mp4"; //upload file to device and add path/name.mp4
////        Uri mp4VideoUri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+urimp4);



        //// II. ADJUST HERE:

        ////        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);
        ////Produces Extractor instances for parsing the media data.
        //        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //This is the MediaSource representing the media to be played:
        //FOR SD CARD SOURCE:
        //        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);


//
//        For HTTP VIdeo
        //FOR LIVESTREAM LINK:
//        MediaSource videoSource = new HlsMediaSource(mp4VideoUri, dataSourceFactory, 1, null, null);
//        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
//        // Prepare the player with the source.
//        player.prepare(videoSource);
//
//        player.addListener(new ExoPlayer.EventListener() {
//
//
//            @Override
//            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
//
//            }
//
//            @Override
//            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
//                Log.v(TAG, "Listener-onTracksChanged... ");
//            }
//
//            @Override
//            public void onLoadingChanged(boolean isLoading) {
//
//            }
//
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                Log.v(TAG, "Listener-onPlayerStateChanged..." + playbackState+"|||isDrawingCacheEnabled():"+simpleExoPlayerView.isDrawingCacheEnabled());
//            }
//
//            @Override
//            public void onRepeatModeChanged(int repeatMode) {
//
//            }
//
//            @Override
//            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
//
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                Log.v(TAG, "Listener-onPlayerError...");
//                player.stop();
//                player.prepare(loopingSource);
//                player.setPlayWhenReady(true);
//            }
//
//            @Override
//            public void onPositionDiscontinuity(int reason) {
//
//            }
//
//            @Override
//            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
//
//            }
//
//            @Override
//            public void onSeekProcessed() {
//
//            }
//        });
//        player.setPlayWhenReady(true); //run file/link when ready to play.
//        player.setVideoDebugListener(this);

//        player.addAnalyticsListener(new AnalyticsListener() {
//
//            @Override
//            public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
//
//                mEqualizer = new Equalizer(1000, audioSessionId);
//                mEqualizer.setEnabled(true);
//                short bands = mEqualizer.getNumberOfBands();
//
//                Log.e("bands", bands+"");
//                Log.e("mEqualizer", mEqualizer.getBand(0)+"");
//
//
//
//            }
//
//
//            @Override
//            public int hashCode() {
//                return super.hashCode();
//            }
//        });


    }



    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        Log.v(TAG, "onVideoSizeChanged [" + " width: " + width + " height: " + height + "]");
        resolutionTextView.setText("RES:(WxH):" + width + "X" + height + "\n           " + height + "p");//shows video info
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {



    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//        if (playbackState == Player.STATE_BUFFERING){
//            progressBar.setVisibility(View.VISIBLE);
//        }else if (playbackState == Player.STATE_READY){
//            progressBar.setVisibility(View.GONE);
//
//        }

    }

    //-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()...");
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
        Log.v(TAG, "onStart()...");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializePlayer();
        Log.v(TAG, "onResume()...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && player != null) {
            player.release();
            player = null;

        }
        if ( mBassBoost != null) {
            mBassBoost.release();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()...");
        if (player !=null) {

            player.release();
            mBassBoost.release();
        }
    }

    private void initializePlayer() {

        if (player == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
//            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
//                    new DefaultTrackSelector(adaptiveTrackSelectionFactory),
//                    new DefaultLoadControl()
//            );

            player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
            player.addListener(componentListener);
            player.setVideoDebugListener(componentListener);
            player.setAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.seekTo(currentWindow, playbackPosition);


        }


        if (sessionManager.isAutoPlayNextEnabled()) {

            /*Making a Playlist of all the videos in playlist*/
            MediaSource[] mediaSources = new MediaSource[videosModelList.size()];
            int i;
            for (i = 0; i < videosModelList.size(); i++) {
                mediaSources[i] = buildMediaSourceLocal(Uri.fromFile(new File(videosModelList.get(i))));
            }
            /* Checking if There are multiple videos in playlist or 1*/
            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);
            /* Playing the currently selected video */
            player.prepare(mediaSource, true, false);
            player.seekTo(pos, 0);

        } else {


            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "yvideo"), bandwidthMeter);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            Uri videoURI = Uri.parse(videoURL);
            MediaSource mediaSource = new ExtractorMediaSource(videoURI, dataSourceFactory, extractorsFactory, null, null);

            player.prepare(mediaSource);
        }

        player.setPlayWhenReady(true);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /*Used for offline links*/
    private MediaSource buildMediaSourceLocal(Uri uri) {
        DataSource.Factory dataSourceFactory = new FileDataSourceFactory();
        return new ExtractorMediaSource(uri, dataSourceFactory,
                new DefaultExtractorsFactory(), null, null);
    }


    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.addVideoListener(null);
            player.setVideoDebugListener(null);
            player.setAudioDebugListener(null);
            player.release();
            player = null;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exo_volume:
                cvVolume.setVisibility(View.VISIBLE);
//                if(player.getVolume() == 0){
//                    ibMuteBtn.setImageResource(R.drawable.ic_baseline_volume_mute_24);
//                    player.setVolume(1f);
//                } else {
//                    ibMuteBtn.setImageResource(R.drawable.ic_baseline_volume_mute_24);
//                    player.setVolume(0f);
//                }
            break;

            case R.id.exo_fullscreen_button:
                exitFullScreen.setVisibility(View.VISIBLE);
                fullScrennButton.setVisibility(View.GONE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                break;

            case R.id.exo_exit_fullscreen_button:
                exitFullScreen.setVisibility(View.GONE);
                fullScrennButton.setVisibility(View.VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;

               case R.id.closeButton:
                   cvVolume.setVisibility(View.GONE);
                    break;

            case R.id.exo_lock_screen:
                exo_unlock_screen.setVisibility(View.VISIBLE);
                if (playerView.getUseController()) {
                    playerView.setUseController(false);
                } else {
                    playerView.setUseController(true);
                }
                break;

            case R.id.exo_unlock_screen:
                exo_unlock_screen.setVisibility(View.GONE);
                if (playerView.getUseController()) {
                    playerView.setUseController(false);
                } else {
                    playerView.setUseController(true);
                }
                break;

            case R.id.exo_equalizer:
                //ll.setVisibility(View.VISIBLE);
                cvEqulizer.setVisibility(View.VISIBLE);
                if (i==1) {
                    i=0;
                    setupEq();
                }

                break;

            case R.id.closeEq:
                    //ll.setVisibility(View.GONE);
                cvEqulizer.setVisibility(View.GONE);
                break;

            case R.id.exo_brightness:
                cvBrightness.setVisibility(View.VISIBLE);
                break;

            case R.id.brightnessCloseButton:
                cvBrightness.setVisibility(View.GONE);
                break;

            case R.id.exo_back_button:
                finish();
                break;

            case R.id.exo_bass_boost:
                setBassboost();
                cvBassboost.setVisibility(View.VISIBLE);
                break;

            case R.id.bassBoostCloseButton:
                cvBassboost.setVisibility(View.GONE);
                break;
        }

    }

    @SuppressLint("SetTextI18n")
    public void setupEq() {
        mEqualizer=new Equalizer(0,player.getAudioSessionId());
        mEqualizer.setEnabled(true);
        short bands=mEqualizer.getNumberOfBands();
        final short min=mEqualizer.getBandLevelRange()[0];
        final short max=mEqualizer.getBandLevelRange()[1];
        for(short i=0;i<bands;i++)
        {
            final short band=1;
            TextView tv1=new TextView(this);
            tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            tv1.setGravity(Gravity.CENTER_HORIZONTAL);
            tv1.setText((mEqualizer.getCenterFreq(band)/1000)+"hz");
            tv1.setTextColor(getResources().getColor(R.color.white));


            ll.addView(tv1);
            LinearLayout lv=new LinearLayout(this);
            lv.setOrientation(LinearLayout.HORIZONTAL);
            //TextView tv2=new TextView(this);
            //tv2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            //tv2.setText((min/100)+"db");
            //TextView tv3=new TextView(this);
            //tv3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            //tv3.setText((max/100)+"db");
            LinearLayout.LayoutParams param=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            param.weight=1;
//            SeekBar bar = findViewById(R.id.bandSeekBar);
            SeekBar bar = new SeekBar(this);
//            android:thumbTint="@color/orangered"
//            android:progressTint="@color/orangered"
            bar.setLayoutParams(param);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bar.setProgressBackgroundTintList(ColorStateList.valueOf(getColor(R.color.white)));
            } else {
                bar.setProgressBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            }

            bar.setMax(max-min);
            bar.setThumb(getResources().getDrawable(R.drawable.custom_thumb));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                bar.setProgressTintList(ColorStateList.valueOf(getColor(R.color.orangered)));
            }
//
//            android:progressDrawable="@drawable/seekbar_progress"
//            android:thumb="@drawable/custom_thumb"
//            android:progressTint="@color/colorPrimaryDark"
            bar.setProgress(mEqualizer.getBandLevel(band));
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {

                        mEqualizer.setBandLevel(band, (short)(progress+min));



                }
            });
            //lv.addView(tv2);
            //lv.addView(tv3);



            if(bar.getParent() != null) {
                ((ViewGroup)bar.getParent()).removeView(bar); // <- fix
            }
            lv.addView(bar);
            if(lv.getParent() != null) {
                ((ViewGroup)lv.getParent()).removeView(lv); // <- fix
            }
            ll.addView(lv);
        }

    }

    public void setBassboost() {
        mBassBoost = new BassBoost(Integer.MAX_VALUE, player.getAudioSessionId());
        mBassBoost.setEnabled(true);
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN){
            try {
                volumeSeekBar.setProgress(audioManager.getStreamVolume(player.getAudioStreamType()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP){
            try {
                volumeSeekBar.setProgress(audioManager.getStreamVolume(player.getAudioStreamType()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onKeyUp(keyCode, event);
    }

//    private boolean checkSystemWritePermission() {
//        boolean retVal = true;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            retVal = Settings.System.canWrite(this);
//            Log.d(TAG, "Can Write Settings: " + retVal);
//            if(retVal){
//                Toast.makeText(this, "Write allowed :-)", Toast.LENGTH_LONG).show();
//            }else{
//                Toast.makeText(this, "Write not allowed :-(", Toast.LENGTH_LONG).show();
//
//            }
//        }
//        return retVal;
//    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Settings.System.canWrite(getApplicationContext()))
                return true;
            else
                openAndroidPermissionsMenu();
        }
        return false;
    }

    private void openAndroidPermissionsMenu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
            getApplicationContext().startActivity(intent);
        }
    }

    private void setBassBoostStrength(final int strength) {
        mBassBoost.setStrength((short) strength);
    }

    private class ComponentListener implements ExoPlayer.EventListener, VideoRendererEventListener,
            AudioRendererEventListener {

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            // Do nothing.
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            // Do nothing.
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            String stateString;
            switch (playbackState) {
                case Player.STATE_IDLE:
                    stateString = "ExoPlayer.STATE_IDLE      -";
                    break;
                case Player.STATE_BUFFERING:
                    stateString = "ExoPlayer.STATE_BUFFERING -";
                    break;
                case Player.STATE_READY:
                    stateString = "ExoPlayer.STATE_READY     -";
                    break;
                case Player.STATE_ENDED:

                    stateString = "ExoPlayer.STATE_ENDED     -";
                    break;
                default:
                    stateString = "UNKNOWN_STATE             -";
                    break;
            }
            Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
        }

        @Override
        public void onRepeatModeChanged(int repeatMode) {

        }

        @Override
        public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            // Do nothing.
        }

        @Override
        public void onPositionDiscontinuity(int reason) {

        }

        @Override
        public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            // Do nothing.
        }

        @Override
        public void onSeekProcessed() {

        }

        @Override
        public void onVideoEnabled(DecoderCounters counters) {

        }

        @Override
        public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onVideoInputFormatChanged(Format format) {

        }

        @Override
        public void onDroppedFrames(int count, long elapsedMs) {

        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
//            Log.i(TAG, width + "*" + height + "/" + unappliedRotationDegrees + "/" + pixelWidthHeightRatio);
            activityInfo = new ActivityInfo();
            if (width <= height) {
                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        @Override
        public void onRenderedFirstFrame(Surface surface) {

        }

        @Override
        public void onVideoDisabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioEnabled(DecoderCounters counters) {

        }

        @Override
        public void onAudioSessionId(int audioSessionId) {

        }

        @Override
        public void onAudioDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

        }

        @Override
        public void onAudioInputFormatChanged(Format format) {

        }

        @Override
        public void onAudioSinkUnderrun(int bufferSize, long bufferSizeMs, long elapsedSinceLastFeedMs) {

        }


        @Override
        public void onAudioDisabled(DecoderCounters counters) {

        }
    }

}