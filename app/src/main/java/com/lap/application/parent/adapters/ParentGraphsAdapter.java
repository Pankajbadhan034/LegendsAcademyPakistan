package com.lap.application.parent.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.lap.application.R;
import com.lap.application.beans.ChildScoreBean;
import com.lap.application.utils.Utilities;

import java.util.ArrayList;

public class ParentGraphsAdapter extends BaseAdapter{

    Context context;
    ArrayList<ChildScoreBean> scoresListing;
    LayoutInflater layoutInflater;
    Typeface helvetica;
    Typeface linoType;

    public ParentGraphsAdapter(Context context, ArrayList<ChildScoreBean> scoresListing) {
        this.context = context;
        this.scoresListing = scoresListing;
        layoutInflater = LayoutInflater.from(context);

        helvetica = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(context.getAssets(),"fonts/LinotypeOrdinarRegular.ttf");
    }

    @Override
    public int getCount() {
        return scoresListing.size();
    }

    @Override
    public Object getItem(int position) {
        return scoresListing.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.parent_adapter_graph_item, null);

        TextView elementName = (TextView) convertView.findViewById(R.id.elementName);
        DonutProgress donutProgress = (DonutProgress) convertView.findViewById(R.id.donutProgress);
//        ImageView watchVideo = (ImageView) convertView.findViewById(R.id.watchVideo);
        ListView subElementListView = (ListView) convertView.findViewById(R.id.subElementListView);
        TextView areaOfDevelopment = (TextView) convertView.findViewById(R.id.areaOfDevelopment);
        TextView trainingPlan = (TextView) convertView.findViewById(R.id.trainingPlan);

        elementName.setTypeface(linoType);
        areaOfDevelopment.setTypeface(helvetica);
        trainingPlan.setTypeface(linoType);

        final ChildScoreBean childScoreBean = scoresListing.get(position);

        elementName.setText(childScoreBean.getElementName());
        elementName.setBackgroundColor(Color.parseColor(childScoreBean.getColorCode()));
        areaOfDevelopment.setText(childScoreBean.getAreaOfDevelopment());

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int sixtyPercent = (screenWidth * 60) / 100;
        float textWidthForTitle = areaOfDevelopment.getPaint().measureText(areaOfDevelopment.getText().toString());
        int numberOfLines = ((int) textWidthForTitle / sixtyPercent) + 1;
        areaOfDevelopment.setLines(numberOfLines);

        try {
            donutProgress.setProgress(Float.parseFloat(childScoreBean.getPerformancePercentage()));
            donutProgress.setText(childScoreBean.getPerformancePercentage()+"%");
        }catch(NumberFormatException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        donutProgress.setFinishedStrokeColor(Color.parseColor(childScoreBean.getColorCode()));

        subElementListView.setAdapter(new ParentSubElementListingAdapter(context, childScoreBean.getDetailedScores()));
        Utilities.setListViewHeightBasedOnChildren(subElementListView);

        if(childScoreBean.getVideoUrl() == null || childScoreBean.getVideoUrl().isEmpty()) {
//            watchVideo.setVisibility(View.GONE);
            trainingPlan.setVisibility(View.GONE);
        } else {
//            watchVideo.setVisibility(View.VISIBLE);
            trainingPlan.setVisibility(View.VISIBLE);
        }

        trainingPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println("Video URL "+childScoreBean.getVideoUrl());

                if(childScoreBean.getVideoUrl() == null || childScoreBean.getVideoUrl().isEmpty()) {
                    Toast.makeText(context, "Video not available", Toast.LENGTH_SHORT).show();
                } else {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(childScoreBean.getVideoUrl())));
                }

            }
        });

        return convertView;
    }


}