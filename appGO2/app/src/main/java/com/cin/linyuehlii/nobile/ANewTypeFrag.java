package com.cin.linyuehlii.nobile;


import java.util.ArrayList;
import java.util.List;

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

import android.view.View;
import android.view.ViewGroup;



public class ANewTypeFrag extends Fragment {

    GraphicalView mChartView;
    private int barcolor = Color.parseColor("#ff3366");
    private ArrayList<Member> aList=new ArrayList<>();
    private NewHelper helperrr;

    /**********************************************************/
    /************************程式碼開始*************************/
    /**********************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //主畫面
        if (helperrr == null) {//資料庫準備
            helperrr = new NewHelper(getActivity());
        }
        aList=null;

        View view = inflater.inflate(R.layout.a_stat_frag, container, false);
        ViewGroup chartContainer = (ViewGroup)view.findViewById(R.id.statFrag);


        Bundle bundle = getArguments();
        aList = bundle.getParcelableArrayList("appDetail");
        chartContainer.addView(TotalTimeBarChart());

        return view;

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
        renderer.setLabelsTextSize(25);
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
        for(int i=0;i<aList.size();i++){
            renderer.addXTextLabel(i+1, String.valueOf(aList.get(i).getName()));
        }
    }

    public GraphicalView TotalTimeBarChart() {
        //指標已經移到下一個了，所以id要往前+1----------------------------->很重要QAQ
        String[] titles = new String[]{""};
        ArrayList<double[]> values=new ArrayList<>();


        //Log.d("appTime", "aList.get(0).getTime() = " + aList.get(0).getTime());
        //long[] appTime = {Long.valueOf(aList.get(0).getTime())};
        double[] ccc=new double[aList.size()];
        for(int i=0;i<aList.size();i++){
            ccc[i] = changeType(aList.get(i).getTime());
        }

        values.add(ccc);

        int[] colors = new int[]{barcolor};

        XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);
        setChartSettings(renderer, "", "", "Minutes", 0.5, 3.5, 0, 300, Color.BLACK, Color.LTGRAY);
        renderer.getSeriesRendererAt(0).setDisplayChartValues(true);//文字顯示
        renderer.setZoomEnabled(false,false);
        // renderer.getSeriesRendererAt( 1).setDisplayChartValues( false );
        renderer.getSeriesRendererAt(0).setChartValuesTextSize(30);
        renderer.setXLabels(0);
        renderer.setYLabels(10);



        renderer.setXLabelsColor(Color.BLACK);
        renderer.setYLabelsColor(0,Color.parseColor("#2b3e51"));
        //renderer.setXLabelsAlign(Align.LEFT);
        renderer.setYLabelsAlign(Paint.Align.LEFT);
        renderer.setPanEnabled(true, false);
        renderer.setZoomEnabled(false);//可否放大
        renderer.setBarSpacing(2f);
        renderer.setApplyBackgroundColor(true);

        renderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));

        mChartView = ChartFactory.getBarChartView(getActivity(), buildBarDataset(titles, values), renderer, BarChart.Type.DEFAULT);
        return mChartView;
    }
    public double changeType(String s){
        Long x=null;
        x=Long.parseLong(s);
        return Double.valueOf(x/60000);
    }
}