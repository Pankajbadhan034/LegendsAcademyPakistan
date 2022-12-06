package com.lap.application.coach.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lap.application.R;
import com.lap.application.beans.StatsLeagueBean;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CoachPlayerLeagueCareerAdapter extends RecyclerView.Adapter<CoachPlayerLeagueCareerAdapter.MyViewHolder> {

    private ArrayList<StatsLeagueBean> coachTeamResultBeanArrayList;
    int pos;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView heading;
        public TextView valueTV;
        public View dividerLine;

        public MyViewHolder(View view) {
            super(view);
            heading = (TextView) view.findViewById(R.id.heading);
            valueTV = view.findViewById(R.id.valueTV);
            dividerLine = view.findViewById(R.id.dividerLine);

        }
    }


    public CoachPlayerLeagueCareerAdapter(ArrayList<StatsLeagueBean> coachTeamResultBeanArrayList, int pos) {
        this.coachTeamResultBeanArrayList = coachTeamResultBeanArrayList;
        this.pos = pos;
    }

    @Override
    public CoachPlayerLeagueCareerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coach_player_league_career_adapter, parent, false);

        return new CoachPlayerLeagueCareerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CoachPlayerLeagueCareerAdapter.MyViewHolder holder, int position) {
        StatsLeagueBean coachTeamResultBean = coachTeamResultBeanArrayList.get(position);

       //     holder.heading.setText(coachTeamResultBean.getHeading()+"\n\n"+coachTeamResultBean.getValue());

        if(pos==0){
            holder.heading.setVisibility(View.VISIBLE);
            holder.valueTV.setVisibility(View.VISIBLE);
            holder.dividerLine.setVisibility(View.VISIBLE);
            holder.heading.setText(""+coachTeamResultBean.getHeading());
            holder.valueTV.setText(""+coachTeamResultBean.getValue());
        }else{
            holder.heading.setVisibility(View.GONE);
            holder.valueTV.setVisibility(View.VISIBLE);
            holder.dividerLine.setVisibility(View.GONE);
            holder.valueTV.setText(""+coachTeamResultBean.getValue());
        }



    }

    @Override
    public int getItemCount() {
        return coachTeamResultBeanArrayList.size();
    }
}
