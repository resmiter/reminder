package com.example.reminder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.example.reminder.dataBase.DBHelper;
import com.example.reminder.dataBase.ReminderItems;
import com.example.reminder.dialogs.TextDialog;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {

    private final List<Item> items = new ArrayList<>();
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
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        isListEmpty();


        //fab button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                reminder = new Reminder(new Date(), "", true);
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


        //swipe
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
    }

    public void add(Reminder reminder) {
        this.items.add(new Item(reminder.getText(), reminder.getDate()));
        itemAdapter.notifyItemInserted(this.items.size() - 1);

        ContentValues cv = new ContentValues();
        cv.put(ReminderItems.ItemEntry.COLUMN_NAME, reminder.getText());
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME, format(reminder.getDate()));

        mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
        itemAdapter.swapCursor(getAllItems());
    }

    private String format(Date date) {
        return String.format(
                Locale.getDefault(), "%02d.%02d.%d  %d:%d",
                date.getSeconds(), date.getMonth(), date.getYear(), date.getHours(), date.getMinutes()
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

    public void isListEmpty() {
        if (items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyData.setVisibility(View.VISIBLE);
        } else {
            emptyData.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        Intent intent;
        switch (id) {
            case (R.id.nav_home):
                Toast.makeText(this, "12313221", Toast.LENGTH_SHORT).show();
                break;
            case (R.id.nav_send):
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:timiroffsergey@gmail.com"));
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("HandlerLeak")
    Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MainActivity.this, "Уведомление создано", Toast.LENGTH_SHORT).show();
            reminder.setText(reminder.getText());
            add(reminder);
//            isListEmpty();
        }
    };

    public void removeItem(long id) {
        mDatabase.delete(ReminderItems.ItemEntry.TABLE_NAME, ReminderItems.ItemEntry._ID + "=" + id, null);
        itemAdapter.swapCursor(getAllItems());
    }
    public void restoreItem(long id, String name, String time, String timeStamp) {

        ContentValues cv = new ContentValues();
        cv.put(ReminderItems.ItemEntry.COLUMN_NAME, name);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME, time);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIMESTAMP, timeStamp);

        mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
        itemAdapter.swapCursor(getAllItems());
    }

    @Override
    public void onSwipe(final RecyclerView.ViewHolder viewHolder, int direction, final int position) {
        if (viewHolder instanceof ItemAdapter.ItemViewHolder) {
            final String name = ((ItemAdapter.ItemViewHolder) viewHolder).nameItem.getText().toString();
            final String time = ((ItemAdapter.ItemViewHolder) viewHolder).timeItem.getText().toString();
            final long id = (long) viewHolder.itemView.getTag();
            final String timeStamp = ((ItemAdapter.ItemViewHolder) viewHolder).getTimeStamp();

            removeItem(id);

            Snackbar snackbar = Snackbar.make(drawer, "Удалено", Snackbar.LENGTH_LONG);
            snackbar.setAction("Отмена", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    restoreItem(id, name, time, timeStamp);
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }
}
