package com.example.reminder.dataBase;

import android.provider.BaseColumns;

public class ReminderItems {

    private ReminderItems(){}

    public static final class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME =  "itemsActualList";
        public static final String COLUMN_NAME =  "name";
        public static final String COLUMN_TIME =  "time";
        public static final String COLUMN_TIMESTAMP =  "timestamp";
    }
}
