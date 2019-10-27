package com.example.reminder.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;
import com.example.reminder.dataBase.ReminderItems;

import java.util.Calendar;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    private SQLiteDatabase mDatabase;

    public ItemAdapter(Context context, SQLiteDatabase mDatabase) {
        this.mContext = context;
        this.mDatabase = mDatabase;
        this.mCursor = getAllItems();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View itemView = inflater.inflate(R.layout.items_actual, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)){
                return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(ReminderItems.ItemEntry.COLUMN_NAME));
        String time = mCursor.getString(mCursor.getColumnIndex(ReminderItems.ItemEntry.COLUMN_TIME));
        String timeStamp = mCursor.getString(mCursor.getColumnIndex(ReminderItems.ItemEntry.COLUMN_TIMESTAMP));
        long id = mCursor.getLong(mCursor.getColumnIndex(ReminderItems.ItemEntry._ID));
        long timeNotification = Long.parseLong(mCursor.getString(mCursor.getColumnIndex(ReminderItems.ItemEntry.COLUMN_TIME_NOTIFICATION)));

        holder.nameItem.setText(name);
        holder.timeItem.setText(time);
        holder.itemView.setTag(id);
        holder.timeStamp = timeStamp;
        holder.timeNotification = timeNotification;
        holder.id = id;
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor (Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public Cursor getAllItems() {
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

    public void removeItem(long id) {
        mDatabase.delete(ReminderItems.ItemEntry.TABLE_NAME, ReminderItems.ItemEntry._ID + "=" + id, null);
        swapCursor(getAllItems());
    }

    public void restoreItem(Calendar c, String name, String time, String timeStamp, long id) {
        ContentValues cv = new ContentValues();
        cv.put(ReminderItems.ItemEntry.COLUMN_NAME, name);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME, time);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIMESTAMP, timeStamp);
        cv.put(ReminderItems.ItemEntry.COLUMN_TIME_NOTIFICATION, c.getTime().getTime());
        cv.put(ReminderItems.ItemEntry._ID, id);

        mDatabase.insert(ReminderItems.ItemEntry.TABLE_NAME, null, cv);
        swapCursor(getAllItems());
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        public TextView nameItem;
        public TextView timeItem;
        public RelativeLayout viewForeground;
        public String timeStamp;
        public long id;
        public long timeNotification;

        ItemViewHolder(View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.nameItemActual);
            timeItem = itemView.findViewById(R.id.timeItemActual);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}
