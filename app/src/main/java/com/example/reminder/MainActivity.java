package com.example.reminder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {

    private DrawerLayout drawer;
    private Reminder reminder;
    private TextDialog textDialog;
    private final List<Item> items = new ArrayList<>();
    private final ItemAdapter itemAdapter= new ItemAdapter(this.items);
    private TextView emptyData;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        emptyData = findViewById(R.id.empty);
        recyclerView = findViewById(R.id.recyclerActual);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        isListEmpty();

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

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack
                = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);
    }

    public void add(Reminder reminder) {
        this.items.add(new Item(reminder.getText(), reminder.getDate()));
        itemAdapter.notifyItemInserted(this.items.size() - 1);
    }

    public void isListEmpty() {
        if(items.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyData.setVisibility(View.VISIBLE);
        }else{
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
            Toast.makeText(MainActivity.this, "Уведомление создано" , Toast.LENGTH_SHORT).show();
            reminder.setText(reminder.getText());
            add(reminder);
            isListEmpty();
        }
    };

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof ItemAdapter.MyViewHolder) {
            final Item deletedItem = items.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            itemAdapter.removeItem(deletedIndex);
            itemAdapter.notifyDataSetChanged();

            Snackbar snackbar = Snackbar.make(drawer, "Удалено", Snackbar.LENGTH_LONG);
            snackbar.setAction("Отмена", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemAdapter.restoreItem(deletedItem, deletedIndex);
                    itemAdapter.notifyDataSetChanged();

                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
    }
}
