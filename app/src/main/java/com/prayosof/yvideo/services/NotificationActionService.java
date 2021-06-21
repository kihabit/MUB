package com.prayosof.yvideo.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("PAPPU_TRACKS")
        .putExtra("pappu_notification", intent.getAction()));
    }
}
