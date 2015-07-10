package com.startup.imagecloud;
import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.startup.imagecloud.db.DbSupport;

/**
 * Created by yenminh on 5/18/2015.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DbSupport.init(getApplicationContext());
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getBaseContext())
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }
}
