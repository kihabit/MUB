package com.prayosof.yvideo.view.activity.audioEffects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import android.media.audiofx.Visualizer;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.view.activity.PlayerActivity;

import java.util.ArrayList;
import java.util.Objects;

public class EquilizerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final float VISUALIZER_HEIGHT_DIP = 50f;
    private static final int PERMISSION = 1234;

    private Visualizer mVisualizer;
    private Equalizer mEqualizer;
//
    private LinearLayout mLinearLayout;
    private VisualizerView mVisualizerView;
//
//    private String statusBarColor, realThemeColor;
    private ImageView eq_back_btn;
    AudioPreferences properties;
//    BassBoost mBassBoost;
    Switch eq_switch;
    private int PRESET_POSITION = 0;

    int sessionId = 0;


//
//    private TextView mBassBoostTextView = null;
//    private SeekBar mBassBoostSeekBar = null;
//
//    private TextView mVirtualizerTextView = null;
//    private SeekBar mVirtualizerSeekBar = null;
//
//    private Virtualizer mVirtualizer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equilizer);
        properties = AudioPreferences.getInstance();
        properties.preferences = getSharedPreferences(properties.app, Context.MODE_PRIVATE);
        properties.edit_preferences = properties.preferences.edit();


        Intent intent = getIntent();
        sessionId = intent.getIntExtra("playerSessionId",0);
//        set the device's volume control to control the audio stream we'll be playing
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Create the MediaPlayer
//        you need to put your audio file in the res/raw folder
//        - the filename must be test_audio_file or
//        change it below to match your filename
//        mMediaPlayer = MediaPlayer.create(this, R.raw.test_audio_file);
//        mMediaPlayer.start();

//        create the equalizer with default priority of 0 & attach to our media player
//        mEqualizer = new Equalizer(0, mMediaPlayer.getAudioSessionId());
        mEqualizer = new Equalizer(0, sessionId);

        mEqualizer.setEnabled(true);
        eq_switch = findViewById(R.id.eq_switch);

        eq_back_btn = findViewById(R.id.eq_back_btn);
        eq_back_btn.setOnClickListener(this);

        eq_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEqualizer.setEnabled(true);

                } else {
                    mEqualizer.setEnabled(false);
                    resetEquaizer();



                }

            }
        });


//        set up visualizer and equalizer bars after getting required permissions
//
//        if (!hasAudioSettingsPermission(this)) {
//            requestAudioSettingsPermission(this);
//        }
//        if (hasAudioSettingsPermission(this)) {
//            setupVisualizerFxAndUI();
//        }

        setupEqualizerFxAndUI();



    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /* shows spinner with list of equalizer presets to choose from
 - updates the seekBar progress and gain levels according
 to those of the selected preset*/
    private void equalizeSound() {
//        set up the spinner
        ArrayList<String> equalizerPresetNames = new ArrayList<String>();
        ArrayAdapter<String> equalizerPresetSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,
                equalizerPresetNames);
        equalizerPresetSpinnerAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner equalizerPresetSpinner = (Spinner) findViewById(R.id.spinner);
//        get list of the device's equalizer presets
        equalizerPresetNames.add("Custom");
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
                    SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
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

    /* displays the SeekBar sliders for the supported equalizer frequency bands
     user can move sliders to change the frequency of the bands*/
    @SuppressLint("SetTextI18n")
    private void setupEqualizerFxAndUI() {




//        get reference to linear layout for the seekBars
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutEqual);

//        equalizer heading
//        TextView equalizerHeading = new TextView(this);
//        equalizerHeading.setText("Equalizer");
//        equalizerHeading.setTextSize(20);
//        equalizerHeading.setGravity(Gravity.CENTER_HORIZONTAL);
//        mLinearLayout.addView(equalizerHeading);
//        mLinearLayout.setPadding(0, 0, 0, 20);

//        get number frequency bands supported by the equalizer engine
        short numberFrequencyBands = mEqualizer.getNumberOfBands();

//        get the level ranges to be used in setting the band level
//        get lower limit of the range in milliBels
        final short lowerEqualizerBandLevel = mEqualizer.getBandLevelRange()[0];
//        get the upper limit of the range in millibels
        final short upperEqualizerBandLevel = mEqualizer.getBandLevelRange()[1];

//        loop through all the equalizer bands to display the band headings, lower
//        & upper levels and the seek bars
        for (short i = 0; i < numberFrequencyBands; i++) {
            final short equalizerBandIndex = i;

//            frequency header for each seekBar
            TextView frequencyHeaderTextview = new TextView(this);
            frequencyHeaderTextview.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            frequencyHeaderTextview.setGravity(Gravity.CENTER_HORIZONTAL);
            frequencyHeaderTextview
                    .setText((mEqualizer.getCenterFreq(equalizerBandIndex) / 1000) + " Hz");
            mLinearLayout.addView(frequencyHeaderTextview);

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


            //            **********  the seekBar  **************
//            set the layout parameters for the seekbar
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT - 60,
                    120);
            layoutParams.weight = 1;

//            create a new seekBar
            SeekBar seekBar = new SeekBar(this);
//            give the seekBar an ID
            seekBar.setId(i);
            //            ColorDrawable seekbg;
            //            seekbg = new ColorDrawable(Color.parseColor(realThemeColor));
            //            seekbg.setAlpha(90);
            ////            seekBar.setBackground(new ColorDrawable(Color.rgb(201, 224, 203)));
                        //seekBar.setBackground(seekbg);
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
            seekBarLayout.setMargins(5, 0, 5, 0);
            seekBarRowLayout.setLayoutParams(seekBarLayout);

//            add the lower and upper band level textviews and the seekBar to the row layout
            seekBarRowLayout.addView(lowerEqualizerBandLevelTextview);
            seekBarRowLayout.addView(seekBar);
            seekBarRowLayout.addView(upperEqualizerBandLevelTextview);

            mLinearLayout.addView(seekBarRowLayout);

            //        show the spinner
            equalizeSound();
        }
        mLinearLayout.setRotation(270);

    }

    /*displays the audio waveform*/
    private void setupVisualizerFxAndUI() {
//        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayoutVisual);
        // Create a VisualizerView to display the audio waveform for the current settings
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density)));
        mLinearLayout.addView(mVisualizerView);

        // Create the Visualizer object and attach it to our media player.
//        mVisualizer = new Visualizer(mMediaPlayer.getAudioSessionId());
        mVisualizer = new Visualizer(sessionId);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                              int samplingRate) {
                mVisualizerView.updateVisualizer(bytes);
            }

            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
            }
        }, Visualizer.getMaxCaptureRate() / 2, true, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (isFinishing() && mMediaPlayer != null) {
//            mVisualizer.release();
//            mEqualizer.release();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
    }

    public static void requestAudioSettingsPermission(Activity activity) {

        String requiredPermission = Manifest.permission.RECORD_AUDIO;
        ActivityCompat.requestPermissions(activity, new String[]{requiredPermission}, PERMISSION);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.eq_back_btn:
                finish();
                break;

        }
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
            SeekBar seekBar = (SeekBar) findViewById(equalizerBandIndex);
//                    get current gain setting for this equalizer band
//                    set the progress indicator of this seekBar to indicate the current gain value
            seekBar.setProgress(mEqualizer.getBandLevel(equalizerBandIndex) - lowerEqualizerBandLevel);
        }
        properties.edit_preferences.putInt("position", PRESET_POSITION).commit();
    }

//
//    public static boolean hasAudioSettingsPermission(Context context) {
//
//        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED);
//        return hasPermission;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION:
//                //If Audio Permission is true
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                } else {
//                    Toast.makeText(this, "permission denied",
//                            Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }

}