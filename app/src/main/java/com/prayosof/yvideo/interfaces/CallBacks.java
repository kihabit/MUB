package com.prayosof.yvideo.interfaces;

/**
 * Created by Yogesh Y. Nikam on 02/07/20.
 */
public interface CallBacks {
    void callbackObserver(Object object);
    interface playerCallBack{
        void onItemClickOnItem(int albumId);
        void onPlayingEnd();
    }
}