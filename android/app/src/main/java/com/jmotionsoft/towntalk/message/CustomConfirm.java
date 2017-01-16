package com.jmotionsoft.towntalk.message;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jmotionsoft.towntalk.R;
import com.jmotionsoft.towntalk.util.CLog;

public class CustomConfirm extends DialogFragment {
    private final String TAG = this.getClass().getSimpleName();

    private static CustomConfirmListener mListener;

    public static CustomConfirm newInstance(String message, CustomConfirmListener listener) {
        mListener = listener;

        CustomConfirm frag = new CustomConfirm();
        Bundle args = new Bundle();
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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String message = getArguments().getString("message", "");

        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_confirm, null);
        ((TextView) view.findViewById(R.id.txt_message)).setText(message);

        view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) mListener.onClickPositiveButton();
            }
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener != null) mListener.onClickNegativeButton();
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
