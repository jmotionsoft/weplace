package com.jmotionsoft.towntalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.module.login.LoginHelper;
import com.jmotionsoft.towntalk.module.login.LoginHelperCallback;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;

import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity{
    private String TAG = this.getClass().getSimpleName();

    private EditText edt_email;
    private EditText edt_password;

    private LoginHelper mLoginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        edt_email = (EditText)findViewById(R.id.edt_email);
        edt_password = (EditText)findViewById(R.id.edt_password);

        findViewById(R.id.btn_login).setOnClickListener(onClickListener);
        findViewById(R.id.btn_join).setOnClickListener(onClickListener);
        findViewById(R.id.btn_loss_password).setOnClickListener(onClickListener);

        mLoginHelper = new LoginHelper(getApplicationContext());
    }

    private void login(){
        final String email = edt_email.getText().toString();
        final String password = edt_password.getText().toString();

        if(email == null || email.trim().equals("")){
            CustomToast.showMessage(this, R.string.msg_input_email);
            return;
        }

        if(password == null || email.trim().equals("")){
            CustomToast.showMessage(this, R.string.msg_input_password);
            return;
        }

        mLoginHelper.byEmailAndPassword(email, password, new LoginHelperCallback() {
            @Override
            public void loginSuccess(Member member) {
                startActivity(new Intent(LoginActivity.this, ContentsListActivity.class));
                finish();
            }

            @Override
            public void loginFalse(String message) {
                CustomAlert.newInstance(message)
                        .show(getFragmentManager(), TAG);
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_login:
                    login();
                    break;

                case R.id.btn_join:
                    Intent join = new Intent(LoginActivity.this, JoinActivity.class);
                    startActivity(join);
                    break;

                case R.id.btn_loss_password:
                    Intent loss_password = new Intent(LoginActivity.this, TempPasswordActivity.class);
                    startActivity(loss_password);
                    break;
            }
        }
    };
}

