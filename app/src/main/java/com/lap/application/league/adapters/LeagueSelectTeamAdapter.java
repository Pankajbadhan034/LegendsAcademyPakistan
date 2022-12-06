package com.lap.application.league.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.LeagueJoinLeagueBean;
import com.lap.application.beans.LeagueManageCustomEditBean;
import com.lap.application.beans.UserBean;
import com.lap.application.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

public class LeagueSelectTeamAdapter  extends BaseAdapter {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<LeagueManageCustomEditBean> leagueManageCustomEditBeanArrayList;
    LayoutInflater layoutInflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions roundOptions;

    public LeagueSelectTeamAdapter(Context context, ArrayList<LeagueManageCustomEditBean> leagueManageCustomEditBeanArrayList) {
        this.context = context;
        this.leagueManageCustomEditBeanArrayList = leagueManageCustomEditBeanArrayList;
        this.layoutInflater = LayoutInflater.from(context);

        sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        roundOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(1000))
                .build();
    }

    @Override
    public int getCount() {
        return leagueManageCustomEditBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return leagueManageCustomEditBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.league_select_team_adapter, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView title = convertView.findViewById(R.id.title);
        final LeagueManageCustomEditBean leagueManageCustomEditBean = leagueManageCustomEditBeanArrayList.get(position);

        if(position==0){
            title.setVisibility(View.VISIBLE);
            name.setVisibility(View.GONE);
            title.setText(leagueManageCustomEditBean.getName());
        }else{
            title.setVisibility(View.GONE);
            name.setVisibility(View.VISIBLE);
            name.setText(leagueManageCustomEditBean.getName());
        }



        return convertView;
    }


}