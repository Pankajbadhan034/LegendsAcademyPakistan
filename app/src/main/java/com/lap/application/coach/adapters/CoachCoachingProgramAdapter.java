package com.lap.application.coach.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lap.application.R;
import com.lap.application.beans.CoachingProgramBean;

import java.util.ArrayList;

public class CoachCoachingProgramAdapter extends BaseAdapter{

    Context context;
    ArrayList<CoachingProgramBean> coachingProgramList;
    LayoutInflater layoutInflater;
    Typeface helvetica;
    Typeface linoType;

    public CoachCoachingProgramAdapter(Context context, ArrayList<CoachingProgramBean> coachingProgramList) {
        this.context = context;
        this.coachingProgramList = coachingProgramList;
        layoutInflater = LayoutInflater.from(context);
        helvetica = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(context.getAssets(),"fonts/LinotypeOrdinarRegular.ttf");
    }

    @Override
    public int getCount() {
        return coachingProgramList.size();
    }

    @Override
    public Object getItem(int position) {
        return coachingProgramList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_coaching_program_item, null);

        TextView coachingProgramName = convertView.findViewById(R.id.coachingProgramName);

        CoachingProgramBean coachingProgramBean = coachingProgramList.get(position);

        coachingProgramName.setText(coachingProgramBean.getCoachinProgramName());
        coachingProgramName.setTypeface(helvetica);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_coaching_program_dropdown_item, null);

        TextView coachingProgramName = convertView.findViewById(R.id.coachingProgramName);

        CoachingProgramBean coachingProgramBean = coachingProgramList.get(position);

        coachingProgramName.setText(coachingProgramBean.getCoachinProgramName());
        coachingProgramName.setTypeface(helvetica);

        return convertView;
    }
}