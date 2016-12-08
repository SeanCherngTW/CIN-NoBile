package com.cin.linyuehlii.nobile;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import SendGmail.SQLiteDataHelper;

public class CSettingActivity extends AppCompatActivity {
    /*^^^^^^^^^^^^按鈕設定^^^^^^^^^^^^^*/
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    private ImageButton Fbt, Lbt;
    private Button button;

    /*^^^^^^^^^^^^^資料庫^^^^^^^^^^^^*/
    private String Receivers[] = new String[3];
    private String UserName, Password, PasswordCheck;
    private static boolean newEmail = true;
    private static boolean newUser = true;
    private static final String DB_FILE = "emailData.db", DB_TABLE = "EmailData";
    private SQLiteDatabase mReceiverDB;
    private static final String COL_id = "_id";
    private static final String COL_receiver1 = "receiver1";
    private static final String COL_receiver2 = "receiver2";
    private static final String COL_receiver3 = "receiver3";
    private static final String COL_user_name = "username";
    private static final String COL_password = "password";
    private TextView barTitle;
    final String PREFS_NAME = "Security";
    public static CSettingActivity INSTANCE;
    AlertDialog.Builder noAccountAltDlg;


    /***********************************************************/
    /***************************設定資料庫***********************/
    /***********************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_setting_ac);
        INSTANCE = this;

        Intent serviceIntent = new Intent(CSettingActivity.this, MyService.class);
        startService(serviceIntent);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        barTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        barTitle.setText("設定");
        Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);
        Lbt = (ImageButton) toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);
        button = (Button) findViewById(R.id.button123);
        button.setVisibility(Button.GONE);

        btSetting = (ImageView) findViewById(R.id.btSetting);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btSetting.setBackground(highlight);

        findViews();


        /*****************************取得來自Service的使用者提示***********************************/
        try {
            Intent intent = getIntent();
            String SetGmail = intent.getStringExtra("setGmail");
            if (SetGmail.equals("goSet")) {
                showAlertDialog();
                noAccountAltDlg.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /*****************************取得來自Service的使用者提示***********************************/

        /*****************************設定資料庫**************************/

        SQLiteDataHelper helper = new SQLiteDataHelper(getApplicationContext(), DB_FILE, null, 1);
        mReceiverDB = helper.getWritableDatabase();
        //建立資料庫

        Cursor cursor = mReceiverDB.rawQuery
                ("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + DB_TABLE + "'", null);

        if (cursor != null) {
            if (cursor.getCount() == 0)
                mReceiverDB.execSQL("CREATE TABLE " + DB_TABLE + " ( " +
                                            COL_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            COL_user_name + " TEXT, " +
                                            COL_password + " TEXT, " +
                                            COL_receiver1 + " TEXT, " +
                                            COL_receiver2 + " TEXT, " +
                                            COL_receiver3 + " TEXT);");

            cursor.close();
        }
        //檢查資料庫有沒有存在，若無，則建立一個
        /*****************************設定資料庫*************************/

        ListView lvMember = (ListView) findViewById(R.id.lvMember);
        lvMember.setAdapter(new MemberAdapter(this));
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Member member = (Member) parent.getItemAtPosition(position);

                switch (member.getId()) {
                    /*********************************************/
                    /*******************信箱密碼*******************/
                    /*********************************************/
                    case 90://email

                        AlertDialog.Builder builderr = new AlertDialog.Builder(CSettingActivity.this);
                        LayoutInflater inflaterr = (CSettingActivity.this).getLayoutInflater();
                        View gmail = inflaterr.inflate(R.layout.c_alert_2, null);
                        builderr.setView(gmail);

                        final EditText email = (EditText) gmail.findViewById(R.id.gmail);
                        final EditText code = (EditText) gmail.findViewById(R.id.code);

                        code.setEllipsize(TextUtils.TruncateAt.END);
                        code.setSingleLine();
                        code.setTransformationMethod(PasswordTransformationMethod.getInstance());

                        final EditText codeCheck = (EditText) gmail.findViewById(R.id.codeCheck);
                        codeCheck.setEllipsize(TextUtils.TruncateAt.END);
                        codeCheck.setSingleLine();
                        codeCheck.setTransformationMethod(PasswordTransformationMethod.getInstance());

                        if (isSetUserName()) {
                            email.setText(UserName);
                            code.setText(Password);
                            codeCheck.setText(Password);
                        }


                        //   EditText editText = (EditText) dialogView.findViewById(R.id.label_field);
                        //editText.setText("test label");

                        // alertMyGmail.setView(lay0);
                        builderr.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                UserName = email.getText().toString();
                                Password = code.getText().toString();
                                PasswordCheck = codeCheck.getText().toString();
                                if (!Password.equals(PasswordCheck))
                                    showToast("兩次輸入的密碼不一樣哦!");
                                else {
                                    if (Password.equals(""))
                                        showToast("請輸入密碼!");
                                }

                                if (UserName.equals("") || !isValidGmail(UserName))
                                    showToast("Gmail位址好像錯了喔!");

                                if (Password.equals(PasswordCheck) && isValidGmail(UserName)
                                        && !UserName.equals("") && !Password.equals(""))
                                    showToast("Gmail位址與密碼設定成功!");

                                ContentValues newRow = new ContentValues();
                                newRow.put(COL_user_name, UserName);
                                newRow.put(COL_password, Password);

                                if (newUser) {
                                    mReceiverDB.insert(DB_TABLE, null, newRow);
                                    newUser = false;
                                } else
                                    mReceiverDB.update(DB_TABLE, newRow, null, null);


                                /***重新啟動偵測服務!**/
                                Intent serviceIntent = new Intent(CSettingActivity.this, MyService.class);
                                stopService(serviceIntent);
                                startService(serviceIntent);
                                /***重新啟動偵測服務!**/

                            }
                        });


                        builderr.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builderr.create();
                        alertDialog.show();
                        alertDialog.getButton(alertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                        alertDialog.getButton(alertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK);


                        break;
                    /*********************************************/
                    /*******************好友信箱*******************/
                    /*********************************************/
                    case 91://好友gmail

                        AlertDialog.Builder alertFriendmail = new AlertDialog.Builder(CSettingActivity.this);
                        LayoutInflater inflaterrr = (CSettingActivity.this).getLayoutInflater();
                        View gmails = inflaterrr.inflate(R.layout.c_alert_1, null);
                        alertFriendmail.setView(gmails);
                        final EditText email1 = (EditText) gmails.findViewById(R.id.email1);
                        final EditText email2 = (EditText) gmails.findViewById(R.id.email2);
                        final EditText email3 = (EditText) gmails.findViewById(R.id.email3);

                        if (isSetEDTvalues()) {
                            email1.setText(Receivers[0]);
                            email2.setText(Receivers[1]);
                            email3.setText(Receivers[2]);
                        }


                        alertFriendmail.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Receivers[0] = email1.getText().toString();
                                Receivers[1] = email2.getText().toString();
                                Receivers[2] = email3.getText().toString();

                                ContentValues newRow = new ContentValues();
                                newRow.put(COL_receiver1, Receivers[0]);
                                newRow.put(COL_receiver2, Receivers[1]);
                                newRow.put(COL_receiver3, Receivers[2]);

                                if (newEmail) {
                                    mReceiverDB.insert(DB_TABLE, null, newRow);
                                    newEmail = false;
                                } else
                                    mReceiverDB.update(DB_TABLE, newRow, null, null);

                                Intent serviceIntent = new Intent(CSettingActivity.this, MyService.class);
                                stopService(serviceIntent);
                                startService(serviceIntent);
                                showToast("好友Email地址設定成功!");
                                //把使用者設定的好友email加到資料庫
                            }
                        });

                        alertFriendmail.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog alertFriendgmail = alertFriendmail.create();
                        alertFriendgmail.show();
                        alertFriendgmail.getButton(alertFriendgmail.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

                        alertFriendgmail.getButton(alertFriendgmail.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        break;
                    /*********************************************/
                    /*******************APP選擇********************/
                    /*********************************************/
                    case 92://選擇app
                        AlertDialog.Builder diaChooseAPP = new AlertDialog.Builder(CSettingActivity.this);
                        LayoutInflater chooseAppInflater = (CSettingActivity.this).getLayoutInflater();
                        View ViewChooseApp = chooseAppInflater.inflate(R.layout.style1_alert_dialog, null);
                        diaChooseAPP.setView(ViewChooseApp);
                        TextView tvTitle = (TextView) ViewChooseApp.findViewById(R.id.tvTitle);
                        tvTitle.setText("前往選擇APP頁面");
                        TextView t0 = (TextView) ViewChooseApp.findViewById(R.id.t0);
                        t0.setText("您確定要前往「選擇APP」頁面嗎?");
                        diaChooseAPP.setIcon(android.R.drawable.ic_dialog_alert);
                        diaChooseAPP.setCancelable(false);
                        diaChooseAPP.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                barTitle.setText("選擇APP & 設定單日使用上限");
                                Intent intent = new Intent();
                                intent.setClass(CSettingActivity.this, CChooseActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        diaChooseAPP.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog diaChooseAppShow = diaChooseAPP.create();
                        diaChooseAppShow.show();
                        diaChooseAppShow.getButton(diaChooseAppShow.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        diaChooseAppShow.getButton(diaChooseAppShow.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                        break;
                    case 93://改密碼
                        AlertDialog.Builder diaChangeCode = new AlertDialog.Builder(CSettingActivity.this);
                        LayoutInflater inflaterrrr = (CSettingActivity.this).getLayoutInflater();
                        View ViewChangeCode = inflaterrrr.inflate(R.layout.c_alert_3, null);
                        diaChangeCode.setView(ViewChangeCode);
                        final EditText mEdtOldCode = (EditText) ViewChangeCode.findViewById(R.id.edtOldCode);
                        final EditText mEdtNewCode = (EditText) ViewChangeCode.findViewById(R.id.edtNewCode);

                        diaChangeCode.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences CodePref = getSharedPreferences("Code", MODE_PRIVATE);
                                SharedPreferences.Editor editor = CodePref.edit();
                                int currentCode = CodePref.getInt("KEY_INT", 7777);
                                if (mEdtOldCode.getText().toString().equals(String.valueOf(currentCode))
                                        && !mEdtNewCode.getText().toString().equals("")) {
                                    currentCode = Integer.parseInt(mEdtNewCode.getText().toString());
                                    editor.putInt("KEY_INT", currentCode);
                                    editor.commit();
                                    showToast("家長監護密碼設定成功");
                                } else {
                                    showToast("舊家長監護密碼輸入錯誤!");
                                }
                            }
                        });

                        diaChangeCode.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog diaChangeCodeShow = diaChangeCode.create();
                        diaChangeCodeShow.show();
                        diaChangeCodeShow.getButton(diaChangeCodeShow.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        diaChangeCodeShow.getButton(diaChangeCodeShow.BUTTON_POSITIVE).setTextColor(Color.BLACK);

                        break;

                    case 94:
                        AlertDialog.Builder diaMonitorMode = new AlertDialog.Builder(CSettingActivity.this);
                        LayoutInflater monitorInflater = (CSettingActivity.this).getLayoutInflater();
                        View ViewMonitor = monitorInflater.inflate(R.layout.style_3_monitor_mode, null);
                        diaMonitorMode.setView(ViewMonitor);
                        final TextView mTxtTitle = (TextView) ViewMonitor.findViewById(R.id.tvTitle);
                        mTxtTitle.setText("是否開啟家長監護模式？");
                        final TextView mTxtGuide = (TextView) ViewMonitor.findViewById(R.id.t0);
                        mTxtGuide.setText("開啟家長監護模式會將所有設定功能以「家長監護密碼」加密，讓戒癮者沒有偷懶的餘地");
                        diaMonitorMode.setCancelable(false);
                        final SharedPreferences settings = getSharedPreferences("MonitorMode", 0);
                        diaMonitorMode.setPositiveButton("開啟監護模式", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("已開啟家長監護模式!");
                                settings.edit().putBoolean("MonitorMode", true).commit();
                            }
                        });
                        diaMonitorMode.setNegativeButton("關閉監護模式", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showToast("已關閉家長監護模式!");
                                settings.edit().putBoolean("MonitorMode", false).commit();
                            }
                        });
                        diaMonitorMode.setNeutralButton("什麼都不做", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog diaMonitorModeShow = diaMonitorMode.create();
                        diaMonitorModeShow.show();
                        diaMonitorModeShow.getButton(diaMonitorModeShow.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                        diaMonitorModeShow.getButton(diaMonitorModeShow.BUTTON_POSITIVE).setTextColor(Color.BLACK);
                        break;
                } // end switch

                SharedPreferences settings = getSharedPreferences("MonitorMode", 0);
                if (settings.getBoolean("MonitorMode", false)) {
                    showCodeDialog();
                    noAccountAltDlg.show();
                }


            } // end onItemClick
        });// end onItemClick

        /******************************新使用者引導********************************************/
        SharedPreferences settings = getSharedPreferences("Set_Once", 0);

        if (settings.getBoolean("First_Set", true)) {
            showFirstSetDialog();
            noAccountAltDlg.show();
            settings.edit().putBoolean("First_Set", false).commit();
        }
        /******************************新使用者引導********************************************/


        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    } // end onCreate

    protected void onDestroy() {
        super.onDestroy();
        mReceiverDB.close();
    }

    public boolean isSetEDTvalues() {
        Cursor c = mReceiverDB.query(true, DB_TABLE,
                                     new String[]{COL_user_name, COL_password, COL_receiver1, COL_receiver2, COL_receiver3},
                                     null, null, null, null, null, null);
        if (c == null)
            return false;

        if (c.getCount() == 0) {
            newEmail = true;
            Toast.makeText(CSettingActivity.this, "請設定好友Email !", Toast.LENGTH_LONG).show();
            return false;

        } else {
            newEmail = false;
            c.moveToFirst();
            UserName = c.getString(0);
            Password = c.getString(1);
            Receivers[0] = c.getString(2);
            Receivers[1] = c.getString(3);
            Receivers[2] = c.getString(4);
            return true;
        }
    }//end IsSetEDTvalues

    public boolean isSetUserName() {
        Cursor c = mReceiverDB.query(true, DB_TABLE,
                                     new String[]{COL_user_name, COL_password, COL_receiver1, COL_receiver2, COL_receiver3},
                                     null, null, null, null, null, null);
        if (c == null)
            return false;

        if (c.getCount() == 0) {
            newUser = true;
            Toast.makeText(CSettingActivity.this, "請輸入您的Gmail位址與密碼!", Toast.LENGTH_LONG).show();
            return false;

        } else {
            newUser = false;
            c.moveToFirst();
            UserName = c.getString(0);
            Password = c.getString(1);
            return true;
        }
    }//end isSetUserName


    private boolean isValidGmail(String email) {
        if (email.equals(""))
            return true;

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void showToast(String Subject) {
        Toast toast = Toast.makeText(getApplicationContext(), Subject, Toast.LENGTH_SHORT);
        toast.show();
    }

    /*public static boolean isValidEmail(String email) {
        //if (email.equals(""))
         //   return true;
        String emailPattern = "^([\\w]+)(([-\\.][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
                "(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        //String emailPattern = "^([\\w]+)(([-\\.][\\w]+)?)*@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
        //       "(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        return email.matches(emailPattern);

    }*/


    public void findViews() {
        btStat = (ImageView) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission = (ImageView) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        // btSetting=(ImageButton) findViewById(R.id.btSetting);
        //btSetting.setOnClickListener(btSettingClick);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        btPoint.setOnClickListener(btPointClick);

        btLeaderboard = (ImageView) findViewById(R.id.btLeaderboard);
        btLeaderboard.setOnClickListener(btLeaderboardClick);

    }

    /*********************
     * 按鈕事件
     ************************/
    private ImageView.OnClickListener btStatClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    /*  private ImageButton.OnClickListener btSettingClick = new ImageButton.OnClickListener(){
          @Override
          public void onClick(View arg0) {
              Intent intent=new Intent();
              intent.setClass(CSettingActivity.this,CSettingActivity.class);
              startActivity(intent);
              finish();
          }
      };*/
    private ImageView.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, DPointActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };

    /********************
     * listView典籍處理Adapter
     *************************/
    private class MemberAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Member> memberList;

        public MemberAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);

            memberList = new ArrayList<>();
            memberList.add(new Member(90, R.drawable.setting_gmail, "輸入您的Gmail帳號密碼", ""));
            memberList.add(new Member(91, R.drawable.setting_profile, "設定好友的收件信箱", ""));
            memberList.add(new Member(92, R.drawable.setting_app, "選擇APP & 設定單日使用上限", ""));
            memberList.add(new Member(93, R.drawable.setting_password, "變更家長監護密碼", ""));
            memberList.add(new Member(94, R.drawable.setting_password, "開啟/關閉家長監護模式", ""));
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
            ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            ivImage.setImageResource(member.getImage());

            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            tvName.setText(member.getName());

            TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            tvTime.setText(member.getTime());
            return convertView;
        }
    }

    /**********
     * 回到StartActivity，才能在設定完後執行MyService
     *************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(CSettingActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showAlertDialog() {

        noAccountAltDlg = new AlertDialog.Builder(CSettingActivity.this);
        LayoutInflater inflaterr = (CSettingActivity.this).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style1_alert_dialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("開啟自動寄信功能");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("為了更完整享受【手機戒癮】\n\n" +
                           "請您：\n" +
                           "1.輸入您的Gmail帳號密碼\n" +
                           "2.設定好友的收件信箱\n" +
                           "\n" +
                           "即可使用：\n" +
                           "1.超時自動寄信功能\n" +
                           "2.點擊「獎盃」圖示參與全球排名!\n" +
                           "3.點擊「信封」圖示查看好友的回饋!");

        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setIcon(android.R.drawable.ic_dialog_info);
        noAccountAltDlg.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    private void showFirstSetDialog() {
        noAccountAltDlg = new AlertDialog.Builder(CSettingActivity.this);
        LayoutInflater inflaterr = (CSettingActivity.this).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style1_alert_dialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("歡迎使用【手機戒癮】");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("請直接點擊\n「選擇APP & 設定單日使用上限」\n來選擇要戒癮的APP!");
        noAccountAltDlg.setIcon(android.R.drawable.ic_dialog_info);
        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void showCodeDialog() {
        noAccountAltDlg = new AlertDialog.Builder(CSettingActivity.this);
        LayoutInflater inflaterr = (CSettingActivity.this).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style_2_codedialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("密碼確認");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("請輸入家長監護密碼(預設:7777)");
        final EditText mEdtEnterCode = (EditText) v.findViewById(R.id.edtEnterCode);
        noAccountAltDlg.setIcon(android.R.drawable.ic_dialog_info);
        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences CodePref = getSharedPreferences("Code", MODE_PRIVATE);
                SharedPreferences.Editor editor = CodePref.edit();
                int currentCode = CodePref.getInt("KEY_INT", 7777);
                if (mEdtEnterCode.getText().toString().equals(String.valueOf(currentCode)))
                    dialog.dismiss();
                else {
                    Toast.makeText(CSettingActivity.this, "密碼錯誤!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(CSettingActivity.this, StartActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}// end class
