package com.example.reminder.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.reminder.dataBase.ReminderItems.ItemEntry;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "itemList.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ITEMSACTUALLIST_TABLE = "CREATE TABLE " +
                ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ItemEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_TIME_NOTIFICATION + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";
        db.execSQL(SQL_CREATE_ITEMSACTUALLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        onCreate(db);
    }
}
