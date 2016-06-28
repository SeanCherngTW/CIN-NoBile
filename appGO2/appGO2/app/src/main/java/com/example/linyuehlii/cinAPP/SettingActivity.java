package com.example.linyuehlii.cinAPP;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
public class SettingActivity extends AppCompatActivity {
    private ImageButton btStat,btPoint,btMission,btSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_blackapp_ac);
        ListView lvMember = (ListView) findViewById(R.id.lvMember);
        lvMember.setAdapter(new MemberAdapter(this));
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Member member = (Member) parent.getItemAtPosition(position);
                String text = "ID = " + member.getId() +
                        ", name = " + member.getName();
                Toast.makeText(SettingActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void  findViews(){
        btStat=(ImageButton) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission=(ImageButton) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

       // btSetting=(ImageButton) findViewById(R.id.btSetting);
        //btSetting.setOnClickListener(btSettingClick);

        btPoint=(ImageButton) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

    }
    //按鈕事件設定區
    private ImageButton.OnClickListener btStatClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(SettingActivity.this,StatActivity.class);
            startActivity(intent);
            finish();
        }
    };
    private ImageButton.OnClickListener btMissionClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(SettingActivity.this,MissionActivity.class);
            startActivity(intent);
            finish();
        }
    };
  /*  private ImageButton.OnClickListener btSettingClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(SettingActivity.this,SettingActivity.class);
            startActivity(intent);
            finish();
        }
    };*/
    private ImageButton.OnClickListener btPointClick = new ImageButton.OnClickListener(){
        @Override
        public void onClick(View arg0) {
            Intent intent=new Intent();
            intent.setClass(SettingActivity.this,PointActivity.class);
            startActivity(intent);
            finish();
        }
    };
private class MemberAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Member> memberList;

    public MemberAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);

        memberList = new ArrayList<>();
        memberList.add(new Member(23,R.mipmap.facebook,"Facebook","50"));
        memberList.add(new Member(3, R.mipmap.line,"Line","100"));

    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return memberList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.blackapp_itemlist, parent, false);
        }

        Member member = memberList.get(position);
        ImageView ivImage = (ImageView) convertView
                .findViewById(R.id.ivImage);
       ivImage.setImageResource(member.getImage());


        TextView tvName = (TextView) convertView
                .findViewById(R.id.tvName);
        tvName.setText(member.getName());

        TextView tvTime = (TextView) convertView
                .findViewById(R.id.tvTime);
        tvTime.setText(member.getTime());
        return convertView;
    }
}

}
