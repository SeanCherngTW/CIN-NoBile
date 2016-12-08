package com.cin.linyuehlii.nobile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shark on 2016/7/27.
 */
public class CChooseActivity extends AppCompatActivity {
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    private TextView barTitle;
    private ImageButton Fbt,Lbt;
    private Button button;
    private FragmentManager manager = getSupportFragmentManager();
    public static CChooseActivity INSTANCE;

    /****8*****************************8****/
    /***8**********程式碼開始**************8***/
    /**8*************************************8**/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_setting_ac);
        INSTANCE = this;

        Intent serviceIntent = new Intent(CChooseActivity.this, MyService.class);
        stopService(serviceIntent);

        /**************************/
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        barTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        barTitle.setText("選擇APP & 設定單日使用上限");
        Fbt=(ImageButton)toolbar.findViewById(R.id.Fbt);
        Lbt=(ImageButton)toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);
        button=(Button)findViewById(R.id.button123);
        button.setVisibility(View.GONE);

        btSetting=(ImageView) findViewById(R.id.btSetting);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btSetting.setBackground(highlight);
        findViews();


        /************fragment傳送**************/
        CChooseAppFrag fragmentB=new CChooseAppFrag();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frag, fragmentB);
        transaction.commit();
        findViews();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

    public void findViews() {
        btStat = (ImageView) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission = (ImageView) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting=(ImageView) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        btLeaderboard = (ImageView) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);

    }

    /****************按鈕事件設定區****************/
    private ImageView.OnClickListener btStatClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CChooseActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CChooseActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
      private ImageView.OnClickListener btSettingClick = new ImageButton.OnClickListener(){
          @Override
          public void onClick(View arg0) {
              Intent intent=new Intent();
              intent.setClass(CChooseActivity.this,CSettingActivity.class);
              startActivity(intent);
              overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
              finish();
          }
      };
    private ImageView.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CChooseActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };
    private ImageView.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CChooseActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition( R.animator.slide_in_up, R.animator.slide_out_up );
            finish();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(CChooseActivity.this, CSettingActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}