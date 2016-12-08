package com.cin.linyuehlii.nobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sean on 2016/7/27.
 */
public class CustomUsageStatsDBHelper extends SQLiteOpenHelper {

    public CustomUsageStatsDBHelper(Context context, String name,
                                    SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
