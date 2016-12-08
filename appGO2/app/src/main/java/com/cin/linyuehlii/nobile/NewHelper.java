package com.cin.linyuehlii.nobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class NewHelper extends SQLiteOpenHelper {//我是新資料庫 放app時間限制的
    private static final String DB_NAME = "TravelSpots";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "spoterr";
    private static final String COL_id = "id";
    private static final String COL_appname = "appname";
    private static final String COL_time = "time";
    private static final String COL_image = "image";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_image + " INTEGER, " +
                    COL_appname + " TEXT NOT NULL, " +
                    COL_time + " TEXT ); ";

    public NewHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public ArrayList<Spoterr> getAllSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_image, COL_appname, COL_time
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);

        ArrayList<Spoterr> memberList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            int image = cursor.getInt(1);
            String appname = cursor.getString(2);
            String time = cursor.getString(3);
            Spoterr spoterr=new Spoterr(id,image,appname,time);
            memberList.add(spoterr);
        }
        cursor.close();
        return memberList;
    }

    public Spoterr findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_id, COL_image, COL_appname, COL_time
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        Spoterr spoterr = null;
        if (cursor.moveToNext()) {
            int image = cursor.getInt(0);
            String appname = cursor.getString(1);
            String time = cursor.getString(2);

            spoterr = new Spoterr(id,image,appname,time);
        }
        cursor.close();
        return spoterr;
    }

    public long insert(Spoterr spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_image, spot.getImage());
        values.put(COL_appname, spot.getAppname());
        values.put(COL_time, spot.getTime());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Spoterr spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_image, spot.getImage());
        values.put(COL_appname, spot.getAppname());
        values.put(COL_time, spot.getTime());
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {Integer.toString(spot.getId())};
        return db.update(TABLE_NAME, values, whereClause, whereArgs);
    }

    public int deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }
}
