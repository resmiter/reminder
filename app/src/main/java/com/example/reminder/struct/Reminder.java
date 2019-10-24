package com.example.reminder.struct;

import java.util.Date;

public class Reminder {

    private Date date;
    private String text;
    private Boolean lifeOfReminder;
    private long id;

    public Reminder (Date date, String text, Boolean lifeOfReminder, long id){
        this.date = date;
        this.text = text;
        this.lifeOfReminder = lifeOfReminder;
        this.id = id;
    }

    public Reminder (){
        this.date = null;
        this.text = null;
        this.lifeOfReminder = null;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Boolean getLifeOfReminder() {
        return lifeOfReminder;
    }

    public void setLifeOfReminder(Boolean lifeOfReminder) {
        this.lifeOfReminder = lifeOfReminder;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
