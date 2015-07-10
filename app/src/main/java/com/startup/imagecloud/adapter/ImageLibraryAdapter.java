package com.startup.imagecloud.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.startup.imagecloud.R;
import com.startup.imagecloud.db.ImageObj;
import com.telpoo.frame.object.BaseObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Tam Phan on 7/10/2015.
 */
public class ImageLibraryAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<BaseObject> data;
    private LayoutInflater inflater = null;
    AQuery aQuery;
    DisplayImageOptions options;
    ImageLoader imageLoader;

    public ImageLibraryAdapter(Activity activity, ArrayList<BaseObject> data) {
        this.data = data;
        this.activity = activity;
        aQuery = new AQuery(activity);
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .build();
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        BaseObject imageObj = data.get(position);
        view = inflater.inflate(R.layout.image_item, null);
        ImageView img = (ImageView) view.findViewById(R.id.image);
        String path = "file:/"+imageObj.get(ImageObj.PATH);
        Log.d("ImageLibraryAdapter",path);
        imageLoader.displayImage(path, img);
        return view;
    }


}
