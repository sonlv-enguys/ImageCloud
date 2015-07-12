package com.startup.imagecloud.service;

import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.startup.imagecloud.Helper;
import com.startup.imagecloud.MainActivity;
import com.startup.imagecloud.R;

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
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

public class SyncService extends IntentService {
    android.app.Notification notification;
    Intent intent;

    public static Boolean isStartService = false;

    SharedPreferences preferences;

    AQuery aQuery;

    public SyncService() {
        super("HelloIntentService");
    }

    /*
                 * Show notification for device
                 */
    private void showNotification() {
        String notificationTitle = "Image Cound";
        String notificationContent = "Sync image to server 1/7";

        // large icon for notification,normally use App icon
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
        int smalIcon = R.drawable.ic_launcher;

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
//                .setVibrate(new long[]{0, 100, 200, 100, 200, 100, 200})
                .setContentIntent(pendingIntent)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

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
        showNotification();
    }

}
