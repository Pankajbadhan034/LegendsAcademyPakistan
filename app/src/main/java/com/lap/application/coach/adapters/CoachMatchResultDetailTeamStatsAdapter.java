package com.lap.application.coach.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.lap.application.R;
import com.lap.application.beans.TeamTotalStatsBean;
import com.lap.application.beans.UserBean;
import com.lap.application.utils.Utilities;

import java.util.ArrayList;

public class CoachMatchResultDetailTeamStatsAdapter extends BaseAdapter {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<TeamTotalStatsBean> coachLeagueDetailOneFixtureBeanArrayList;
    LayoutInflater layoutInflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions roundOptions;
    Typeface sportspress;

    public CoachMatchResultDetailTeamStatsAdapter(Context context, ArrayList<TeamTotalStatsBean> coachLeagueDetailOneFixtureBeanArrayList) {
        this.context = context;
        this.coachLeagueDetailOneFixtureBeanArrayList = coachLeagueDetailOneFixtureBeanArrayList;
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
                .displayer(new RoundedBitmapDisplayer(1000))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        sportspress = Typeface.createFromAsset(context.getAssets(),"fonts/sportspress.ttf");

    }

    @Override
    public int getCount() {
        return coachLeagueDetailOneFixtureBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return coachLeagueDetailOneFixtureBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_team_stats_item, null);

        ImageView image = convertView.findViewById(R.id.image);
        TextView ttfImage = (TextView) convertView.findViewById(R.id.ttfImage);
        TextView text = (TextView) convertView.findViewById(R.id.text);

        ttfImage.setTypeface(sportspress);


        final TeamTotalStatsBean teamDetailBean = coachLeagueDetailOneFixtureBeanArrayList.get(position);

        if(teamDetailBean.getIcon_type().equalsIgnoreCase("0")){
            ttfImage.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            ttfImage.setTextColor(Color.parseColor("#"+teamDetailBean.getColor()));
            Utilities.fontImageText(ttfImage, teamDetailBean.getIcon_name());
        }else{
            ttfImage.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            imageLoader.displayImage(teamDetailBean.getImage_url(), image, roundOptions);
        }


        text.setText(teamDetailBean.getValue());




        return convertView;
    }





}
