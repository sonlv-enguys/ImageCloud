package com.startup.imagecloud.service;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.loopj.android.http.RequestParams;
import com.startup.imagecloud.Base64;
import com.startup.imagecloud.Helper;
import com.startup.imagecloud.MainActivity;
import com.startup.imagecloud.MyUrl;
import com.startup.imagecloud.R;
import com.startup.imagecloud.db.DbSupport;
import com.startup.imagecloud.db.ImageObj;
import com.telpoo.frame.object.BaseObject;
import com.telpoo.frame.utils.SPRSupport;

import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SyncService extends IntentService {
    public static Boolean isStartService = false;
    ArrayList<BaseObject> images;
    AQuery aQuery;
    String TAG = "SyncService";
    SPRSupport mSPrSupport;
    Boolean isUpload = false;
    int index = 0;
    int countDone = 0;

    public SyncService() {
        super("SyncService");
    }

    /*
                 * Show notification for device
                 */
    private void showNotification(String notificationContent) {
        String notificationTitle = "Image Cound";

        // large icon for notification,normally use App icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_notification);
        int smalIcon = R.drawable.ic_notification;

        long when = Calendar.getInstance().getTimeInMillis();
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
/*
         * create intent for show notification details when user clicks
		 * notification
		 */
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext()).setWhen(when)
                .setContentText(notificationContent)
                .setContentTitle(notificationTitle).setSmallIcon(smalIcon)
                .setAutoCancel(true).setTicker(notificationTitle)
                .setLargeIcon(largeIcon)
                .setVibrate(new long[]{0, 100, 200, 100, 200, 100, 200})
                .setContentIntent(pendingIntent)
                .setSound(null);

		/* Create notification with builder */
        android.app.Notification notification = notificationBuilder.build();
        notificationManager.notify(Helper.NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        aQuery = new AQuery(getApplicationContext());
        mSPrSupport = new SPRSupport();
        Handler handler = new Handler(Looper.getMainLooper());
        if(isStartService){
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(SyncService.this.getApplicationContext(), getString(R.string.uploading), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            images = intent.getParcelableArrayListExtra("images");
            if (images == null) {
                images = DbSupport.getImageToUpload();
            }
            if (images.size()>0){
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(SyncService.this.getApplicationContext(), getString(R.string.start_sync), Toast.LENGTH_SHORT).show();
                    }
                });
                isStartService = true;
                upload();
            }
            else {

                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(SyncService.this.getApplicationContext(), getString(R.string.upload_empty), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void upload() {

        if (images.size() > 0 && index < images.size()) {
            BaseObject imageObj = images.get(index);
            if (!imageObj.getBool(ImageObj.UPLOADED) && !isUpload) {
                showNotification(getString(R.string.sms_sync, index + 1, images.size()));
                try {
                    uploadImage(imageObj);
                } catch (Exception e) {
                    Log.d(TAG, "" + e);
                }
            } else {
                index++;
                countDone = countDone + 1;
                upload();
            }
        }
        if (index == images.size()) {
            showNotification(getString(R.string.sms_sync_done, countDone, images.size()));
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(SyncService.this.getApplicationContext(), getString(R.string.upload_true), Toast.LENGTH_SHORT).show();
                }
            });
            isStartService = false;
        }
    }

    public void uploadImage(final BaseObject obj) {
        isUpload = true;
        ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(obj.get(ImageObj.PATH));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
        byte[] byteImg = stream1.toByteArray();
        String encodedImage = Base64.encodeBytes(byteImg);
        Map<String, Object> params = new HashMap<>();
        params.put("employeeId", mSPrSupport.getString("employeeId", getApplicationContext()));
        Log.d(TAG, "" + mSPrSupport.getString("employeeId", getApplicationContext()));
        Log.d(TAG, "" + encodedImage);
        Log.d(TAG, "" + obj.get(ImageObj.ID));
        params.put("imageCode", encodedImage);
        params.put("key", obj.get(ImageObj.ID));
        aQuery.progress(R.id.progress).ajax(MyUrl.upload, params, XmlDom.class, new AjaxCallback<XmlDom>() {

            @Override
            public void callback(String url, XmlDom data, AjaxStatus status) {
                isUpload = false;
                if (status.getCode() == 200 && Integer.valueOf(data.text()) > 0) {
                    ImageObj imageObj = new ImageObj();
                    imageObj.set(ImageObj.ID, obj.get(ImageObj.ID));
                    imageObj.set(ImageObj.UPLOADED, true);
                    imageObj.set(ImageObj.PATH, obj.get(ImageObj.PATH));
                    DbSupport.updateImage(imageObj);
                    countDone = countDone + 1;
                }
                index++;
                upload();

                Log.d(TAG, "" + data);
                Log.d(TAG, "" + status.getMessage());
            }
        }.method(AQuery.METHOD_POST));
    }

}
