package com.example.reminder.listItems;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;
import com.example.reminder.dataBase.ReminderItems;
import com.example.reminder.struct.Item;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context mContext;
    private Cursor mCursor;


    public ItemAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
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

        holder.nameItem.setText(name);
        holder.timeItem.setText(time);
        holder.itemView.setTag(id);
        holder.timeStamp = timeStamp;
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

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        public TextView nameItem;
        public TextView timeItem;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;
        public String timeStamp;

        public ItemViewHolder(View itemView) {
            super(itemView);
            nameItem = itemView.findViewById(R.id.nameItemActual);
            timeItem = itemView.findViewById(R.id.timeItemActual);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

        public String getTimeStamp() {
            return timeStamp;
        }

    }
}
