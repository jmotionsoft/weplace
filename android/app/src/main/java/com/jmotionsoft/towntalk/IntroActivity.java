package com.jmotionsoft.towntalk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomConfirm;
import com.jmotionsoft.towntalk.message.CustomConfirmListener;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.model.Version;
import com.jmotionsoft.towntalk.module.login.LoginHelper;
import com.jmotionsoft.towntalk.module.login.LoginHelperCallback;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;

import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Response;

public class IntroActivity extends AppCompatActivity {
    private final String TAG = IntroActivity.class.getSimpleName();

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private final int INTRO_TIME = 2000;

    private WePlaceService mWePlaceService;
    private LoginHelper mLoginHelper;
    private AppPreferences mAppPreferences;

    private Timer mTimer;
    private boolean isNextActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_intro);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
        mLoginHelper = new LoginHelper(getApplicationContext());
        mAppPreferences = new AppPreferences(getApplicationContext());

        checkPlayServices();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(isNextActivity)
                    goLogin();
                else
                    isNextActivity = true;
            }
        }, INTRO_TIME);
    }

    private void getVersion(){
        Call<Version> call = mWePlaceService.getVersion();
        call.enqueue(new CustomCallback<Version>(IntroActivity.this) {
            @Override
            public void onSuccess(Call<Version> call, Response<Version> response) {
                if(response.isSuccessful()){
                    Version version = response.body();
                    if (version.getVersion_code() > BuildConfig.VERSION_CODE &&
                            version.getRequired_yn().equals("Y")) {
                        updateTowntalk();
                        return;
                    }
                }

                if (isNextActivity)
                    goLogin();
                else
                    isNextActivity = true;
            }
        });
    }

    private void updateTowntalk(){
        CustomConfirm.newInstance(getString(R.string.msg_confirm_update),
                new CustomConfirmListener() {
            @Override
            public void onClickPositiveButton() {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }

                finish();
            }

            @Override
            public void onClickNegativeButton() {
                finish();
            }
        }).show(getFragmentManager(), TAG);
    }

    private void goLogin(){
        if(mAppPreferences.getEmail() == null || mAppPreferences.getPassword() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if(mAppPreferences.getLoginToken() != null
                && mAppPreferences.getLoginTokenExpirationDay() > 10 ){
            goMain();
            return;
        }

        mLoginHelper.byAuto(new LoginHelperCallback() {
            @Override
            public void loginSuccess(Member member) {
                startActivity(new Intent(IntroActivity.this, ContentsListActivity.class));
                finish();
            }

            @Override
            public void loginFalse(String message) {
                CustomAlert.newInstance(message, new CustomAlertListener() {
                    @Override
                    public void onClickPositiveButton() {
                        startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                        finish();
                    }
                }).show(getFragmentManager(), TAG);
            }
        });
    }

    private void goMain(){
        Call<Member> call = mWePlaceService.getMember();
        call.enqueue(new CustomCallback<Member>(this) {
            @Override
            public void onSuccess(Call<Member> call, Response<Member> response) {
                if(response.isSuccessful()){
                    mAppPreferences.setMember(response.body());
                    startActivity(new Intent(IntroActivity.this, ContentsListActivity.class));
                    finish();
                    return;
                }else{
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
            }
        });
    }

    private void checkPlayServices(){
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }else{
                CLog.d(TAG, "This device is not supported.");
                finish();
            }
        }else{
            getVersion();
        }
    }



    @Override
    protected void onDestroy() {
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }

        super.onDestroy();
    }
}
