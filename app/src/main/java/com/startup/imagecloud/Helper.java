package com.startup.imagecloud;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.ByteArrayOutputStream;

import dreamers.graphics.RippleDrawable;

/**
 * Created by Tam Phan on 7/8/2015.
 */
public class Helper {
    public static final int NOTIFICATION_ID=100;
    public static String encodedImageBase64(String path) {
        String encodedImage = "";
        try {
//            Bitmap bm = BitmapFactory.decodeFile(path);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
//            byte[] byteArrayImage = baos.toByteArray();
//            encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
            Bitmap bm = BitmapFactory.decodeFile(path);
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao);
            byte[] ba = bao.toByteArray();
            encodedImage = Base64.encodeBytes(ba);
        } catch (Exception e) {
            encodedImage = e.getMessage();
        }
        return encodedImage;
    }

    public static boolean isOnline(final Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
    public static void setRippe(View view,Context context) {
        RippleDrawable ab = RippleDrawable.makeFor(view, context.getResources().getColorStateList(R.color.statelistrippe), false);
    }
    static DisplayImageOptions imageOptions = null;

    public static DisplayImageOptions getDisplayImageOptions() {
        if (imageOptions == null) {
            imageOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_stub)
                    .showImageOnFail(R.drawable.ic_warning).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }
        return imageOptions;

    }
}
