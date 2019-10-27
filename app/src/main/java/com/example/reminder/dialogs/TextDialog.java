package com.example.reminder.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.reminder.struct.Reminder;
import com.example.reminder.R;

public class TextDialog extends DialogFragment implements OnClickListener {

    Reminder reminder;
    EditText input;
    Handler h;
    InputMethodManager imm;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity(), R.style.textDialog);
        alert.setTitle(R.string.reminder);
        alert.setPositiveButton(R.string.next, this);
        alert.setNegativeButton(R.string.cancel, this);
        input = new EditText(getActivity());
        input.setText(reminder.getText());
        alert.setView(input);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(20);
        input.setFilters(FilterArray);
        //открывает клавиатуру, при открытии клавиатуры
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        input.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                input.requestFocus();
                imm.showSoftInput(input, 0);
            }
        }, 100);
        return alert.create();
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                reminder.setText(input.getText().toString());
                new TimeDialog(h, reminder).show(getFragmentManager(), "timeDialog");
                break;
            case Dialog.BUTTON_NEGATIVE:
                break;
        }
    }

    public void setHandler(Handler h) {
        this.h = h;
    }

    public void setReminder(Reminder reminder){
        this.reminder = reminder;
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }
}
