package com.cin.linyuehlii.nobile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import SendGmail.GMailSender;
import SendGmail.SQLiteDataHelper;
import bolts.Task;

public class MyService extends Service {

    final int notifyID = 1; // 通知的識別號碼
    private final String LOG_TAG = "service demo";
    private static final String DB_NAME = "EmailData", DB_FILE = "emailData.db", DB_TABLE = "EmailData";
    private static final String DB_STAT_NAME = "StatData", DB_STAT_FILE = "Stat.db", DB_STAT_TABLE = "StatData";

    private static final int DETECT_INTERVAL = 10000;
    private static final int SEND_INTERVAL = 784;
    private SQLiteDatabase mReceiverDB, mStatDataDB;

    List<UsageStats> queryUsageStats;
    ArrayList<Member> monitorAppList;
    UsageStatsManager mUsageStatsManager;
    List<CustomUsageStats> FinalCustomUsageStats;
    GMailSender sender; //自動寄gmail要用
    String Receiver1, Receiver2, Receiver3, textSubject, textMessage, UserName, Password;
    ContentResolver mAppUsageStatRes;
    BMission mission = new BMission("noAPP", 0);
    final String PREFS_NAME = "NextDay";
    public static boolean isGmailSetted = true;
    boolean isReachedLimit = true;

    private static final int CASE_REACH_TIME_LIMIT = 1;
    private static final int CASE_MISSION_COMPLETED = 2;
    private static final int CASE_MISSION_FAILED = 3;
    private static final int DID_NOT_REACH_TIME_LIMIT = 4;

    private static final String COL_id = "_id";
    private static final String COL_receiver1 = "receiver1";
    private static final String COL_receiver2 = "receiver2";
    private static final String COL_receiver3 = "receiver3";
    private static final String COL_user_name = "username";
    private static final String COL_password = "password";

    private static final String COL_stat_appname = "appname";
    private static final String COL_stat_apptime = "apptime";
    int score;
    final Timer timer = new Timer();

    //設定內文、標題
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    //意義不明，應該是寄gmail會用到

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    private LocalBinder mLocBin = new LocalBinder();

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "開始偵測囉!!!!!!!!!!!!!!!");
        //Toast.makeText(MyService.this, "已開始監聽您設定的APP之使用量!", Toast.LENGTH_SHORT).show();
        super.onCreate();
        mAppUsageStatRes = getContentResolver();

        /**********************************初始化""統計資料資料庫""*********************************************/

        CustomUsageStatsDBHelper StatHelper =
                new CustomUsageStatsDBHelper(getApplicationContext(), DB_STAT_FILE, null, 1);
        mStatDataDB = StatHelper.getWritableDatabase();

        Cursor StatDataCursor =
                mStatDataDB.rawQuery("select DISTINCT tbl_name " +
                                             "from sqlite_master " +
                                             "where tbl_name = '" + DB_STAT_TABLE + "'", null);

        if (StatDataCursor != null) {
            if (StatDataCursor.getCount() == 0)
                mStatDataDB.execSQL("CREATE TABLE " + DB_STAT_TABLE + " ( " +
                                            COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            COL_stat_appname + " TEXT, " +
                                            COL_stat_apptime + " TEXT);");
            StatDataCursor.close();
        }


        /**********************************初始化""統計資料資料庫""*********************************************/


        /*************************初始化""Gmail資料庫""並取出收件者地址*****************************************/

        SQLiteDataHelper helper = new SQLiteDataHelper(getApplicationContext(), DB_FILE, null, 1);
        mReceiverDB = helper.getWritableDatabase();

        Cursor cursor = mReceiverDB.rawQuery
                ("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                         + DB_NAME + "'", null);

        if (cursor == null) {
            isGmailSetted = false;
            //Toast.makeText(MyService.this, "請至設定頁面輸入您的Gmail帳號密碼，與好友的GMAIL位址", Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(MyService.this, CSettingActivity.class));
        } else {
            if (cursor.getCount() == 0) {
                mReceiverDB.execSQL("CREATE TABLE " + DB_TABLE + " ( " +
                                            COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            COL_user_name + " TEXT, " +
                                            COL_password + " TEXT, " +
                                            COL_receiver1 + " TEXT, " +
                                            COL_receiver2 + " TEXT, " +
                                            COL_receiver3 + " TEXT);");
                cursor.close();
            } else {
                try {
                    Cursor c = mReceiverDB.query
                            (true, DB_TABLE,
                             new String[]{COL_user_name, COL_password, COL_receiver1, COL_receiver2, COL_receiver3},
                             null, null, null, null, null, null);
                    c.moveToFirst();
                    UserName = c.getString(0);
                    Password = c.getString(1);
                    Receiver1 = c.getString(2);
                    Receiver2 = c.getString(3);
                    Receiver3 = c.getString(4);
                    c.close();
                    isGmailSetted = true;
                    Log.d("EmailData.db", UserName + "\n" + Password + "\n" +
                            Receiver1 + "\n" + Receiver2 + "\n" + Receiver3);
                } catch (Exception ex) {
                    ex.getStackTrace();
                    isGmailSetted = false;
                    //Toast.makeText(MyService.this, "請至設定頁面輸入您的Gmail帳號密碼，與好友的GMAIL位址", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MyService.this, CSettingActivity.class));
                }// end catch
            }//end else
        }///end if

        /*************************初始化""Gmail資料庫""並取出收件者地址*********************************************/


        /****************************************設置自動發送Gmail環境****************************************/

        sender = new GMailSender(UserName, Password);
        //設定使用者之帳密
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //不知道，設GMAIL要用

        /****************************************設置自動發送Gmail環境****************************************/


        /*************************************從檔案讀取設定的APP********************************************/

        FileInputStream fileIn = null;
        ObjectInputStream objectIn = null;
        monitorAppList = new ArrayList<Member>();

        try {

            fileIn = openFileInput("t.tmp");

            objectIn = new ObjectInputStream(fileIn);
            monitorAppList = (ArrayList<Member>) (objectIn.readObject());
            objectIn.close();

            Log.d("file", "t.tmp(設定的APP)讀取成功");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            Log.d("file", "t.tmp(設定的APP)不存在");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("file", "t.tmp(設定的APP)讀取失敗");

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            Log.d("file", "t.tmp(設定的APP)讀取失敗");
        }

        /*************************************從檔案讀取設定的APP********************************************/


        /*************************************從檔案讀取每日挑戰APP********************************************/

        try {

            fileIn = openFileInput("mission.tmp");
            objectIn = new ObjectInputStream(fileIn);
            mission = (BMission) (objectIn.readObject());
            objectIn.close();

            Log.d("file", "fileIn");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            Log.d("file", "fileIn FNFE");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("file", "fileIn IOE");

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            Log.d("file", "fileIn CNFE");
        }

        Log.d("mission", "mission name = " + mission.MissionAppName);
        Log.d("mission", "mission time = " + mission.MissionAppTime);

        /*************************************從檔案讀取每日挑戰APP********************************************/


        go();//開始監聽

    }  // end onCreate()

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    private void go() {
        //mAppUsageStatRes = getContentResolver();
        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        TimerTask action = new TimerTask() {
            @Override
            public void run() {
                Log.d("SendGmail", "Start Tracing");

                List<UsageStats> usageStatsList = getUsageStatistics();
                Collections.sort(usageStatsList, new TotalUsingTimeComparatorDesc());
                FinalCustomUsageStats = updateAppsList(usageStatsList);
                reachTimeLimit(FinalCustomUsageStats, monitorAppList);


                /********************檢查有沒有到下一天******************/
                long stDay = System.currentTimeMillis() / 86400000;
                SharedPreferences DatePref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = DatePref.edit();
                long yesterday = DatePref.getLong("KEY_LONG", 0);

                Log.d("mission", "Today: " + stDay);
                Log.d("mission", "Yesterday: " + yesterday);
                Log.d("isReachedLimit", "(沒到下一天)isReachedLimit = " + isReachedLimit);

                if (stDay > yesterday) {
                    //如果到了下一天
                    editor.putLong("KEY_LONG", stDay);
                    editor.commit();
                    Log.d("mission", "Next Day");
                    Log.d("isReachedLimit", "(已到下一天)isReachedLimit = " + isReachedLimit);
                    pushNotification(2, "手機戒癮", "轉盤已經準備好囉!", "");
                    SharedPreferences TimeLimitSP = getSharedPreferences("isReachedLimit", 0);
                    isReachedLimit = TimeLimitSP.getBoolean("isReachedLimit", true);
                    Log.d("isReachedLimit", "(已到下一天並抓出來)isReachedLimit = " + isReachedLimit);
                    if(isReachedLimit) //今天一整天都沒超時
                        activateSend("",DID_NOT_REACH_TIME_LIMIT);
                    TimeLimitSP.edit().putBoolean("isReachedLimit", true).commit();

                    missionCheck(FinalCustomUsageStats, mission);
                }
                /********************檢查有沒有到下一天******************/
            }
        };

        timer.schedule(action, DETECT_INTERVAL, DETECT_INTERVAL);
        // (任務,第一次執行延遲,下次執行延遲)

        /*APP業師教育部計畫
        TimerTask sendHTTP = new TimerTask() {
            @Override
            public void run() {
                Log.d("SendGmail", "Send HTTP");
                String url = "http://140.115.197.16/?school=ncu&app=im2016_nobile";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                try{
                    HttpResponse response = client.execute(request);
                }catch (Exception e) {
                    //Exception
                }
            }
        };
        timer.schedule(sendHTTP, 0, SEND_INTERVAL);
        APP業師教育部計畫*/

    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        //super.onDestroy();
        mReceiverDB.close();
        mStatDataDB.close();
        Intent localIntent = new Intent();
        localIntent.setClass(this, MyService.class); //销毁时重新启动Service
        this.startService(localIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind()");
        return mLocBin;
    }


    /*****************************
     * 取得今日統計資料(原始資料)
     ****************************************/

    public List<UsageStats> getUsageStatistics() {
        long st = System.currentTimeMillis();
        queryUsageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, st - 2 * 1000, st);
        return queryUsageStats;
    }  //end getUsageStatistics()

    /*****************************
     * 取得今日統計資料(原始資料)
     ****************************************/


    private List<CustomUsageStats> updateAppsList(List<UsageStats> usageStatsList) {

        /*************************把原始資料加工，成為List<CustomUsageStats>****************************/
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();

        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            // 自定義的類別，包含 UsageStats usageStats, Drawable appIcon
            customUsageStats.usageStats = usageStatsList.get(i);
            // 取出usageStatsList中的第i個值(也就是第i筆APP的統計資料)，設給customUsageStats.usageStats
            PackageManager pm = getPackageManager();
            long TotalTimeFG = customUsageStats.usageStats.getTotalTimeInForeground();

            if (TotalTimeFG < 60000) continue;

            try {

                ApplicationInfo ai = pm.getApplicationInfo(customUsageStats.usageStats.getPackageName(), 0);
                customUsageStats.appName = (String) pm.getApplicationLabel(ai);
                customUsageStats.appIcon = getPackageManager().getApplicationIcon(customUsageStats.usageStats.getPackageName());


                /**********************把統計資料放入ContentProvider******************************************/


                ContentValues newRow = new ContentValues();
                newRow.put("appName", customUsageStats.appName);
                newRow.put("time", TotalTimeFG);
                mAppUsageStatRes.insert(AppUsageStatProvider.CONTENT_URI, newRow);
                Log.d("insert", customUsageStats.appName + " " + TotalTimeFG + " is inserted into Content Provider successfully\n");
                customUsageStatsList.add(customUsageStats);

                /**********************把統計資料放入ContentProvider******************************************/

            } catch (PackageManager.NameNotFoundException e) {
                customUsageStats.appIcon = getDrawable(R.drawable.black_radius);
            }
            customUsageStatsList.add(customUsageStats);
            /***********************把原始資料加工，成為List<CustomUsageStats>****************************/
        }//end outer for
        return customUsageStatsList;//回傳加工完的list
    }  // end updateAppsList()


    private void reachTimeLimit(List<CustomUsageStats> usageList, ArrayList<Member> monitorList) {

        for (int i = 0; i < monitorList.size(); i++) {
            Log.d("SendGmail", "for (int i = 0; i < monitorList.size(); i++)");
            for (int j = 0; j < usageList.size(); j++) {
                Log.d("SendGmail", "for (int j = 0; j < usageList.size(); j++)");
                if (monitorList.get(i).getName().equals(usageList.get(j).appName)
                        && monitorList.get(i).getTimeInMilliSecond() < usageList.get(j).usageStats.getTotalTimeInForeground()
                        && usageList.get(j).usageStats.getLastTimeUsed() > (System.currentTimeMillis() - DETECT_INTERVAL)) {
                    Log.d("SendGmail", "if (monitorList.get(i).getName().equals(usageList.get(j).appName)");
                    activateSend(monitorList.get(i).getName(), CASE_REACH_TIME_LIMIT);
                    Log.d("SendGmail", "activateSend(monitorList.get(i).getName());");
                }
            }
        }
    }  // end sendGmail
        /*每當:
         *   1.兩個list擁有相同APP名稱之物件
         *   2.該APP使用時間超出限制
         *   3.使用者正在使用他
         *
         *執行:
         *   針對該APP的超時訊息寄出Gmail
         */

    private void activateSend(String name, int sendCase) {

        SharedPreferences ScorePref = getSharedPreferences("totalScore", MODE_PRIVATE);
        SharedPreferences.Editor editor = ScorePref.edit();
        int currentScore = ScorePref.getInt("KEY_INT", 0);

        Log.d("function", "activateSend");
        String ContentText = "";
        String IntentText = "";
        switch (sendCase) {//BANNER通知的文字
            case 1: //超時處罰
                //updateScore(score, CASE_REACH_TIME_LIMIT);
                currentScore--;
                ContentText = " 您使用「" + name + "」已逾單日時間上限";
                IntentText = "您的好友在意外之中失守了信約，如今已於意外間沉迷於「" + name + "」" +
                        "(ΩДΩ)，請讓您的好友感受到您的關心，找回遺失的時間(✺ω✺)";
                SharedPreferences TimeLimitSP = getSharedPreferences("isReachedLimit", 0);
                TimeLimitSP.edit().putBoolean("isReachedLimit", false).commit();
                break;
            case 2: //任務完成
                //updateScore(score, CASE_MISSION_COMPLETED);
                currentScore = currentScore + 5;
                ContentText = "您成功完成今日挑戰!";
                IntentText = "恭喜您! 今日使用 " + mission.MissionAppName + " 未超過 " +
                        mission.getMissionAppTimeFormatted() + " 。請繼續保持 :)";
                break;
            case 3: //任務失敗
                //updateScore(score, CASE_MISSION_FAILED);
                currentScore = currentScore - 5;
                ContentText = "今日挑戰失敗!";
                IntentText = "您今日使用 " + mission.MissionAppName + " 已超過 " +
                        mission.getMissionAppTimeFormatted() + " 。明天會更好，請繼續努力QQ";
                break;
            case 4: //一整天未超時
                //updateScore(score, CASE_MISSION_FAILED);
                currentScore = currentScore + 5;
                ContentText = "恭喜您!一整天都沒有超時!請繼續保持";
                IntentText = "今天一整天都沒超時~朝著戒癮的遠大目標又前進了一小步~";
                break;
        }

        editor.putInt("KEY_INT", currentScore).commit();

        pushNotification(1, "點擊分享你的戒癮之路", ContentText, IntentText);
        if (!isGmailSetted)
            pushNotification(3, "點擊設定", "想嘗試更多有趣好玩的功能嗎?", "goSet");

        try {
            sender.sendMail(setTextSubject(name, sendCase), setTextMessage(name, sendCase), UserName, Receiver1);
            sender.sendMail(setTextSubject(name, sendCase), setTextMessage(name, sendCase), UserName, Receiver2);
            sender.sendMail(setTextSubject(name, sendCase), setTextMessage(name, sendCase), UserName, Receiver3);
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    } // end activateSend

    private void pushNotification(int caseID, String ContentTitle, String ContentText, String IntentText) {
        int notifyID = caseID; // 1:超時、任務完成、任務失敗、一整天沒超時 2:轉盤ok 3:提醒設定Gmail 4:
        int requestCode = notifyID;
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        // ONE_SHOT：PendingIntent只使用一次
        // CANCEL_CURRENT：PendingIntent執行前會先結束掉之前的
        // NO_CREATE：沿用先前的PendingIntent，不建立新的PendingIntent
        // UPDATE_CURRENT：更新先前PendingIntent所帶的額外資料，並繼續沿用
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_small);
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        Intent intent = new Intent(this, StartActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags);

        switch (caseID) {
            case 1://超時、任務完成、任務失敗、一整天沒超時
                intent = new Intent(this, PostFBActivity.class);
                intent.putExtra("AppDescription", IntentText);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags);
                break;
            case 2: //轉盤OK
                intent = new Intent(MyService.this, BMissionActivity.class);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags);
                break;
            case 3://提醒設定Gmail
                intent = new Intent(this, CSettingActivity.class);
                intent.putExtra("setGmail", IntentText);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, flags);
                break;
        }


        Log.d("notification","times");
        //產生pendingIntent，可放進notification

        final Notification timeOutNotification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(android.R.drawable.btn_star_big_on)
                .setLargeIcon(largeIcon)
                .setContentIntent(pendingIntent)
                .setContentTitle(ContentTitle)
                .setContentText(ContentText)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .build();
        timeOutNotification.flags = Notification.FLAG_AUTO_CANCEL;  //點一次通知就消除
        notificationManager.notify(notifyID, timeOutNotification);
    }


    private void missionCheck(List<CustomUsageStats> usageList, BMission mission) {
        Log.d("function", "missionCheck");
        int missionStatus = 0;
        for (int i = 0; i < usageList.size(); i++) {
            if (usageList.get(i).appName.equals(mission.MissionAppName)) {
                if (usageList.get(i).usageStats.getTotalTimeInForeground() > mission.getMissionAppTime()) {
                    missionStatus = 1; //任務失敗
                    break;
                }
            }
        } // end for

        if (mission.getMissionAppName().equals("noAPP"))
            missionStatus = 2;

        switch (missionStatus) {
            case 0:
                missionComplete();
                break;
            case 1:
                missionFailed();
                break;
            case 2:
                break;
        }
    }

    private void missionComplete() {
        Log.d("function", "missionComplete");
        activateSend(mission.MissionAppName, CASE_MISSION_COMPLETED);
    }

    private void missionFailed() {
        Log.d("function", "missionFailed");
        activateSend(mission.MissionAppName, CASE_MISSION_FAILED);
    }

    private static class TotalUsingTimeComparatorDesc implements Comparator<UsageStats> {
        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getTotalTimeInForeground(), left.getTotalTimeInForeground());
        }
    }

    /********************************
     * 處理自動寄EMAIL之方法
     ******************************************/

    public String setTextSubject(String name, int SubjectCase) {
        switch (SubjectCase) {
            case 1: //超時處罰
                textSubject = "【手機戒癮】-「好友的時間不能等」";
                break;
            case 2: //任務成功
                textSubject = "【手機戒癮】-「與好友同慶」";
                break;
            case 3: //任務失敗
                textSubject = "【手機戒癮】-「鼓勵好友更待何時」";
                break;
            case 4: //任務失敗
                textSubject = "【手機戒癮】-「我成功了!」";
                break;
        }

        return textSubject;
    }

    public String setTextMessage(String name, int MessageCase) {
        switch (MessageCase) {
            case 1: //超時處罰
                textMessage = "您的好友在意外之中失守了信約，如今已於意外間沉迷於「" + name +
                        "」(ΩДΩ)，然而，懸崖勒馬回頭是岸，需要您以最輕鬆的態度給予最嚴厲的警惕，" +
                        "回覆這封信吧(✺ω✺)！讓您的好友感受到您的關心，得以重新找回遺失的時間～";
                break;
            case 2:
                textMessage = "您的好友完成了艱鉅的任務: 一日使用「" + mission.MissionAppName +
                        "」未超過「" + mission.getMissionAppTimeFormatted() +
                        "」┌|◎o◎|┘　└|◎o◎|┐為了一起分享喜悅，共同繼續為手機戒癮目標打拼，" +
                        "特捎來此信，邀您回覆並予以祝福，普天同慶 ｡:.ﾟヽ(*´∀`)ﾉﾟ.:｡";
                break;
            case 3:
                textMessage = "您的好友在挑戰不可能任務: 一日使用「" + mission.MissionAppName +
                        "」不超過「" + mission.getMissionAppTimeFormatted() + "」時，" +
                        "不幸因故失敗了(´⊙ω⊙`)，然而失敗為成功之母，此時此刻正需要你簡單的鼓勵，" +
                        "請回覆信件(✺ω✺)，協助您的好友重新振作再接再勵，挑戰自我！";
                break;
            case 4:
                textMessage = "我成功了!!!!今天一整天都沒超時喔!!!!快點回覆信件給我鼓勵吧~";
                break;
        }

        return textMessage;
    }

    /********************************
     * 處理自動寄EMAIL之方法
     ******************************************/
} // end MyService