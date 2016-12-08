package com.cin.linyuehlii.nobile;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private ImageButton btStat, btPoint, btMission, btSetting, btLeaderboard;
    private MySQLiteOpenHelper helper;
    TextView minValue, hrValue;
    int min, hr = 0;
    private int time = 0;
    private int avgTime = 0;
    private MyService mMyServ;
    final String PREFS_NAME = "MyPrefsFile";
    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer tExit;
    TimerTask task;


    private ServiceConnection mServComm = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.d("service", "onServiceConnected() " + name.getClassName());
            mMyServ = ((MyService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.d("service", "onServiceDisconnected()" + name.getClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_ac);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        helper = new MySQLiteOpenHelper(this);
        helper.insertIfEmpty();
        time = helper.getTotalTime(false).get(3);
        avgTime = helper.getAvgTotalTime();

        //隨機時間
        hr = time / 60;
        min = time - 60 * hr;
        //  Log.d("hhh", String.valueOf(time));
        //hrValue.setText(String.valueOf(hr));
        //hrValue.setText("");
        // minValue.setText(String.valueOf(min));

        findViews();

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            startService(new Intent(StartActivity.this, MyService.class));
            Intent itSetting = new Intent(StartActivity.this, CSettingActivity.class);
            settings.edit().putBoolean("my_first_time", false).commit();
            startActivity(itSetting);
            // record the fact that the app has been started at least once

        }

        mMyServ = null;

        if (!isNoSwitch()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            Toast.makeText(StartActivity.this, "請開啟存取使用量之權限或重新開啟", Toast.LENGTH_LONG).show();
        }


        tExit = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                isExit = false;
                hasTask = true;
            }
        };
    }

    public void findViews() {


        btStat = (ImageButton) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission = (ImageButton) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting = (ImageButton) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageButton) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        btLeaderboard = (ImageButton) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //以23小1380時為限-->min
        imageView.setImageResource(R.drawable.running_man);
        /*if (time <= (avgTime / 4)) {
            imageView.setImageResource(R.drawable.start1);
        } else if (time > (avgTime / 4) && time <= (avgTime / 4 * 2)) {
            imageView.setImageResource(R.drawable.start2);
        } else if (time > (avgTime / 4 * 2) && time <= (avgTime / 4 * 3)) {
            imageView.setImageResource(R.drawable.start3);
        } else if (time > (avgTime / 4 * 3) && time <= (avgTime)) {
            imageView.setImageResource(R.drawable.start4);
        } else if (time > (avgTime)) {
            imageView.setImageResource(R.drawable.start5);
        }*/

    }

    //按鈕事件設定區
    private ImageButton.OnClickListener btStatClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageButton.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageButton.OnClickListener btSettingClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, CSettingActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageButton.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };

    private ImageButton.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(StartActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };

    private boolean isNoSwitch() {
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext()
                .getSystemService("usagestats");
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
            return false;
        }
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            if (keyCode == KeyEvent.KEYCODE_BACK)
                if (isExit == false) {
                    isExit = true;
                    Toast.makeText(this, "再按一次後退鍵退出應用程式", Toast.LENGTH_SHORT).show();
                    if (!hasTask)
                        tExit.schedule(task, 2000);
                } else {
                    try {
                        if (AStatActivity.INSTANCE != null)
                            AStatActivity.INSTANCE.finish();

                        if (BMissionActivity.INSTANCE != null)
                            BMissionActivity.INSTANCE.finish();

                        if (BMissionShowActivity.INSTANCE != null)
                            BMissionShowActivity.INSTANCE.finish();

                        if (CSettingActivity.INSTANCE != null)
                            CSettingActivity.INSTANCE.finish();

                        if (CChooseActivity.INSTANCE != null)
                            CChooseActivity.INSTANCE.finish();

                        if (DPointActivity.INSTANCE != null)
                            DPointActivity.INSTANCE.finish();

                        if (ELeaderboardActivity.INSTANCE != null)
                            ELeaderboardActivity.INSTANCE.finish();

                        if (LoginActivity.INSTANCE != null)
                            LoginActivity.INSTANCE.finish();

                        if (PostFBActivity.INSTANCE != null)
                            PostFBActivity.INSTANCE.finish();

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    } finally {
                        finish();
                        System.exit(0);
                    }

                }
        return false;
    }
}
