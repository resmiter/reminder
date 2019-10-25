package com.example.reminder.notificationHelper;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.reminder.R;
import com.example.reminder.struct.Reminder;

import java.util.Calendar;


public class NotificationHelper extends ContextWrapper {

    public static final String channelID = "channelID";
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), channelID)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.add);
    }

    public void createNotification(Calendar c, String name, long id, Context context) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("reminder", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    public void cancelNotification(long id, Context context) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void toastNotification(Calendar c, Reminder reminder) {

        Calendar calendar = Calendar.getInstance();
        long mouthInSec = 2419200;

        if ((c.getTimeInMillis() - System.currentTimeMillis()) < mouthInSec * 1000) {
            int day;
            int hour;
            int minute;

            if (reminder.getDate().getDate() == calendar.get(Calendar.DAY_OF_MONTH)) {
                day = 0;
            } else if (reminder.getDate().getDate() - calendar.get(Calendar.DAY_OF_MONTH) < 0) {
                day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + reminder.getDate().getDate();
            } else {
                day = reminder.getDate().getDate() - calendar.get(Calendar.DAY_OF_MONTH);
            }

            if (reminder.getDate().getHours() == calendar.get(Calendar.HOUR_OF_DAY)) {
                hour = 0;
            } else if (reminder.getDate().getHours() - calendar.get(Calendar.HOUR_OF_DAY) < 0) {
                day--;
                hour = 24 - calendar.get(Calendar.HOUR_OF_DAY) + reminder.getDate().getHours();
            } else {
                hour = reminder.getDate().getHours() - calendar.get(Calendar.HOUR_OF_DAY);
            }

            if (reminder.getDate().getMinutes() == calendar.get(Calendar.MINUTE)) {
                minute = 0;
            } else if (reminder.getDate().getMinutes() - calendar.get(Calendar.MINUTE) < 0) {
                hour = hour - 1;
                minute = 60 - calendar.get(Calendar.MINUTE) + reminder.getDate().getMinutes();
            } else {
                minute = reminder.getDate().getMinutes() - calendar.get(Calendar.MINUTE);
            }

            String result = "Оталось - ";
            if (hour < 0) hour += 24;
            if (day != 0) result += "дней: " + day + ", ";
            if (hour != 0) result += "часов: " + hour + ", ";
            if (minute != 0) result += "минут: " + minute + ", ";
            result += "!";
            result = result.replace(", !", ".");

            Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        }
    }
}
