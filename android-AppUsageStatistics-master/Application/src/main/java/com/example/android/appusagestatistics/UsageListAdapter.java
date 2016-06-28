/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.appusagestatistics;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Provide views to RecyclerView with the directory entries.
 */
public class UsageListAdapter extends RecyclerView.Adapter<UsageListAdapter.ViewHolder> {

    private List<CustomUsageStats> mCustomUsageStatsList = new ArrayList<>();
    // 把CustomUsage物件裝在mCustomUsageStatsList這個list之中
    private DateFormat mDateFormat = new SimpleDateFormat();
    // Formats or parses dates and times.
    // This class provides factories for obtaining instances configured for a specific locale.
    // The most common subclass is SimpleDateFormat.
    // http://developer.android.com/reference/java/text/SimpleDateFormat.html

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // 課本5-21
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.usage_row, viewGroup, false);
        return new ViewHolder(v);
        // onCreateViewHolder呼叫完會自動呼叫onBindViewHolder

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // onCreateViewHolder呼叫完會自動呼叫onBindViewHolder
        // 把要顯示的內容與值，套用在class ViewHolder管理的View上面
        // 所以才會傳入ViewHolder，以及現在想要編輯哪一個選項的資料(ex:0->第一筆資料，1->第二筆資料)

        String packageName = mCustomUsageStatsList.get(position).usageStats.getPackageName();
        long lastTimeUsed = mCustomUsageStatsList.get(position).usageStats.getLastTimeUsed();
        long totalUsedTime = mCustomUsageStatsList.get(position).usageStats.getTotalTimeInForeground();

        String totalUsedTimeFormatted = millisecondsToTimeFormat(totalUsedTime);
        //把毫秒轉為"HH:MM:SS"

        viewHolder.getPackageName().setText(packageName);
        //透過getPackageName()抓到儲存名稱的變數，然後用setText去抓出系統的名稱
        viewHolder.getLastTimeUsed().setText("上次使用日: " + mDateFormat.format(new Date(lastTimeUsed)));
        viewHolder.getTotalUsedTime().setText(totalUsedTimeFormatted);
        viewHolder.getAppIcon().setImageDrawable(mCustomUsageStatsList.get(position).appIcon);
        //把packageName、上次使用時間、Icon等資料設給viewHolder，也就是顯示在view上
    }

    public String millisecondsToTimeFormat(long milliseconds) {
        int s = (int) (milliseconds / 1000) % 60;
        int m = (int) ((milliseconds / (1000 * 60)) % 60);
        int h = (int) ((milliseconds / (1000 * 60 * 60)) % 24);

        return String.format("總使用時間: %d時%02d分%02d秒", h, m, s);
    }

    /*public String packageNameToAppName(String packageName) {
        Context context = new Context() {
        };
        PackageManager pm = context.getPackageManager();
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }*/

    @Override
    public int getItemCount() {
        return mCustomUsageStatsList.size();
    }

    public void setCustomUsageStatsList(List<CustomUsageStats> customUsageStats) {
        mCustomUsageStatsList = customUsageStats;
    }

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder是自定義UsageListAdapter的子類別
        // 官方類別RecyclerView的子類別ViewHolder
        private final TextView mPackageName;
        private final TextView mLastTimeUsed;
        private final TextView mTotalUsedTime;
        private final ImageView mAppIcon;

        public ViewHolder(View v) {
            super(v);
            mPackageName = (TextView) v.findViewById(R.id.textview_package_name);
            mLastTimeUsed = (TextView) v.findViewById(R.id.textview_last_time_used);
            mTotalUsedTime = (TextView) v.findViewById(R.id.textview_total_used_time);
            mAppIcon = (ImageView) v.findViewById(R.id.app_icon);
            // 設定參照給自己設定的變數，這是為了方便未來使用
        }

        public TextView getLastTimeUsed() {
            return mLastTimeUsed;
        }
        // 取得上次使用時間，回傳TextView(文字)

        public TextView getPackageName() {
            return mPackageName;
        }
        // 取得某一APP的Package name，用package name來監控APP的使用量，回傳TextView(文字)

        public TextView getTotalUsedTime() {
            return mTotalUsedTime;
        }
        // 取得總使用時間

        public ImageView getAppIcon() {
            return mAppIcon;
        }
        // 取得某一APP的圖示，回傳ImageView

    }
}