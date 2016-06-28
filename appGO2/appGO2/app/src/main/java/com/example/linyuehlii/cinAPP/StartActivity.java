package com.example.linyuehlii.cinAPP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.view.View;


public class StartActivity extends AppCompatActivity {
    private ImageButton btStat,btPoint,btMission,btSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViews();
    }
    public void  findViews(){
        btStat=(ImageButton) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission=(ImageButton) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting=(ImageButton) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint=(ImageButton) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

    }
    //按鈕事件設定區
    private ImageButton.OnClickListener btStatClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
                Intent intent=new Intent();
                intent.setClass(StartActivity.this,StatActivity.class);
                startActivity(intent);
                finish();
        }
    };
   private ImageButton.OnClickListener btMissionClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(StartActivity.this,MissionActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private ImageButton.OnClickListener btSettingClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(StartActivity.this,SettingActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private ImageButton.OnClickListener btPointClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(StartActivity.this,PointActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
