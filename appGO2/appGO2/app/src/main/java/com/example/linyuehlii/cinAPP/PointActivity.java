package com.example.linyuehlii.cinAPP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class PointActivity extends AppCompatActivity {
    private ImageButton btStat,btPoint,btMission,btSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mission_ac);
        findViews();
    }
    public void  findViews(){
        btStat=(ImageButton) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission=(ImageButton) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting=(ImageButton) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

      //  btPoint=(ImageButton) findViewById(R.id.btPoint);
       // btPoint.setOnClickListener(btPointClick);

    }
    //按鈕事件設定區
    private ImageButton.OnClickListener btStatClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(PointActivity.this,StatActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private ImageButton.OnClickListener btMissionClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(PointActivity.this,MissionActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private ImageButton.OnClickListener btSettingClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(PointActivity.this,SettingActivity.class);
            startActivity(intent);
            finish();
        }
    };
   /* private ImageButton.OnClickListener btPointClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(PointActivity.this,PointActivity.class);
            startActivity(intent);
            finish();
        }
    };*/
}
