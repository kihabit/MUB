package com.prayosof.yvideo.view.browser.api;

public interface ResponseStatus {
    void onFail(Object obj);

    void onSuccess(Object obj);
}