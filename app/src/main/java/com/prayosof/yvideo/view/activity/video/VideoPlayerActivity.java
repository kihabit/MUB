package com.prayosof.yvideo.view.activity.video;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
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
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.DefaultTimeBar;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.android.material.card.MaterialCardView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.databinding.DownloadDialogBinding;
import com.prayosof.yvideo.databinding.FileDetailLayBinding;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.interfaces.Playable;
import com.prayosof.yvideo.services.NewMediaPlayerService;
import com.prayosof.yvideo.view.activity.AudioPlayerActivity;
import com.prayosof.yvideo.view.activity.PlayerActivity;
import com.prayosof.yvideo.view.activity.audioEffects.AudioPreferences;
import com.prayosof.yvideo.view.browser.adapters.VimeoVideoAdapter;
import com.prayosof.yvideo.view.browser.models.DownloadFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import denis.musicplayer.data.media.model.Track;

public class VideoPlayerActivity extends AppCompatActivity implements VideoRendererEventListener, Player.EventListener, View.OnClickListener, Playable {

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "CustomExoPlayerActivity";
    private SimpleExoPlayer player;
    private TextView resolutionTextView;
    String videoURL = "";
    ProgressBar progressBar;
    ImageButton ibVolumeBtn, fullScrennButton, exo_lock_screen, exo_unlock_screen, exitFullScreen, exo_equalizer, closeEq, vdoAspectRation;
    PlayerView playerView;
    SeekBar brightnessSeekBar;

    DefaultTimeBar exo_progress;
    MaterialCardView cvEqulizer;
    LinearLayout llControls;
    ImageButton exoBrightness, brightnessCloseButton, exo_back_button, exo_bass_boost, bassBoostCloseButton, btnDownload;

    private TextView tv;
    private LinearLayout ll;
    private MaterialCardView cvBrightness, cvBassboost;
    int i = 1;

    int brightness;
    SessionManager sessionManager;

    private boolean playWhenReady = true;
    private boolean eq_initilized = false;
    private int currentWindow;
    int pos = 0;
    boolean isRepeat = false;
    boolean isShuffle = false;
    private boolean singleVideo;
    private ActivityInfo activityInfo;
    private long playbackPosition;
    static ArrayList<String> videosModelList = new ArrayList<>();
    private ComponentListener componentListener;
    ColorDrawable seekbg;
    boolean isIntentFile = false;
    static Uri videoURI;


    BassBoost mBassBoost1 = null;
    Virtualizer mVirtualizer = null;

    SeekBar mBassBoostSeekBar = null;
    SeekBar mVirtualizerSeekBar = null;

    //    Volumne
    private AudioManager mAudioManager = null;
    private int mMaxVolume = 0;
    private int mCurrentVolume = 0;
    private TextView mCurrentVolumeTextView = null;
    private SeekBar mVolumeBar = null;

    //    Equalizer
    private Equalizer mEqualizer;
    private LinearLayout video_linearLayoutEqual;
    AudioPreferences properties;
    private int PRESET_POSITION = 0;
    View promptsView;

    // Manage vidoe player

    Intent playerIntent;
    NewMediaPlayerService musicService;
    boolean serviceBound = false;
    Playable mPlayable;
    ImageButton mpButtonPrevious, mpCurrentSongPauseBtn, mpButtonNext;
    Runnable mSeekbarUpdater;
    private Handler handler = new Handler();
    TextView mpSongTitle;
    ServiceConnection serviceConnection;
    private String type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideUiButtons();
        setContentView(R.layout.activity_video_player);

        if (getIntent().getExtras() != null) {
            videoURL = getIntent().getStringExtra("videoPath");
            videosModelList = getIntent().getExtras().getStringArrayList("videosModelList");
            pos = getIntent().getExtras().getInt("current_video_pos");
            singleVideo = getIntent().getBooleanExtra("SingleVideo", false);
            type = getIntent().getStringExtra("type");
        }

        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = intent.getData();
            if (uri != null) {

                videoURL = uri.toString();
            }
            singleVideo = true;
        }

        sessionManager = new SessionManager(getApplicationContext());


        playerView = (PlayerView) findViewById(R.id.exo_player_view);
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            playerView.showController();
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                llControls.setVisibility(visibility);
                exo_back_button.setVisibility(visibility);
                if (View.GONE == visibility) {
                    hideUiButtons();
                }

            }
        });

//        ImageButton closeVolumeBar = findViewById(R.id.closeButton);
//        closeVolumeBar.setOnClickListener(this);

        bassBoostCloseButton = findViewById(R.id.bassBoostCloseButton);
        bassBoostCloseButton.setOnClickListener(this);


//        cvVolume = findViewById(R.id.cvVolume);
        cvBrightness = findViewById(R.id.cvBrightness);
        cvBassboost = findViewById(R.id.cvBassBoost);


        fullScrennButton = findViewById(R.id.exo_fullscreen_button);
        fullScrennButton.setOnClickListener(this);
        exitFullScreen = findViewById(R.id.exo_exit_fullscreen_button);
        exitFullScreen.setOnClickListener(this);


        cvEqulizer = findViewById(R.id.cvEqulizer);
        exo_equalizer = findViewById(R.id.video_btn_equalizer);
        exo_equalizer.setOnClickListener(this);
        llControls = findViewById(R.id.ll_controls);
        progressBar = findViewById(R.id.progressBar);

        tv = new TextView(this);
        ll = findViewById(R.id.lll);//new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tv);

        closeEq = findViewById(R.id.closeEq);
        closeEq.setOnClickListener(this);

        brightnessCloseButton = findViewById(R.id.brightnessCloseButton);
        brightnessCloseButton.setOnClickListener(this);


        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);

        vdoAspectRation = findViewById(R.id.video_aspectratio);

        brightnessSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable brightNess = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.black);
        brightNess.actionBarSize();
        brightnessSeekBar.setThumb(brightNess);
        brightnessSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        brightnessSeekBar.setBackground(seekbg);
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

        exoBrightness = findViewById(R.id.video_brightness);
        exoBrightness.setOnClickListener(this);

//        if (sessionManager.isBrightnessEnabled()) {
        exoBrightness.setVisibility(View.GONE);
//        } else {
//            exoBrightness.setVisibility(View.GONE);
//        }


        exo_back_button = findViewById(R.id.exo_back_button);
        exo_back_button.setOnClickListener(this);

        exo_bass_boost = findViewById(R.id.video_btn_bassBoost);
        exo_bass_boost.setOnClickListener(this);

        componentListener = new ComponentListener();

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

        vdoAspectRation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int aspectRatio = sessionManager.getKeyAspectRatioValue();
                if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_FIT)
                    aspectRatio = AspectRatioFrameLayout.RESIZE_MODE_ZOOM;
                else if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
                    aspectRatio = AspectRatioFrameLayout.RESIZE_MODE_FILL;
                else if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_FILL)
                    aspectRatio = AspectRatioFrameLayout.RESIZE_MODE_FIT;
                sessionManager.setKeyAspectRatioValue(aspectRatio);
                setAspectRAtionAndButton();
            }
        });
        setAspectRAtionAndButton();
        setupBassoostViertualizer();

        //kjs
        btnDownload = (ImageButton) findViewById(R.id.video_btn_download);

        if (type != null && type.equals("1")) {
            btnDownload.setVisibility(View.GONE);
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Toast.makeText(VideoPlayerActivity.this, "Video Downloading...", Toast.LENGTH_SHORT).show();
                    startDownLoad();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startDownLoad() {
        if (!videoURL.equals("")) {
            final FileDetailLayBinding fileDetailLayBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.file_detail_lay, null, false);
            fileDetailLayBinding.etFileName.setText(String.valueOf(System.currentTimeMillis()));
            fileDetailLayBinding.etVideoUrl.setText(videoURL);

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            //give YourVideoUrl below
            retriever.setDataSource(videoURL, new HashMap<String, String>());

            // this gets frame at 2nd second
            Bitmap image = retriever.getFrameAtTime(2000000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            fileDetailLayBinding.imgThumb.setImageBitmap(image);

            //  Glide.with(context).load(Uri.fromFile(new File(downloadUrl))).placeholder(R.drawable.placce_holder_video).into(fileDetailLayBinding.imgThumb);
            fileDetailLayBinding.tvCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("label", fileDetailLayBinding.etVideoUrl.getText().toString().trim());
                    if (clipboard != null) {
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(VideoPlayerActivity.this, "copied text", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            final String[] filename = {fileDetailLayBinding.etFileName.getText().toString().trim() + ".mp4"};
            final String[] videoPath = {"file://" + sessionManager.getFolderPath(this) + filename[0]};
            final String[] savePath = {sessionManager.getFolderPath(this) + "/" + filename[0]};
            fileDetailLayBinding.savePath.setText(savePath[0]);


            //KJS Download Video From Here Auto
            filename[0] = fileDetailLayBinding.etFileName.getText().toString().trim() + ".mp4";
            videoPath[0] = "file://" + sessionManager.getFolderPath(this) + filename[0];
            savePath[0] = sessionManager.getFolderPath(this) + "/" + filename[0];
            try {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(videoURL));
                request.setTitle("Video Download");
                request.allowScanningByMediaScanner();
                request.setDestinationUri(Uri.parse(videoPath[0]));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);


                long downloadId = 0;
                if (manager != null) {
                    downloadId = manager.enqueue(request);
                }
//                Toast.makeText(this, "Download Start", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent("string");

                intent.putExtra("isdownload", true);
                // put your all data using put extra

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                final long finalDownloadId = downloadId;
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        boolean downloading = true;

                        while (downloading) {

                            DownloadManager.Query q = new DownloadManager.Query();
                            q.setFilterById(finalDownloadId);

                            Cursor cursor = manager.query(q);
                            cursor.moveToFirst();
                            if (cursor.getCount() != 0) {
                                int bytes_downloaded = cursor.getInt(cursor
                                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    downloading = false;
                                }

                                final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);

                                long fileSizeInKB = bytes_total / 1024;
                                float totalizeInMB = fileSizeInKB / 1024;
                                float totalizeInGB = totalizeInMB / 1024;
                                long SizeInKB = bytes_downloaded / 1024;
                                long downloadSizeInMB = SizeInKB / 1024;

                                String totalSize = "";
                                String sizeDownload = "";
                                if (downloadSizeInMB == 0) {
                                    sizeDownload = SizeInKB + "KB";
                                } else {
                                    sizeDownload = downloadSizeInMB + " MB";
                                }
                                if (totalizeInGB > 1) {
                                    totalSize = String.format("%.2f", totalizeInGB).replace(".00", "") + " GB";
                                } else if (totalizeInMB > 1) {
                                    totalSize = String.format("%.2f", totalizeInMB).replace(".00", "") + " MB";
                                } else {
                                    totalSize = fileSizeInKB + " KB";
                                }

                                if (dl_progress >= 0 && dl_progress <= 100) {
                                    Intent intent = new Intent("filter_string");
                                    DownloadFile downloadFile = new DownloadFile();
                                    downloadFile.setId(finalDownloadId);
                                    downloadFile.setFilename(filename[0]);
                                    downloadFile.setTotalSize(totalSize);
                                    downloadFile.setCompleteSize(sizeDownload);
                                    downloadFile.setProgress((int) dl_progress);
                                    downloadFile.setPath(savePath[0]);
                                    intent.putExtra("progress", downloadFile);
                                    // put your all data using put extra

                                    LocalBroadcastManager.getInstance(VideoPlayerActivity.this).sendBroadcast(intent);
                                }
                            } else {
                                Intent intent = new Intent("filter_string");
                                DownloadFile downloadFile = new DownloadFile();
                                downloadFile.setId((int) finalDownloadId);
                                downloadFile.setFilename(filename[0]);
                                downloadFile.setProgress(101);
                                downloadFile.setPath(savePath[0]);
                                intent.putExtra("progress", downloadFile);
                                LocalBroadcastManager.getInstance(VideoPlayerActivity.this).sendBroadcast(intent);
                            }
                            cursor.close();
                        }

                    }
                }).start();
            } catch (Exception e) {
                Toast.makeText(this, "error :" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Downloading url not found!", Toast.LENGTH_SHORT).show();
        }
    }
    //kjs end

    private void setAspectRAtionAndButton() {
        int aspectRatio = sessionManager.getKeyAspectRatioValue();
        playerView.setResizeMode(aspectRatio);
        if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_FIT)
            vdoAspectRation.setImageResource(R.drawable.ic_baseline_crop_24);
        else if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
            vdoAspectRation.setImageResource(R.drawable.ic_baseline_fullscreen_24);
        else if (aspectRatio == AspectRatioFrameLayout.RESIZE_MODE_FILL)
            vdoAspectRation.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24);
    }

    private void hideUiButtons() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void hideUiButtonsLock() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public void setupBassoostViertualizer() {
        seekbg = new ColorDrawable(getResources().getColor(R.color.md_grey_300));
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
        if (playbackState == Player.STATE_BUFFERING) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            progressBar.setVisibility(View.GONE);

        }


    }

    //-------------------------------------------------------ANDROID LIFECYCLE---------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
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

        playerIntent = new Intent(this, NewMediaPlayerService.class);


        if (checkIsPlayerRunning()) {

            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    // We've bound to LocalService, cast the IBinder and get LocalService instance
                    NewMediaPlayerService.LocalBinder binder = (NewMediaPlayerService.LocalBinder) service;
                    musicService = binder.getService();
                    musicService.registerCallBack(VideoPlayerActivity.this);
                    serviceBound = true;

                    musicService.pauseMedia();


                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    serviceBound = false;
                }

                @Override
                public void onBindingDied(ComponentName name) {
                }
            };

            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        } else {

            return;
        }


        initializePlayer();
        Log.v(TAG, "onResume()...");
    }

    public boolean checkIsPlayerRunning() {
        return NewMediaPlayerService.isRunning();
    }


    @Override
    protected void onPause() {
        super.onPause();
        releasePlayer();
        if (isFinishing() && player != null) {
            player.release();
            player = null;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (PlayerActivity.mediaPlayer != null) {
//
//            if (!PlayerActivity.mediaPlayer.isPlaying()) {
//                PlayerActivity.mediaPlayer.start();
//
//            }

        }
        if (player != null) {

            player.release();
            mBassBoost1.release();
        }

        if (serviceBound) {
            musicService.resumeMedia();
            unbindService(serviceConnection);
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


        if (sessionManager.isAutoPlayNextEnabled() && !singleVideo) {

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
            videoURI = Uri.parse(videoURL);
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

                ShowVolumePopup();

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

            case R.id.exo_lock_screen:

                exo_unlock_screen.setVisibility(View.VISIBLE);
                if (playerView.getUseController()) {
                    playerView.setUseController(false);
                } else {
                    playerView.setUseController(true);
                }
                hideUiButtonsLock();
                break;

            case R.id.exo_unlock_screen:
                exo_unlock_screen.setVisibility(View.GONE);
                if (playerView.getUseController()) {
                    playerView.setUseController(false);
                } else {
                    playerView.setUseController(true);
                }
                hideUiButtons();
                break;

            case R.id.video_btn_equalizer:

                showEqPopup();

                break;

            case R.id.closeEq:
                cvEqulizer.setVisibility(View.GONE);
                break;

            case R.id.video_brightness: {
//                if (sessionManager.isBrightnessEnabled()) {
                try {
                    if (checkSystemWritePermission()) {
                        cvBrightness.setVisibility(View.VISIBLE);
//                Toast.makeText(getApplicationContext(), "Set as ringtoon successfully ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Allow modify system settings ==> ON ", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "unable to set as Ringtoon ", Toast.LENGTH_SHORT).show();
                }
//                }

            }
            break;

            case R.id.brightnessCloseButton:
                cvBrightness.setVisibility(View.GONE);
                break;

            case R.id.exo_back_button:
                finish();
                break;

            case R.id.video_btn_bassBoost:
                ShowBassVirtualizerPopup();
                break;

            case R.id.bassBoostCloseButton:
                cvBassboost.setVisibility(View.GONE);
                break;
        }

    }

    public void ShowBassVirtualizerPopup() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.bass_boost_virtualizer, null);

        ImageView closeBtn = promptsView.findViewById(R.id.closeBtn);


        mBassBoostSeekBar = (SeekBar) promptsView.findViewById(R.id.dAudioBassboostSeekBar);

        mBassBoostSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable equalizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        equalizer.actionBarSize();
        mBassBoostSeekBar.setThumb(equalizer);
        mBassBoostSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mBassBoostSeekBar.setBackground(seekbg);
        mBassBoostSeekBar.setProgress(sessionManager.getKeyBassboostValue());
        mBassBoostSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null != mBassBoost1) {
                    mBassBoost1.setStrength((short) (progress * 10));
                    sessionManager.setKeyBassboostValue(progress);
                }
//                short strength = mBassBoost1.getRoundedStrength();
//                boolean isSupported = mBassBoost1.getStrengthSupported();
//                mBassBoostTextView.setText(Integer.toString(progress) + ", "
//                        + strength + ", " + isSupported);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//
//        mVirtualizerTextView = (TextView) promptsView.findViewById(R.id.dVirtualizerTextView1);
        mVirtualizerSeekBar = (SeekBar) promptsView.findViewById(R.id.dVirtualizerSeekBar);

        mVirtualizerSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable vEqualizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        vEqualizer.actionBarSize();
        mVirtualizerSeekBar.setThumb(vEqualizer);
        mVirtualizerSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mVirtualizerSeekBar.setBackground(seekbg);
        mVirtualizerSeekBar.setProgress(sessionManager.getKeyVirtualizerValue());
        mVirtualizerSeekBar
                .setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                        short strength = 0;
                        boolean isSupported = false;

                        if (null != mVirtualizer) {
                            mVirtualizer.setStrength((short) (progress * 10));
                            sessionManager.setKeyVirtualizerValue(progress);
//                            strength = mVirtualizer.getRoundedStrength();
//                            isSupported = mVirtualizer.getStrengthSupported();

                        }
//                        mVirtualizerTextView.setText(Integer.toString(progress) + ", "
//                                + strength + ", " + isSupported);

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        android.app.AlertDialog.Builder alertDialogBuilder = new
                android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            try {
                mVolumeBar.setProgress(mAudioManager.getStreamVolume(player.getAudioStreamType()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            try {
                mVolumeBar.setProgress(mAudioManager.getStreamVolume(player.getAudioStreamType()));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return super.onKeyUp(keyCode, event);
    }

    private boolean checkSystemWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(getApplicationContext()))
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


    @Override
    public void onTrackPrevious() {

    }

    @Override
    public void onTrackPlay(String soneName) {

    }

    @Override
    public void onTrackPause() {

    }

    @Override
    public void onTrackNext(Track track) {

    }

    @Override
    public void onTrackResume() {

    }

    @Override
    public void onServiceStoped() {

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
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                case Player.STATE_READY:
                    progressBar.setVisibility(View.GONE);
                    stateString = "ExoPlayer.STATE_READY     -";
                    mBassBoost1 = new BassBoost(0, player.getAudioSessionId());
                    mBassBoost1.setEnabled(true);
                    mBassBoost1.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));

                    mVirtualizer = new Virtualizer(0, player.getAudioSessionId());
                    mVirtualizer.setEnabled(true);
                    mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
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
//            if (width <= height) {
//                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            } else {
//                setRequestedOrientation(activityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//            }
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

    public void showEqPopup() {
        LayoutInflater li = LayoutInflater.from(this);
        promptsView = li.inflate(R.layout.dialog_equlizer, null);
        ImageView closeBtn = promptsView.findViewById(R.id.eqCloseBtn);
        Switch eq_switch;
        properties = AudioPreferences.getInstance();
        properties.preferences = getSharedPreferences(properties.app, Context.MODE_PRIVATE);
        properties.edit_preferences = properties.preferences.edit();

        mEqualizer = new Equalizer(0, player.getAudioSessionId());

        RelativeLayout layout_top_btn = promptsView.findViewById(R.id.layout_top_btn);

        RelativeLayout eqSpinner = promptsView.findViewById(R.id.eqSpinner);


        video_linearLayoutEqual = (LinearLayout) promptsView.findViewById(R.id.video_linearLayoutEqual);

        short numberFrequencyBands = mEqualizer.getNumberOfBands();
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            video_linearLayoutEqual.addView(frequencyHeaderTextview);

//            set up linear layout to contain each seekBar
            LinearLayout seekBarRowLayout = new LinearLayout(this);
            seekBarRowLayout.setOrientation(LinearLayout.HORIZONTAL);

//            set up lower level textview for this seekBar
            TextView lowerEqualizerBandLevelTextview = new TextView(this);
            lowerEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            lowerEqualizerBandLevelTextview.setText((lowerEqualizerBandLevel / 100) + " dB");
            lowerEqualizerBandLevelTextview.setRotation(90);
//            set up upper level textview for this seekBar
            TextView upperEqualizerBandLevelTextview = new TextView(this);
            upperEqualizerBandLevelTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            upperEqualizerBandLevelTextview.setText((upperEqualizerBandLevel / 100) + " dB");
            upperEqualizerBandLevelTextview.setRotation(90);


            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT - 60,
                    120);
            layoutParams.weight = 1;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);

            seekBar.setId(i);
            seekBar.setPadding(35, 15, 35, 15);

            seekBar.setLayoutParams(layoutParams);
            seekBar.setMax(upperEqualizerBandLevel - lowerEqualizerBandLevel);
//            set the progress for this seekBar
            final int seek_id = i;
            int progressBar = properties.preferences.getInt("seek_" + seek_id, 1500);
//            Log.i("storedOld_seek_"+seek_id,":"+ progressBar);
            if (progressBar != 1500) {
                seekBar.setProgress(progressBar);
                mEqualizer.setBandLevel(equalizerBandIndex,
                        (short) (progressBar + lowerEqualizerBandLevel));
            } else {
                seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex));
                mEqualizer.setBandLevel(equalizerBandIndex,
                        (short) (progressBar + lowerEqualizerBandLevel));
            }
//            change progress as its changed by moving the sliders
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress,
                                              boolean fromUser) {
                    mEqualizer.setBandLevel(equalizerBandIndex,
                            (short) (progress + lowerEqualizerBandLevel));

                }

                public void onStartTrackingTouch(SeekBar seekBar) {
                    //not used
                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    //not used
                    properties.edit_preferences.putInt("seek_" + seek_id, seekBar.getProgress()).commit();
                    properties.edit_preferences.putInt("position", 0).commit();
                }
            });

            IconDrawable equalizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
            equalizer.actionBarSize();
            seekBar.setThumb(equalizer);
            seekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
// seekbar row layout settings. The layout is rotated at 270 so left=>bottom, Right=>top and so on
            LinearLayout.LayoutParams seekBarLayout = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            seekBarLayout.weight = 1;
            //seekBarLayout.setMargins(5, 0, 5, 0);
            // seekBarRowLayout.setLayoutParams(seekBarLayout);

//            add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            video_linearLayoutEqual.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }

//        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            video_linearLayoutEqual.setRotation(0);
//            layout_top_btn.setVisibility(View.GONE);
//            eqSpinner.setVisibility(View.GONE);
//        } else {
        layout_top_btn.setVisibility(View.VISIBLE);
        eqSpinner.setVisibility(View.VISIBLE);
        video_linearLayoutEqual.setRotation(270);
//        }

        eq_initilized = true;

        mEqualizer.setEnabled(true);
        eq_switch = promptsView.findViewById(R.id.video_eq_switch);
        eq_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEqualizer.setEnabled(true);
                    for (int i = 0; i < video_linearLayoutEqual.getChildCount(); i++) {
                        View child = video_linearLayoutEqual.getChildAt(i);

                        child.setEnabled(true);
                    }

                } else {
                    for (int i = 0; i < video_linearLayoutEqual.getChildCount(); i++) {
                        View child = video_linearLayoutEqual.getChildAt(i);

                        child.setClickable(true);
                    }
                    mEqualizer.setEnabled(false);
                    resetEquaizer();
                }

            }
        });


        android.app.AlertDialog.Builder alertDialogBuilder = new
                android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    public void resetEquaizer() {
        if (PRESET_POSITION != 0) {
            mEqualizer.usePreset((short) (PRESET_POSITION - 1));
        }
        short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//                set seekBar indicators according to selected preset
        for (short i = 0; i < numberFrequencyBands; i++) {
            short equalizerBandIndex = (short) (i);
            SeekBar seekBar = (SeekBar) promptsView.findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
        }
        properties.edit_preferences.putInt("position", PRESET_POSITION).commit();
    }

    private void equalizeSound() {
//        set up the spinner
        ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerPresetSpinner = (Spinner) promptsView.findViewById(R.id.video_spinner);
//        get list of the device's equalizer presets
//        equalizerPresetNames.add("Custom");
        for (short i = 0; i < mEqualizer.getNumberOfPresets(); i++) {
            equalizerPresetNames.add(mEqualizer.getPresetName(i));
        }

        equalizerPresetSpinner.setAdapter(equalizerPresetSpinnerAdapter);

//        handle the spinner item selections
        int current = properties.preferences.getInt("position", 0);
        equalizerPresetSpinner.setSelection(current);
        equalizerPresetSpinner.setOnItemSelectedListener(new AdapterView
                .OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //first list item selected by default and sets the preset accordingly
                PRESET_POSITION = position;
                if (position != 0) {
                    mEqualizer.usePreset((short) (position - 1));
                }
//                get the number of frequency bands for this equalizer engine
                short numberFrequencyBands = mEqualizer.getNumberOfBands();
//                get the lower gain setting for this equalizer band
                final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//                set seekBar indicators according to selected preset
                for (short i = 0; i < numberFrequencyBands; i++) {
                    short equalizerBandIndex = (short) (i);
                    SeekBar seekBar = (SeekBar) promptsView.findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
                    seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
                }
                properties.edit_preferences.putInt("position", position).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//               not used
            }

        });
    }


    private void updateCurrentVolume() {
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolumeTextView.setText("" + mCurrentVolume);
        mVolumeBar.setProgress(mCurrentVolume);

    }

    public void ShowVolumePopup() {
        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.volume_audio_player_dialog, null);


        ImageButton closeBtn = promptsView.findViewById(R.id.volumeCloseBtn);
        mCurrentVolumeTextView = (TextView) promptsView.findViewById(R.id.currentVolume);
        mVolumeBar = (SeekBar) promptsView.findViewById(R.id.mVolumeSeekBar);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mVolumeBar.setMax(mMaxVolume);
        updateCurrentVolume();


        mVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                    updateCurrentVolume();

                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mVolumeBar.setPadding(35, 15, 35, 15);
        IconDrawable equalizer2 = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        equalizer2.actionBarSize();
        mVolumeBar.setThumb(equalizer2);
        mVolumeBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        //        seekbg.setAlpha(70);
        mVolumeBar.setBackground(seekbg);

        android.app.AlertDialog.Builder alertDialogBuilder = new
                android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }


}