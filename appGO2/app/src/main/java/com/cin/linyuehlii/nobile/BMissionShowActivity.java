package com.cin.linyuehlii.nobile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



/**
 * Created by Shark on 2016/7/27.
 */
public class BMissionShowActivity extends AppCompatActivity {

    /*^^^^^^^^^^^按鈕、TextView宣告^^^^^^^^^^^*/
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    private static String TAG = "fragment";
    private FragmentManager manager = getSupportFragmentManager();
    private TextView barTitle;
    private ImageButton Fbt,Lbt;
    String mission = "";
    public static BMissionShowActivity INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_mission_show_ac);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        INSTANCE = this;

        btMission=(ImageView) findViewById(R.id.btMission);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btMission.setBackground(highlight);

        Intent serviceIntent = new Intent(BMissionShowActivity.this, MyService.class);
        startService(serviceIntent);

        /*********************從檔案讀取日期********************/
        try {
            FileInputStream fileIn = openFileInput("MissionPage");
            byte[] bufBytes = new byte[1];

            do {
                int c = fileIn.read(bufBytes);
                if (c == -1) break;
                else mission += new String(bufBytes);
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

        Log.d("mission", "Mission = " + mission);
        /***************************從檔案讀取日期***************************/

        barTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        barTitle.setText("每日挑戰");
        Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);
        Lbt = (ImageButton) toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);

        Bundle bundle0311 = this.getIntent().getExtras();
        TextView todaymission = (TextView) findViewById(R.id.tvMission);



        if (bundle0311 != null) {
            mission = bundle0311.getString("mission");
        }
        todaymission.setText( mission);



        /***********************************寫入今日日期至檔案*******************************************/
        try {
            FileOutputStream fileOut = openFileOutput("MissionPage", MODE_PRIVATE);
            // 寫入檔案 (FileName, Type)

            fileOut.write(mission.getBytes());
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

        Log.d("mission", "Mission = " + mission);
        /***********************************寫入今日日期至檔案*******************************************/


        findViews();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    public void findViews() {

        btStat = (ImageView) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btSetting = (ImageView) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        btLeaderboard = (ImageView) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);

    }

    /***********按鈕事件設定區*************/
    private ImageView.OnClickListener btStatClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(BMissionShowActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btSettingClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(BMissionShowActivity.this, CSettingActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(BMissionShowActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(BMissionShowActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(BMissionShowActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

