package com.jmotionsoft.towntalk.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jmotionsoft.towntalk.R;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.util.CLog;

/**
 * Created by dooseon on 2016. 8. 13..
 */
public class CheckPasswordDialog extends DialogFragment {
    private final String TAG = getClass().getSimpleName();

    private static CheckPasswordDialogCallback mCallback;

    public static CheckPasswordDialog newInstance(CheckPasswordDialogCallback callback){
        mCallback = callback;

        CheckPasswordDialog dialog = new CheckPasswordDialog();
        return dialog;
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
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_password, null);

        final EditText edt_password = (EditText)view.findViewById(R.id.edt_password);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edt_password.getText().toString();
                if(password == null || password.trim().equals("")){
                    CustomToast.showMessage(getActivity(), R.string.msg_input_password);
                }else{
                    mCallback.onClickDone(CheckPasswordDialog.this, password);
                    dismiss();
                }
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
