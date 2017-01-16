package com.jmotionsoft.towntalk.module.location;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.PermissionUtil;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by dooseon on 2016. 8. 16..
 */
public class MyLocation {
    private final String TAG = getClass().getSimpleName();

    private final long LOCATION_LIMIT_TIME = 1000 * 60 * 10;
    private final long ADDRESS_LIMIT_TIME = 1000 * 60 * 10;

    private Context mContext;
    private MyLocationCallback mQuickLocationCallback;
    private GoogleApiClient mGoogleApiClient;
    private Handler mHandler;
    private Timer mTimer;
    private Location mLocation;
    private boolean mIsAddress = false;

    public MyLocation(Context context){
        mContext = context;
    }

    public void getLastLocation(MyLocationCallback quickLocationCallback, boolean isAddress){
        mQuickLocationCallback = quickLocationCallback;
        mIsAddress = isAddress;

        if(mGoogleApiClient == null){
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(mConnectionCallbacks)
                    .addOnConnectionFailedListener(mOnConnectionFailedListener)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();
    }

    private GoogleApiClient.ConnectionCallbacks mConnectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if(!PermissionUtil.checkLocationPermission(mContext)){
                        sendFalseAndDisconnect("Do not have permission.");
                        return;
                    }

                    mLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);

                    if(mLocation == null){
                        sendFalseAndDisconnect("Can not get Location.");
                        return;
                    }
                    CLog.df(TAG, "latitude: %s, longitude: %s, provider: %s, time: %s",
                            mLocation.getLatitude(), mLocation.getLongitude(),
                            mLocation.getProvider(), mLocation.getTime());

                    long after_time = System.currentTimeMillis() - mLocation.getTime();
                    if(after_time > LOCATION_LIMIT_TIME){
                        sendFalseAndDisconnect("Time Over: "+after_time);
                        return;
                    }
                    CLog.df(TAG, "location after time: %s", after_time);

                    if(mIsAddress){
                        getAddress();
                    }else{
                        mQuickLocationCallback.getSuccess(mLocation, null);
                    }
                    mGoogleApiClient.disconnect();
                }

                @Override
                public void onConnectionSuspended(int i) {
                    CLog.d(TAG, "onConnectionSuspended("+i+")");
                }
            };

    private void sendFalseAndDisconnect(String message){
        mQuickLocationCallback.getFalse(message);
        mGoogleApiClient.disconnect();
    }

    private void getAddress(){
        mHandler = new Handler();
        mTimer = new Timer();

        final Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                try{
                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(
                            mLocation.getLatitude(), mLocation.getLongitude(), 1);

                    if(addresses == null || addresses.size() == 0){
                        mQuickLocationCallback.getSuccess(mLocation, null);
                    }

                    Address address = addresses.get(0);
                    String location_address = address.getAddressLine(0).toString();

                    CLog.df(TAG, "Address: "+location_address);

                    mQuickLocationCallback.getSuccess(mLocation, location_address);
                }catch (Exception e){
                    CLog.e(TAG, e);
                    mQuickLocationCallback.getSuccess(mLocation, null);
                }

                if(mTimer != null){
                    mTimer.cancel();
                }
            }
        };

        mHandler.post(mRunnable);

        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.removeCallbacks(mRunnable);
                mQuickLocationCallback.getSuccess(mLocation, null);
            }
        }, ADDRESS_LIMIT_TIME);
    }

    private GoogleApiClient.OnConnectionFailedListener mOnConnectionFailedListener =
        new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                mQuickLocationCallback.getFalse("Can not connect Google API Client.");
            }
        };
}
