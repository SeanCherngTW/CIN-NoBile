package com.cin.linyuehlii.nobile;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cin.linyuehlii.nobile.view.LuckyPanView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class BMissionActivity extends AppCompatActivity {
    private LuckyPanView mLuckyPanView;

    private ImageView mStartBtn;
    private int ran;
    private BdBHelper dbhelper;
    private EditText editName = null;
    private EditText editSec = null;
    private BMission mission;
    private boolean isMissionAvailable;
    private long today;
    String strTodayPre;
    String strTodayNow;
    private boolean isNewDay = false;
    FileOutputStream fileOut = null;
    ObjectOutputStream objectOut = null;
    String PREFS_NAME = "ActivityPREF";
    public static BMissionActivity INSTANCE;// Activity引用 在下個頁面關閉本Activity


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_mission_ac);
        INSTANCE = this;

        Intent serviceIntent = new Intent(BMissionActivity.this, MyService.class);
        startService(serviceIntent);




        /********************檢查有沒有到下一天******************/
        long stDay = System.currentTimeMillis() / 86400000;
        SharedPreferences DatePref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = DatePref.edit();
        long yesterday = DatePref.getLong("KEY_LONG", 0);

        Log.d("mission", "(BMissionActivity) Today: " + stDay);
        Log.d("mission", "(BMissionActivity) Yesterday: " + yesterday);

        if (stDay > yesterday) {
            //如果到了下一天，就正常進行此activity
            editor.putLong("KEY_LONG", stDay);
            editor.commit();
            Log.d("mission", "Next Day");

        }else{//否則跳到showActivity
            Intent intentNowMission = new Intent();
            intentNowMission.setClass(BMissionActivity.this, BMissionShowActivity.class);
            startActivity(intentNowMission);
        }
        /********************檢查有沒有到下一天******************/



        /**********************從檔案讀取前一次存檔之日期*******************/
        try {
            FileInputStream fileIn = openFileInput("Today");
            byte[] bufBytes = new byte[1];

            do {
                Log.d("date", "do while");
                int c = fileIn.read(bufBytes);
                if (c == -1) break;
                else strTodayPre += new String(bufBytes);
            } while (true);

            fileIn.close();
            Log.d("file", "fileIn");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            Log.d("file", "fileIn FNFE");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("file", "fileIn IOE");
        }

        Log.d("date", "Last Time = " + strTodayPre);
        Log.d("date", "This Time = " + strTodayNow);
        /**********************從檔案讀取前一次存檔之日期*******************************/


        /***********************************寫入今日日期至檔案*******************************************/
        try {
            FileOutputStream fileOut = openFileOutput("Today", MODE_PRIVATE);
            // 寫入檔案 (FileName, Type)

            if (Long.parseLong(strTodayNow) > Long.parseLong(strTodayPre)) {
                isNewDay = true;
                Log.d("file", "isNewDay = true;");
            } else {
                isNewDay = false;
                Log.d("file", "isNewDay = false;");
            }

            fileOut.write(strTodayNow.getBytes());
            fileOut.close();
            Log.d("file", "fileOut");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            Log.d("file", "fileOutFNFE");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("file", "fileOutIOE");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("file", "NewBie");
        }

        Log.d("date", "This Time = " + strTodayNow);
        /***********************************寫入今日日期至檔案*******************************************/
/*
        if (!isNewDay) { //時間未到，不能轉
            //new一個intent物件，並指定Activity切換的class
            Intent intentNowMission = new Intent();
            intentNowMission.setClass(BMissionActivity.this, BMissionShowActivity.class);
            /*Bundle bundle = new Bundle();
            bundle.putString("NowMissionName", mission.MissionAppName);
            bundle.putString("NowMissionTime", mission.getMissionAppTimeFormatted());
            intentNowMission.putExtras(bundle);*/
           /*startActivity(intentNowMission);
        } else { //一天過去了，可以轉
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
    */
    }

    @Override
    protected void onStart() {
        super.onStart();
        openDatabase();
        mLuckyPanView = (LuckyPanView) findViewById(R.id.id_luckypan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);


        mStartBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BMissionActivity.this);
                if (!mLuckyPanView.isStart()) {
                    ran = (int) (Math.random() * mLuckyPanView.mItemCount);
                    mStartBtn.setImageResource(R.drawable.mission_stop);
                    mLuckyPanView.luckyStart(ran);  //傳入至luckyindex
                    Toast.makeText(BMissionActivity.this, "請點擊停止轉盤\n決定今天的挑戰吧!", Toast.LENGTH_LONG).show();
                } else if (!mLuckyPanView.isShouldEnd()) {
                    mStartBtn.setImageResource(R.drawable.mission_start);
                    mLuckyPanView.luckyEnd();


                    dialog.setTitle("每日挑戰");

                    switch (ran) {
                        case 0:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[0]);
                            mission = new BMission("Facebook", 3600000);
                            break;
                        case 1:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[1]);
                            mission = new BMission("Messenger", 3600000);
                            break;
                        case 2:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[2]);
                            mission = new BMission("Line", 3600000);
                            break;
                        case 3:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[3]);
                            mission = new BMission("Instagram", 3600000);
                            break;
                        case 4:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[4]);
                            mission = new BMission("Chrome", 3600000);
                            break;
                        case 5:
                            dialog.setMessage("今日挑戰為:" + mLuckyPanView.mStrs[5]);
                            mission = new BMission("Youtube", 3600000);
                            break;
                        default:
                            break;
                    }
                    add(mission.getMissionAppName(), mission.getMissionAppTime());

                    //final String tt = mission.getMissionAppName() + mission.getMissionAppTime();
                    dialog.setPositiveButton("接受挑戰！", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {  //可更改想要toast的植


                            /***重新啟動偵測服務!**/
                            Intent serviceIntent = new Intent(BMissionActivity.this, com.cin.linyuehlii.nobile.MyService.class);
                            startService(serviceIntent);
                            /***重新啟動偵測服務!**/


                            /*************************************把抽到的APP的存成檔案*****************************************/

                            try {
                                //Toast.makeText(BMissionActivity.this, "設定成功", Toast.LENGTH_SHORT).show();
                                fileOut = openFileOutput("mission.tmp", Context.MODE_PRIVATE);
                                objectOut = new ObjectOutputStream(fileOut);
                                objectOut.writeObject(mission);//存入單一物件
                                objectOut.close();
                                fileOut.close();

                                Log.d("file", "轉盤任務存入成功");

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                Log.d("file", "fileOut ioe");

                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Log.d("file", "fileOut ex");
                            }

                            /*************************************把抽到的APP的存成檔案*****************************************/

                            //Toast.makeText(BMissionActivity.this, tt, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(BMissionActivity.this, BMissionShowActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("mission", mission.MissionAppName + "\n" + mission.getMissionAppTimeFormatted());
                            //將Bundle物件assign給intent
                            intent.putExtras(bundle);

                            startActivity(intent);
                            finish();
                        }
                    });
                    dialog.show();
                }
            }  //end onclick

            private void add(String AppName, long AppTime) {
                try {
                    SQLiteDatabase db = dbhelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("appName", AppName);
                    values.put("sec", AppTime);
                    db.insert("dailyMission", null, values);
                } catch (Exception ex) {
                    System.out.print("Try later");
                }
            }
        });

        closeDatabase();
    }

    private void openDatabase() {
        dbhelper = new BdBHelper(this);
    }

    private void closeDatabase() {
        dbhelper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDatabase();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(BMissionActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
