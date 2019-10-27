package com.example.reminder.dialogs;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;

import com.example.reminder.R;
import com.example.reminder.struct.Reminder;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment {
    Handler h;
    Reminder reminder;

    public DateDialog(Handler arg_h, Reminder reminder){
        this.h = arg_h;
        this.reminder = reminder;
    }

    private DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int mouth, int day) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("btn", "Selected date - day: " + day + "  mouth: " + (mouth + 1) + "  year: " + year);
            msg.setData(data);
            reminder.getDate().setDate(day);
            reminder.getDate().setMonth(mouth);
            reminder.getDate().setYear(year);
            if (reminder.isSwipe()){
            reminder.getItemAdapter().removeItem(reminder.getId());
            reminder.getNotificationHelper().cancelNotification(reminder.getId(), reminder.getContext());
            reminder.setSwipe(false);
            }
            h.sendMessage(msg);
        }
    };

    public Dialog onCreateDialog(Bundle bundle){
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.datePicker,
                callback, reminder.getDate().getYear(), reminder.getDate().getMonth(), reminder.getDate().getDate());
        return dpd;
    }
}
