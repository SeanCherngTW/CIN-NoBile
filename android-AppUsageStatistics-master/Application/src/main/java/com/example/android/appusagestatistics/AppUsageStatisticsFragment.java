/*
* Copyright 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.appusagestatistics;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Fragment that demonstrates how to use App Usage Statistics API.
 */
public class AppUsageStatisticsFragment extends Fragment {

    private static final String TAG = AppUsageStatisticsFragment.class.getSimpleName();
    private final long MILLIS_OF_A_DAY = 86400000;
    private final long MILLIS_OF_A_WEEK = 604800000;

    // VisibleForTesting for variables below
    UsageStatsManager mUsageStatsManager;
    // Provides access to device usage history and statistics.
    // Usage data is aggregated into time intervals: days, weeks, months, and years.

    UsageListAdapter mUsageListAdapter;
    RecyclerView mRecyclerView;
    // A flexible view for providing a limited window into a large data set.
    // http://frank-zhu.github.io/android/2015/01/16/android-recyclerview-part-1/

    RecyclerView.LayoutManager mLayoutManager;
    Button mOpenUsageSettingButton;
    Spinner mSpinner;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment {@link AppUsageStatisticsFragment}.
     */
    public static AppUsageStatisticsFragment newInstance() {
        return new AppUsageStatisticsFragment();
    }

    public AppUsageStatisticsFragment() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUsageStatsManager = (UsageStatsManager) getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_app_usage_statistics, container, false);
        // 這邊設定此fragment的layout
    }

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);
        // Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle)
        // has returned, but before any saved state has been restored in to the view.

        mUsageListAdapter = new UsageListAdapter();
        // 自訂的class
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_app_usage);
        // 設定RecyclerView的樣式，在fragment_app_usage_statistics.xml中的recyclerview_app_usage
        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.scrollToPosition(0);
        mRecyclerView.setAdapter(mUsageListAdapter);
        // adapter就是数据适配器，它的功能是将数据绑定到UI界面上；系统本身提供了几种简单的适配器，如果界面比较复杂的话最好用自定义适配器。
        // 在适配器中主要是先加载布局，即要填充数据的部分的布局，然后将相应的控件实例化，并且设置相应的值即可
        mOpenUsageSettingButton = (Button) rootView.findViewById(R.id.button_open_usage_setting);
        mSpinner = (Spinner) rootView.findViewById(R.id.spinner_time_span);
        // 設定spinner(下拉式選單)要顯示的內容，即fragment_app_usage_statistics.xml中的spinner_time_span

        SpinnerAdapter spinnerAdapter =
                ArrayAdapter.createFromResource(getActivity(), R.array.action_list, android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(spinnerAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //使用者選yearly,monthly,weekly,daily
            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                StatsUsageInterval statsUsageInterval = StatsUsageInterval.getValue(strings[position]);
                /*  StatsUsageInterval是自定義的資料型別(在下方)
                    position = 0 return daily
                    position = 1 return weekly
                    position = 2 return monthly
                    position = 3 return yearly
                 */

                if (statsUsageInterval != null) {
                    List<UsageStats> usageStatsList = getUsageStatistics(statsUsageInterval.mInterval);
                    // 這只是得到原始的統計data
                    // 透過自定義方法getUsageStatistics取得選定時間區間之統計資料(return list)
                    // 看傳入的參數statsUsageInterval.mInterval決定是daily, weekly, monthly, yearly
                    Collections.sort(usageStatsList, new LastTimeLaunchedComparatorDesc());
                    // 以降冪排列usageStatsList
                    updateAppsList(usageStatsList);
                    // 呼叫自定義方法updateAppsList，傳入統計資料的list
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 必須實作OnItemSelectedListener()的方法
            }
        });  // end setOnItemSelectedListener
    }

    /**
     * Returns the {@link #mRecyclerView} including the time span specified by the
     * intervalType argument.
     *
     * @param intervalType The time interval by which the stats are aggregated.
     *                     Corresponding to the value of {@link UsageStatsManager}.
     *                     E.g. {@link UsageStatsManager#INTERVAL_DAILY}, {@link
     *                     UsageStatsManager#INTERVAL_WEEKLY},
     * @return A list of {@link android.app.usage.UsageStats}.
     */
    public List<UsageStats> getUsageStatistics(int intervalType) {
        // 只是得到原始的data
        // 傳入int intervalType，決定是要看DAILY, WEEKLY, MONTHLY, YEARLY
        // Return list，此list包含指定時段的APP統計資料

        Calendar cal = Calendar.getInstance();
        int thisYear = cal.get(Calendar.YEAR) - 1900;
        int thisMonth = cal.get(Calendar.MONTH);
        int today = cal.get(Calendar.DAY_OF_WEEK);
        // (0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday)
        long timeRangeMillis = 0;

        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy-mm-ss");

        switch (intervalType) {
            case 0:
                timeRangeMillis = cal.getTimeInMillis() + MILLIS_OF_A_DAY;
                break;

            case 1:
                cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                timeRangeMillis = cal.getTimeInMillis() + MILLIS_OF_A_WEEK;
                Log.d("weekly",f.format(cal.getTime()));
                break;

            case 2:
                cal.add(Calendar.MONTH, -1);
                break;

            case 3:
                cal.add(Calendar.YEAR, -1);
                break;

            default:
                break;
        }

        List<UsageStats> queryUsageStats = mUsageStatsManager.queryUsageStats
                (0, cal.getTimeInMillis(), timeRangeMillis);
        /*
        3 PARAMETERS
        -------------
        intervalType	int: The time interval by which the stats are aggregated.
        分成：INTERVAL_DAILY、 INTERVAL_WEEKLY、 INTERVAL_MONTHLY、 INTERVAL_YEARLY、 INTERVAL_BEST
        beginTime	    long: The inclusive beginning of the range of stats to include in the results.
        endTime	        long: The exclusive end of the range of stats to include in the results.
         */

        if (queryUsageStats.size() == 0) {
            // 若list的數量為0(可能是程式錯誤或是使用者沒有開啟權限)
            // 此時會出現一個按鈕，使用者按下後就連到設定頁面
            Log.i(TAG, "The user may not allow the access to apps usage. ");
            Toast.makeText(getActivity(),
                           getString(R.string.explanation_access_to_appusage_is_not_enabled),
                           Toast.LENGTH_LONG).show();
            mOpenUsageSettingButton.setVisibility(View.VISIBLE);

            mOpenUsageSettingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                    // 連到設定頁面
                }
            });  //end setOnClickListener
        } //end if

        return queryUsageStats;
        // 不管怎樣都會return這個list
    }  // end getUsageStatistics

    /**
     * Updates the {@link #mRecyclerView} with the list of {@link UsageStats} passed as an argument.
     *
     * @param usageStatsList A list of {@link UsageStats} from which update the
     *                       {@link #mRecyclerView}.
     */
    //VisibleForTesting

    void updateAppsList(List<UsageStats> usageStatsList) {
        // 把原始統計data傳入，整頓一番

        ArrayList<String> APPpackageName = new ArrayList<>();
        List<CustomUsageStats> customUsageStatsList = new ArrayList<>();
        //存放CustomUsageStats的list

        for (int i = 0; i < usageStatsList.size(); i++) {
            CustomUsageStats customUsageStats = new CustomUsageStats();
            // 自定義的類別，包含 UsageStats usageStats, Drawable appIcon
            customUsageStats.usageStats = usageStatsList.get(i);
            // 取出usageStatsList中的第i個值(也就是第i筆APP的統計資料)，設給customUsageStats.usageStats
            try {
                // 取得此APP的圖像
                customUsageStats.appIcon = getActivity().getPackageManager()
                        .getApplicationIcon(customUsageStats.usageStats.getPackageName());
                // 並設為customUsageStats.appIcon
            } catch (PackageManager.NameNotFoundException e) {
                // 若沒圖像，抓住例外並設為預設圖像
                // 預設圖像:getActivity().getDrawable(R.drawable.ic_default_app_launcher);
                Log.w(TAG, String.format("App Icon is not found for %s",
                                         customUsageStats.usageStats.getPackageName()));
                customUsageStats.appIcon = getActivity()
                        .getDrawable(R.drawable.ic_default_app_launcher);
            }

            // 一筆APP的統計資料必須: 1.使用時間不等於零 2.名稱沒和其他重複 才可以通過這個if
            if (APPpackageName.contains(customUsageStats.usageStats.getPackageName())
                    || customUsageStats.usageStats.getTotalTimeInForeground() == 0)
                continue;

            APPpackageName.add(customUsageStats.usageStats.getPackageName());
            customUsageStatsList.add(customUsageStats);

        }//end outer for

        mUsageListAdapter.setCustomUsageStatsList(customUsageStatsList);
        // 呼叫自訂方法setCustomUsageStatsList，把customUsageStatsList傳入mUsageListAdapter中
        mUsageListAdapter.notifyDataSetChanged();
        // 通知資料被變動，更新 ListView 顯示內容。
        // https://chris930921.gitbooks.io/android-traveling-gitbook/content/listview/notifydatasetchanged.html
        mRecyclerView.scrollToPosition(0);
        // Scroll the RecyclerView to make the position visible.
        // https://developer.android.com/reference/android/support/v7/widget/LinearLayoutManager.html#scrollToPosition(int)
    }

    /**
     * The {@link Comparator} to sort a collection of {@link UsageStats} sorted by the timestamp
     * last time the app was used in the descendant order.
     */
    private static class LastTimeLaunchedComparatorDesc implements Comparator<UsageStats> {

        @Override
        public int compare(UsageStats left, UsageStats right) {
            return Long.compare(right.getLastTimeUsed(), left.getLastTimeUsed());
        }
    }

    /**
     * Enum represents the intervals for {@link android.app.usage.UsageStatsManager} so that
     * values for intervals can be found by a String representation.
     */
    //VisibleForTesting
    enum StatsUsageInterval {
        // enum為設定幾個固定常數的資料型別，http://pydoing.blogspot.tw/2010/12/java-enum.html
        // 可以直接用StatsUsageInterval.DAILY , .WEEKLY, .MONTHLY, .YEARLY 來呼叫
        DAILY("Daily", UsageStatsManager.INTERVAL_DAILY),
        WEEKLY("Weekly", UsageStatsManager.INTERVAL_WEEKLY),
        MONTHLY("Monthly", UsageStatsManager.INTERVAL_MONTHLY),
        YEARLY("Yearly", UsageStatsManager.INTERVAL_YEARLY);

        private int mInterval;
        private String mStringRepresentation;

        StatsUsageInterval(String stringRepresentation, int interval) {
            // 建構子，在這邊確定了enum常數的資料型別必須是(string,int)
            mStringRepresentation = stringRepresentation;
            mInterval = interval;
        }

        static StatsUsageInterval getValue(String stringRepresentation) {
            // 回傳自定義的StatsUsageInterval資料型別的內容
            for (StatsUsageInterval statsUsageInterval : values()) {
                // 判斷StatsUsageInterval的每筆資料，String符合輸入的stringRepresentation者，回傳
                if (statsUsageInterval.mStringRepresentation.equals(stringRepresentation)) {
                    return statsUsageInterval;
                }
            }
            return null;
        }
    }
} //end class AppUsageStatisticsFragment
