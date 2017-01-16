package com.jmotionsoft.towntalk.message;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmotionsoft.towntalk.R;

public class CustomProgress {
    private static CustomProgress instance;

    private Context context;
    private ProgressDialogExtend progressDialog;


    public static CustomProgress getInstance(Context context){
        if(instance == null){
            instance = new CustomProgress(context);
        }else if(instance.context != context){
            instance.dismiss();
            instance = new CustomProgress(context);
        }

        return instance;
    }

    private CustomProgress(Context context){
        this.context = context;
        progressDialog = new ProgressDialogExtend(context);
    }

    public void show(){
        show(null);
    }

    public void show(String message){
        if(message == null  || message.equals("")){
            message = context.getString(R.string.msg_wait);
        }

        progressDialog.setCancelable(false);
        progressDialog.setBodyText(message);
        progressDialog.show();
    }

    public void dismiss(){
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    class ProgressDialogExtend extends ProgressDialog{
        private final String TAG = ProgressDialogExtend.class.getSimpleName();

        private TextView txt_message;
        private ImageView img_progress;
        private String message = "";

        public ProgressDialogExtend(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.custom_progress);

            txt_message = (TextView) findViewById(R.id.txt_message);
            img_progress = (ImageView) findViewById(R.id.img_progress);
        }

        public void setBodyText(String message){
            this.message = message;
        }

        @Override
        public void show() {
            super.show();
            txt_message.setText(message);
            img_progress.startAnimation(AnimationUtils.loadAnimation(context, R.anim.progress_rotate));
        }

        @Override
        public void dismiss() {
            super.dismiss();
        }
    }
}
