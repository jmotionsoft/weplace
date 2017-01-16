package com.jmotionsoft.towntalk.http;

import android.app.Activity;
import android.content.Context;

import com.jmotionsoft.towntalk.R;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.util.CLog;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class CustomCallback<T> implements Callback<T> {
    private final String TAG = CustomCallback.class.getSimpleName();
    private Activity mActivity;

    public CustomCallback(Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if(response.code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            showMessage(mActivity.getString(R.string.msg_http_unauthorized));
            return;
        }else if(response.code() >= 500){
            showMessage(mActivity.getString(R.string.msg_http_server_error));
            return;
        }

        onSuccess(call, response);
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        showMessage(mActivity.getString(R.string.msg_http_error));
        CLog.e(TAG, t.getMessage(), t);
    }

    public abstract void onSuccess(Call<T> call, Response<T> response);

    private void showMessage(String message){
        CustomAlert.newInstance(message).show(mActivity.getFragmentManager(),
                mActivity.getClass().getSimpleName());
    }
}
