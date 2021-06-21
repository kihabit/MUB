package com.prayosof.yvideo.view.fragment.audio;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.prayosof.yvideo.R;
import com.prayosof.yvideo.adapter.audioAdapter.MusicAdapter;
import com.prayosof.yvideo.interfaces.CustomOnClickListener;
import com.prayosof.yvideo.view.fragment.BaseFragment;


import static com.prayosof.yvideo.view.fragment.audio.AudioFragment.musicFiles;

public class SongsFragment extends BaseFragment implements CustomOnClickListener {


    RecyclerView recyclerView;
    MusicAdapter musicAdapter;
    CustomOnClickListener listner;
    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_songs, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        listner = this;
        if (!(musicFiles.size() < 1)) {
            musicAdapter = new MusicAdapter(getContext(), musicFiles, listner);
            recyclerView.setAdapter(musicAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;


    }

    @Override
    public void onShareClick(String fileName, String filePath) {
        startFileShareIntent(filePath);
    }
}