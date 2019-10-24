package com.example.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.reminder.helper.NotificationHelper;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        nb.setContentTitle(intent.getStringExtra("reminder"));
        notificationHelper.getManager().notify(1, nb.build());
    }
}



















//    public static void createChennelIfNeeded(NotificationManager manager){
//        NotificationChannel notificationChannel = null;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            notificationChannel = new NotificationChannel(CHENNEL_ID, CHENNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
//            manager.createNotificationChannel(notificationChannel);
//        }
//    }


/*
int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("todo");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingintent = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHENNEL_ID)
        .setSmallIcon(R.drawable.add)
        .setContentTitle(message)
        .setWhen(System.currentTimeMillis())
        .setAutoCancel(true)
        .setContentIntent(pendingintent)
                        .setFullScreenIntent(pendingintent, true)
        .setPriority(Notification.PRIORITY_HIGH)
        .setDefaults(Notification.DEFAULT_ALL);
        createChennelIfNeeded(notificationManager);
        notificationManager.notify(notificationId, notificationBuilder.build());
        */