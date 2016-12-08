package com.cin.linyuehlii.nobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 大芳鎖定你 on 2016/7/22.
 */
public class CSetAppTimeFrag extends Fragment {

    private List<Member> memberList;
    private ViewPager vpMember;
    private FragmentManager manager = getFragmentManager();
    private ImageButton Fbt, Lbt;
    private SeekBar mSeekBar;
    private TextView mTxtSeekBarProgress;
    private Dialog mAppTimePickerDlg;
    private ImageView mImgAppIcon;
    AlertDialog.Builder noAccountAltDlg;

    int hour, minute, seekBarProgress;
    private Boolean valueupdate = false;
    String TimeFormatted;
    private ListView lvMember;
    private int po;
    private ArrayList<Member> aList;
    private String a;

    FileOutputStream fileOut = null;
    ObjectOutputStream objectOut = null;
    private Button button;
    private NewHelper helperrr;


    /********************************************/
    /*****************主程式開始******************/
    /********************************************/
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (LinearLayout) inflater.inflate(R.layout.a_fragment_applist, container, false);
        lvMember = (ListView) view.findViewById(R.id.applist);

        /********************介面***********************/
        Fbt = (ImageButton) getActivity().findViewById(R.id.Fbt);
        Lbt = (ImageButton) getActivity().findViewById(R.id.Lbt);
        Fbt.setVisibility(ImageButton.VISIBLE);
        Fbt.setBackgroundResource(R.mipmap.bt_back);
        Lbt.setVisibility(ImageButton.VISIBLE);
        Lbt.setBackgroundResource(R.mipmap.bt_ok);
        button = (Button) getActivity().findViewById(R.id.button123);
        button.setVisibility(Button.VISIBLE);

        if (helperrr == null) {//資料庫準備
            helperrr = new NewHelper(getActivity());
        }

        /********************資料接收************************/
        Bundle i = getArguments();
        aList = i.getParcelableArrayList("appChoosedList");


        /***********************************/
        /**************設定完成**************/
        /***********************************/
        Lbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*******************把選好APP的存成檔案***************/

                try {
                    setAlertDialog();
                    noAccountAltDlg.show();
                    fileOut = getActivity().openFileOutput("t.tmp", Context.MODE_PRIVATE);
                    objectOut = new ObjectOutputStream(fileOut);
                    if (memberList.size() > 0)
                        objectOut.writeObject(memberList);

                    Log.d("file", "存入成功");

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.d("file", "fileOut ioe");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("file", "fileOut ex");
                }

                /*************************把選好APP的存成檔案***************************/

                /****將資料存入"NewHelper"SQL***/
                SQLiteDatabase db = helperrr.getWritableDatabase();
                db.delete("spoterr", null, null);
                Spoterr spoterr = null;

                for (int i = 0; i < memberList.size(); i++) {
                    spoterr = new Spoterr(memberList.get(i).getImage(),
                                          memberList.get(i).getName(),
                                          memberList.get(i).getTime());
                    long rowId = helperrr.insert(spoterr);

                }
            }
        });

        Fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager;
                manager = getActivity().getSupportFragmentManager();
                FragmentTransaction gg = manager.beginTransaction();
                CChooseAppFrag ty = new CChooseAppFrag();
                gg.replace(R.id.frag, ty);
                gg.commit();
            }
        });


        //清除全部
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memberList.clear();

                /***重新啟動偵測服務!**/
                Intent serviceIntent = new Intent(getActivity(), com.cin.linyuehlii.nobile.MyService.class);
                getActivity().startService(serviceIntent);
                /***重新啟動偵測服務!**/

                /*******************把選好APP的存成檔案***************/

                try {
                    Toast.makeText(getActivity(), "清除成功!", Toast.LENGTH_SHORT).show();
                    fileOut = getActivity().openFileOutput("t.tmp", Context.MODE_PRIVATE);
                    objectOut = new ObjectOutputStream(fileOut);
                    if (memberList.size() > 0)
                        objectOut.writeObject(memberList);

                    Log.d("file", "存入成功");

                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.d("file", "fileOut ioe");

                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.d("file", "fileOut ex");
                }

                /*************************把選好APP的存成檔案***************************/
                lvMember.invalidateViews();
            }
        });

        /********ListView Adapter**********/
        lvMember.setAdapter(new MemberAdapter(getActivity()));
        lvMember.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Member member = (Member) parent.getItemAtPosition(position);
                po = member.getId();
                setAppTimePickerDlg();
                mAppTimePickerDlg.show();

            }//end onItemClick
        });
        return view;
    }

    public void setAppTimePickerDlg() {
        mAppTimePickerDlg = new Dialog(this.getActivity());
        mAppTimePickerDlg.setTitle("請設定單日使用時間上限");
        mAppTimePickerDlg.setCancelable(false);
        mAppTimePickerDlg.setContentView(R.layout.c_app_time_picker);

        TimeFormatted = memberList.get(po).getTime();
        Button mBtnPickTimeSubmit = (Button) mAppTimePickerDlg.findViewById(R.id.btnPickTimeSubmit);
        mBtnPickTimeSubmit.setOnClickListener(mAppTimePickerDlgOkOnClick);
        mSeekBar = (SeekBar) mAppTimePickerDlg.findViewById(R.id.seekBar);//DIALOG的滑動條
        mSeekBar.setProgress(memberList.get(po).getSeekBarProgress());
        mSeekBar.setOnSeekBarChangeListener(seekBarOnChange);//dialog的拖曳滑動條listener
        mTxtSeekBarProgress = (TextView) mAppTimePickerDlg.findViewById(R.id.txtSeekBarProgress);//dialog的滑動條動態文字
        mTxtSeekBarProgress.setText(memberList.get(po).getTime());
        mImgAppIcon = (ImageView) mAppTimePickerDlg.findViewById(R.id.imgAppIcon);
        try {
            Drawable appIcon = getActivity().getPackageManager().getApplicationIcon(memberList.get(po).getPackageName());
            mImgAppIcon.setImageDrawable(appIcon);
            mImgAppIcon.getLayoutParams().height = 128;
            mImgAppIcon.getLayoutParams().width = 128;
            mImgAppIcon.requestLayout();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarOnChange = new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            //progress = 拉動的值

            hour = progress / 60;
            minute = progress % 60;
            if (minute < 10)
                TimeFormatted = hour + " 小時 0" + minute + " 分鐘 ";
            else
                TimeFormatted = hour + " 小時 " + minute + " 分鐘 ";
            seekBarProgress = progress;
            mTxtSeekBarProgress.setText(TimeFormatted);
            memberList.get(po).setSeekBarProgress(progress);

        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    //DIALOG內的確認按鈕
    View.OnClickListener mAppTimePickerDlgOkOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            memberList.get(po).setTime(TimeFormatted);
            lvMember.invalidateViews();
            TimeFormatted = memberList.get(po).getTime();
            mAppTimePickerDlg.cancel();
            valueupdate = true;
        }
    };


    /**************************************************************************/


    private class MemberAdapter extends BaseAdapter {//加入member
        private LayoutInflater layoutInflater;

        private MySQLiteOpenHelper helper = new MySQLiteOpenHelper(getActivity());

        public MemberAdapter(Context context) {
            layoutInflater = LayoutInflater.from(context);

            memberList = new ArrayList<>();
            memberList = aList;
            //  memberList.add(new Member(2, R.mipmap.messenger, "Messenger", "90"));

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
                convertView = layoutInflater.inflate(R.layout.c_app_list_row, parent, false);
            }

            try {
                Member member = memberList.get(position);

                Drawable appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(member.getPackageName());
                ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
                ivImage.setImageDrawable(appIcon);
                //設ICON

                TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
                tvName.setText(member.getName());

                TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                tvTime.setText(member.getTime());


            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return convertView;
        }//end getView
    }

    private void setAlertDialog() {


        noAccountAltDlg = new AlertDialog.Builder(getActivity());
        LayoutInflater inflaterr = (getActivity()).getLayoutInflater();
        View v = inflaterr.inflate(R.layout.style1_alert_dialog, null);
        noAccountAltDlg.setView(v);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTitle.setText("設定完成");
        TextView t0 = (TextView) v.findViewById(R.id.t0);
        t0.setText("設定成功!\n已開始偵測使用時間\n請注意不要超時囉:)");

        noAccountAltDlg.setIcon(android.R.drawable.ic_dialog_info);
        noAccountAltDlg.setCancelable(false);
        noAccountAltDlg.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /*********************換頁***********************/
                Intent intent = new Intent(getActivity(), CSettingActivity.class);
                startActivity(intent);

                /***重新啟動偵測服務!**/
                Intent serviceIntent = new Intent(getActivity(), com.cin.linyuehlii.nobile.MyService.class);
                getActivity().startService(serviceIntent);
                /***重新啟動偵測服務!**/
                dialog.dismiss();
            }
        });
    }
}