package com.jmotionsoft.towntalk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomConfirm;
import com.jmotionsoft.towntalk.message.CustomConfirmListener;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.UserLocation;

import java.net.HttpURLConnection;
import java.util.LinkedList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class LocationListActivity extends AppCompatActivity {
    private final String TAG = LocationListActivity.class.getSimpleName();

    private final int REQUEST_ADD_LOCATION = 1;

    private ListView lst_location;
    private LocationAdapter mLocationAdapter;

    private WePlaceService mWePlaceService;

    private List<UserLocation> mLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_list);

        lst_location = (ListView)findViewById(R.id.lst_location);

        mLocationList = new LinkedList<>();

        mLocationAdapter = new LocationAdapter();
        lst_location.setAdapter(mLocationAdapter);
        lst_location.setOnItemLongClickListener(mOnItemLongClickListener);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());

        getLocationList();
    }

    private void getLocationList(){
        Call<List<UserLocation>> call = mWePlaceService.getLocationList();
        call.enqueue(new CustomCallback<List<UserLocation>>(this) {
            @Override
            public void onSuccess(Call<List<UserLocation>> call, Response<List<UserLocation>> response) {
                if(!response.isSuccessful()) {
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_http_error);
                    return;
                }

                mLocationList = response.body();
                if(mLocationList == null)
                    mLocationList = new LinkedList<>();

                mLocationAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addLocation(UserLocation userLocation){
        Call<ResponseBody> call = mWePlaceService.addLocation(userLocation);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_server_error);
                    return;
                }

                getLocationList();
            }
        });
    }

    private void removeLocation(int location_no){
        Call<ResponseBody> call = mWePlaceService.removeLocation(location_no);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_server_error);
                    return;
                }

                if(response.code() == HttpURLConnection.HTTP_NOT_MODIFIED){
                    CustomToast.showMessage(getApplicationContext(), R.string.msg_location_must_one);
                    return;
                }

                getLocationList();
            }
        });
    }

    private class LocationAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return mLocationList.size();
        }

        @Override
        public Object getItem(int position) {
            return mLocationAdapter.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserLocation userLocation = mLocationList.get(position);
            ViewHolder viewHolder = new ViewHolder();

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_location, parent, false);

                viewHolder.txt_location_name = (TextView)convertView.findViewById(R.id.txt_location_name);
                viewHolder.txt_address = (TextView)convertView.findViewById(R.id.txt_address);
                viewHolder.img_default = (ImageView)convertView.findViewById(R.id.img_default);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.txt_location_name.setText(userLocation.getLocation_name());
            viewHolder.txt_address.setText(userLocation.getAddress());
            /*
            if(userLocation.getDefault_yn().equals("Y"))
                viewHolder.img_default.setVisibility(View.VISIBLE);
            else
                viewHolder.img_default.setVisibility(View.GONE);
            */
            return convertView;
        }

        class ViewHolder{
            TextView txt_location_name;
            TextView txt_address;
            ImageView img_default;
        }
    }

    private AdapterView.OnItemLongClickListener mOnItemLongClickListener =
            new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showLocationMoreMenu(view, position);
            return false;
        }
    };

    private void showLocationMoreMenu(View v, final int position){
        PopupMenu popupMenu = new PopupMenu(this, v);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.location_list_popup, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_delete_location){
                    CustomConfirm.newInstance(getString(R.string.msg_confirm_delete_contents),
                            new CustomConfirmListener() {
                        @Override
                        public void onClickPositiveButton() {
                            removeLocation(mLocationList.get(position).getLocation_no());
                        }

                        @Override
                        public void onClickNegativeButton() {}
                    }).show(getFragmentManager(), TAG);
                    return true;
                }

                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) return;

        switch (requestCode){
            case REQUEST_ADD_LOCATION:
                UserLocation userLocation = new UserLocation();
                userLocation.setLocation_name(data.getStringExtra("location_name"));
                userLocation.setAddress(data.getStringExtra("address"));
                userLocation.setLatitude(data.getDoubleExtra("latitude", 0));
                userLocation.setLongitude(data.getDoubleExtra("longitude", 0));
                userLocation.setDefault_yn("N");

                addLocation(userLocation);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_location:
                startActivityForResult(new Intent(
                        LocationListActivity.this, LocationActivity.class),REQUEST_ADD_LOCATION);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
