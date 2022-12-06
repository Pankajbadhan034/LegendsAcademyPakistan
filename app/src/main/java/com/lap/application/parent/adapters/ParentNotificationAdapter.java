package com.lap.application.parent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.lap.application.R;
import com.lap.application.beans.ParentNotficationBean;

import java.util.ArrayList;

public class ParentNotificationAdapter extends BaseAdapter {
    ArrayList<ParentNotficationBean> parentNotficationBeanArrayList;
    Context context;
    LayoutInflater layoutInflater;


    public ParentNotificationAdapter(Context context, ArrayList<ParentNotficationBean>parentNotficationBeanArrayList){
        this.context = context;
        this.parentNotficationBeanArrayList = parentNotficationBeanArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return parentNotficationBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parentNotficationBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.parent_adapter_notfications, null);
        RelativeLayout mainRelativeLayout = convertView.findViewById(R.id.mainRelativeLayout);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView desc = (TextView) convertView.findViewById(R.id.desc);
        TextView dateTime = convertView.findViewById(R.id.dateTime);

        final ParentNotficationBean parentNotficationBean = parentNotficationBeanArrayList.get(position);
        title.setText(parentNotficationBean.getTitle());
        desc.setText(parentNotficationBean.getMessage());
        dateTime.setText(parentNotficationBean.getCreated_at_date()+", "+parentNotficationBean.getCreated_at_time());

        if(position % 2 == 0) {
            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            title.setTextColor(context.getResources().getColor(R.color.black));
            desc.setTextColor(context.getResources().getColor(R.color.black));
            dateTime.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
            title.setTextColor(context.getResources().getColor(R.color.white));
            desc.setTextColor(context.getResources().getColor(R.color.white));
            dateTime.setTextColor(context.getResources().getColor(R.color.white));
        }
        return convertView;
    }
}
