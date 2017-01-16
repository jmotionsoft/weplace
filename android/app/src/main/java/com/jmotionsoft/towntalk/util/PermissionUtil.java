package com.jmotionsoft.towntalk.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by dooseon on 2016. 8. 16..
 */
public class PermissionUtil {
    private static final String TAG = PermissionUtil.class.getSimpleName();

    public static  boolean checkLocationPermission(Context context){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }
    }

    public static boolean checkLocationPermissionRationale(Activity context){
        return ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static  boolean checkReadStoragePermission(Context context){
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            return false;
        }else{
            return true;
        }
    }
}
