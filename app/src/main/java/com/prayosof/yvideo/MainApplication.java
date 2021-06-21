package com.prayosof.yvideo;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.prayosof.yvideo.view.browser.api.ApiClient;
import com.prayosof.yvideo.view.browser.api.ApiService;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;


public class MainApplication extends Application {

    private static MainApplication instance;
    private Intent downloadService;
    private ApiService apiService;
    Scheduler scheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule()).with(new EntypoModule());
        instance = this;
    }

    public Intent getDownloadService() {
        return downloadService;
    }

    public static MainApplication getInstance() {
        return instance;
    }

    public static MainApplication create(Context context) {
        return MainApplication.get(context);
    }

    private static MainApplication get(Context context) {
        return (MainApplication) context.getApplicationContext();
    }

    public ApiService getApiService() {
        if (apiService == null) {
            apiService = ApiClient.create();
        }
        return apiService;
    }

    public Scheduler subscribeScheduler() {
        if (scheduler == null) {
            scheduler = Schedulers.io();
        }

        return scheduler;
    }
}