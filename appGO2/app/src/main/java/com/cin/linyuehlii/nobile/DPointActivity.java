package com.cin.linyuehlii.nobile;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class DPointActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private ImageView btStat, btPoint, btMission, btSetting, btLeaderboard;
    private FragmentManager manager = getSupportFragmentManager();
    private com.google.api.services.gmail.Gmail mService = null;
    GoogleAccountCredential mCredential;
    private TextView mOutputText;
    private String OutputText;
    ProgressDialog mProgress;
    private ImageButton Fbt, Lbt;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    int EmailSize;
    String EmailId;
    String EmailAddress;
    String EmailSubject;
    String EmailSnippet;
    ArrayList<Item> emailItem = new ArrayList<Item>();
    ArrayList<Item> checkItem = new ArrayList<Item>();
    public static DPointActivity INSTANCE;
    FileOutputStream fileOut = null;
    ObjectOutputStream objectOut = null;
    FileInputStream fileIn = null;
    ObjectInputStream objectIn = null;
    boolean isMailContain = false;
    AlertDialog.Builder noAccountAltDlg;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {
            GmailScopes.GMAIL_LABELS,
            GmailScopes.GMAIL_MODIFY,
            GmailScopes.GMAIL_READONLY,
            GmailScopes.MAIL_GOOGLE_COM};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_point_ac);
        INSTANCE = this;

        Intent serviceIntent = new Intent(DPointActivity.this, MyService.class);
        startService(serviceIntent);

        /*************************************從檔案讀取點過的信件********************************************/


        try {

            fileIn = openFileInput("mail.tmp");
            objectIn = new ObjectInputStream(fileIn);
            checkItem = (ArrayList<Item>) (objectIn.readObject());
            objectIn.close();

            Log.d("file", "mail.tmp 讀取成功");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            Log.d("file", "fileIn FNFE mail.tmp 不存在");

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.d("file", "fileIn IOE mail.tmp 讀取失敗");

        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            Log.d("file", "fileIn CNFE mail.tmp 讀取失敗");
        }

        for (Item item : checkItem) {
            Log.d("emailID", "Loaded ID = " + item.getId());
        }
        Log.d("emailID", "目前積分 = " + String.valueOf(checkItem.size()));

        /*************************************從檔案讀取點過的信件********************************************/

        if (!MyService.isGmailSetted) {
            setAlertDialog();
            noAccountAltDlg.show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);//匯入title_bar
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);
        Lbt = (ImageButton) toolbar.findViewById(R.id.Lbt);
        Fbt.setVisibility(View.GONE);
        Lbt.setVisibility(View.GONE);
        mTitle.setText("查看回信");
        ImageButton Fbt = (ImageButton) toolbar.findViewById(R.id.Fbt);

        btPoint = (ImageView) findViewById(R.id.btPoint);
        Drawable highlight = getResources().getDrawable(R.drawable.bt_highlight);
        btPoint.setBackground(highlight);

        findViews();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        init();
        getResultsFromApi();


    }


    public void findViews() {
        btStat = (ImageView) findViewById(R.id.btStat);
        btStat.setOnClickListener(btStatClick);

        btMission = (ImageView) findViewById(R.id.btMission);
        btMission.setOnClickListener(btMissionClick);

        btSetting = (ImageView) findViewById(R.id.btSetting);
        btSetting.setOnClickListener(btSettingClick);

        //btPoint = (ImageButton) findViewById(R.id.btPoint);
        //btPoint.setOnClickListener(btPointClick);

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
            intent.setClass(DPointActivity.this, AStatActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btMissionClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(DPointActivity.this, BMissionActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    private ImageView.OnClickListener btSettingClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(DPointActivity.this, CSettingActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };
    /*private ImageButton.OnClickListener btPointClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(CSettingActivity.this, DPointActivity.class);
            startActivity(intent);
            finish();
        }
    };*/
    private ImageView.OnClickListener btLeaderboardClick = new ImageButton.OnClickListener() {
        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent();
            intent.setClass(DPointActivity.this, ELeaderboardActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
            finish();
        }
    };


    private void init() {

        mOutputText = (TextView) findViewById(R.id.output);
        mOutputText.setMovementMethod(ScrollingMovementMethod.getInstance());

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        // Initializing Progress Dialog
        mProgress = new ProgressDialog(this);
        mProgress.setMessage("正在從Gmail讀取信件...");
        mProgress.setCancelable(false);

    }

    public void listPrint() {
        //執行的地方在onPostExecute()
        //listview呼叫setadapter並套用建立好的memberadapter
        final ListView lvMember = (ListView) findViewById(R.id.lvMember);
        lvMember.setAdapter(new MemberAdapter(DPointActivity.this));
        //arrayadapter:下拉式選單、autocompletely，很多要給人選都會需要adapter；memberadapter項目很多
        //parent:被點擊的listview、view:被點擊的選項載入的layout內容->listview_item.xml內的linearlayout元件
        // position:被點擊的索引位置、id:實作baseadapter.getItemId所回傳的id
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { //將member的動作黏回listview
                //Member member = (Member) parent.getItemAtPosition(position);
                // 呼叫getItemAtPosition會取得baseadapter.getItemId回傳的物件，此為會員物件呈現在toast上
                view.setVisibility(View.INVISIBLE);
                Toast.makeText(DPointActivity.this, "積分 + 5", Toast.LENGTH_SHORT).show();
                //告知哪一個被點選到；Toast 是在畫面上面直接跳出訊息的方式，
                // 一般的用法為Toast.makeText(目標,訊息內容,訊息格式).show();
                // 另外LENGTH_SHORT Show the view or text notification for a short period of time

                SharedPreferences ScorePref = getSharedPreferences("totalScore", MODE_PRIVATE);
                SharedPreferences.Editor editor = ScorePref.edit();
                int currentScore = ScorePref.getInt("KEY_INT", 0);
                currentScore = currentScore + 5;
                editor.putInt("KEY_INT", currentScore);
                editor.commit();
                Log.d("totalScore","收了一封信，目前積分: " + currentScore);
                checkItem.add(emailItem.get(position));

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*******************把選過的信存成檔案***************/

        try {
            fileOut = openFileOutput("mail.tmp", Context.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            if (checkItem.size() > 0)
                objectOut.writeObject(checkItem);

            Log.d("file", "存入信件成功");

        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d("file", "fileOut ioe");

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.d("file", "fileOut ex");
        }

        /*************************把選過的信存成檔案***************************/
    }

    //Listview要用到的客製化部分
    private class MemberAdapter extends BaseAdapter { //自己設定adapter
        private LayoutInflater layoutInflater;

        //List<Item> itemList;
        //Pass an context parameter to constructor of your adapter and save it as a member
        public MemberAdapter(Context context) { //memberadapter的建構子，每一行都加一個member；回答是第幾號(id)，認getid
            Log.d("emailID", " public MemberAdapter(Context context)");
            layoutInflater = LayoutInflater.from(context); //跟context連結在一起，即memberadapter；取得layoutinflater物件以便動態載入layout檔案供選項列使用
        }

        @Override
        public int getCount() {
            Log.d("emailID", "  getCount() = " + EmailSize);
            return EmailSize;
        } //提供選項列總數；系統會依照回傳值決定呼叫getview的次數 //itemList.size()

        @Override
        public Object getItem(int position) {
            return null;
        } //依據索引位置(position)提供該選項列對應的物件，這裡提供Member物件 //itemList.get(position)

        @Override //上級要求的，superclass:baseadapter
        public long getItemId(int position) {
            return 0;
        } //依據索引位置(position)提供該選項列對應的ID //itemList.get(position).getId()

        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) { //空的準備秀出東西
                convertView = layoutInflater.inflate(R.layout.listview_item, parent, false);
                //每一行使用了一個layout檔，每一個layout檔只規範一個小框框
            } //產生一個畫面(原本都是用oncreate)inflat一個layout ；依據索引位置(position)提供該選項列對應的View


            Item item = emailItem.get(position);

            TextView tvName = (TextView) convertView.findViewById(R.id.tvAddress);
            tvName.setText(item.getAddress());

            TextView tvSub = (TextView) convertView.findViewById(R.id.tvSub);
            tvSub.setText(item.getSub());

            TextView tvSnip = (TextView) convertView.findViewById(R.id.tvSnippet);
            tvSnip.setText(item.getSnip());

            Log.d("EmailID", "item.getAddress() = " + item.getAddress());
            Log.d("EmailID", "item.getSub() = " + item.getSub());
            Log.d("EmailID", "item.getSnip() = " + item.getSnip());

            return convertView; //東西輸入後就return；在此範例回傳convertview就是回傳linearlayout(參看listview_item.xml)
        }
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            mOutputText.setText("No network connection available.");
        } else {
            new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog

            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account.",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    Manifest.permission.GET_ACCOUNTS);


        }// (via Contacts)
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    mOutputText.setText(
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode))
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);

    }

    void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                DPointActivity.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }


    class MakeRequestTask extends AsyncTask<Void, Void, List<Message>> {
        //private com.google.api.services.gmail.Gmail mService = null;
        private Exception mLastError = null;

        public MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Gmail API Android FetchEmail")
                    .build();
        }

        @Override
        protected List<Message> doInBackground(Void... params) {
            try {
                //List<Message> print = new ArrayList<Message>();
                //print.addAll(listMessagesMatchingQuery(mService, "me", "subject:(from手機戒癮726)"));
                return listMessagesMatchingQuery(mService, "me", "subject:(Re: 【手機戒癮】-「)");
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        public List<Message> listMessagesMatchingQuery(Gmail service, String userId, String query) throws IOException {

            ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();
            List<Message> messages = new ArrayList<Message>();
            while (response.getMessages() != null) {
                messages.addAll(response.getMessages());
                if (response.getNextPageToken() != null) {
                    String pageToken = response.getNextPageToken();
                    response = service.users().messages().list(userId).setQ(query)
                            .setPageToken(pageToken).execute();
                } else {
                    break;
                }
            }


            for (Message message : messages) {
                isMailContain = false;
                Log.d("Message Detail", "toPrettyString()  = " + message.toPrettyString());
                Log.d("Message Detail", "getId()           = " + message.getId());
                System.out.println(message.getInternalDate());
                Log.d("Message Detail", "getInternalDate() = " + String.valueOf(message.getInternalDate()));

                EmailId = message.getId();
                EmailAddress = getMessageAddress(mService, "me", message.getId());
                EmailSubject = getMessageSubject(mService, "me", message.getId());
                EmailSnippet = getMessageSnippet(mService, "me", message.getId());
                String[] SnippetArr = EmailSnippet.split("&lt;", 2); //把原文切掉，只留回復的文字
                EmailSnippet = SnippetArr[0];
                Item item = new Item(EmailId, EmailAddress, EmailSubject, EmailSnippet);

                for (Item check : checkItem) { // 如果這封信已經被點過
                    if (EmailId.equals(check.getId())) {
                        Log.d("emailID", "checkItem already contains = " + EmailId);
                        isMailContain = true;
                    }
                }
                Log.d("emailID", "isMailContain = " + String.valueOf(isMailContain));
                if (!isMailContain) {
                    Log.d("emailID", "New Email ID = " + EmailId);
                    emailItem.add(item);
                }

                //OutputText += "標題: " + EmailSubject + "\n" + "內文: " + EmailSnippet + "\n\n";
            }
            EmailSize = emailItem.size();
            Log.d("emailID", "EmailSize = " + EmailSize);
            List idFetch = new ArrayList();
            for (Message idMessage : messages) {
                idFetch.add(idMessage.getId());
            }

            return idFetch; //將原本包含threadID跟ID的List，印出只有ID的List
        }  // end listMessagesMatchingQuery

        //把listMessagesMatchingQuery()印出的List轉成String型態的陣列，可以放入參數(第三個參數要求為string型態，才出此計策，不然原本的是Message型態的List)
        /*public String[] idGroup() throws IOException {
            List push = new ArrayList();
            push.add(this.listMessagesMatchingQuery(mService, "me", "subject:(from手機戒癮726)"));
            String[] totalId = (String[]) push.toArray(new String[push.size()]);
            return totalId;
        }*/

        //在listview裡預期也有寄件者的欄位，因為每次只需要一個值(實際也只印出一個值，因為參數messageId只能放一個)，我就把List型態改成String
        public String getMessageAddress(Gmail service, String userId, String messageId)
                throws IOException {
            service.users().messages().get(userId, messageId).execute();
            String text = "寄件者: " + findAddress(mService, "me", messageId);
            //String snippet = message.toString();
            //List<String> response = new ArrayList<String>();
            //if (snippet != null)
            //    response.add(text);
            //System.out.println("Message snippet: " + message.getSnippet());
            return text;
        }

        //抓取並印出內文的方法，改成String型態的理由同Address
        public String getMessageSnippet(Gmail service, String userId, String messageId)
                throws IOException {
            Message message = service.users().messages().get(userId, messageId).execute();
            String text = "信件內文: " + message.getSnippet();
            //String snippet = message.toString();
            //List<String> response = new ArrayList<String>();
            //if (snippet != null)
            //    response.add(text);
            return text;
        }

        //印出主旨的方法，改成String型態的理由同Address
        public String getMessageSubject(Gmail service, String userId, String messageId)
                throws IOException {
            service.users().messages().get(userId, messageId).execute();
            String text = "信件標題: " + findSubject(mService, "me", messageId);
            //String snippet = message.toString();
            //List<String> response = new ArrayList<String>();
            //if (snippet != null)
            //    response.add(text);
            //System.out.println("Message snippet: " + message.getSnippet());
            return text;
        }

        //抓出主旨的方法，因為messageId的限制，一樣一次只能抓一個值就改成String型態
        public String findSubject(Gmail service, String userId, String messageId) throws IOException {
            Message message = service.users().messages().get(userId, messageId).execute();
            String value = "";
            //List<String> value = new ArrayList<String>();
            //List<MessagePart> parts = message.getPayload().getParts();
            List<MessagePartHeader> headers = message.getPayload().getHeaders();
            for (MessagePartHeader header : headers) {
                String name = header.getName();
                if (name.equals("Subject") || name.equals("subject")) {
                    value = header.getValue();
                    break;
                }
            }
            return value;
        }

        //抓出寄件者名稱的方法，String型態理由同findSubject
        public String findAddress(Gmail service, String userId, String messageId) throws IOException {
            Message message = service.users().messages().get(userId, messageId).execute();
            String valueFrom = new String();
            //List<String> valueFrom = new ArrayList<String>();
            List<MessagePartHeader> headers = message.getPayload().getHeaders();
            for (MessagePartHeader header : headers) {
                String from = header.getName();
                if (from.equals("From") || from.equals("from")) {
                    valueFrom = header.getValue();
                    break;
                }
            }
            return valueFrom;
        }

        //紀錄時間的方法，但尚未完成(>3<|||)；真的不行的話，我們就先捨棄排行榜然後就只呈現有回應的信件吧XDD
        /*public void findInternalDate(Gmail service, String userId, String messageId) throws IOException {
            Message message = service.users().messages().get(userId, messageId).execute();
            long time = message.getInternalDate();
            List<String> value = new ArrayList<String>();
        }*/

        @Override
        protected void onPreExecute() {
            mOutputText.setText("");
            mProgress.show();
        }

        @Override
        protected void onPostExecute(List output) {
            mProgress.dismiss();
            Log.d("emailID", "List output size = " + output.size());
            if (output == null || output.size() == 0) {
                mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the Gmail API:");
                mOutputText.setText(OutputText);
                mOutputText.setText(TextUtils.join("\n", output));
                displayText();
                listPrint();
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.dismiss();
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            DPointActivity.REQUEST_AUTHORIZATION);
                } else {
                    mOutputText.setText("目前還沒有來自好友的回覆哦!");
                    //mOutputText.setText("The following error occurred:\n" + mLastError.getMessage());
                }
            } else {
                mOutputText.setText("Request cancelled.");
            }
        }
    }

    public void displayText() {
        mOutputText.setText(OutputText);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回鍵
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            startActivity(new Intent(DPointActivity.this, StartActivity.class));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void setAlertDialog() {
        noAccountAltDlg = new AlertDialog.Builder(DPointActivity.this);
        LayoutInflater inflaterr = (DPointActivity.this).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style1_alert_dialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("找不到Gmail名稱");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("請先至設定頁面輸入\n\n1.您的Gmail帳號密碼\n2.好友的GMAIL位址\n\n才可以使用查看回信功能");
        noAccountAltDlg.setIcon(android.R.drawable.ic_dialog_alert);
        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setPositiveButton("前往「設定」", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(DPointActivity.this, CSettingActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
