package com.jmotionsoft.towntalk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by dooseon on 2016. 10. 1..
 */

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    public static final int UPLOAD_IMAGE_LIMIT = 5;
    public static final int MAX_IMAGE_SIZE = 1920;
    public static final int JPG_IMAGE_QUALITY = 70;

    public static Bitmap resizeImageFromUri(Context context, Uri uri){
        if(uri == null) return null;

        try{
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            bitmap = resizeScaledBitmap(bitmap, MAX_IMAGE_SIZE);
            return bitmap;
        }catch (Exception e){
            CLog.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static File saveBitmap(Context context, Bitmap bitmap, String name){
        if(bitmap == null) return  null;

        try{
            File file = new File(context.getCacheDir(), name+".jpg");
            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, JPG_IMAGE_QUALITY, fos);
            fos.flush();
            fos.close();

            return file;
        }catch (Exception e){
            CLog.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    public static Bitmap resizeScaledBitmap(Bitmap bitmap, int maxSize){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height){
            if(width > maxSize){
                rate = maxSize / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxSize;
            }
        }else{
            if(height > maxSize){
                rate = maxSize / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxSize;
            }
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
