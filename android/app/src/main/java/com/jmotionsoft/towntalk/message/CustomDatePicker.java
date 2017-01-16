package com.jmotionsoft.towntalk.message;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.DatePicker;

import com.jmotionsoft.towntalk.util.CLog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    private String TAG = this.getClass().getSimpleName();

    private static CustomDatePickerListener mListener;
    private SimpleDateFormat DATA_FORMAT;

    public static CustomDatePicker newInstance(String date, CustomDatePickerListener listener){
        mListener = listener;

        CustomDatePicker frag = new CustomDatePicker();
        Bundle args = new Bundle();
        args.putString("date", date);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try{
            super.show(manager, tag);
        }catch (IllegalStateException e){
            CLog.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();

        String date = getArguments().getString("date", "");

        if(!date.equals("")){
            try{
                calendar.setTime(DATA_FORMAT.parse(date));
            }catch (Exception e){
                CLog.e(TAG, e.getMessage(), e);
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        mListener.onDataSet(DATA_FORMAT.format(calendar.getTime()));
    }
}
