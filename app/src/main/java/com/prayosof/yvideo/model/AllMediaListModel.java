package com.prayosof.yvideo.model;

public class AllMediaListModel {

    public String fileName;
    public boolean isSelected = false;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}