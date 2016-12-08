package com.cin.linyuehlii.nobile;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class  CApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
	private List<ApplicationInfo> appsList = null;
	private Context context;
	private PackageManager packageManager;
	private Boolean checker = false;
	private LayoutInflater layoutInflater;

	public CApplicationAdapter(Context context, int textViewResourceId, List<ApplicationInfo> appsList) {

		super(context, textViewResourceId, appsList);
		this.context = context;
		this.appsList = appsList;
		packageManager = context.getPackageManager();
	}

	@Override
	public int getCount() {
		return ((null != appsList) ? appsList.size() : 0);
	}

	@Override
	public ApplicationInfo getItem(int position) {
		return ((null != appsList) ? appsList.get(position) : null);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	/*	View view = convertView;
			if (null == view) {
			//LayoutInflater layoutInflater = (LayoutInflater) context
			//		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = convertView.inflate(R.layout.c_appitem, null);

		}*/
		layoutInflater = LayoutInflater.from(context);


		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.blackapp_itemlist, parent, false);
		}

		ApplicationInfo data = appsList.get(position);
		if (null != data) {
			TextView appName = (TextView) convertView.findViewById(R.id.tvName);
			//TextView packageName = (TextView) view.findViewById(R.id.app_paackage);
			ImageView iconview = (ImageView) convertView.findViewById(R.id.ivImage);
			//ImageButton check = (ImageButton) convertView.findViewById(R.id.ivImage2);
			appName.setText(data.loadLabel(packageManager));
			//packageName.setText(data.packageName);
			iconview.setImageDrawable(data.loadIcon(packageManager));
/*
			check.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (checker == false) {
						check.setBackgroundResource(R.mipmap.checkcircle);
						checker = true;

					} else {
						check.setBackgroundResource(R.mipmap.nullcircle);
						checker = false;
					}
				}
			});*/



		}
		return convertView;
	}

}