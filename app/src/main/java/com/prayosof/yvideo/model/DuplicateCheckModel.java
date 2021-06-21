package com.prayosof.yvideo.model;

import android.net.Uri;

/**
 * Created by Yogesh Y. Nikam on 05/07/20.
 */
public class DuplicateCheckModel {

    String videoTitle;
    String videoDuration;
    Uri videoUri;
    int numberOfCount;
    String size;
    String videoPath;

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
    }

    public Uri getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(Uri videoUri) {
        this.videoUri = videoUri;
    }

    public int getNumberOfCount() {
        return numberOfCount;
    }

    public void setNumberOfCount(int numberOfCount) {
        this.numberOfCount = numberOfCount;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }
}
