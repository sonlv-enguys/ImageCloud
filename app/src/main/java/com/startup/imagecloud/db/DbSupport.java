package com.startup.imagecloud.db;
import android.content.Context;
import android.util.Log;
import com.telpoo.frame.database.DbCacheUrl;
import com.telpoo.frame.object.BaseObject;
import java.util.ArrayList;

/**
 * Created by Tam Phan on 6/28/2015.
 */
public class DbSupport {
    public static final void init(Context context) {
        DbCacheUrl.initDb(context);
        MyDb.init(TableDbObj.tables, TableDbObj.keys, context, "image_cloud_db", 1);
    }

    public static void updateImage(ImageObj imageObj) {
        Boolean updated = false;

        if (imageIsExist(imageObj.get(ImageObj.ID))) {
            updated = MyDb.update(imageObj, TableDbObj.IMAGE, ImageObj.ID);
            Log.d("updateImage", "updated");
        } else {
            ArrayList<BaseObject> data = new ArrayList<>();
            data.add(imageObj);
            updated = MyDb.addToTable(data, TableDbObj.IMAGE);
            Log.d("updateImage", "added");
        }
        Log.d("updateImage", "" + updated);
    }

    public static Boolean deleteImageById(String mediaId) {
        return MyDb.deleteRowInTable(TableDbObj.IMAGE, ImageObj.ID, mediaId);
    }

    public static Boolean imageIsExist(String imageId) {
        Boolean mediaVoted = false;
        String querry = "select * from " + TableDbObj.IMAGE + " where " + ImageObj.ID + " = '" + imageId + "'";
        ArrayList<BaseObject> data = MyDb.rawQuery(querry);
        if (data.size() > 0) {
            mediaVoted = true;
        }
        Log.d("imageIsExist", "" + mediaVoted);
        return mediaVoted;
    }public static Boolean imageIsUploaded(String imageId) {
        Boolean imageIsUploaded = false;
        String querry = "select * from " + TableDbObj.IMAGE + " where " + ImageObj.ID + " = '" + imageId + "'";
        ArrayList<BaseObject> data = MyDb.rawQuery(querry);
        if (data.size() > 0) {
            imageIsUploaded= data.get(0).getBool(ImageObj.UPLOADED);
        }
        Log.d("imageIsExist", "" + imageIsUploaded);
        return imageIsUploaded;
    }
    public static final  ArrayList<BaseObject> getImageToUpload(String userId){
        String querry="select * from "+TableDbObj.IMAGE+" where "+ ImageObj.UPLOADED+" = 'false' and "+ImageObj.USER+" = '"+userId+ "'";

        ArrayList<BaseObject> data = MyDb.rawQuery(querry);
        return data;
    }
    public static final  ArrayList<BaseObject> getImageById(String userId){
        String querry="select * from "+TableDbObj.IMAGE+" where "+ImageObj.USER+" = '"+userId+ "'";

        ArrayList<BaseObject> data = MyDb.rawQuery(querry);
        return data;
    }

}
