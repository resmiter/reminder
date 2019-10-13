package com.example.reminder.listItems;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reminder.R;
import com.example.reminder.struct.Item;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Item> items;
    private Context context;

    public ItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_actual, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TextView name = holder.itemView.findViewById(R.id.nameItemActual);
        TextView time = holder.itemView.findViewById(R.id.timeItemActual);
        Item item = this.items.get(position);
        name.setText(this.items.get(position).getName());
        time.setText(format(item.getDate()));

    }

    private String format(Date date) {
        return String.format(
                Locale.getDefault(), "%02d.%02d.%d  %d:%d",
                date.getSeconds(), date.getMonth(), date.getYear(), date.getHours(), date.getMinutes()
        );
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Item item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView name;
        public TextView date;
        public RelativeLayout viewBackground;
        public RelativeLayout viewForeground;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameItemActual);
            date = itemView.findViewById(R.id.timeItemActual);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }
}
