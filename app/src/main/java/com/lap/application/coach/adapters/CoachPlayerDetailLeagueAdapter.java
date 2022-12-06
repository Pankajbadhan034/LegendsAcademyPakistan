package com.lap.application.coach.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.SessionLeagueBean;
import com.lap.application.beans.UserBean;
import com.lap.application.utils.Utilities;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CoachPlayerDetailLeagueAdapter extends BaseAdapter {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<SessionLeagueBean> coachLeagueBeanArrayList;
    LayoutInflater layoutInflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions roundOptions;

    public CoachPlayerDetailLeagueAdapter(Context context, ArrayList<SessionLeagueBean> coachLeagueBeanArrayList) {
        this.context = context;
        this.coachLeagueBeanArrayList = coachLeagueBeanArrayList;
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
        return coachLeagueBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return coachLeagueBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_player_detail_league, null);

        TextView text = (TextView) convertView.findViewById(R.id.text);
        RecyclerView careerRV = convertView.findViewById(R.id.careerRV);

        final SessionLeagueBean coachLeagueBean = coachLeagueBeanArrayList.get(position);
       // text.setText(coachLeagueBean.getLeague_name());


        System.out.println("SIZEHERE::"+coachLeagueBean.getStatsLeagueBeanArrayList().size());
        CoachPlayerLeagueCareerAdapter coachPlayerLeagueCareerAdapter = new CoachPlayerLeagueCareerAdapter(coachLeagueBean.getStatsLeagueBeanArrayList(), position);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        careerRV.setLayoutManager(mLayoutManager2);
        careerRV.setAdapter(coachPlayerLeagueCareerAdapter);



        return convertView;
    }


}