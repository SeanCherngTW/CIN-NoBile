package com.cin.linyuehlii.nobile;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;

import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;

import android.widget.LinearLayout;

import android.widget.RadioButton;
import android.widget.TextView;

public class AAppTimeFrag extends Fragment {
    Calendar c = Calendar.getInstance();
    int month = c.get(Calendar.MONTH) + 1;//calender月份從0-11
    int day = c.get(Calendar.DAY_OF_MONTH);
    int day4 = c.get(Calendar.DAY_OF_MONTH) - 1;
    int day3 = c.get(Calendar.DAY_OF_MONTH) - 2;
    int day2 = c.get(Calendar.DAY_OF_MONTH) - 3;
    private MySQLiteOpenHelper helper;
    private List<Spot> utList;
    private Spot toDay;
    GraphicalView mChartView;
    private PagerAdapter adapter;
    private ImageButton Fbt,Lbt;
    private ArrayList<Integer> data = new ArrayList<>();
    private int barcolor = Color.parseColor("#ff3366");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //主畫面
        View view = inflater.inflate(R.layout.jj, container, false);

        RadioButton r3 = (RadioButton) getActivity().findViewById(R.id.radioButton3);
        r3.setVisibility(View.GONE);//暫時將activity裡的第三頁按鈕移走

        Fbt = (ImageButton) getActivity().findViewById(R.id.Fbt);
        Fbt.setVisibility(View.VISIBLE);
        Lbt=(ImageButton)getActivity().findViewById(R.id.Lbt);
        Lbt.setVisibility(ImageButton.GONE);
        Fbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {


                FragmentManager manager ;
                manager= getActivity().getSupportFragmentManager() ;
                FragmentTransaction gg = manager.beginTransaction() ;
                ATotalTimeFrag  ty= new ATotalTimeFrag();
                gg.replace(R.id.frameLayout,ty);
                gg.commit();


            }
        });

        final ViewPager mViewPager = (ViewPager) view.findViewById(R.id.vpMember);
        final TextView titleText=(TextView)getActivity().findViewById(R.id.titleText);
        titleText.setText("本日總使用時間");
        //抓取值
        int position = getArguments().getInt("position");


        //三個pager頁面
        View vDay = inflater.inflate(R.layout.a_fragment_total, null);
        View vWeek = inflater.inflate(R.layout.a_fragment_total_day, null);
        //頁面內容
        LinearLayout dayBar = (LinearLayout) vDay.findViewById(R.id.Total);
        LinearLayout weekBar = (LinearLayout) vWeek.findViewById(R.id.Totald);
        dayBar.addView(TotalTimeBarChart(position, false));
        weekBar.addView(TotalTimeBarChart(position, true));

        //頁面放入viewpager
        ArrayList<View> viewList = new ArrayList<>();
        viewList.add(vDay);
        viewList.add(vWeek);


        adapter = new MyViewPagerAdapter(viewList);
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int arg0) {
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
                RadioButton r1 = (RadioButton) getActivity().findViewById(R.id.radioButton);
                RadioButton r2 = (RadioButton) getActivity().findViewById(R.id.radioButton2);
                int x = mViewPager.getCurrentItem();
                switch (x) {
                    case 0:
                        r1.setChecked(true);
                        r2.setChecked(false);
                        titleText.setText("本日總使用時間");
                        break;
                    case 1:
                        r1.setChecked(false);
                        r2.setChecked(true);
                        titleText.setText("本週總使用時間");
                        break;

                }

            }
        });
        mViewPager.setCurrentItem(0);
        return view;

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mListViews.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }


    protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        int length = titles.length;
        for (int i = 0; i < length; i++) {
            CategorySeries series = new CategorySeries(titles[i]);
            double[] v = values.get(i);
            int seriesLength = v.length;
            for (int k = 0; k < seriesLength; k++) {
                series.add(v[k]);
            }
            dataset.addSeries(series.toXYSeries());
        }
        return dataset;
    }

    protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        renderer.setAxisTitleTextSize(30);
        renderer.setChartTitleTextSize(40);
        renderer.setLabelsTextSize(20);
        renderer.setLegendTextSize(20);
        int length = colors.length;
        for (int i = 0; i < length; i++) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(colors[i]);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }

    protected void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle, String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor, int labelsColor) {
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.addXTextLabel(1, month + "/" + day2);
        renderer.addXTextLabel(2, month + "/" + day3);
        renderer.addXTextLabel(3, month + "/" + day4);
        renderer.addXTextLabel(4, month + "/" + day);
    }

    public GraphicalView TotalTimeBarChart(int position, boolean p) {
        //p為0-->day p為1-->week
        if (helper == null) {
            helper = new MySQLiteOpenHelper(getActivity());
        }
        helper.insertIfEmpty();
        utList = helper.getAllSpots();//抓所有資料
        //指標已經移到下一個了，所以id要往前+1----------------------------->很重要QAQ
        String[] titles = new String[]{"Facebook"};
        ArrayList<double[]> values = new ArrayList<>();
        if (p == false) {
            data = helper.getAppTime(position, false);
        } else {
            data = helper.getAppTime(position, true);
        }

        values.add(new double[]{
                data.get(0), data.get(1), data.get(2), data.get(3)});


        int[] colors = new int[]{barcolor};

        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "", "", "Minutes", 0.5, 4.5, 0, 10000, Color.BLACK, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);//文字顯示
        // renderer.getSeriesRendererAt( 1).setDisplayChartValues( false );
        renderer.getSeriesRendererAt(0).setChartValuesTextSize(30);
        renderer.setXLabels(0);
        renderer.setYLabels(10);
        //renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false);//可否放大
        renderer.setZoomRate(1.8f);//放大大小
        renderer.setBarSpacing(2f);
        renderer.setApplyBackgroundColor(true);
        renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
        renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));



        mChartView = ChartFactory.getBarChartView(getActivity(), buildBarDataset(titles, values), renderer, BarChart.Type.DEFAULT);
        return mChartView;
    }

}
