package com.prayosof.yvideo.view.fragment.duplicate_video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DuplicateDirectoryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DuplicateDirectoryViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}