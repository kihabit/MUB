package com.prayosof.yvideo.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Yogesh Y. Nikam on 18/08/20.
 */

public class ModelAudios implements Serializable {
    String str_folder;
    ArrayList<String> allAudioPaths;

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAllAudioPaths() {
        return allAudioPaths;
    }

    public void setAllAudioPaths(ArrayList<String> allAudioPaths) {
        this.allAudioPaths = allAudioPaths;
    }
}
