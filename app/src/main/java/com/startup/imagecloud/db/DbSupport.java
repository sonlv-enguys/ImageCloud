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
    }
    public static final  ArrayList<BaseObject> getImageToUpload(){
        String querry="select * from "+TableDbObj.IMAGE+" where "+ ImageObj.UPLOADED+" = 'false'";

        ArrayList<BaseObject> data = MyDb.rawQuery(querry);
        return data;
    }

}
