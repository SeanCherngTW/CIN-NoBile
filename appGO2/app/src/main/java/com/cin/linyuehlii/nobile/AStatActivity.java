package com.cin.linyuehlii.nobile;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;


public class AStatActivity extends AppCompatActivity {

    /*^^^^^^^^^^^按鈕、TextView宣告^^^^^^^^^^^*/
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    private ImageButton Fbt, Lbt;
    private static String TAG = "fragment";
    private FragmentManager manager = getSupportFragmentManager();
    private TextView barTitle;

    /*^^^^^^^^^^^資料庫、service宣告^^^^^^^^^^^*/
    private MyService mMyServ;
    private ContentResolver mAppUsageStatRes;
    private NewHelper helperrr;//資料庫引進

    /*^^^^^^^^^^^變數宣告^^^^^^^^^^^*/
    private static String strAppName;
    private static String strTime;
    private String aa, bb = null;
    private int cc;
    public static AStatActivity INSTANCE;

    /*^^^^^^^^^^^取得今日日期^^^^^^^^^^^*/
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;//calender月份從0-11
    int day = c.get(Calendar.DAY_OF_MONTH);


    /**********************************************************/
    /************************程式碼開始*************************/
    /**********************************************************/
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
        setContentView(R.layout.a_stat_ac);
        INSTANCE = this;

        /**^^^^^^介面^^^^^^**/
        TextView titletext = (TextView) findViewById(R.id.titleText);
        int dayPre = day - 1;
        String title = month + "月" + day + "日";
        titletext.setText(title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        barTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        barTitle.setText("統計資料");
        Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);
        Lbt = (ImageButton) toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);
        RadioGroup r = (RadioGroup) findViewById(R.id.radiogroup);
        r.setVisibility(View.GONE);//暫時將activity裡的第三頁按鈕移走

        btStat=(ImageView) findViewById(R.id.btStat);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btStat.setBackground(highlight);

        if (helperrr == null) {//資料庫準備
            helperrr = new NewHelper(this);
        }

        findViews();

        /*************抓取使用時間資料***************/
        ArrayList<Member> appDetail = new ArrayList<>();//欲輸出的使用時間
        for (int i = 0; i < helperrr.getAllSpots().size(); i++) {
            Member m = null;
            cc = helperrr.getAllSpots().get(i).getImage();
            aa = helperrr.getAllSpots().get(i).getAppname();
            bb = getAppUsingTime(aa);
            if (bb == null) {
                bb = "0";
            }
            m = new Member(i, cc, aa, bb);
            Log.d("member m", "member m = " + m.getName());
            Log.d("member m", "member m = " + m.getTime());
            //Log.d("member m",m.getName());
            appDetail.add(m);
        }

        if (appDetail.isEmpty())
            Toast.makeText(AStatActivity.this, "請到設定頁面設定要監控的APP喔", Toast.LENGTH_LONG);

        /*************跳fragment***************/
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("appDetail", appDetail);

        FragmentManager manager;
        manager = getSupportFragmentManager();
        FragmentTransaction gg = manager.beginTransaction();
        ANewTypeFrag ty = new ANewTypeFrag();
        ty.setArguments(bundle);
        gg.replace(R.id.frameLayout, ty);
        gg.commit();


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void findViews() {
        btMission = (ImageView) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting = (ImageView) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        btLeaderboard = (ImageView) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);
    }

    /**********
     * 按鈕事件設定區
     *************/
    private ImageButton.OnClickListener gobackClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            ATotalTimeFrag fragmentB = new ATotalTimeFrag();
            //AAppTimeFrag fragmentB=new AAppTimeFrag();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.frameLayout, fragmentB, TAG);
            transaction.commit();
        }
    };
    private ImageView.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(AStatActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btSettingClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(AStatActivity.this, CSettingActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(AStatActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(AStatActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };

    /*******
     * 依照app名稱取使用
     *********/
    public String getAppUsingTime(String n) {
        mAppUsageStatRes = getContentResolver();
        String aa = null;
        Cursor c = null;
        String[] appUsageStatArray = new String[]{"appName", "time"};
        c = mAppUsageStatRes.query(AppUsageStatProvider.CONTENT_URI, appUsageStatArray, null, null, null);
        try {
            if (c.getCount() == 0) {
                //Toast.makeText(this, "資料統計中，請稍後再來~", Toast.LENGTH_LONG).show();
            } else {
                strAppName = "";
                strTime = "";
                c.moveToLast();
                while (c.moveToPrevious()) {
                    strAppName = c.getString(0);
                    if (n.equals(strAppName)) {
                        aa = c.getString(1);
                        //Log.d("aa", "c.getString(1); = " + n + aa);
                        break;
                    } else {
                        // Log.d("jjj", "jjj");
                    }
                }// end while
            }// end else
            c.close();
        } catch (NullPointerException ex) {
            //Toast.makeText(AStatActivity.this, "資料統計中，請稍後再來~", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
        return aa;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(AStatActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

