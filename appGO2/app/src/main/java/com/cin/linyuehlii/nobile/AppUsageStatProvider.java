package com.cin.linyuehlii.nobile;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by Sean on 2016/7/12.
 */
public class AppUsageStatProvider extends ContentProvider {
    private static final String AUTHORITY = "com.cin.linyuehlii.nobile.AppUsageStatProvider";
    private static final String DB_FILE = "appUsageStat.db", DB_TABLE = "appUsageStat";
    private static final int URI_ROOT = 0, DB_TABLE_APPUSAGESTAT = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + DB_TABLE);
    private static final UriMatcher sUriMatcher = new UriMatcher(URI_ROOT);

    static {
        sUriMatcher.addURI(AUTHORITY, DB_TABLE, DB_TABLE_APPUSAGESTAT);
    }

    private SQLiteDatabase mAppUsageStatDB;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        if (sUriMatcher.match(uri) != DB_TABLE_APPUSAGESTAT) {
            Log.d("insert","if (sUriMatcher.match(uri) != DB_TABLE_APPUSAGESTAT)");
            //先解析，看insert的是不是app usage
            throw new IllegalArgumentException("Unknown URI " + uri);

        }

        long rowId = mAppUsageStatDB.insert(DB_TABLE, null, values);
        Uri insertedRowUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
        getContext().getContentResolver().notifyChange(insertedRowUri, null);
        Log.d("insert","else");
        return insertedRowUri;
    }

    @Override
    public boolean onCreate() {
        // TODO Auto-generated method stub
        AppUsageOpenHelper appUsageOpenHelper = new AppUsageOpenHelper(
                getContext(), DB_FILE,
                null, 1);

        mAppUsageStatDB = appUsageOpenHelper.getWritableDatabase();

        Cursor cursor = mAppUsageStatDB.rawQuery(
                "select DISTINCT tbl_name from sqlite_master where tbl_name = '" +
                        DB_TABLE + "'", null);

        if (cursor != null) {
            if (cursor.getCount() == 0)
                mAppUsageStatDB.execSQL("CREATE TABLE " + DB_TABLE + " (" +
                        "_id INTEGER PRIMARY KEY," +
                        "appName TEXT NOT NULL," +
                        "time TEXT);");

            cursor.close();
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO Auto-generated method stub
        if (sUriMatcher.match(uri) != DB_TABLE_APPUSAGESTAT) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor c = mAppUsageStatDB.query(true, DB_TABLE, projection,
                selection, null, null, null, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }
}
