package com.startup.imagecloud.db;

import com.telpoo.frame.object.BaseObject;

public class ImageObj extends BaseObject {

    public static final String[] keys = {
            "id", "path", "uploaded","selected"
    };
    public static final String[] keysDb = {
            "primarykey_id", "path", "uploaded"
    };

    public static final String ID = keys[0];
    public static final String PATH = keys[1];
    public static final String UPLOADED = keys[2];
    public static final String SELECTED = keys[3];

}