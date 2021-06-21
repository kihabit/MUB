package com.prayosof.yvideo.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.audioAdapter.TrackAdapter;
import com.prayosof.yvideo.services.MediaPlayerService;
import com.prayosof.yvideo.services.MusicService;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import denis.musicplayer.data.media.model.Track;

import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.musicFiles;

public class TestActivity extends AppCompatActivity {

    int position = 0;

    static ArrayList<Track> selectedTrackData =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        if (null != getIntent().getExtras()) {
            if (Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("is_track_adapter")).equals("track")) {
                selectedTrackData = TrackAdapter.tracksData;//(ArrayList<Track>) getIntent().getSerializableExtra("tracks");

            } else {
                selectedTrackData = musicFiles;
            }
        }

        position = getIntent().getIntExtra("position", -1);

        File currentFile = new File(selectedTrackData.get(position).getData());
        if (currentFile.exists()) {
            Toast.makeText(getApplicationContext(), "Exist", Toast.LENGTH_LONG).show();
            String songName = selectedTrackData.get(position).getTitle();
            MediaPlayerService.MUSIC_PATH =  selectedTrackData.get(position).getData();


        } else {

            Toast.makeText(getApplicationContext(), "File not Exist", Toast.LENGTH_LONG).show();
            return;

        }


        Intent intent = new Intent( getApplicationContext(), MediaPlayerService.class );
        intent.setAction( MediaPlayerService.ACTION_PLAY );
        startService( intent );
    }
}