package com.prayosof.yvideo.model;

import java.util.ArrayList;

/**
 * Created by Yogesh Y. Nikam on 24/06/20.
 */
public class ModelVideos {
    String str_folder;
    public ArrayList<String> allVideoPath;
    boolean isSelected;

    public String getStr_folder() {
        return str_folder;
    }

    public void setStr_folder(String str_folder) {
        this.str_folder = str_folder;
    }

    public ArrayList<String> getAllVideoPath() {
        return allVideoPath;
    }

    public void setAllVideoPath(ArrayList<String> allVideoPath) {
        this.allVideoPath = allVideoPath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}