package com.jmotionsoft.towntalk.message;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jmotionsoft.towntalk.util.CLog;

public class CustomDialog extends DialogFragment {
    private final String TAG = getClass().getSimpleName();
    String title, message;

    static CustomDialog newInstance(String title, String message) {
        CustomDialog frag = new CustomDialog();
        Bundle args = new Bundle();

        args.putString("title", title);
        args.putString("message", message);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        title = getArguments().getString("title", "");
        message = getArguments().getString("message", "");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
