package com.jmotionsoft.towntalk.module.login;

import android.content.Context;

import com.jmotionsoft.towntalk.R;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by sin31 on 2016-07-28.
 */
public class LoginHelper {
    private final String TAG = getClass().getSimpleName();

    private Context mContext;

    private WePlaceService mWePlaceService;
    private AppPreferences mAppPreferences;

    public LoginHelper(Context context){
        mContext = context;

        mAppPreferences = new AppPreferences(context);
        mWePlaceService = HttpApi.getWePlaceService(context);
    }

    public void byAuto(LoginHelperCallback loginHelperCallback){
        String email = mAppPreferences.getEmail();
        String password = mAppPreferences.getPassword();

        byEmailAndPassword(email, password, loginHelperCallback);
    }

    public void byEmailAndPassword(final String email, final String password,
                                   final LoginHelperCallback loginHelperCallback){

        CLog.i(TAG, "byEmailAndPassword()=> ");

        Call<Member> call = mWePlaceService.login(email, password);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                CLog.i(TAG, "isSuccessful: "+response.isSuccessful());

                if(!response.isSuccessful()){
                    loginHelperCallback.loginFalse(mContext.getString(R.string.msg_login_not_found_member));
                    return;
                }

                String token = response.headers().get(HttpApi.HTTP_HEADER_TOKEN);
                String token_exp = response.headers().get(HttpApi.HTTP_HEADER_TOKEN_EXP);
                Member member = response.body();

                mAppPreferences.setEmail(email);
                mAppPreferences.setPassword(password);
                mAppPreferences.setLoginToken(token);
                mAppPreferences.setMember(member);
                mAppPreferences.setLoginTokenExpiration(token_exp);

                CLog.df(TAG, "Token Exp Day: %s\n Token: %s\n name: %s",
                        mAppPreferences.getLoginTokenExpirationDay(),
                        mAppPreferences.getLoginToken(),
                        mAppPreferences.getMember().getNickname());

                loginHelperCallback.loginSuccess(member);
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                loginHelperCallback.loginFalse(mContext.getString(R.string.msg_network_error));
            }
        });
    }
}
