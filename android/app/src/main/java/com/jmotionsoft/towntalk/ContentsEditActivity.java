package com.jmotionsoft.towntalk;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jmotionsoft.towntalk.http.CustomCallback;
import com.jmotionsoft.towntalk.http.HttpApi;
import com.jmotionsoft.towntalk.http.WePlaceService;
import com.jmotionsoft.towntalk.message.CustomAlert;
import com.jmotionsoft.towntalk.message.CustomAlertListener;
import com.jmotionsoft.towntalk.message.CustomToast;
import com.jmotionsoft.towntalk.model.Contents;
import com.jmotionsoft.towntalk.util.CLog;
import com.jmotionsoft.towntalk.util.ImageUtil;
import com.jmotionsoft.towntalk.util.PermissionUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ContentsEditActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private static final int REQUEST_TAKE_IMAGE = 1;
    private static final int REQUEST_PERMISSION = 2;

    private EditText edt_title;
    private EditText edt_body;
    private LinearLayout lay_image_saved_list;
    private LinearLayout lay_image_add_list;

    private WePlaceService mWePlaceService;

    private Set<String> mSavedImage;
    private List<Uri> mImageUriList;
    private int mBoardNo = -1;
    private int mContentsNo = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Contents.ID_BOARD_NAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_contents_edit);

        edt_title = (EditText)findViewById(R.id.edt_title);
        edt_body = (EditText)findViewById(R.id.edt_body);
        lay_image_saved_list = (LinearLayout)findViewById(R.id.lay_image_saved_list);
        lay_image_add_list = (LinearLayout)findViewById(R.id.lay_image_add_list);

        mBoardNo = getIntent().getIntExtra(Contents.ID_BOARD_NO, -1);
        mContentsNo = getIntent().getIntExtra(Contents.ID_CONTENTS_NO, -1);

        mWePlaceService = HttpApi.getWePlaceService(getApplicationContext());
        mImageUriList = new LinkedList<>();
        mSavedImage = new LinkedHashSet<>();

        getContents();
    }

    private void getContents(){
        Call<Contents> call = mWePlaceService.getContents(mBoardNo, mContentsNo);
        call.enqueue(new CustomCallback<Contents>(this) {
            @Override
            public void onSuccess(Call<Contents> call, Response<Contents> response) {
                if(!response.isSuccessful()){
                    CustomAlert.newInstance(getString(R.string.msg_server_error), new CustomAlertListener() {
                        @Override
                        public void onClickPositiveButton() {
                            finish();
                        }
                    }).show(getFragmentManager(), TAG);
                    return;
                }

                Contents contents = response.body();
                edt_title.setText(contents.getTitle());
                edt_body.setText(contents.getBody());

                if(contents.getImages_no() != null && !contents.getImages_no().trim().equals("")){
                    String[] imageArray = contents.getImages_no().split(",");
                    for (String anImageArray : imageArray) {
                        mSavedImage.add(anImageArray);
                    }
                    displayImageFromServer();
                }
            }
        });
    }

    private void displayImageFromServer(){
        for(final String image_no : mSavedImage){
            View imageLayout = getLayoutInflater().inflate(R.layout.item_image, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.img_upload);
            imageLayout.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSavedImage.remove(image_no);
                    lay_image_saved_list.removeView((View) v.getParent());
                }
            });

            HttpApi.loadImage(this, image_no).into(imageView);
            lay_image_saved_list.addView(imageLayout);
        }
    }

    private void editContents(){
        String title = edt_title.getText().toString();
        String body = edt_body.getText().toString();

        if(title == null || title.trim().equals("")){
            CustomToast.showMessage(this, R.string.msg_input_title);
            return;
        }

        if(body == null || body.trim().equals("")){
            CustomToast.showMessage(this, R.string.msg_input_body);
            return;
        }

        Map<String, RequestBody> parts = new LinkedHashMap<>();
        parts.put("title", RequestBody.create(MediaType.parse("text/plain"), title));
        parts.put("body", RequestBody.create(MediaType.parse("text/plain"), body));

        int index = 0;
        String editImages = "";
        for(String image_no : mSavedImage){
            CLog.i(TAG, "image_no: "+image_no);
            editImages += image_no;
            if(mSavedImage.size() > (index + 1))
                editImages += ",";

            index++;
        }
        parts.put("editImages", RequestBody.create(MediaType.parse("text/plain"), editImages));

        for(Uri aUri : mImageUriList){
            Bitmap bitmap = ImageUtil.resizeImageFromUri(this, aUri);
            File file = ImageUtil.saveBitmap(this, bitmap, aUri.getLastPathSegment());
            String media_type = getContentResolver().getType(aUri);

            CLog.i(TAG, "file type: "+media_type);

            RequestBody requestBody = RequestBody.create(MediaType.parse(media_type), file);
            parts.put("images\"; filename=\""+file.getName(), requestBody);
        }

        Call<ResponseBody> call = mWePlaceService.updateContents(mBoardNo, mContentsNo, parts);
        call.enqueue(new CustomCallback<ResponseBody>(this) {
            @Override
            public void onSuccess(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    CustomAlert.newInstance(getString(R.string.msg_server_error))
                            .show(getFragmentManager(), TAG);
                    return;
                }

                CustomToast.showMessage(ContentsEditActivity.this, R.string.msg_success_edit);
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    private void displayImage(){
        lay_image_add_list.removeAllViews();

        for(Uri aUri : mImageUriList){
            View imageLayout = getLayoutInflater().inflate(R.layout.item_image, null);
            ((ImageView)imageLayout.findViewById(R.id.img_upload))
                    .setImageBitmap(ImageUtil.resizeImageFromUri(this, aUri));
            imageLayout.findViewById(R.id.img_cancel).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeImage(v);
                        }
                    });
            lay_image_add_list.addView(imageLayout);
        }
    }

    private void removeImage(View v){
        int imageIndex = lay_image_add_list.indexOfChild((View)v.getParent());
        CLog.iformat(TAG, "image index: %s", imageIndex);

        mImageUriList.remove(imageIndex);
        lay_image_add_list.removeViewAt(imageIndex);
    }

    private void getPhoto(){
        if(mSavedImage.size() + mImageUriList.size() >= ImageUtil.UPLOAD_IMAGE_LIMIT){
            CustomToast.showMessage(this, R.string.msg_limit_select_image);
            return;
        }

        if(PermissionUtil.checkReadStoragePermission(this)){
            /*
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent, REQUEST_TAKE_IMAGE);
            */

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.txt_select_picture)),
                    REQUEST_TAKE_IMAGE);

        }else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_PERMISSION
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION){
            if(grantResults.length == 1){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getPhoto();
                    return;
                }
            }

            CustomAlert.newInstance(getString(R.string.msg_allow_permission))
                    .show(getFragmentManager(), TAG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_OK || data == null){
            return;
        }

        if(requestCode == REQUEST_TAKE_IMAGE){
            if(data.getData() != null){
                mImageUriList.add(data.getData());
                displayImage();
            }else if(data.getClipData() != null && data.getClipData().getItemCount() > 0){
                ClipData clipData = data.getClipData();
                CLog.iformat(TAG, "image Count: %s", clipData.getItemCount());

                int item_count = clipData.getItemCount();
                if(item_count + mImageUriList.size() + mSavedImage.size() > ImageUtil.UPLOAD_IMAGE_LIMIT){
                    CustomToast.showMessage(
                            ContentsEditActivity.this, R.string.msg_limit_select_image);
                    return;
                }

                for(int i = 0; item_count > i; i++){
                    mImageUriList.add(clipData.getItemAt(i).getUri());

                    if((mImageUriList.size() >= ImageUtil.UPLOAD_IMAGE_LIMIT)){
                        break;
                    }
                }

                displayImage();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contents_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

            case R.id.action_photo:
                getPhoto();
                return true;

            case R.id.action_done:
                editContents();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
