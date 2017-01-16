package com.jmotionsoft.towntalk.module.location;

import android.location.Location;

/**
 * Created by dooseon on 2016. 8. 16..
 */
public interface MyLocationCallback {
    void getSuccess(Location location, String address);

    void getFalse(String message);
}
