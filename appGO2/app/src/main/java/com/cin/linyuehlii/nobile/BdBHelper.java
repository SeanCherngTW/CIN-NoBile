package com.cin.linyuehlii.nobile;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static android.provider.BaseColumns._ID;
import static com.cin.linyuehlii.nobile.BdbConstants.appNAME;
import static com.cin.linyuehlii.nobile.BdbConstants.sec;

/**
 * Created by leoqaz12 on 2016/7/20.
 */
public class BdBHelper extends SQLiteOpenHelper {
    private static final int _DBVersion = 1;
    private final static String _DBName = "turntable.db";
    private final static String _TableName = "dailyMission";
    private static SQLiteDatabase database;

    public BdBHelper(Context context) {
        super(context, _DBName, null, _DBVersion);
    }

    @Override//找不到生成的資料庫檔案時觸發
    public  void onCreate(SQLiteDatabase db){
        String aa = String.valueOf(sec);
        String bb = String.valueOf(_ID);
        final String INIT_TABLE = "CREATE TABLE " + _TableName + " (" +
                bb + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                appNAME + " CHAR, " +
                 aa + " CHAR);";
        db.execSQL(INIT_TABLE);

  }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion , int newVersion){
        final String SQL = "DROP TABLE " + _TableName;
        db.execSQL(SQL);
        onCreate(db);
    }


}
