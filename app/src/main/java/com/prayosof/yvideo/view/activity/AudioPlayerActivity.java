package com.prayosof.yvideo.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.prayosof.yvideo.MainActivity;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.VideoAdapter;
import com.prayosof.yvideo.adapter.audioAdapter.TrackAdapter;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.interfaces.Playable;
import com.prayosof.yvideo.services.MediaPlayerService;
import com.prayosof.yvideo.services.MusicService;
import com.prayosof.yvideo.services.NewMediaPlayerService;
import com.prayosof.yvideo.view.activity.audioEffects.EquilizerActivity;
import com.prayosof.yvideo.view.activity.audios.CustomTouchListener;
import com.prayosof.yvideo.view.activity.audios.RecyclerView_Adapter;
import com.prayosof.yvideo.view.activity.audios.onItemClickListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import denis.musicplayer.data.media.model.Track;

import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.musicFiles;
import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.repeatBoolean;
import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.shuffleBoolean;

public class AudioPlayerActivity extends AppCompatActivity implements Playable, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView musicStatus, song_name, song_artist;
    private SeekBar seekBar;
    private FloatingActionButton btnPlayOrPause;
//    private Button btnStop;
    private ImageView  nextBtnClicked, prevBtnClicked;
    private ImageView btnQuit, shuffleBtn, repeatBtn, btn_close_back;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss", Locale.getDefault());
    private boolean tag1 = false;
    private boolean tag2 = false;
    private MusicService musicService;
    boolean mBound = false;
    AppBarLayout app_bar;

    ObjectAnimator animator;
    int position = 0;

    public static final String Broadcast_PLAY_NEW_AUDIO = "PlayNewAudio";
    public static final String Broadcast_NEW_PLAY_AUDIO = "NewPlayAudio";
    public static final String UPDATE_NEXT_SONG = "update_next_song";
    public static final String UPDATE_SEEKBAR = "update_seekbar";

    RecyclerView in_player_recyclerview;
    RecyclerView_Adapter adapter;
//    private DataUpdateReceiver dataUpdateReceiver;
    Intent playerIntent;
    public static Activity fa;


    NewMediaPlayerService player;
    boolean musicIsRunning  = false;

//    Volume ids
    private AudioManager mAudioManager = null;
    private int mMaxVolume = 0;
    private int mCurrentVolume = 0;
    private TextView mCurrentVolumeTextView = null;
    private SeekBar mVolumeBar = null;
    private SeekBar mLeftSeekBar = null;
    private SeekBar mRightSeekBar = null;
    private TextView mLeftTextView = null;
    private TextView mRightTextView = null;
    private TextView duration_played, duration_total;
    private int mLeftVolume = 0;
    private int mRightVolume = 0;

    ColorDrawable seekbg;
    SessionManager sessionManager;


    //Audio Effects
    BassBoost mBassBoost = null;
    Virtualizer mVirtualizer = null;
    SeekBar mBassBoostSeekBar = null;
    SeekBar mVirtualizerSeekBar = null;
    static ArrayList<Track> selectedTrackData =null;


    boolean serviceBound = false;
    boolean boundSerivce = false;
    Intent action_view_intent;
    boolean stop = false;

    private void bindServiceConnection() {
//
//        if (!MusicService.isRunning()) {
//
//            Intent intent = new Intent(AudioPlayerActivity.this, MusicService.class);
//            Bundle bundle = new Bundle();
//            bundle.putInt("position",position);
//            bundle.putSerializable("selectedTrackData", selectedTrackData);
//            intent.putExtras(bundle);
//
//            int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//            duration_total.setText(formattedTime(durationTotal));
//
//
//            startService(intent);
//            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
//        } else {
//
//            Intent intenta = new Intent("RECEIVER_FILTER");
//
//            Bundle args = new Bundle();
//            args.putInt("position",position);
//            args.putSerializable("selectedTrackData", selectedTrackData);
//            intenta.putExtra("DATA",args);
//
//            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intenta);
//
//            int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//            duration_total.setText(formattedTime(durationTotal));
//
//        }



    }
//
//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//
//
//            if (!MusicService.isRunning()) {
//
//                Intent intent = new Intent(AudioPlayerActivity.this, MusicService.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt("position",position);
//                bundle.putSerializable("selectedTrackData", selectedTrackData);
//                intent.putExtras(bundle);
//
//                int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//                duration_total.setText(formattedTime(durationTotal));
//
//
//        } else {
//
//                Intent intenta = new Intent("RECEIVER_FILTER");
//
//                Bundle args = new Bundle();
//                args.putInt("position",position);
//                args.putSerializable("selectedTrackData", selectedTrackData);
//                intenta.putExtra("DATA",args);
//
//                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intenta);
//
//                int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//                duration_total.setText(formattedTime(durationTotal));
//
//        }
//
//
//
//            mBound = true;
//            musicService = ((MusicService.MyBinder) (service)).getService();
//            musicService.registerCallBack(AudioPlayerActivity.this);
//
//            onTrackPlay();
//
//
//
//        }

//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicService = null;
//            mBound = false;
//
//        }
//    };

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            int durationTotal = player.getCurrentPosition() / 1000;

            duration_played.setText(formattedTime(durationTotal));

            seekBar.setProgress(player.getCurrentPosition());
            seekBar.setMax(player.getDuration());
            if (!stop) {
                handler.postDelayed(runnable, 200);

            }

        }
    };

    private void findViewById() {
        seekBar = (SeekBar) findViewById(R.id.MusicSeekBar);
        btnPlayOrPause = (FloatingActionButton) findViewById(R.id.BtnPlayorPause);
//        btnStop = (Button) findViewById(R.id.BtnStop);
        btnQuit = (ImageView) findViewById(R.id.BtnQuit);
        musicStatus = (TextView) findViewById(R.id.MusicStatus);
        musicStatus.setText("");
        nextBtnClicked = findViewById(R.id.nextBtnClicked);
        prevBtnClicked = findViewById(R.id.prevBtnClicked);
        song_name = findViewById(R.id.song_name);
        song_artist = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.duration_played);
        duration_total = findViewById(R.id.duration_total);

        shuffleBtn = findViewById(R.id.audio_id_shuffle);
        repeatBtn = findViewById(R.id.audio_id_repeat);
        btn_close_back = findViewById(R.id.btn_close_back);

        btn_close_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);

                //unbindService(serviceConnection);
//                unbindService(serviceConnection);
                try {
                    AudioPlayerActivity.this.finish();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: e = " + e.toString());
                }
            }
        });


        app_bar = findViewById(R.id.app_bar);

        app_bar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    //  Collapsed
                     shuffleBtn.setVisibility(View.GONE);
                     btn_close_back.setVisibility(View.VISIBLE);

                }
                else
                {
                    //Expanded
                    shuffleBtn.setVisibility(View.VISIBLE);
                    btn_close_back.setVisibility(View.GONE);

                }
            }
        });


        in_player_recyclerview = findViewById(R.id.in_player_recyclerview);


        in_player_recyclerview.addOnItemTouchListener(new CustomTouchListener(this, new onItemClickListener() {
            @Override
            public void onClick(View view, final int index) {
                Log.e("sogindex", index+"");
//                final Handler handler = new Handler(Looper.getMainLooper());
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                }, 1000);

                player.playNewAudio(index);

            }
        }));

        ImageButton volumeDialog = findViewById(R.id.audio_volumeDialog);
        volumeDialog.setOnClickListener(this);

        ImageButton btn_bassBoost = findViewById(R.id.audio_btn_bassBoost);
        btn_bassBoost.setOnClickListener(this);
        seekbg = new ColorDrawable(getResources().getColor(R.color.md_grey_300));
        sessionManager = new SessionManager(this);
        if (sessionManager.getRepeatMode())
            repeatBtn.setImageResource(R.drawable.ic_repeat_on);
        if (sessionManager.getShuffleToggle())
            shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
        ImageButton equalizerButton = findViewById(R.id.audio_btn_equalizer);
        //equalizerButton.setBackground(eq);
        equalizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendWhatsAppAudio();
                onPause();
                Intent eqPage = new Intent(getApplicationContext(), EquilizerActivity.class);
                eqPage.putExtra("playerSessionId", NewMediaPlayerService.getSessionId());
                eqPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(eqPage);
            }
        });
    }

    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totlaNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totlaNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totlaNew;
        } else {
            return totalOut;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyStoragePermissions(this);
        setContentView(R.layout.activity_audio_player);



        findViewById();
        fa = this;

        playerIntent = new Intent(this, NewMediaPlayerService.class);
        action_view_intent = getIntent();
        String action = action_view_intent.getAction();

        if (null != getIntent().getExtras()) {
            if (Objects.requireNonNull(getIntent().getExtras()).containsKey("is_track_adapter") && Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("is_track_adapter")).equals("track")) {
                Log.e("Come from", "is_track_adapter");


                selectedTrackData = TrackAdapter.tracksData;//(ArrayList<Track>) getIntent().getSerializableExtra("tracks");
                checkFileExist();

            } else if (Objects.requireNonNull(getIntent().getExtras()).containsKey("is_track_adapter") && Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("is_track_adapter")).equals("from_mini_player")) {
                Log.e("Come from", "mini player");
                bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);

                if (NewMediaPlayerService.isplaying()){
                    musicStatus.setText(R.string.str_playing);
                    btnPlayOrPause.setImageResource(R.drawable.exo_icon_pause);

                } else {
                    musicStatus.setText(R.string.str_stopped);
                    btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
                }

//                if (!musicIsRunning) {
//                    musicStatus.setText(R.string.str_stopped);
//                    btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
//                }

            } else if (Intent.ACTION_VIEW.equals(action)) {
                Log.e("Come from", "file manager");

                try {
                    playAudioFromDeviceFiles();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                selectedTrackData = musicFiles;
                checkFileExist();
            }


        }




        adapter = new RecyclerView_Adapter(selectedTrackData, getApplication());
        in_player_recyclerview.setAdapter(adapter);
        myListener();



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void checkFileExist() {
        position = getIntent().getIntExtra("position", -1);
        File currentFile = new File(selectedTrackData.get(position).getData());
        if (currentFile.exists()) {
            // Toast.makeText(getApplicationContext(), "Exist", Toast.LENGTH_LONG).show();
            String name = selectedTrackData.get(position).getTitle();
            song_name.setText(name);
            song_artist.setText(selectedTrackData.get(position).getArtist());
            Log.e("position", position+"");
            //bindServiceConnection();
            playAudio(position);
            //MusicService.MUSIC_PATH =  selectedTrackData.get(position).getData();


        } else {
            Toast.makeText(getApplicationContext(), "File not Exist", Toast.LENGTH_LONG).show();
            return;
        }
    }
    private void myListener() {
        ImageView imageView = (ImageView) findViewById(R.id.Image);
        animator = ObjectAnimator.ofFloat(imageView, "rotation", 0f, 360.0f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(-1);

        btnPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player == null) {
                    return;
                }
                seekBar.setProgress(player.getCurrentPosition());
                seekBar.setMax(player.getDuration());
                if (!player.isTag()) {
                    btnPlayOrPause.setImageResource(R.drawable.ic_pause);
                    musicStatus.setText(R.string.str_playing);
                    player.playOrPause();

                    player.setTag(true);

                    if (!tag1) {
                        animator.start();
                        tag1 = true;
                    } else {
                        animator.resume();
                    }
                } else {
                    btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);

                    musicStatus.setText(R.string.str_paused);
                    player.playOrPause();
                    animator.pause();
                    player.setTag(false);
                }
                if (!tag2) {
                    handler.post(runnable);
                    tag2 = true;
                }
            }
        });

//        btnStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                musicStatus.setText(R.string.str_stopped);
//                btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
//                musicService.stop();
//                animator.pause();
//                musicService.setTag(false);
//            }
//        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                //unbindService(serviceConnection);
//                unbindService(serviceConnection);
                try {
                    AudioPlayerActivity.this.finish();
                } catch (Exception e) {
                    Log.e(TAG, "onClick: e = " + e.toString());
                }
            }
        });

        nextBtnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //MusicService.MUSIC_PATH = selectedTrackData.get(position+1).getData();

                Log.e("size", selectedTrackData.size()-1 + "");
                Log.e("position", position + "");

                if (position > selectedTrackData.size()-1) {
                    player.skipToNext();
                } else {
                    //musicService.nextBtnClicked();
                    player.skipToNext();
                    position = position + 1;
                }

            }
        });

        prevBtnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // musicService.prevBtnClicked();
                player.skipToPrevious();
            }
        });

        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("shuffle",sessionManager.getShuffleToggle()+"");

                if (sessionManager.getShuffleToggle()) {
                    sessionManager.setShuffleToggle(false);

                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                    Toast.makeText(AudioPlayerActivity.this, R.string.shuffle_off,Toast.LENGTH_SHORT).show();
                } else {
                    sessionManager.setShuffleToggle(true);
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                    Toast.makeText(AudioPlayerActivity.this, R.string.shuffle_on,Toast.LENGTH_SHORT).show();
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("repeat",sessionManager.getRepeatMode()+"");
                if (sessionManager.getRepeatMode()) {
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                    sessionManager.setRepeatMode(false);
                    Toast.makeText(AudioPlayerActivity.this, R.string.repeat_mode_off,Toast.LENGTH_SHORT).show();
                } else {
                    sessionManager.setRepeatMode(true);

                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                    Toast.makeText(AudioPlayerActivity.this, R.string.repeat_mode_on,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            moveTaskToBack(false);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackPrevious() {
        //onPlayerStart();

        Log.e("Status","Player Previous clicked");


    }

    @Override
    public void onTrackPlay(String soneName) {
        musicIsRunning = true;

        //Set Bassboost and Virtualizer
        mBassBoost = new BassBoost(0, NewMediaPlayerService.getSessionId());
        mBassBoost.setEnabled(true);
        mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));

        mVirtualizer = new Virtualizer(0, NewMediaPlayerService.getSessionId());
        mVirtualizer.setEnabled(true);
        mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));

       // onPlayerStart();
        song_name.setText(soneName);
        musicStatus.setText(R.string.str_playing);
        btnPlayOrPause.setImageResource(R.drawable.exo_icon_pause);
        animator.start();

        seekBar.setProgress(player.getCurrentPosition());
        seekBar.setMax(player.getDuration());
        if (player != null) {
            int durationTotal = player.getDuration() / 1000;
            duration_total.setText(formattedTime(durationTotal));
        }
//        if (!player.isTag()) {
//            btnPlayOrPause.setImageResource(R.drawable.ic_pause);
//            musicStatus.setText(R.string.str_playing);
//            player.playOrPause();
//
//            player.setTag(true);
//
//            if (!tag1) {
//                animator.start();
//                tag1 = true;
//            } else {
//                animator.resume();
//            }
//        } else {
//            btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
//
//            musicStatus.setText(R.string.str_paused);
//            player.playOrPause();
//            animator.pause();
//            player.setTag(false);
//        }
        if (!tag2) {
            handler.post(runnable);
            tag2 = true;
        }


//        seekBar.setProgress(player.getCurrentPosition());
//        seekBar.setMax(player.getDuration());
        //player.setTag(true);
        Log.e("Status","Player start");

//        if (musicService == null) {
//            return;
//        }
//        seekBar.setProgress(musicService.getCurrentPosition());
//        seekBar.setMax(musicService.getDuration());
//        if (!musicService.isTag()) {
//            btnPlayOrPause.setImageResource(R.drawable.ic_pause);
//            musicStatus.setText(R.string.str_playing);
//            musicService.setTag(true);
//
//            if (!tag1) {
//                animator.start();
//                tag1 = true;
//            } else {
//                animator.resume();
//            }
//        } else {
//            btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
//            musicStatus.setText(R.string.str_paused);
//            animator.pause();
//            musicService.setTag(false);
//        }
//        if (!tag2) {
//            handler.post(runnable);
//            tag2 = true;
//        }
    }

    @Override
    public void onTrackPause() {
        musicIsRunning = false;

        musicStatus.setText(R.string.str_paused);
        btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
        animator.pause();
        Log.e("Status","Player Pause");
//
//        btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
//        musicStatus.setText(R.string.str_paused);
//        animator.pause();
//        player.setTag(false);

    }

    @Override
    public void onTrackResume() {
        musicIsRunning = true;
        musicStatus.setText(R.string.str_playing);
        btnPlayOrPause.setImageResource(R.drawable.exo_icon_pause);
        animator.resume();
        Log.e("Status","Player Resume");


    }

    @Override
    public void onServiceStoped() {

    }

    @Override
    public void onTrackNext(Track track) {
       // onPlayerStart();
        musicIsRunning = true;

        musicStatus.setText(R.string.str_playing);
        btnPlayOrPause.setImageResource(R.drawable.exo_icon_pause);
        animator.resume();

        Log.e("Status","Player Next clicked");

        int durationTotal = player.getDuration() / 1000;
        duration_total.setText(formattedTime(durationTotal));

//        btnPlayOrPause.setImageResource(R.drawable.exo_icon_pause);
//        musicStatus.setText(R.string.str_playing);
//        animator.start();
//        player.setTag(true);

        song_name.setText(track.getTitle());
        song_artist.setText(track.getArtist());
    }

    public void ShowVolumePopup(){
        LayoutInflater li = LayoutInflater.from(this);

        View promptsView = li.inflate(R.layout.volume_audio_player_dialog, null);


        ImageButton closeBtn = promptsView.findViewById(R.id.volumeCloseBtn);
        mCurrentVolumeTextView = (TextView) promptsView.findViewById(R.id.currentVolume);
        mVolumeBar = (SeekBar) promptsView.findViewById(R.id.mVolumeSeekBar);

        mLeftTextView = (TextView) promptsView.findViewById(R.id.leftTextView);
        mRightTextView = (TextView) promptsView.findViewById(R.id.rightTextView);
        mLeftSeekBar = (SeekBar) promptsView.findViewById(R.id.leftSeekBar);
        mRightSeekBar = (SeekBar) promptsView.findViewById(R.id.rightSeekBar);

        mRightSeekBar.setVisibility(View.VISIBLE);
        mLeftSeekBar.setVisibility(View.VISIBLE);
        mRightTextView.setVisibility(View.VISIBLE);
        mLeftTextView.setVisibility(View.VISIBLE);

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mLeftSeekBar.setMax(mMaxVolume);
        mRightSeekBar.setMax(mMaxVolume);
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



        mLeftSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mLeftVolume = progress;
                setLeftAndRight();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mRightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRightVolume = progress;
                setLeftAndRight();
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

        mLeftSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable equalizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        equalizer.actionBarSize();
        mLeftSeekBar.setThumb(equalizer);
        mLeftSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mLeftSeekBar.setBackground(seekbg);

        mRightSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable equalizer1 = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        equalizer1.actionBarSize();
        mRightSeekBar.setThumb(equalizer1);
        mRightSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mRightSeekBar.setBackground(seekbg);



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
    private void updateCurrentVolume() {
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolumeTextView.setText("" + mCurrentVolume);
        mVolumeBar.setProgress(mCurrentVolume);

    }

    private void setLeftAndRight() {
        float leftRatio = mLeftVolume / (float) mMaxVolume;
        float rightRatio = mRightVolume / (float) mMaxVolume;
        //mediaPlayer.setVolume(leftRatio, rightRatio);
        mLeftTextView.setText("" + mLeftVolume + ", ratio:" + leftRatio);
        mRightTextView.setText("" + mRightVolume + ", ratio:" + rightRatio);

        updateCurrentVolume();

    }

    public void ShowBassVirtualizerPopup(){



        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.bass_boost_virtualizer, null);


        ImageView closeBtn = promptsView.findViewById(R.id.closeBtn);



//
//        mBassBoostTextView = (TextView) promptsView.findViewById(R.id.bassBoostTextView1);
        mBassBoostSeekBar = (SeekBar) promptsView.findViewById(R.id.dAudioBassboostSeekBar);

        mBassBoostSeekBar.setProgress(sessionManager.getKeyBassboostValue());


        mBassBoostSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable equalizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        equalizer.actionBarSize();
        mBassBoostSeekBar.setThumb(equalizer);
        mBassBoostSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mBassBoostSeekBar.setBackground(seekbg);

        mBassBoostSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (null != mBassBoost) {
                    mBassBoost.setStrength((short) (progress * 10));
                    sessionManager.setKeyBassboostValue(progress);

                }
//                        short strength = mBassBoost.getRoundedStrength();
//                        boolean isSupported = mBassBoost.getStrengthSupported();
//                        mBassBoostTextView.setText(Integer.toString(progress) + ", "
//                                + strength + ", " + isSupported);
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

        mVirtualizerSeekBar.setProgress(sessionManager.getKeyVirtualizerValue());

        mVirtualizerSeekBar.setPadding(35, 15, 35, 15);
        IconDrawable vEqualizer = new IconDrawable(this, FontAwesomeIcons.fa_minus_square).colorRes(R.color.colorAccent);
        vEqualizer.actionBarSize();
        mVirtualizerSeekBar.setThumb(vEqualizer);
        mVirtualizerSeekBar.setProgressDrawable(new ColorDrawable(Color.rgb(56, 60, 62)));
        mVirtualizerSeekBar.setBackground(seekbg);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.audio_btn_bassBoost:
                ShowBassVirtualizerPopup();
                break;

            case R.id.audio_volumeDialog:
                ShowVolumePopup();
                break;
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putBoolean("serviceStatus", sessionManager.isMusicServiceRunning());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("serviceStatus");
        sessionManager.setMusicServiceRunning(savedInstanceState.getBoolean("serviceStatus"));
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            NewMediaPlayerService.LocalBinder binder = (NewMediaPlayerService.LocalBinder) service;
            player = binder.getService();
            player.registerCallBack(AudioPlayerActivity.this);
            if (Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("is_track_adapter")).equals("from_mini_player")) {
                //onPlayerStart();
                song_name.setText(NewMediaPlayerService.getSongTitle());
                int durationTotal = player.getDuration() / 1000;
                duration_total.setText(formattedTime(durationTotal));
            }
            serviceBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
            sessionManager.setMusicServiceRunning(false);

        }
    };

    private void playAudio(int audioIndex) {
//        if (player != null) {
//            int durationTotal = player.getDuration() / 1000;
//            duration_total.setText(formattedTime(durationTotal));
//        } else {
//            try {
//                int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//                duration_total.setText(formattedTime(durationTotal));
//            } catch (NumberFormatException e) {
//
//            }
//
//        }


        //Check is service is active
        if (!NewMediaPlayerService.isRunning()) {
            Bundle bundle = new Bundle();
            bundle.putInt("position",audioIndex);
            bundle.putSerializable("selectedTrackData", selectedTrackData);
            playerIntent.putExtras(bundle);



            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(playerIntent);

        } else {

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);

            Bundle bundle = new Bundle();
            bundle.putInt("position",audioIndex);
            bundle.putSerializable("selectedTrackData", selectedTrackData);
            broadcastIntent.putExtras(bundle);

            sendBroadcast(broadcastIntent);

            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);


//
//            Intent intenta = new Intent(Broadcast_PLAY_NEW_AUDIO);
//
//            Bundle args = new Bundle();
//            args.putInt("position",position);
//            args.putSerializable("selectedTrackData", selectedTrackData);
//            intenta.putExtra("DATA",args);
//            sendBroadcast(intenta);
//
//            int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//            duration_total.setText(formattedTime(durationTotal));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }

        if (!NewMediaPlayerService.isplaying()) {

            Intent playerIntent = new Intent(AudioPlayerActivity.this, NewMediaPlayerService.class);
            stopService(playerIntent);
        }

        stop = true;

    }

    public void onPlayerStart() {
        seekBar.setProgress(player.getCurrentPosition());
        seekBar.setMax(player.getDuration());
        if (!player.isTag()) {
            btnPlayOrPause.setImageResource(R.drawable.ic_pause);
            musicStatus.setText(R.string.str_playing);
            player.setTag(true);

            if (!tag1) {
                animator.start();
                tag1 = true;
            } else {
                animator.resume();
            }
        } else {

            btnPlayOrPause.setImageResource(R.drawable.exo_controls_play);
            musicStatus.setText(R.string.str_paused);
            animator.pause();
            player.setTag(false);
        }
        if (!tag2) {
            handler.post(runnable);
            tag2 = true;
        }
    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Intent intent = new Intent(this, MusicService.class);
//
//            Bundle bundle = new Bundle();
//            bundle.putInt("position",position);
//            bundle.putSerializable("selectedTrackData", selectedTrackData);
//            intent.putExtras(bundle);
//
//            int durationTotal = Integer.parseInt(selectedTrackData.get(position).getDuration()) / 1000;
//            duration_total.setText(formattedTime(durationTotal));
//
//            if(!MusicService.isRunning()) {
//                startService(intent);
//            }
////            else {
////                stopService(intent);
////                startService(intent);
////            }
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//
//
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unbindService(serviceConnection);
//        mBound = false;
//    }

//    d


    @Override
    protected void onResume() {
        super.onResume();
//        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
//        IntentFilter intentFilter = new IntentFilter(UPDATE_NEXT_SONG);
//        registerReceiver(dataUpdateReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        this.finish();
//    }



    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        handler.removeCallbacks(runnable);
        //unbindService(serviceConnection);
       //unbindService(serviceConnection);

        try {
            AudioPlayerActivity.this.finish();
        } catch (Exception e) {
            Log.e(TAG, "onClick: e = " + e.toString());
        }
    }

    private void playAudioFromDeviceFiles() throws IOException {
        FileInputStream inputStream = null;

        Uri uri = action_view_intent.getData();
        Log.e("file name", String.valueOf(uri));
        File currentFile = new File(String.valueOf(uri));
        inputStream = new FileInputStream(currentFile);

        if (currentFile.exists()) {

            song_name.setText(currentFile.getName());
            playAudio(position);


        } else {
            Toast.makeText(getApplicationContext(), "File not Exist", Toast.LENGTH_LONG).show();
            return;
        }

        if (uri != null) {
            @SuppressLint("Recycle") Cursor c = getContentResolver().query(uri, null, null, null, null);
            if (c != null) {
                c.moveToFirst();
            }
            if (c != null) {
                song_name.setText(c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME)));
            }
            Log.e("song_name",song_name.getText()+"");

            MediaMetadataRetriever metaRetreiver = new MediaMetadataRetriever();
            metaRetreiver.setDataSource(inputStream.getFD());
            long time = Long.parseLong(metaRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Log.e("time", String.valueOf(time));

            duration_total.setText(String.valueOf(time));
            String title = metaRetreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);

            Log.e("title",title);

            Track musicFiles = new Track(0, title, "artist", String.valueOf(uri), "0",0);

            selectedTrackData.add(0,musicFiles);
            playAudio(0);

        }
    }



}

