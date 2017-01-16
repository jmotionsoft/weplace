package com.jmotionsoft.towntalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomToast;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class TempPasswordActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();

    private EditText edt_email;

    private WePlaceService mWePlaceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.txt_temp_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_temp_password);

        edt_email = (EditText)findViewById(R.id.edt_email);
        findViewById(R.id.btn_done).setOnClickListener(onClickListener);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
    }

    private void getTempPassword(){
        String email = edt_email.getText().toString();

        if(email == null || email.trim().equals("")){
            CustomToast.showMessage(this, R.string.msg_input_email);
            return;
        }

        Call<ResponseBody> call = mWePlaceService.getTempPassword(email);
        call.enqueue(new CustomCallback<ResponseBody>(TempPasswordActivity.this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    CustomAlert.newInstance(getString(R.string.msg_temp_success),
                            new CustomAlertListener() {
                                @Override
                                public void onClickPositiveButton() {
                                    finish();
                                }
                            }
                    ).show(getFragmentManager(), TAG);
                }else{
                    int message_id;
                    if(response.code() == HttpURLConnection.HTTP_NOT_FOUND){
                        message_id = R.string.msg_login_not_found_member;
                    }else if(response.code() == HttpURLConnection.HTTP_CONFLICT){
                        message_id = R.string.msg_temp_already;
                    }else{
                        message_id = R.string.msg_server_error;
                    }

                    CustomAlert.newInstance(getString(message_id)).show(getFragmentManager(), TAG);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_done:
                    getTempPassword();
                    break;
            }
        }
    };
}
