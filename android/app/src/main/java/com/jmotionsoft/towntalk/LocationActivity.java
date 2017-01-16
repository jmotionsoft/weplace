package com.jmotionsoft.towntalk;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomProgress;
import com.jmotionsoft.towntalk.model.Member;
import com.jmotionsoft.towntalk.util.AppPreferences;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.PermissionUtil;
import com.jmotionsoft.towntalk.util.StringUtil;

import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    public static final String EXTRA_LOCATION_NAME = "LOCATION_NAME";
    public static final String EXTRA_LONGITUDE = "LONGITUDE";
    public static final String EXTRA_LATITUDE = "LATITUDE";

    private final int REQUEST_PERMISSION = 100;

    private EditText edt_location_name;
    private TextView txt_address;

    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private Geocoder mGeocoder;
    private Marker mMarker;

    private String response_address;
    private Double response_latitude;
    private Double response_longitude;

    private Double mLatitude;
    private Double mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        getSupportActionBar().setElevation(5f);
        getSupportActionBar().setTitle(R.string.txt_my_location);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_location_name = (EditText)findViewById(R.id.edt_location_name);
        txt_address = (TextView)findViewById(R.id.txt_address);

        mLatitude = getIntent().getDoubleExtra(EXTRA_LATITUDE, 0);
        mLongitude = getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0);

        if(getIntent().getStringExtra(EXTRA_LOCATION_NAME) != null)
            edt_location_name.setText(getIntent().getStringExtra(EXTRA_LOCATION_NAME));

        checkLocalPermission();
    }

    private void checkLocalPermission(){
        if(PermissionUtil.checkLocationPermission(this) || PermissionUtil.checkLocationPermissionRationale(this)){
            setGoogleMap();
        }else{
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSION);
        }
    }

    private void setGoogleMap(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(onMapReadyCallback);

        mGeocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks =
            new GoogleApiClient.ConnectionCallbacks() {

        @Override
        public void onConnected(Bundle bundle) {
            CustomProgress.getInstance(LocationActivity.this).dismiss();

            LatLng latLng;
            if(mLatitude != 0 && mLongitude != 0){
                latLng = new LatLng(mLatitude, mLongitude);
            }else{
                if(PermissionUtil.checkLocationPermission(getApplicationContext())){
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                    if(location != null){
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    }else{
                        return;
                    }
                }else{
                    return;
                }
            }

            mMarker = mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            displayAddress(latLng.latitude, latLng.longitude);
        }

        @Override
        public void onConnectionSuspended(int i) {
            CLog.d(TAG, "onConnectionSuspended()");
        }
    };

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener =
            new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            CustomProgress.getInstance(LocationActivity.this).dismiss();
            CustomAlert.newInstance(getString(R.string.msg_get_fail_location))
                    .show(getFragmentManager(), TAG);
        }
    };

    private OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setOnMapClickListener(onMapClickListener);

            CustomProgress.getInstance(LocationActivity.this).show(getString(R.string.msg_get_location));
            mGoogleApiClient.connect();
        }
    };

    private GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            if(mMarker != null) mMarker.remove();

            mMarker = mMap.addMarker(new MarkerOptions().position(latLng));

            displayAddress(latLng.latitude, latLng.longitude);
        }
    };

    private void displayAddress(double latitude, double longitude){
        String address = getAddress(latitude, longitude);

        if(address == null) address = "";

        txt_address.setText(address);

        response_address = address;
        response_latitude = latitude;
        response_longitude = longitude;
    }

    private String getAddress(double latitude, double longitude){
        String address = "";
        List<Address> addressList = null;

        try{
            addressList = mGeocoder.getFromLocation(latitude, longitude, 1);
            if(addressList == null){
                CLog.d(TAG, "Error Get Address!");
                return null;
            }
        }catch (Exception e){
            CLog.e(TAG, e.getMessage(), e);
        }

        if(addressList != null && addressList.size() > 0){
            address += StringUtil.checkNull(addressList.get(0).getAdminArea(), "");
            address += " "+StringUtil.checkNull(addressList.get(0).getLocality(), "");
            address += " "+StringUtil.checkNull(addressList.get(0).getThoroughfare(), "");
            //address += " "+addressList.get(0).getFeatureName();
        }

        return address;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.location_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case  R.id.action_done:
                String location_name = edt_location_name.getText().toString();
                if(location_name == null || location_name.trim().equals("")){
                    CustomAlert.newInstance(getString(R.string.msg_input_location_name))
                            .show(getFragmentManager(), TAG);
                    return true;
                }

                if(response_address == null || response_address.trim().equals("")){
                    CustomAlert.newInstance(getString(R.string.msg_input_location_select))
                            .show(getFragmentManager(), TAG);
                    return true;
                }

                Intent intent = getIntent();
                intent.putExtra("location_name", location_name);
                intent.putExtra("address", response_address);
                intent.putExtra("longitude", response_longitude);
                intent.putExtra("latitude", response_latitude);
                setResult(Activity.RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        setGoogleMap();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
