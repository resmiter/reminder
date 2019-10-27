package com.example.reminder.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TimePicker;

import com.example.reminder.R;
import com.example.reminder.struct.Reminder;

import java.util.Date;

@SuppressLint("ValidFragment")
public class TimeDialog extends DialogFragment {
    Date currentDate = new Date();
    Handler h;
    Reminder reminder;

    public TimeDialog(Handler arg_h, Reminder reminder){
        this.h = arg_h;
        this.reminder = reminder;
        currentDate.setTime(reminder.getDate().getTime());
    }

    private TimePickerDialog.OnTimeSetListener callback = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("btn", "Selected Time - " + hourOfDay + ":" + minute);
            msg.setData(data);
            reminder.getDate().setHours(hourOfDay);
            reminder.getDate().setMinutes(minute);
            reminder.getDate().setSeconds(0);
            new DateDialog(h, reminder).show(getFragmentManager(), "dateDialog");
        }
    };

    public Dialog onCreateDialog(Bundle bundle){
        TimePickerDialog tpd = new TimePickerDialog(getActivity(), R.style.timePicker,
                callback, currentDate.getHours(), currentDate.getMinutes(), true);
        return tpd;
    }
}
