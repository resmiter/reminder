package com.example.reminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.example.reminder.dataBase.DBHelper;
import com.example.reminder.dataBase.ReminderItems;
import com.example.reminder.dialogs.TextDialog;
import com.example.reminder.notificationHelper.NotificationHelper;
import com.example.reminder.touchHelper.RecyclerItemTouchHelper;
import com.example.reminder.touchHelper.RecyclerItemTouchHelperListener;
import com.example.reminder.adapters.ItemAdapter;
import com.example.reminder.struct.Reminder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {

    private ItemAdapter itemAdapter;
    private Reminder reminder;
    private TextView emptyData;
    private DrawerLayout drawer;
    private RecyclerView recyclerView;
    private SQLiteDatabase mDatabase;
    private NotificationHelper notificationHelper;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.label_name);
        setSupportActionBar(toolbar);

        notificationHelper = new NotificationHelper(context);
        emptyData = findViewById(R.id.empty);
        DBHelper dbHelper = new DBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerActual);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        itemAdapter = new ItemAdapter(this, mDatabase);
        recyclerView.setAdapter(itemAdapter);
        isListEmpty();

        //fab button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                date.setTime(System.currentTimeMillis());
                date.setYear(calendar.get(Calendar.YEAR));
                date.setMonth(calendar.get(Calendar.MONTH));
                date.setDate(calendar.get(Calendar.DAY_OF_MONTH));
                openDialogs(new Reminder(date, "", MainActivity.this));
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
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this, MainActivity.this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

    }

    @SuppressLint("HandlerLeak")
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            add(reminder);
            isListEmpty();
        }
    };

    private void openDialogs(Reminder reminder) {
        this.reminder = reminder;
        TextDialog textDialog = new TextDialog();
        textDialog.setHandler(h);
        textDialog.setReminder(reminder);
        textDialog.show(getFragmentManager(), "textDialog");
    }

    private void isListEmpty() {
        if (itemAdapter.getAllItems().getCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyData.setVisibility(View.VISIBLE);
        } else {
            emptyData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void add(Reminder reminder) {
        long timeMills = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        reminder.setId(timeMills);
        itemAdapter.removeItem(timeMills);

        Calendar c = Calendar.getInstance();
        c.set(reminder.getDate().getYear(),
                reminder.getDate().getMonth(),
                reminder.getDate().getDate(),
                reminder.getDate().getHours(),
                reminder.getDate().getMinutes(),
                0);

        if ((c.getTime().getTime() - System.currentTimeMillis()) <= (long) 0) {
            Toast.makeText(this, "Выбранное время уже прошло!\nПопробуйте еще раз", Toast.LENGTH_SHORT).show();
            openDialogs(reminder);
        } else {
            cv.put(ReminderItems.ItemEntry.COLUMN_NAME, reminder.getText());
            cv.put(ReminderItems.ItemEntry.COLUMN_TIME, reminder.format());
            cv.put(ReminderItems.ItemEntry._ID, timeMills);
            cv.put(ReminderItems.ItemEntry.COLUMN_TIME_NOTIFICATION, c.getTimeInMillis());

            mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
            itemAdapter.swapCursor(itemAdapter.getAllItems());


            notificationHelper.toastNotification(c, reminder, drawer);
            notificationHelper.createNotification(c, reminder.getText(), timeMills, context);
        }
    }

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

    @Override
    public void onSwipe(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof ItemAdapter.ItemViewHolder) {
            final String name = ((ItemAdapter.ItemViewHolder) viewHolder).nameItem.getText().toString();
            final String time = ((ItemAdapter.ItemViewHolder) viewHolder).timeItem.getText().toString();
            final long id = (long) viewHolder.itemView.getTag();
            final long timeNotification = ((ItemAdapter.ItemViewHolder) viewHolder).timeNotification;
            final String timeStamp = ((ItemAdapter.ItemViewHolder) viewHolder).timeStamp;
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timeNotification);

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    itemAdapter.removeItem(id);
                    notificationHelper.cancelNotification(id, context);

                    Snackbar snackbar = Snackbar.make(drawer, "Удалено", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Отмена", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            itemAdapter.restoreItem(c, name, time, timeStamp, id);
                            notificationHelper.createNotification(c, name, id, context);
                            isListEmpty();
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();

                    isListEmpty();
                    break;
                case ItemTouchHelper.RIGHT:
                    Date date = new Date();
                    date.setTime(c.getTimeInMillis());
                    date.setYear(c.get(Calendar.YEAR));
                    date.setMonth(c.get(Calendar.MONTH));
                    date.setDate(c.get(Calendar.DAY_OF_MONTH));

                    Reminder reminder = new Reminder(date, name, MainActivity.this);
                    reminder.setItemAdapter(itemAdapter);
                    reminder.setNotificationHelper(notificationHelper);
                    reminder.setSwipe(true);
                    reminder.setId(id);
                    itemAdapter.restoreItem(c, name, time, timeStamp, id);
                    openDialogs(reminder);
                    isListEmpty();
                    break;
            }
        }
    }
}
