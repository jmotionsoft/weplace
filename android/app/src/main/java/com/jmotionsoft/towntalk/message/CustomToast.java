package com.jmotionsoft.towntalk.message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jmotionsoft.towntalk.R;

public class CustomToast {
    public static void showMessage(Context context, String message){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_toast, null);
        ((TextView) view.findViewById(R.id.txt_message)).setText(message);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view);
        toast.show();
    }

    public static void showMessage(Context context, int resource_id){
        showMessage(context, context.getString(resource_id));
    }
}
