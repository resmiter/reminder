package com.example.reminder.helper;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerItemTouchHelperListener {
    void onSwipe (RecyclerView.ViewHolder viewHolder, int direction, int position);
}
