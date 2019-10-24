package com.example.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.reminder.dataBase.DBHelper;
import com.example.reminder.dataBase.ReminderItems;
import com.example.reminder.dialogs.TextDialog;
import com.example.reminder.dialogs.TimeDialog;
import com.example.reminder.helper.RecyclerItemTouchHelper;
import com.example.reminder.helper.RecyclerItemTouchHelperListener;
import com.example.reminder.listItems.ItemAdapter;
import com.example.reminder.struct.Item;
import com.example.reminder.struct.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.ItemTouchUIUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;
import static android.telephony.AvailableNetworkInfo.PRIORITY_LOW;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {

    private ItemAdapter itemAdapter;
    private Reminder reminder;
    private TextView emptyData;
    private TextDialog textDialog;
    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        emptyData = findViewById(R.id.empty);
        DBHelper dbHelper = new DBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerActual);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        itemAdapter = new ItemAdapter(this, getAllItems());
        recyclerView.setAdapter(itemAdapter);
        isListEmpty();

        //fab button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reminder = new Reminder(new Date(), "", true, System.currentTimeMillis());
                textDialog = new TextDialog();
                textDialog.setHandler(h);
                textDialog.setReminder(reminder);
                textDialog.show(getFragmentManager(), "textDialog");
            }
        });

        //navigation bar
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //swipe item
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
    }

    public void isListEmpty() {
        if (getAllItems().getCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyData.setVisibility(View.VISIBLE);
        } else {
            emptyData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @SuppressLint("HandlerLeak")
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            reminder.setText(reminder.getText());
            add(reminder);
            isListEmpty();
        }
    };

    public void toastNotification(Calendar c, Reminder reminder) {
        Calendar calendar = Calendar.getInstance();

        if ((c.getTime().getTime() - System.currentTimeMillis()) <= (long) 0) {
            Toast.makeText(this, "Выбранное время уже прошло!", Toast.LENGTH_SHORT).show();
            return;
        }

        long mouthInSec = 2592000;
        if ((c.getTime().getTime() - System.currentTimeMillis()) < mouthInSec * 1000) {
            int day;
            int hour;
            int minute;

            if (reminder.getDate().getDate() == calendar.get(Calendar.DAY_OF_MONTH)) {
                day = 0;
            } else if (reminder.getDate().getDate() - calendar.get(Calendar.DAY_OF_MONTH) < 0) {
                day = c.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + reminder.getDate().getDate();
            } else {
                day = reminder.getDate().getDate() - calendar.get(Calendar.DAY_OF_MONTH);
            }


            if (reminder.getDate().getHours() == calendar.get(Calendar.HOUR_OF_DAY)) {
                hour = 0;
            } else if (reminder.getDate().getHours() - calendar.get(Calendar.HOUR_OF_DAY) < 0) {
                day = day - 1;
                hour = 60 + reminder.getDate().getHours() - calendar.get(Calendar.HOUR_OF_DAY);
            } else {
                hour = reminder.getDate().getHours() - calendar.get(Calendar.HOUR_OF_DAY);
            }

            if (reminder.getDate().getMinutes() == calendar.get(Calendar.MINUTE)) {
                minute = 0;
            } else if (reminder.getDate().getMinutes() - calendar.get(Calendar.MINUTE) < 0) {
                hour = hour - 1;
                day = day - 1;
                minute = 60 + reminder.getDate().getMinutes() - calendar.get(Calendar.MINUTE);
            } else {
                minute = reminder.getDate().getMinutes() - calendar.get(Calendar.MINUTE);
            }

            if (hour < 0) hour += 24;

            Toast.makeText(this, "Осталось   " + "дней: " + day + "   часов: " + hour + "   минут: " + minute, Toast.LENGTH_LONG).show();
        }
    }

    private String format(Date date) {
        return String.format(
                Locale.getDefault(), "%02d.%02d.%d  %d:%d",
                date.getDate(), date.getMonth() + 1, date.getYear(), date.getHours(), date.getMinutes()
        );
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                ReminderItems.ItemEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                ReminderItems.ItemEntry.COLUMN_TIMESTAMP + " DESC"
        );
    }


    private void createNotification(Calendar c, String name, long id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("reminder", name);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) id, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelNotification(long id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) id, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    public void removeItem(long id) {
        mDatabase.delete(ReminderItems.ItemEntry.TABLE_NAME, ReminderItems.ItemEntry._ID + "=" + id, null);
        itemAdapter.swapCursor(getAllItems());
    }

    public void restoreItem(Calendar c, String name, String time, String timeStamp) {

        ContentValues cv = new ContentValues();
        cv.put(ReminderItems.ItemEntry.COLUMN_NAME, name);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME, time);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIMESTAMP, timeStamp);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME_NOTIFICATION, c.getTime().getTime());

        mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
        itemAdapter.swapCursor(getAllItems());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_settings) {
//            deleteAllItems();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
            case (R.id.nav_home):
                break;
            case (R.id.nav_send):
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:timiroffsergey@gmail.com"));
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void deleteAllItems () {
        mDatabase.delete(ReminderItems.ItemEntry.TABLE_NAME, null, null);
        itemAdapter.swapCursor(getAllItems());

        isListEmpty();

    }

    public void add(Reminder reminder) {
        long timeMills = System.currentTimeMillis();
        ContentValues cv = new ContentValues();

        Calendar c = Calendar.getInstance();
        c.set(reminder.getDate().getYear(),
                reminder.getDate().getMonth(),
                reminder.getDate().getDate(),
                reminder.getDate().getHours(),
                reminder.getDate().getMinutes(),
                0);

        cv.put(ReminderItems.ItemEntry.COLUMN_NAME, reminder.getText());
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME, format(reminder.getDate()));
        cv.put(ReminderItems.ItemEntry._ID, timeMills);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME_NOTIFICATION, c.getTimeInMillis());

        mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
        itemAdapter.swapCursor(getAllItems());

        toastNotification(c, reminder);
        createNotification(c, reminder.getText(), timeMills);
    }


    @Override
    public void onSwipe(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof ItemAdapter.ItemViewHolder) {
            final String name = ((ItemAdapter.ItemViewHolder) viewHolder).nameItem.getText().toString();
            final String time = ((ItemAdapter.ItemViewHolder) viewHolder).timeItem.getText().toString();
            final long id = (long) viewHolder.itemView.getTag();
            final long timeNotification = ((ItemAdapter.ItemViewHolder) viewHolder).timeNotification;
            final String timeStamp = ((ItemAdapter.ItemViewHolder) viewHolder).timeStamp;

            removeItem(id);
            cancelNotification(id);

            Snackbar snackbar = Snackbar.make(drawer, "Удалено", Snackbar.LENGTH_LONG);
            snackbar.setAction("Отмена", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(timeNotification);
                    restoreItem(c, name, time, timeStamp);
                    //сразу создает нотификацию
                    createNotification(c, name, id);
                    isListEmpty();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
            isListEmpty();
        }
    }


}
