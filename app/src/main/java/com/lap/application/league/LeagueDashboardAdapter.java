package com.lap.application.league;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lap.application.R;
import com.lap.application.beans.ParentDashboardBean;

import java.util.ArrayList;

public class LeagueDashboardAdapter extends BaseAdapter {

    Context context;
    ArrayList<ParentDashboardBean> parentDashboardBeanArrayList;
    LayoutInflater layoutInflater;

    Typeface helvetica;
    Typeface linoType;

    public LeagueDashboardAdapter(Context context, ArrayList<ParentDashboardBean> parentDashboardBeanArrayList){
        this.context = context;
        this.parentDashboardBeanArrayList = parentDashboardBeanArrayList;
        layoutInflater = LayoutInflater.from(context);

        helvetica = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(context.getAssets(),"fonts/LinotypeOrdinarRegular.ttf");
    }

    @Override
    public int getCount() {
        return parentDashboardBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return parentDashboardBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.parent_dashboard_adapter, null);
        TextView name = (TextView) convertView.findViewById(R.id.text);
        ImageView image = convertView.findViewById(R.id.image);
        RelativeLayout relative = convertView.findViewById(R.id.relative);
        Button button = convertView.findViewById(R.id.button);
        TextView badge = convertView.findViewById(R.id.badge);

        final ParentDashboardBean parentDashboardBean = parentDashboardBeanArrayList.get(position);

//        if(parentDashboardBean.getLabel().equalsIgnoreCase("REPORT ABUSE") || parentDashboardBean.getLabel().equalsIgnoreCase("LOGOUT")){
//            button.setVisibility(View.VISIBLE);
//            relative.setVisibility(View.GONE);
//            button.setText(parentDashboardBean.getPreferred_text());
//        }else{
        button.setVisibility(View.GONE);
        relative.setVisibility(View.VISIBLE);
        name.setText(parentDashboardBean.getPreferred_text());
        name.setSelected(true);
//        }



        String label = parentDashboardBean.getLabel();
        if(label.equalsIgnoreCase("Join League")){
            image.setBackgroundResource(R.drawable.tournament);
        }else if(label.equalsIgnoreCase("Team")){
            image.setBackgroundResource(R.drawable.team);
        }else if(label.equalsIgnoreCase("Players")){
            image.setBackgroundResource(R.drawable.scores);
        }

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(parentDashboardBean.getLabel().equalsIgnoreCase("REPORT ABUSE")){
//                    ((ParentMainScreen) context).showContactCoach("REPORT AN ABUSE");
//                }else{
//                    ((ParentMainScreen) context).showLogoutDialog();
//                }
//
//            }
//        });


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int sixtyPercent = (screenWidth * 80) / 100;
        float textWidthForTitle = name.getPaint().measureText(name.getText().toString());
        int numberOfLines = ((int) textWidthForTitle / sixtyPercent) + 1;
        name.setLines(numberOfLines);
//

        return convertView;
    }
}