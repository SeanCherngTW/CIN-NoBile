package com.cin.linyuehlii.nobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class timePicker extends Fragment {
    private ViewPager viewPager;
    private String timevalue;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //主畫面
        View view = inflater.inflate(R.layout.normal_picker, container, false);
        /*
        final Picker picker1 = (Picker) view.findViewById(R.id.picker);
        // final TextView et =  (TextView)view.findViewById(R.id.et);
        //   final Button btn  =   (Button)view.findViewById(R.id.btn);

        String minute = Integer.toString(picker1.getCurrentMin());
        Log.d("timeValue","minute AT onCreateView = "+ minute);

        if (picker1.getCurrentMin() < 10) {minute = "0" + minute;}

            timevalue = ("It's " + picker1.getCurrentHour() + ":" + minute);
            Log.d("timeValue","timeValue AT onCreateView = "+ timevalue);
            //      et.setText(timevalue);
*/
            return view;
    }

    public  String getValue(){
        Log.d("timeValue","timeValue AT getValue = "+ timevalue);
        return timevalue;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
