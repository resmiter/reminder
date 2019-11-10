package com.example.reminder.touchHelper;

import androidx.recyclerview.widget.RecyclerView;


public interface RecyclerItemTouchHelperListener {
    void onSwipe (RecyclerView.ViewHolder viewHolder, int direction, int position);
}
