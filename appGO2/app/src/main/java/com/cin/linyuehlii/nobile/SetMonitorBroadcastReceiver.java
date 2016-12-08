package com.cin.linyuehlii.nobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Shark on 2016/7/28.
 */
public class SetMonitorBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Toast.makeText(context,"收到設定的監控APP",Toast.LENGTH_LONG).show();
    }
}

