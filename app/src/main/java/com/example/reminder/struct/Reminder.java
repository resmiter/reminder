package com.example.reminder.struct;

import android.content.Context;

import com.example.reminder.adapters.ItemAdapter;
import com.example.reminder.notificationHelper.NotificationHelper;

import java.util.Date;
import java.util.Locale;

public class Reminder {

    private Date date;
    private String text;
    private boolean isSwipe;
    private long id;
    private Context context;
    private ItemAdapter itemAdapter;
    private NotificationHelper notificationHelper;

    public Reminder (Date date, String text, Context context){
        this.date = date;
        this.text = text;
        this.context = context;
        this.isSwipe = false;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Context getContext() {
        return context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSwipe() {
        return isSwipe;
    }

    public void setSwipe(boolean swipe) {
        isSwipe = swipe;
    }

    public ItemAdapter getItemAdapter() {
        return itemAdapter;
    }

    public void setItemAdapter(ItemAdapter itemAdapter) {
        this.itemAdapter = itemAdapter;
    }

    public NotificationHelper getNotificationHelper() {
        return notificationHelper;
    }

    public void setNotificationHelper(NotificationHelper notificationHelper) {
        this.notificationHelper = notificationHelper;
    }

    public String format() {
        return String.format(
                Locale.getDefault(), "%02d.%02d.%d  %02d:%02d",
                date.getDate(), date.getMonth() + 1, date.getYear(), date.getHours(), date.getMinutes()
        );
    }
}
