package com.prayosof.yvideo.view.fragment.audio;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.prayosof.yvideo.R;
import com.prayosof.yvideo.interfaces.AudioPlayerListners;
import com.prayosof.yvideo.interfaces.Playable;
import com.prayosof.yvideo.model.MusicFiles;
import com.prayosof.yvideo.services.MusicService;
import com.prayosof.yvideo.services.NewMediaPlayerService;
import com.prayosof.yvideo.view.activity.AudioPlayerActivity;
import com.prayosof.yvideo.view.activity.PlayerActivity;
import com.prayosof.yvideo.view.fragment.AudioListFragment;
import com.prayosof.yvideo.view.fragment.audio.genres.GenresFragment;

import java.util.ArrayList;
import java.util.Objects;

import denis.musicplayer.data.media.model.Album;
import denis.musicplayer.data.media.model.Track;

import static android.content.ContentValues.TAG;


public class AudioFragment extends Fragment implements View.OnClickListener, Playable {

    private AudioViewModel audioViewModel;
    public static final int REQUEST_CODE = 1;
    public static ArrayList<Track> musicFiles;
    public static ArrayList<Album> albumFiles;
    public static boolean shuffleBoolean = false, repeatBoolean = false;

    View root;
    ImageButton playPauseNowPlaying, currentSongPauseBtn;
    TextView nowPlayingSongName;
    MaterialCardView currentPlayingCard;

    MusicService musicService;


    //MINI PLAYER
    NewMediaPlayerService player;
    SeekBar miniPlayerSeekbar;
    MaterialCardView includeMiniPlayer;

    Intent playerIntent;
    boolean serviceBound = false;
    Playable mPlayable;
    ImageButton mpButtonPrevious, mpCurrentSongPauseBtn, mpButtonNext;
    Runnable mSeekbarUpdater;
    private Handler handler = new Handler();
    TextView mpSongTitle;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        audioViewModel =
                ViewModelProviders.of(this).get(AudioViewModel.class);
        root = inflater.inflate(R.layout.fragment_audios, container, false);

        permission();
        audioViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        return root;
    }

    private void initViewPager() {


        mPlayable = this;
        includeMiniPlayer = root.findViewById(R.id.mini_player_cardview);
        includeMiniPlayer.setOnClickListener(this);
        includeMiniPlayer.setVisibility(View.GONE);

        mpButtonPrevious = root.findViewById(R.id.mpButtonPrevious);
        mpCurrentSongPauseBtn= root.findViewById(R.id.mpCurrentSongPauseBtn);
        mpButtonNext = root.findViewById(R.id.mpButtonNext);

        mpButtonPrevious.setOnClickListener(this);
        mpCurrentSongPauseBtn.setOnClickListener(this);
        mpButtonNext.setOnClickListener(this);

        miniPlayerSeekbar = root.findViewById(R.id.miniPlayerSeekbar);

        mpSongTitle = root.findViewById(R.id.mpSongTitle);


        ViewPager viewPager = root.findViewById(R.id.viewpager);
        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragments(new AudioListFragment(), "Folder");
        //viewPagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewPagerAdapter.addFragments(new AlbumFragment(), "Albums");
        //viewPagerAdapter.addFragments(new ArtistFragment(), "Artist");
        //viewPagerAdapter.addFragments(new GenresFragment(), "Genres");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }



    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do whatever you want permission related
//                Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    musicFiles = getAllAudio();
                    //albumFiles = getAllAlbums();
                } else {
                    musicFiles = getAllAudio();
                    //albumFiles = getAllAlbums();
                }
                initViewPager();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void permission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
//            Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            musicFiles = getAllAudio();
            //albumFiles  =getAllAlbums();
            initViewPager();
        }
    }




    public static class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public  ArrayList<Track> getAllAudio() {
        ArrayList<Track> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID
        };
        Cursor cursor =   requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                String mAlbumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                try {
                    if (id != null && mAlbumId != null) {
                        Track musicFiles = new Track(Long.parseLong(id), title, artist, path, duration,Long.parseLong(mAlbumId));
                        tempAudioList.add(musicFiles);
                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }


            }
            cursor.close();
        }
        return tempAudioList;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public  ArrayList<Album> getAllAlbums() {

        ArrayList<Album> albums = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM,
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART

        };
        Cursor cursor =   requireContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums._ID));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST));
                String cover = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));

                albums.add(new Album(Long.parseLong(id),album,artist,cover));
                // take log:e for check
            }
            cursor.close();
        }

        return albums;
    }

//    private ServiceConnection serviceConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            musicService = ((MusicService.MyBinder) (service)).getService();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            musicService = null;
//        }
//    };

    @Override
    public void onResume() {
        super.onResume();

        playerIntent = new Intent(requireActivity(), NewMediaPlayerService.class);


        if (checkIsPlayerRunning()) {
            includeMiniPlayer.setVisibility(View.VISIBLE);
            startMiniPlayer();

        } else {
            includeMiniPlayer.setVisibility(View.GONE);

        }
    }

    public void startMiniPlayer() {

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                NewMediaPlayerService.LocalBinder binder = (NewMediaPlayerService.LocalBinder) service;
                player = binder.getService();
                player.registerCallBack(mPlayable);
                serviceBound = true;
                if (NewMediaPlayerService.isplaying()) {
                    mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause);
                } else {
                    mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_play);
                }



            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceBound = false;


            }
        };

        requireActivity().bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        mSeekbarUpdater = new Runnable() {
            @Override
            public void run() {

                // user interface interactions and updates on screen
                if (player != null) {
                    miniPlayerSeekbar.setProgress(player.getCurrentPosition());
                    miniPlayerSeekbar.setMax(player.getDuration());
                }
                handler.postDelayed(mSeekbarUpdater, 200);

            }
        };

        mpSongTitle.setText(NewMediaPlayerService.getSongTitle());
        handler.postDelayed(mSeekbarUpdater, 1000);

        miniPlayerSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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



    public boolean checkIsPlayerRunning() {
        return NewMediaPlayerService.isRunning();
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.mpButtonNext:
                player.skipToNext();
                break;

            case R.id.mpButtonPrevious:
                player.skipToPrevious();
                break;

            case R.id.mpCurrentSongPauseBtn:
                player.playOrPause();
                break;

            case R.id.mini_player_cardview:

                Intent intent = new Intent(requireContext(), AudioPlayerActivity.class);
                intent.putExtra("is_track_adapter", "from_mini_player");
                //intent.putExtra("position", position);
                requireActivity().startActivity(intent);

                break;


        }
    }

    @Override
    public void onTrackPrevious() {

    }

    @Override
    public void onTrackPlay(String songName) {
        mpSongTitle.setText(songName);

        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause);
        miniPlayerSeekbar.setProgress(player.getCurrentPosition());
        miniPlayerSeekbar.setMax(player.getDuration());

    }

    @Override
    public void onTrackPause() {
        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_controls_play);
        miniPlayerSeekbar.setProgress(player.getCurrentPosition());
        miniPlayerSeekbar.setMax(player.getDuration());
    }

    @Override
    public void onTrackNext(Track track) {
        mpSongTitle.setText(track.getTitle());

    }

    @Override
    public void onTrackResume() {
        mpCurrentSongPauseBtn.setImageResource(R.drawable.exo_icon_pause);
        miniPlayerSeekbar.setProgress(player.getCurrentPosition());
        miniPlayerSeekbar.setMax(player.getDuration());
    }

    @Override
    public void onServiceStoped() {
        includeMiniPlayer.setVisibility(View.GONE);
    }
}
