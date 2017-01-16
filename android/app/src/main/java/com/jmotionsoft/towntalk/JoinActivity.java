package com.jmotionsoft.towntalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.Validate;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class JoinActivity extends AppCompatActivity {
    private final String TAG = JoinActivity.class.getSimpleName();

    private final int REQUEST_LOCATION = 1;

    private EditText edt_email;
    private EditText edt_nickname;
    private EditText edt_password;
    private EditText edt_password_confirm;
    private Button btn_select_location;
    private TextView txt_location_name;
    private TextView txt_address;

    private String p_location_name;
    private Double p_latitude;
    private Double p_longitude;
    private String p_address;

    private WePlaceService mWePlaceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.txt_join);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_join);

        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_nickname = (EditText)findViewById(R.id.edt_nickname);
        edt_password = (EditText)findViewById(R.id.edt_password);
        edt_password_confirm = (EditText)findViewById(R.id.edt_password_confirm);
        txt_location_name = (TextView) findViewById(R.id.txt_location_name);
        txt_address = (TextView) findViewById(R.id.txt_address);

        btn_select_location = (Button)findViewById(R.id.btn_select_location);
        btn_select_location.setOnClickListener(onClickListener);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
    }

    public void join(){
        String p_email = "";
        String p_nickname = "";
        String p_password = "";
        String p_password_confirm = "";

        p_email = edt_email.getText().toString().trim();
        p_nickname = edt_nickname.getText().toString().trim();
        p_password = edt_password.getText().toString().trim();
        p_password_confirm = edt_password_confirm.getText().toString().trim();

        if(p_email.isEmpty()){
            CustomToast.showMessage(this, getString(R.string.msg_input_email));
            return;
        }

        if(!Validate.isEmail(p_email,false )){
            CustomToast.showMessage(this, getString(R.string.msg_check_email));
            return;
        }

        if(p_nickname.isEmpty()){
            CustomToast.showMessage(this, getString(R.string.msg_input_nickname));
            return;
        }

        if(p_password.isEmpty()){
            CustomToast.showMessage(this, getString(R.string.msg_input_password));
            return;
        }

        if(!Validate.isPassword(p_password, false)){
            CustomToast.showMessage(this, getString(R.string.msg_check_password));
            return;
        }

        if(p_password_confirm.isEmpty()){
            CustomToast.showMessage(this, getString(R.string.msg_input_password_confirm));
            return;
        }

        if(!p_password.equals(p_password_confirm)){
            CustomToast.showMessage(this, getString(R.string.msg_no_same_password));
            return;
        }

        if(p_address == null || p_longitude == null || p_latitude == null){
            CustomToast.showMessage(this, getString(R.string.msg_input_location));
            return;
        }

        Member member = new Member();
        member.setEmail(p_email);
        member.setNickname(p_nickname);
        member.setPassword(p_password);
        member.setLocation_name(p_location_name);
        member.setLongitude(p_longitude);
        member.setLatitude(p_latitude);
        member.setAddress(p_address);

        Call<ResponseBody> call = mWePlaceService.join(member);
        call.enqueue(new CustomCallback<ResponseBody>(JoinActivity.this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    CustomToast.showMessage(getApplicationContext(), getString(R.string.msg_join_ok));
                    finish();
                }else{
                    if(response.code() == HttpURLConnection.HTTP_CONFLICT){
                        CLog.i(TAG, response.message()+"/");

                        String message = "";
                        String show_message = getString(R.string.msg_http_server_error);

                        try{
                            message = response.errorBody().string().trim();
                        }catch (Exception e){
                            CLog.e(TAG, e);
                        }

                        if(message.equals("email"))
                            show_message = getString(R.string.msg_same_email);
                        if(message.equals("nickname"))
                            show_message = getString(R.string.msg_same_nickname);

                        CustomToast.showMessage(JoinActivity.this, show_message);
                    }else{
                        CustomToast.showMessage(JoinActivity.this, getString(R.string.msg_server_error));
                    }
                }
            }
        });
    }

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_select_location:
                    startActivityForResult(
                            new Intent(JoinActivity.this, LocationActivity.class), REQUEST_LOCATION);
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK) return;

        if(requestCode == REQUEST_LOCATION){
            p_location_name = data.getStringExtra("location_name");
            p_address = data.getStringExtra("address");
            p_latitude = data.getDoubleExtra("latitude", 0);
            p_longitude = data.getDoubleExtra("longitude", 0);

            CLog.df(TAG, "latitude: %s, longitude: %s, address: %s", p_latitude, p_longitude, p_address);

            txt_location_name.setText(p_location_name);
            txt_address.setText(p_address);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.join_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_done:
                join();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
