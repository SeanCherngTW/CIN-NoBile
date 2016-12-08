package com.cin.linyuehlii.nobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "AppGo2";
    private static final int DB_VERSION = 2;
    private static final String TABLE_NAME = "UseTimeTable";
    private static final String COL_id = "id";
    private static final String COL_day = "day";
    private static final String COL_fb = "fb";
    private static final String COL_line = "line";
    private static final String COL_message = "message";
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;//calender月份從0-11
    int day = c.get(Calendar.DAY_OF_MONTH);
    private String toDayDate = month + "/" + day;
    private List<Spot> todayList;
    private static ContentResolver mAppUsageStatRes;


    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_day + " TEXT NOT NULL, " +
                    COL_fb + " TEXT, " +
                    COL_line + " TEXT, " +
                    COL_message + " TEXT); ";

    public MySQLiteOpenHelper(Context context) {
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

    public void insertIfEmpty() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_day, COL_fb, COL_line, COL_message
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        Spot spot1 = new Spot("6/18", 100, 246, 700);
        Spot spot2 = new Spot("6/19", 100, 357, 793);
        Spot spot3 = new Spot("6/20", 100, 300, 200);
        Spot spot4 = new Spot("6/21", 100, 357, 700);
        Spot spot5 = new Spot("6/22", 100, 570, 793);
        Spot spot6 = new Spot("6/23", 100, 570, 793);
        Spot spot7 = new Spot("6/24", 100, 570, 793);
        Spot spot8 = new Spot("6/25", 100, 570, 793);
        Spot spot9 = new Spot("6/26", 100, 570, 793);
        Spot spot10 = new Spot("6/27", 100, 570, 793);
        Spot spot11 = new Spot("6/28", 100, 570, 793);
        Spot spot12 = new Spot("6/29", 100, 570, 793);
        Spot spot13 = new Spot("6/30", 100, 570, 793);
        Spot spot14 = new Spot("7/1", 100, 570, 793);
        Spot spot15 = new Spot("7/2", 100, 570, 793);
        Spot spot16 = new Spot("7/3", 100, 570, 793);
        Spot spot17 = new Spot("7/4", 100, 570, 793);
        Spot spot18 = new Spot("7/5", 100, 570, 793);
        Spot spot19 = new Spot("7/6", 100, 570, 793);
        Spot spot20 = new Spot("7/7", 100, 570, 793);
        Spot spot21 = new Spot("7/8", 100, 570, 793);
        Spot spot22 = new Spot("7/9", 100, 570, 793);
        Spot spot23 = new Spot("7/10", 100, 570, 793);
        Spot spot24 = new Spot("7/11", 100, 570, 793);
        Spot spot25 = new Spot("7/12", 100, 570, 793);
        Spot spot26 = new Spot("7/13", 100, 570, 793);
        Spot spot27 = new Spot("7/14", 100, 570, 793);
        Spot spot28 = new Spot("7/15", 100, 100, 100);
        insert(spot1);
        insert(spot2);
        insert(spot3);
        insert(spot4);
        insert(spot5);
        insert(spot6);
        insert(spot7);
        insert(spot8);
        insert(spot9);
        insert(spot10);
        insert(spot11);
        insert(spot12);
        insert(spot13);
        insert(spot14);
        insert(spot15);
        insert(spot16);
        insert(spot17);
        insert(spot18);
        insert(spot19);
        insert(spot20);
        insert(spot21);
        insert(spot22);
        insert(spot23);
        insert(spot24);
        insert(spot25);
        insert(spot26);
        insert(spot27);
        insert(spot28);
        cursor.close();
    }

    public List<Spot> getAllSpots() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {
                COL_id, COL_day, COL_fb, COL_line, COL_message
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,null);
        List<Spot> spotList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String day = cursor.getString(1);
            int fb = cursor.getInt(2);
            int line = cursor.getInt(3);
            int message = cursor.getInt(4);
            Spot spot = new Spot(id, day, fb, line, message);
            spotList.add(spot);
        }
        cursor.close();
        return spotList;
    }

    public Spot findById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        String[] columns = {
                COL_day, COL_fb, COL_line, COL_message
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                                 null, null, null);
        Spot spot = null;
        if (cursor.moveToNext()) {
            String day = cursor.getString(0);
            int fb = cursor.getInt(1);
            int line = cursor.getInt(2);
            int message = cursor.getInt(3);
            spot = new Spot(id, day, fb, line, message);
        }
        cursor.close();

        return spot;

    }

    public ArrayList<Integer> getAppTime(int item, boolean period) {
        todayList = getAllSpots();
        int newest = todayList.size() - 1;
        int Time = 0, Time1 = 0, Time2 = 0, Time3 = 0;
        ArrayList<Integer> data = new ArrayList<Integer>();
        if (period == false) {
            switch (item) {
                case 0:
                    data.add(todayList.get(newest - 3).getFb());
                    data.add(todayList.get(newest - 2).getFb());
                    data.add(todayList.get(newest - 1).getFb());
                    data.add(todayList.get(newest).getFb());
                    break;
                case 1:
                    data.add(todayList.get(newest - 3).getLine());
                    data.add(todayList.get(newest - 2).getLine());
                    data.add(todayList.get(newest - 1).getLine());
                    data.add(todayList.get(newest).getLine());
                    break;
                case 2:
                    data.add(todayList.get(newest - 3).getMessage());
                    data.add(todayList.get(newest - 2).getMessage());
                    data.add(todayList.get(newest - 1).getMessage());
                    data.add(todayList.get(newest).getMessage());
                    break;
            }
        } else if (period == true) {
            switch (item) {
                case 0:
                    for (int i = 21; i < 28; i++) {
                        Time3 += todayList.get(newest - i).getFb();
                    }
                    for (int i = 14; i < 21; i++) {
                        Time2 += todayList.get(newest - i).getFb();
                    }
                    for (int i = 7; i < 14; i++) {
                        Time1 += todayList.get(newest - i).getFb();
                    }
                    for (int i = 0; i < 7; i++) {
                        Time += todayList.get(newest - i).getFb();
                    }
                    break;
                case 1:
                    for (int i = 21; i < 28; i++) {
                        Time3 += todayList.get(newest - i).getLine();
                    }
                    for (int i = 14; i < 21; i++) {
                        Time2 += todayList.get(newest - i).getLine();
                    }
                    for (int i = 7; i < 14; i++) {
                        Time1 += todayList.get(newest - i).getLine();
                    }
                    for (int i = 0; i < 7; i++) {
                        Time += todayList.get(newest - i).getLine();
                    }
                    break;
                case 2:
                    for (int i = 21; i < 28; i++) {
                        Time3 += todayList.get(newest - i).getMessage();
                    }
                    for (int i = 14; i < 21; i++) {
                        Time2 += todayList.get(newest - i).getMessage();
                    }
                    for (int i = 7; i < 14; i++) {
                        Time1 += todayList.get(newest - i).getMessage();
                    }
                    for (int i = 0; i < 7; i++) {
                        Time += todayList.get(newest - i).getMessage();
                    }
                    break;
            }
            data.add(Time3);
            data.add(Time2);
            data.add(Time1);
            data.add(Time);
        }
        return data;
    }

    public ArrayList<Integer> getTotalTime(boolean period) {
        ArrayList<Integer> data = new ArrayList<Integer>();
        int Time = 0, Time1 = 0, Time2 = 0, Time3 = 0;
        boolean flag = false;
        if (period == false) {
            flag = false;
        } else if (period == true) {
            flag = true;
        }
        Time = getAppTime(0, flag).get(3) + getAppTime(1, flag).get(3) + getAppTime(2, flag).get(3);
        Time1 = getAppTime(0, flag).get(2) + getAppTime(1, flag).get(2) + getAppTime(2, flag).get(2);
        Time2 = getAppTime(0, flag).get(1) + getAppTime(1, flag).get(1) + getAppTime(2, flag).get(1);
        Time3 = getAppTime(0, flag).get(0) + getAppTime(1, flag).get(0) + getAppTime(2, flag).get(0);
        data.add(Time3);
        data.add(Time2);
        data.add(Time1);
        data.add(Time);
        return data;
    }


    public long insert(Spot spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_day, spot.getDay());
        values.put(COL_fb, spot.getFb());
        values.put(COL_line, spot.getLine());
        values.put(COL_message, spot.getMessage());
        return db.insert(TABLE_NAME, null, values);
    }

    public int getAvgTotalTime() {
        ArrayList<Integer> data = new ArrayList<Integer>();
        boolean flag = true;
        int Time;
        Time = (getTotalTime(flag).get(3));
        return Time / 7;

    }

    public int update(Spot spot) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_day, spot.getDay());
        values.put(COL_fb, spot.getFb());
        values.put(COL_line, spot.getLine());
        values.put(COL_message, spot.getMessage());
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
