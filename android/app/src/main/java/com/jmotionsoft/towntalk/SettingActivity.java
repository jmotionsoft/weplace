package com.jmotionsoft.towntalk;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.util.AppPreferences;

import retrofit2.Call;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {
    private String TAG = getClass().getSimpleName();

    private final int REQUEST_EDIT_MEMBER = 1;
    private final int REQUEST_EDIT_LOCATION = 2;

    private AppPreferences mAppPreferences;
    private WePlaceService mWePlaceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.txt_setting);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.lay_edit_location).setOnClickListener(mOnClickListener);
        findViewById(R.id.lay_edit_my_info).setOnClickListener(mOnClickListener);
        findViewById(R.id.lay_logout).setOnClickListener(mOnClickListener);

        mAppPreferences = new AppPreferences(getApplicationContext());
        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());

        fillData();
    }

    private void fillData(){

    }

    private void logout(){
        Call call = mWePlaceService.logout();
        call.enqueue(new CustomCallback(SettingActivity.this) {
            @Override
            public void onSuccess(Call call, Response response) {
                mAppPreferences.clearMember();

                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.lay_edit_location:
                    startActivityForResult(
                            new Intent(SettingActivity.this, LocationListActivity.class),
                            REQUEST_EDIT_LOCATION);
                    break;

                case R.id.lay_edit_my_info:
                    startActivityForResult(
                            new Intent(SettingActivity.this, EditMemberActivity.class),
                            REQUEST_EDIT_MEMBER);
                    break;

                case R.id.lay_logout:
                    logout();
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) return;

        if(requestCode == REQUEST_EDIT_MEMBER){
            setResult(Activity.RESULT_OK);
        }
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
}
