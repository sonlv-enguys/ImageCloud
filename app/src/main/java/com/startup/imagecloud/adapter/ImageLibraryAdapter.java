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
import com.startup.imagecloud.Helper;
import com.startup.imagecloud.R;
import com.startup.imagecloud.db.DbSupport;
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
        options = Helper.getDisplayImageOptions();
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
    public View getView(int position, View view, ViewGroup parent) {
        BaseObject imageObj = data.get(position);
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.image_item, parent, false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.imageViewStatus = (ImageView) view.findViewById(R.id.image_status);
            holder.imageSelected = (ImageView) view.findViewById(R.id.image_selected);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        String path = "file:/"+imageObj.get(ImageObj.PATH);
        Log.d("ImageLibraryAdapter", path + "");
        imageLoader.displayImage(path, holder.imageView, options);
        holder.imageViewStatus.setVisibility(View.GONE);
        if(DbSupport.imageIsUploaded(imageObj.get(ImageObj.ID))){
            holder.imageViewStatus.setVisibility(View.VISIBLE);
        }
        if (imageObj.getBool(ImageObj.SELECTED)) {
            holder.imageSelected.setVisibility(View.VISIBLE);
        } else {
            holder.imageSelected.setVisibility(View.GONE);
        }
        return view;
    }
    static class ViewHolder {
        ImageView imageView;
        ImageView imageViewStatus;
        ImageView imageSelected;
    }


}
