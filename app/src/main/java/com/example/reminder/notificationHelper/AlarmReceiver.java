package com.example.reminder.notificationHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        nb.setContentTitle(intent.getStringExtra("reminder")).setAutoCancel(true);
        notificationHelper.getManager().notify(1, nb.build());
    }
}