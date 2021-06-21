package com.prayosof.yvideo.interfaces;

import denis.musicplayer.data.media.model.Track;

public interface Playable {
    void onTrackPrevious();
    void onTrackPlay(String soneName);
    void onTrackPause();
    void onTrackNext(Track track);
    void onTrackResume();
    void onServiceStoped();
}
