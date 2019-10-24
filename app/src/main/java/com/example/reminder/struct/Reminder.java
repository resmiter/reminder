package com.example.reminder.struct;

import java.util.Date;
import java.util.Locale;

public class Reminder {

    private Date date;
    private String text;

    public Reminder (Date date, String text){
        this.date = date;
        this.text = text;
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

    public String format() {
        return String.format(
                Locale.getDefault(), "%02d.%02d.%d  %02d:%02d",
                date.getDate(), date.getMonth() + 1, date.getYear(), date.getHours(), date.getMinutes()
        );
    }
}
