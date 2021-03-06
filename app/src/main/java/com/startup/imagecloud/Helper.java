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
    public static final String FILTER = "com.startup.imagecloud.ServiceUpload";
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
        return new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.ic_stub)
                    .showImageForEmptyUri(R.drawable.ic_stub)
                    .showImageOnFail(R.drawable.ic_warning).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true)
                    .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

    }
}
