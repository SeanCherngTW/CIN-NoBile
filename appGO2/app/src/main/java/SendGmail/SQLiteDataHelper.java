package SendGmail;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "EmailData";
    SQLiteDatabase db = getWritableDatabase();
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "Spot";
    private static final String COL_id = "id";
    //private static final String COL_account = "account";
    //private static final String COL_password = "password";
    private static final String COL_receiver1 = "receiver1";
    private static final String COL_receiver2 = "receiver2";
    private static final String COL_receiver3 = "receiver3";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_receiver1 + " TEXT, " +
                    COL_receiver2 + " TEXT, " +
                    COL_receiver3 + " TEXT ); ";

    public SQLiteDataHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    } //MainActivity建構子在這裡

    // MySQLiteOpenHelper執行完都會去呼叫onCreate、onUpgrade
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Data> getAllSpots() {
        String[] columns = {
                COL_id, COL_receiver1, COL_receiver2, COL_receiver3
        };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null,
                null);
        List<Data> dataList = new ArrayList<>();
        while (cursor.moveToNext()) { //有資料就會繼續做；true
            int id = cursor.getInt(0);
            //String account = cursor.getString(1);
            //String password = cursor.getString(2);
            String receiver1 = cursor.getString(1);
            String receiver2 = cursor.getString(2);
            String receiver3 = cursor.getString(3);
            Data data = new Data(id, receiver1, receiver2, receiver3);
            dataList.add(data);
        }
        cursor.close();
        return dataList;
    }//依序對應到50行的欄位

    public Data findById(int id) {

        String[] columns = {
                COL_receiver1, COL_receiver2, COL_receiver3
        };
        String selection = COL_id + " = ?;";
        String[] selectionArgs = {String.valueOf(id)};
        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs,
                null, null, null);
        Data data = null;
        if (cursor.moveToNext()) {
            //String account = cursor.getString(1);
            //String password = cursor.getString(2);
            String receiver1 = cursor.getString(1);
            String receiver2 = cursor.getString(2);
            String receiver3 = cursor.getString(3);
            data = new Data(id, receiver1, receiver2, receiver3);
        }
        cursor.close();
        return data;
    }

    public long insert(Data data) {
        ContentValues values = new ContentValues(); //資料庫裡的一行
        //values.put(COL_account, data.getAccount());
        //values.put(COL_password, data.getPassword());
        values.put(COL_receiver1, data.getReceiver1());
        values.put(COL_receiver2, data.getReceiver2());
        values.put(COL_receiver3, data.getReceiver3());
        return db.insert(TABLE_NAME, null, values);
    }

    public int update(Data data) {
        ContentValues values = new ContentValues();
        //values.put(COL_account, data.getAccount());
        //values.put(COL_password, data.getPassword());
        values.put(COL_receiver1, data.getReceiver1());
        values.put(COL_receiver2, data.getReceiver2());
        values.put(COL_receiver3, data.getReceiver3());
        String whereClause = COL_id + " = " + data.getId();
        //String[] whereArgs = {Integer.toString(data.getId())};
        return db.update(TABLE_NAME, values, whereClause, null);
    }

    public int deleteById(int id) {
        String whereClause = COL_id + " = ?;";
        String[] whereArgs = {String.valueOf(id)};
        return db.delete(TABLE_NAME, whereClause, whereArgs);
    }

    public String select1() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToPosition(0);
        String s1 = cursor.getString(1);
        return s1;
    }

    public String select2() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToPosition(0);
        String s2 = cursor.getString(2);
        return s2;
    }

    public String select3() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToPosition(0);
        String s3 = cursor.getString(3);
        return s3;
    }

    public int counting(){
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }
}

