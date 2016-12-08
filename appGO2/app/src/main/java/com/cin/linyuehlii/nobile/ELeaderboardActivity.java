package com.cin.linyuehlii.nobile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import SendGmail.SQLiteDataHelper;

/**
 * Created by Sean on 2016/9/7.
 */
public class ELeaderboardActivity extends AppCompatActivity {


    Thread HttpThread;
    EditText ed1;
    EditText ed2;
    private ImageButton Fbt, Lbt;
    private Button button;
    private TextView barTitle;
    TextView mtxtName, mtxtYourScore, mtxtScore, mtxtRanking;
    Boolean isGoogleAccountSetted = true;
    AlertDialog.Builder noAccountAltDlg;
    private SQLiteDatabase mReceiverDB;
    private static final String COL_id = "_id";
    private static final String COL_receiver1 = "receiver1";
    private static final String COL_receiver2 = "receiver2";
    private static final String COL_receiver3 = "receiver3";
    private static final String COL_user_name = "username";
    private static final String COL_password = "password";
    private static final String DB_NAME = "EmailData", DB_FILE = "emailData.db", DB_TABLE = "EmailData";
    String UserName, Password;
    FileInputStream fileIn = null;
    ObjectInputStream objectIn = null;
    ArrayList<Item> checkItem = new ArrayList<Item>();
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    ArrayList<Member> NameScoreList = new ArrayList<>();
    int currentScore = 0;

    public static ELeaderboardActivity INSTANCE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.e_leaderboard_ac);
        INSTANCE = this;
        findViews();
        mtxtName = (TextView) findViewById(R.id.txtName);
        mtxtYourScore = (TextView) findViewById(R.id.txtYourScore);
        mtxtScore = (TextView) findViewById(R.id.txtScore);
        mtxtRanking = (TextView) findViewById(R.id.txtRanking);

        SharedPreferences ScorePref = getSharedPreferences("totalScore", MODE_PRIVATE);
        SharedPreferences.Editor editor = ScorePref.edit();
        currentScore = ScorePref.getInt("KEY_INT", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        barTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        barTitle.setText("排行榜");
        Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);
        Lbt = (ImageButton) toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);
        /*button = (Button) findViewById(R.id.button123);
        button.setVisibility(Button.GONE);*/

        btLeaderboard = (ImageView) findViewById(R.id.btLeaderboard);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btLeaderboard.setBackground(highlight);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //trytry


        /*************************初始化""Gmail資料庫""並取出收件者地址*****************************************/

        SQLiteDataHelper helper = new SQLiteDataHelper(getApplicationContext(), DB_FILE, null, 1);
        mReceiverDB = helper.getWritableDatabase();

        Cursor cursor = mReceiverDB.rawQuery
                ("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DB_NAME + "'", null);

        if (cursor == null) {
            isGoogleAccountSetted = false;
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
                    c.close();
                    Log.d("EmailData.db", UserName + "\n" + Password);
                } catch (Exception ex) {
                    ex.getStackTrace();
                    isGoogleAccountSetted = false;
                    // startActivity(new Intent(MyService.this, CSettingActivity.class));
                }// end catch
            }//end else
        }///end if

        try {
            if (!isGoogleAccountSetted || UserName.equals("") || Password.equals("")) {
                setAlertDialog();
                noAccountAltDlg.show();
                /*AlertDialog alertDialog = noAccountAltDlg.create();
                alertDialog.show();
                alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);*/
                //noAccountAltDlg.show();
            }
            String[] UserNameFormat = UserName.split("@");
            UserName = UserNameFormat[0];
        } catch (NullPointerException np) {
            setAlertDialog();
            noAccountAltDlg.show();
           /* AlertDialog alertDialog = noAccountAltDlg.create();
            alertDialog.show();
            alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);*/
            //noAccountAltDlg.show();
        }
        Log.d("usernameNOemail", "username = " + UserName);

        /*************************初始化""Gmail資料庫""並取出收件者地址*********************************************/

        mtxtYourScore.setText("您目前的積分為: " + currentScore);
        Log.d("totalScore", "目前積分 = " + String.valueOf(currentScore));


        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.MyName = UserName;
        //myThread.MyScore=Integer.parseInt(ed2.getText().toString());
        myThread.MyScore = String.valueOf(currentScore);
        //myThread.Url=ed3.getText().toString();
        myThread.Url = "http://nobile.mgt.ncu.edu.tw/testW3/combine.php";
        //開始執行緒
        myThread.start();

    }

    public void findViews() {
        btStat = (ImageView) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission = (ImageView) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting = (ImageView) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        /*btLeaderboard = (ImageButton) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);*/

    }

    /*********************
     * 按鈕事件
     ************************/
    private ImageView.OnClickListener btStatClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(ELeaderboardActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(ELeaderboardActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btSettingClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(ELeaderboardActivity.this, CSettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(ELeaderboardActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    /*private ImageButton.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            finish();
        }
    };*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //必須利用Handler來改變主執行緒的UI值
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String StrName = "挑戰者";
            String StrScore = "分數";
            String StrRanking = "名次";
            int ranking = 1;
            //msg.getData會傳回Bundle，Bundle類別可以由getString(<KEY_NAME>)取出指定KEY_NAME的值
            String StrLeaderBoard = msg.getData().getString("response");
            Log.d("NameScoreList",StrLeaderBoard);
            String[] nameArray = StrLeaderBoard.split("     ");
            //用五個空格切，可以得到"名字"+"分數與日期"
            for (int i = 1; i < nameArray.length-1; i = i+2) {
                //i奇數=名字，i偶數=i-1這個人的分數

                String score[] = nameArray[i+1].split("   ");
                //把剛剛拿到的"分數與日期"切成只有分數，每個分數都會存在score[0]

                StrRanking += "\n" + ranking++;
                StrName += "\n" + nameArray[i];
                StrScore += "\n" + score[0];
            }
           /* for (Member NS : NameScoreList) {
                Log.d("NameScoreList", "Name = " + NS.getName() + " Score = " + NS.getTime());
                StrRanking += "\n" + ranking++;
                StrName += "\n" + NS.getName();
                StrScore += "\n" + NS.getTime();
            }*/

            mtxtRanking.setText(StrRanking);
            mtxtScore.setText(StrScore);
            mtxtName.setText(StrName);
        }
    };

    //按鈕的Click事件
    /*public void btn_onClick(View v) {
        //產生新的HttpThread物件
        HttpThread myThread = new HttpThread();
        //設定變數值
        myThread.MyName = UserName;
        //myThread.MyScore=Integer.parseInt(ed2.getText().toString());
        myThread.MyScore = String.valueOf(score);
        //myThread.Url=ed3.getText().toString();
        myThread.Url = "http://nobile.mgt.ncu.edu.tw/testW3/combine.php";
        //開始執行緒
        myThread.start();
    }*/


    //宣告一個新的類別並擴充Thread
    class HttpThread extends Thread {

        //宣告變數並指定預設值
        public String MyName = "N/A";
        public String MyScore = "0";
        public String Url;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            //宣告一個新的Bundle物件，Bundle可以在多個執行緒之間傳遞訊息
            Bundle myBundle = new Bundle();

            try {
                HttpClient client = new DefaultHttpClient();
                URI website = new URI(this.Url);

                //指定POST模式
                HttpPost request = new HttpPost();

                //POST傳值必須將key、值加入List<NameValuePair>
                List<NameValuePair> parmas = new ArrayList<NameValuePair>();

                //逐一增加POST所需的Key、值
                parmas.add(new BasicNameValuePair("MyName", this.MyName));
                parmas.add(new BasicNameValuePair("MyScore", this.MyScore));
                //parmas.add(new BasicNameValuePair("MyMail","leoqaz12@yahoo.com.tw"));

                //宣告UrlEncodedFormEntity來編碼POST，指定使用UTF-8
                UrlEncodedFormEntity env = new UrlEncodedFormEntity(parmas, HTTP.UTF_8);
                request.setURI(website);

                //設定POST的List
                request.setEntity(env);

                HttpResponse response = client.execute(request);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    myBundle.putString("response", EntityUtils.toString(resEntity));
                } else {
                    myBundle.putString("response", "Nothing");
                }

                Message msg = new Message();
                msg.setData(myBundle);
                mHandler.sendMessage(msg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//END CLASS

    private void setAlertDialog() {
        noAccountAltDlg = new AlertDialog.Builder(ELeaderboardActivity.this);
        LayoutInflater inflaterr = (ELeaderboardActivity.this).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style1_alert_dialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("找不到Gmail名稱");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("請先至設定頁面輸入\n\n1.您的Gmail帳號密碼\n2.好友的GMAIL位址\n\n才可以使用排行榜功能");

        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setPositiveButton("前往「設定」", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(ELeaderboardActivity.this, CSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(ELeaderboardActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /********************
     * listView典籍處理Adapter
     *************************/
    private class MemberAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Member> memberList;

        public MemberAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);

            memberList = new ArrayList<>();
            for (Member NS : NameScoreList)
                memberList.add(new Member(NS.getName(), NS.getTime()));

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


            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            tvName.setText(member.getName());

            TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            tvTime.setText(member.getTime());
            return convertView;
        }
    }
}

