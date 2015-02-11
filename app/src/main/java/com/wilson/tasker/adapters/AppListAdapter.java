package com.wilson.tasker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wilson.tasker.R;
import com.wilson.tasker.ui.AppListFragment;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends ArrayAdapter<AppListFragment.AppEntry> {

	private LayoutInflater inflater;

	public AppListAdapter(Context context) {
		super(context, R.layout.list_item_app);
		inflater = LayoutInflater.from(context);
	}

	public void setData(List<AppListFragment.AppEntry> data) {
		clear();
		if (data != null) {
			addAll(data);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_app, parent, false);
		}

		AppListFragment.AppEntry app = getItem(position);
		((TextView) convertView.findViewById(R.id.tv_label)).setText(app.label);
		((ImageView) convertView.findViewById(R.id.iv_icon)).setImageDrawable(app.icon);

		return convertView;
	}
}