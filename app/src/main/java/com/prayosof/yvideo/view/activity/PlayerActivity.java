package com.prayosof.yvideo.view.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Virtualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.audioAdapter.TrackAdapter;
import com.prayosof.yvideo.helper.Constants;
import com.prayosof.yvideo.helper.CreateNotification;
import com.prayosof.yvideo.helper.SessionManager;
import com.prayosof.yvideo.model.MusicFiles;
import com.prayosof.yvideo.services.NotificationService;
import com.prayosof.yvideo.services.OnClearFromRecentService;
import com.prayosof.yvideo.view.activity.audioEffects.EquilizerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import denis.musicplayer.data.media.model.Track;

import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.musicFiles;
import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.repeatBoolean;
import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.shuffleBoolean;



public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, View.OnClickListener{
    private static final String TAG = "PlayerActivity";
    private static RemoteViews contentView;
    private static Notification notification;
    private static NotificationManager notificationManager;
    private static final int NotificationID = 1005;
    private static NotificationCompat.Builder mBuilder;
    BroadcastReceiver broadcastReceiver;
//Audio Effects
    BassBoost mBassBoost = null;
    Virtualizer mVirtualizer = null;
    SeekBar mBassBoostSeekBar = null;
    SeekBar mVirtualizerSeekBar = null;

//    Volume
    private AudioManager mAudioManager = null;
    private int mMaxVolume = 0;
    private int mCurrentVolume = 0;
    private TextView mCurrentVolumeTextView = null;
    private SeekBar mVolumeBar = null;
    private SeekBar mLeftSeekBar = null;
    private SeekBar mRightSeekBar = null;
    private TextView mLeftTextView = null;
    private TextView mRightTextView = null;
    private int mLeftVolume = 0;
    private int mRightVolume = 0;


    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<Track> selectedTrackData =null;
    static Uri uri;
    public static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, pauseThread, nextThread, prevThread;
    public static String SONG_NAME;
    public static int SONG_INDEX;
    private IconDrawable eq;
    ColorDrawable seekbg;

    SessionManager sessionManager;

    boolean isIntentSong = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        initViews();


        if (null != getIntent().getExtras()) {
            if (Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("is_track_adapter")).equals("track")) {
                selectedTrackData = TrackAdapter.tracksData;//(ArrayList<Track>) getIntent().getSerializableExtra("tracks");

            } else {
                selectedTrackData = musicFiles;
            }
        }

        sessionManager = new SessionManager(this);
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if(Intent.ACTION_VIEW.equals(action)){
            Uri uri = intent.getData();
            if (uri != null) {



                    @SuppressLint("Recycle") Cursor c = getContentResolver().query(uri, null, null, null, null);
                if (c != null) {
                    c.moveToFirst();
                }
                SONG_NAME = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    artist_name.setVisibility(View.GONE);



                song_name.setText(SONG_NAME);

                mediaPlayer = new MediaPlayer();
                try {
                    // mediaPlayer.setDataSource(String.valueOf(myUri));
                    mediaPlayer.setDataSource(getApplicationContext(),uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                isIntentSong = true;
                playPauseBtn.setImageResource(R.drawable.ic_pause);

                metaData(uri);
            }


        } else {
            getIntentMethod();
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                String songName = selectedTrackData.get(SONG_INDEX).getTitle();
                SONG_NAME = songName;
                song_name.setText(songName);
                artist_name.setText(selectedTrackData.get(SONG_INDEX).getArtist());
                mediaPlayer.setOnCompletionListener(this);



            } else {
                String songName = selectedTrackData.get(position).getTitle();
                SONG_NAME = songName;
                song_name.setText(songName);
                artist_name.setText(selectedTrackData.get(position).getArtist());
                mediaPlayer.setOnCompletionListener(this);
            }
            Log.d(TAG, "intent was something else: "+action);
        }



        seekbg = new ColorDrawable(getResources().getColor(R.color.md_grey_300));


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                } else {
                    shuffleBoolean = true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                } else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });



    }


    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();

    }

    private void prevThreadBtn() {
        prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(selectedTrackData.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (selectedTrackData.size() - 1) : (position - 1));
            }
            uri = Uri.parse(selectedTrackData.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
            mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));
            metaData(uri);
            song_name.setText(selectedTrackData.get(position).getTitle());
            artist_name.setText(selectedTrackData.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(selectedTrackData.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (selectedTrackData.size() - 1) : (position - 1));
            }
            uri = Uri.parse(selectedTrackData.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
            mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));
            metaData(uri);
            song_name.setText(selectedTrackData.get(position).getTitle());
            artist_name.setText(selectedTrackData.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
            mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private void nextThreadBtn() {
        nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
        mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(selectedTrackData.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % selectedTrackData.size());
            }

            uri = Uri.parse(selectedTrackData.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(selectedTrackData.get(position).getTitle());
            artist_name.setText(selectedTrackData.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        } else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(selectedTrackData.size() - 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % selectedTrackData.size());
            }

            uri = Uri.parse(selectedTrackData.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(selectedTrackData.get(SONG_INDEX).getTitle());
            artist_name.setText(selectedTrackData.get(SONG_INDEX).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    private void playThreadBtn() {
        playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
        mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
        mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
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

    private void getIntentMethod() {
       if (mediaPlayer != null && mediaPlayer.isPlaying()) {
           playPauseBtn.setImageResource(R.drawable.ic_pause);
           uri = Uri.parse(selectedTrackData.get(SONG_INDEX).getData());
           seekBar.setMax(mediaPlayer.getDuration() / 1000);
           int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
           seekBar.setProgress(mCurrentPosition);
           mediaPlayer.seekTo(mCurrentPosition);
//           int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
//           seekBar.setProgress(mCurrentPosition);
//           mediaPlayer.seekTo(mCurrentPosition);

           metaData(uri);

       } else {

           position = getIntent().getIntExtra("position", -1);
           Log.e("position", position+"r");
           SONG_INDEX = position;
           if (selectedTrackData != null) {
               playPauseBtn.setImageResource(R.drawable.ic_pause);
               uri = Uri.parse(selectedTrackData.get(position).getData());
           }
           if (mediaPlayer != null) {
               mediaPlayer.stop();
               mediaPlayer.release();
               mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
               mediaPlayer.start();
           } else {
               mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
               int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
               seekBar.setProgress(mCurrentPosition);
               mediaPlayer.seekTo(mCurrentPosition);
               mediaPlayer.start();
           }

//           CreateNotification.createNotification(this, selectedTrackData.get(position),
//                   R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);

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
//                           CreateNotification.createNotification(getApplicationContext(), selectedTrackData.get(position),
//                                   R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);
                           prevBtnClicked();
                           break;
                       case CreateNotification.ACTION_PLAY:

                           if (mediaPlayer.isPlaying()){
//                               CreateNotification.createNotification(getApplicationContext(), selectedTrackData.get(position),
//                                       R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);
                               playPauseBtnClicked();
                           } else {
//                               CreateNotification.createNotification(getApplicationContext(), selectedTrackData.get(position),
//                                       R.drawable.exo_controls_play, position, selectedTrackData.size()-1);
                               playPauseBtnClicked();
                           }
                           break;
                       case CreateNotification.ACTION_NEXT:
//                           CreateNotification.createNotification(getApplicationContext(), selectedTrackData.get(position),
//                                   R.drawable.exo_controls_pause, position, selectedTrackData.size()-1);
                           nextBtnClicked();
                           break;
                   }
               }
           };
           registerReceiver(broadcastReceiver, new IntentFilter("PAPPU_TRACKS"));
           startService(new Intent(getBaseContext(), OnClearFromRecentService.class));

           //notificationBuilder();
//           Intent serviceIntent = new Intent(PlayerActivity.this, NotificationService.class);
//           serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
//           startService(serviceIntent);
           seekBar.setMax(mediaPlayer.getDuration() / 1000);
           metaData(uri);
       }
       if (mediaPlayer != null){
           //Set Bassboost and Virtualizer
           mBassBoost = new BassBoost(0, mediaPlayer.getAudioSessionId());
           mBassBoost.setEnabled(true);
           mBassBoost.setStrength((short) (sessionManager.getKeyBassboostValue() * 10));

           mVirtualizer = new Virtualizer(0, mediaPlayer.getAudioSessionId());
           mVirtualizer.setEnabled(true);
           mVirtualizer.setStrength((short) (sessionManager.getKeyVirtualizerValue() * 10));
       }


    }





    private void initViews() {
//        eq = new IconDrawable(this, EntypoIcons.entypo_sound_mix).colorRes(R.color.white).sizeDp(25);
        ImageButton btn_bassBoost = findViewById(R.id.btn_bassBoost);
        btn_bassBoost.setOnClickListener(this);
        ImageButton volumeDialog = findViewById(R.id.volumeDialog);
        volumeDialog.setOnClickListener(this);
        ImageButton equalizerButton = findViewById(R.id.btn_equalizer);
        equalizerButton.setBackground(eq);
        equalizerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendWhatsAppAudio();
                onPause();
                Intent eqPage = new Intent(getApplicationContext(), EquilizerActivity.class);
                eqPage.putExtra("playerSessionId", mediaPlayer.getAudioSessionId());
                eqPage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(eqPage);
            }
        });
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }


    private void sendWhatsAppAudio(){
        try {
            //Copy file to external ExternalStorage.
            Intent shareMedia = new Intent(Intent.ACTION_SEND);
            //set WhatsApp application.
            shareMedia.setPackage("com.whatsapp");
            shareMedia.setType("audio/*");
            //set path of media file in ExternalStorage.
            shareMedia.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareMedia, "Compartiendo archivo."));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Whatsapp no se encuentra instalado", Toast.LENGTH_LONG).show();
        }
    }

    private void metaData(Uri uri) {
        if (isIntentSong) {
            duration_total.setText(formattedTime(mediaPlayer.getDuration()));
        } else {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri.toString());
            int durationTotal = Integer.parseInt(selectedTrackData.get(SONG_INDEX).getDuration()) / 1000;
            duration_total.setText(formattedTime(durationTotal));
            byte[] art = retriever.getEmbeddedPicture();
            Bitmap bitmap;
            if (art != null) {
                bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                ImageAnimation(this, cover_art, bitmap);
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@Nullable Palette palette) {
                        Palette.Swatch swatch = palette.getDominantSwatch();
                        if (swatch != null) {
                            //ImageView gradient = findViewById(R.id.imageViewGradient);
//                        RelativeLayout mContainer = findViewById(R.id.mContainer);
//                        gradient.setBackgroundResource(R.drawable.gradient_bg);
//                        mContainer.setBackgroundResource(R.drawable.main_bg);
//                        GradientDrawable gradientDrawable = new GradientDrawable(
//                                GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), 0x00000000});
//                        gradient.setBackground(gradientDrawable);

                            GradientDrawable gradientDrawableBg = new GradientDrawable(
                                    GradientDrawable.Orientation.BOTTOM_TOP, new int[]{swatch.getRgb(), swatch.getRgb()});
//                        mContainer.setBackground(gradientDrawableBg);
                            song_name.setTextColor(Color.WHITE);
                            artist_name.setTextColor(Color.WHITE);
                        } else {
//                        ImageView gradient = findViewById(R.id.imageViewGradient);
//                        RelativeLayout mContainer = findViewById(R.id.mContainer);
//                        gradient.setBackgroundResource(R.drawable.gradient_bg);
//                        mContainer.setBackgroundResource(R.drawable.main_bg);
//                        GradientDrawable gradientDrawable = new GradientDrawable(
//                                GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0x00000000});
//                        gradient.setBackground(gradientDrawable);
//
//                        GradientDrawable gradientDrawableBg = new GradientDrawable(
//                                GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0xff000000, 0xff000000});
//                        mContainer.setBackground(gradientDrawableBg);
                            song_name.setTextColor(Color.WHITE);
                            artist_name.setTextColor(Color.WHITE);
                        }
                    }
                });
            } else {
                Glide.with(getApplicationContext())
                        .asBitmap()
                        .load(R.drawable.brand_logo)
                        .into(cover_art);
//            ImageView gradient = findViewById(R.id.imageViewGradient);
//            RelativeLayout mContainer = findViewById(R.id.mContainer);
//            gradient.setBackgroundResource(R.drawable.gradient_bg);
//            mContainer.setBackgroundResource(R.drawable.main_bg);
                song_name.setTextColor(Color.WHITE);
                artist_name.setTextColor(Color.WHITE);
            }
        }

    }

    public void ImageAnimation(final Context context, final ImageView imageView, final Bitmap bitmap) {
        Animation animOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if (mediaPlayer != null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
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

    private void updateCurrentVolume() {
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolumeTextView.setText("" + mCurrentVolume);
        mVolumeBar.setProgress(mCurrentVolume);

    }

    private void setLeftAndRight() {
        float leftRatio = mLeftVolume / (float) mMaxVolume;
        float rightRatio = mRightVolume / (float) mMaxVolume;
        mediaPlayer.setVolume(leftRatio, rightRatio);
        mLeftTextView.setText("" + mLeftVolume + ", ratio:" + leftRatio);
        mRightTextView.setText("" + mRightVolume + ", ratio:" + rightRatio);

        updateCurrentVolume();

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


   public void notificationBuilder() {


           notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
           mBuilder = new NotificationCompat.Builder(getApplicationContext(), "notify_001");

           contentView = new RemoteViews(getPackageName(), R.layout.notification_music_player);
           contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
//           Intent switchIntent = new Intent(this, BackgroundService.switchButtonListener.class);
//           PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 1020, switchIntent, 0);
//           contentView.setOnClickPendingIntent(R.id.exo_exit_fullscreen_button, pendingSwitchIntent);

           mBuilder.setSmallIcon(R.mipmap.ic_launcher);
           mBuilder.setAutoCancel(false);
           mBuilder.setOngoing(true);
           mBuilder.setPriority(Notification.PRIORITY_HIGH);
           mBuilder.setOnlyAlertOnce(true);
           mBuilder.build().flags = Notification.FLAG_NO_CLEAR | Notification.PRIORITY_HIGH;
           mBuilder.setContent(contentView);

           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
               String channelId = "channel_id";
               NotificationChannel channel = new NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_HIGH);
               channel.enableVibration(true);
               channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
               notificationManager.createNotificationChannel(channel);
               mBuilder.setChannelId(channelId);
           }

           notification = mBuilder.build();
           notificationManager.notify(NotificationID, notification);


   }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;

            case R.id.btn_bassBoost:
                ShowBassVirtualizerPopup();
                break;

            case R.id.volumeDialog:
                ShowVolumePopup();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationManager.cancelAll();
        }

        unregisterReceiver(broadcastReceiver);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("PAPPU_TRACKS"));
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,
                    "KOD Dev", NotificationManager.IMPORTANCE_LOW);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null){
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
