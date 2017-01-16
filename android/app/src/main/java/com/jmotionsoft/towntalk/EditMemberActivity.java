package com.jmotionsoft.towntalk;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jmotionsoft.towntalk.dialog.CheckPasswordDialog;
import com.jmotionsoft.towntalk.dialog.CheckPasswordDialogCallback;
import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditMemberActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private TextView txt_email;
    private EditText edt_nickname;
    private EditText edt_password;
    private EditText edt_password_confirm;

    private WePlaceService mWePlaceService;
    private AppPreferences mAppPreferences;

    private Member mMember;
    private Member mChangeMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.txt_edit_my_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_member);

        txt_email = (TextView)findViewById(R.id.txt_email);
        edt_nickname = (EditText)findViewById(R.id.edt_nickname);
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_password_confirm = (EditText)findViewById(R.id.edt_password_confirm);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
        mAppPreferences = new AppPreferences(getApplicationContext());

        fillMember();
    }

    private void fillMember(){
        mMember = mAppPreferences.getMember();

        txt_email.setText(mMember.getEmail());
        edt_nickname.setText(mMember.getNickname());
    }

    private void updateMember(){
        String p_nickname = null;
        String p_password = null;
        String p_password_confirm = null;

        p_nickname = edt_nickname.getText().toString().trim();
        p_password = edt_password.getText().toString().trim();
        p_password_confirm = edt_password_confirm.getText().toString().trim();

        if(!p_password.isEmpty()){
            if(p_password_confirm.isEmpty()){
                CustomToast.showMessage(this, getString(R.string.msg_input_password_confirm));
                return;
            }

            if(!p_password.equals(p_password_confirm)){
                CustomToast.showMessage(this, getString(R.string.msg_no_same_password));
                return;
            }
        }

        mChangeMember = new Member();
        if(p_nickname != null && !p_nickname.trim().equals("")){
            if(!mMember.getNickname().equals(p_nickname)){
                mChangeMember.setNickname(p_nickname);
            }
        }
        if(p_password != null && !p_password.trim().equals("")){
            mChangeMember.setPassword(p_password);
        }

        Call<ResponseBody> call = mWePlaceService.updateMember(mChangeMember);
        call.enqueue(new CustomCallback<ResponseBody>(EditMemberActivity.this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    CustomAlert.newInstance(getString(R.string.msg_success_edit), new CustomAlertListener() {
                        @Override
                        public void onClickPositiveButton() {
                            successChange();
                        }
                    }).show(getFragmentManager(), TAG);
                }else{
                    CustomAlert.newInstance(getString(R.string.msg_server_error))
                            .show(getFragmentManager(), TAG);
                }
            }
        });
    }

    private void successChange(){
        if(mChangeMember.getPassword() != null){
            mAppPreferences.setPassword(mChangeMember.getPassword());
        }

        Call<Member> call = mWePlaceService.getMember();
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                if(response.isSuccessful()){
                    mAppPreferences.setMember(response.body());
                    setResult(Activity.RESULT_OK);
                    finish();
                }else{
                    restartApp();
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                CLog.e(TAG, (Exception)t);
                restartApp();
            }
        });
    }

    private void restartApp(){
        Intent intent = new Intent(EditMemberActivity.this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getPassword(){
        CheckPasswordDialog.newInstance(new CheckPasswordDialogCallback() {
            @Override
            public void onClickDone(DialogFragment dialog, String password) {
                withdraw(password);
            }
        }).show(getFragmentManager(), TAG);
    }

    private void withdraw(String password){
        Call<ResponseBody> call = mWePlaceService.deleteMember(password);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    if(response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                        CustomAlert.newInstance(getString(R.string.msg_no_same_password))
                                .show(getFragmentManager(), TAG);
                    }else{
                        CustomAlert.newInstance(getString(R.string.msg_server_error))
                                .show(getFragmentManager(), TAG);
                    }
                    return;
                }

                mAppPreferences.clearMember();

                Intent intent = new Intent(EditMemberActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                updateMember();
                return true;

            case R.id.action_withdraw_member:
                getPassword();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
