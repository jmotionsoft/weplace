package com.jmotionsoft.towntalk.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.jmotionsoft.towntalk.R;
import com.jmotionsoft.towntalk.util.CLog;

/**
 * Created by dooseon on 2016. 8. 9..
 */
public class CommentDialog extends DialogFragment{
    private final String TAG = getClass().getSimpleName();

    private static CommentDialogCallback mCallback;

    public static CommentDialog newInstance(String comment, CommentDialogCallback callback){
        mCallback = callback;

        CommentDialog dialog = new CommentDialog();
        Bundle bundle = new Bundle();
        bundle.putString("comment", comment);
        dialog.setArguments(bundle);

        return dialog;
    }

    public static CommentDialog newInstance(CommentDialogCallback callback){
        return newInstance(null, callback);
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
        String comment = getArguments().getString("comment", null);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_comment, null);

        final EditText edt_comment = (EditText)view.findViewById(R.id.edt_comment);
        if(comment != null) edt_comment.setText(comment);

        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClickDone(CommentDialog.this, edt_comment.getText().toString());
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }
}
