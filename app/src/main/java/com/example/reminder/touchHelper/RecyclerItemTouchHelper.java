package com.example.reminder.touchHelper;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback{

    private RecyclerItemTouchHelperListener listener;
    private Context context;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener, Context context) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.context = context;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if(listener != null)
            listener.onSwipe(viewHolder, direction, viewHolder.getAdapterPosition());
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftActionIcon(R.drawable.delite)
                .addSwipeRightActionIcon(R.drawable.pen)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.materialRed))
                .addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.materialYellow))
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
