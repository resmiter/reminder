package com.example.reminder.dialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.reminder.MainActivity;
import com.example.reminder.R;
import com.example.reminder.struct.Reminder;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class DateDialog extends DialogFragment {
    Handler h;
    Reminder reminder;
    Calendar calendar = Calendar.getInstance();

    public DateDialog(Handler arg_h, Reminder reminder){
        h = arg_h;
        this.reminder = reminder;
    }

    private DatePickerDialog.OnDateSetListener callback = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int mouth, int day) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("btn", "Selected date - day: " + day + "  mouth: " + (mouth + 1) + "  year: " + year);
            msg.setData(data);
            reminder.getDate().setSeconds(day);
            reminder.getDate().setMonth(mouth + 1);
            reminder.getDate().setYear(year);
            h.sendMessage(msg);
        }
    };

    //то, что показанно при создании диалога
    public Dialog onCreateDialog(Bundle bundle){
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), R.style.datePicker,
                callback, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        return dpd;
    }
}
