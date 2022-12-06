package com.lap.application.coach.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.lap.application.R;
import com.lap.application.beans.CoachLeagueTimedArrayBean;
import com.lap.application.beans.CoachPlayerStatsbean;
import com.lap.application.coach.CoachLeagueMatchResultScreen;
import com.lap.application.utils.Utilities;

import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class RecycleCoachLeagueTeam2Adapter extends RecyclerView.Adapter<RecycleCoachLeagueTeam2Adapter.MyViewHolder> {
    Dialog dialog;
    String editStr;
    private ArrayList<CoachPlayerStatsbean> coachTeamResultBeanArrayList;
    String timedValuesStr = "";
    private Context context;
    String team1IdStr;
    String player1IDStr;
    ArrayList<CoachLeagueTimedArrayBean>  arrayBeanArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public Button heading;
        public GridView listview1;


        public MyViewHolder(View view) {
            super(view);
            heading = (Button) view.findViewById(R.id.heading);
            listview1 = view.findViewById(R.id.listview1);

        }
    }


    public RecycleCoachLeagueTeam2Adapter(ArrayList<CoachPlayerStatsbean> coachTeamResultBeanArrayList, Context context, String team1IdStr, String player1IDStr) {
        this.coachTeamResultBeanArrayList = coachTeamResultBeanArrayList;
        this.context = context;
        this.team1IdStr = team1IdStr;
        this.player1IDStr = player1IDStr;
    }

    @Override
    public RecycleCoachLeagueTeam2Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.coach_recycle_league_team1_adapter, parent, false);

        return new RecycleCoachLeagueTeam2Adapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecycleCoachLeagueTeam2Adapter.MyViewHolder holder, final int position) {
        final CoachPlayerStatsbean coachTeamResultBean = coachTeamResultBeanArrayList.get(position);
        holder.heading.setText(coachTeamResultBean.getScore());





        if(position == 0 || position == 1){
            //  holder.heading.setFocusable(false);
            holder.heading.setBackgroundColor(context.getResources().getColor(R.color.grey1));
            holder.heading.setTextColor(context.getResources().getColor(R.color.white));

        }
        else{

            if(coachTeamResultBean.getTimed_display().equalsIgnoreCase("1")){

                if(coachTeamResultBean.getTimed_value().equalsIgnoreCase("")){
                    holder.listview1.setVisibility(View.GONE);
                }else{
                    holder.listview1.setVisibility(View.VISIBLE);
                    String[] array = coachTeamResultBean.getTimed_value().split(",");

                    arrayBeanArrayList = new ArrayList<>();
                    arrayBeanArrayList.clear();
                    for(int i=0; i<array.length; i++){
                        CoachLeagueTimedArrayBean coachLeagueTimedArrayBean = new CoachLeagueTimedArrayBean();
                        coachLeagueTimedArrayBean.setId(""+i);
                        coachLeagueTimedArrayBean.setValue(array[i]);
                        arrayBeanArrayList.add(coachLeagueTimedArrayBean);
                    }
                    holder.listview1.setAdapter(new RecyclCoachLeagueListIn2Adapter(context, arrayBeanArrayList, RecycleCoachLeagueTeam2Adapter.this, coachTeamResultBeanArrayList.get(position).getAttribute_id(), coachTeamResultBeanArrayList.get(position).getScore()));
                    Utilities.setGridViewHeightBasedOnChildren(holder.listview1,2);
                }

            }

            holder.heading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(coachTeamResultBean.getTimed_display().equalsIgnoreCase("1")){

                        holder.listview1.setVisibility(View.VISIBLE);
                        holder.listview1.setAdapter(null);

                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.coach_dialog_recycle_edit);
                        dialog.setTitle(R.string.ifa_dialog);

                        // set the custom dialog components - text, image and button
                        final EditText parentPassword = (EditText) dialog.findViewById(R.id.parentPassword);
                        Button submit = (Button) dialog.findViewById(R.id.submit);


                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editStr = parentPassword.getText().toString().trim();
                                if (editStr == null || editStr.isEmpty()) {
                                    dialog.dismiss();
//                                     Toast.makeText(context, "Please enter Parent Password", Toast.LENGTH_LONG).show();
                                }else if(editStr.equalsIgnoreCase("0")){
                                    holder.heading.setText(editStr);
                                    arrayHere(editStr, coachTeamResultBeanArrayList.get(position).getAttribute_id(), "");
                                    dialog.dismiss();
                                } else {
                                    holder.heading.setText(editStr);

                                    String myArray[] = new String[Integer.parseInt(editStr)];

                                    arrayBeanArrayList = new ArrayList<>();
                                    arrayBeanArrayList.clear();
                                    for(int i=0; i<myArray.length; i++){
                                        System.out.println("myARRAy:::"+myArray[i]);
                                        if(myArray[i]==null){
                                            System.out.println("yes:::");
                                            CoachLeagueTimedArrayBean coachLeagueTimedArrayBean = new CoachLeagueTimedArrayBean();
                                            coachLeagueTimedArrayBean.setId(""+i);
                                            coachLeagueTimedArrayBean.setValue("");
                                            arrayBeanArrayList.add(coachLeagueTimedArrayBean);
                                        }else{
                                            System.out.println("no:::");
                                            CoachLeagueTimedArrayBean coachLeagueTimedArrayBean = new CoachLeagueTimedArrayBean();
                                            coachLeagueTimedArrayBean.setId(""+i);
                                            coachLeagueTimedArrayBean.setValue(myArray[i]);
                                            arrayBeanArrayList.add(coachLeagueTimedArrayBean);
                                        }

                                    }

                                    RecyclCoachLeagueListIn2Adapter recyclCoachLeagueListInAdapter = new RecyclCoachLeagueListIn2Adapter(context, arrayBeanArrayList, RecycleCoachLeagueTeam2Adapter.this, coachTeamResultBeanArrayList.get(position).getAttribute_id(), editStr);
                                    holder.listview1.setAdapter(recyclCoachLeagueListInAdapter);
                                    Utilities.setGridViewHeightBasedOnChildren(holder.listview1,2);
                                    commaSeprate(coachTeamResultBeanArrayList.get(position).getAttribute_id(), editStr);

                                    dialog.dismiss();

                                }

                            }

                        });

                        dialog.show();


                    }else{


                        dialog = new Dialog(context);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.coach_dialog_recycle_edit);
                        dialog.setTitle(R.string.ifa_dialog);

                        // set the custom dialog components - text, image and button
                        final EditText parentPassword = (EditText) dialog.findViewById(R.id.parentPassword);
                        Button submit = (Button) dialog.findViewById(R.id.submit);


                        submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                editStr = parentPassword.getText().toString();
                                if (editStr == null || editStr.isEmpty()) {
                                    dialog.dismiss();
                                    // Toast.makeText(context, "Please enter Parent Password", Toast.LENGTH_LONG).show();
                                } else {
                                    holder.heading.setText(editStr);
                                    arrayHere(editStr, coachTeamResultBeanArrayList.get(position).getAttribute_id(), "");
                                    dialog.dismiss();
                                }
                            }
                        });

                        dialog.show();





                    }













                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return coachTeamResultBeanArrayList.size();
    }

    public void commaSeprate(String att_id, String editStr){
        for(int i=0; i<arrayBeanArrayList.size(); i++){
            timedValuesStr = timedValuesStr + arrayBeanArrayList.get(i).getValue()+",";
        }

        timedValuesStr = timedValuesStr.substring(0, timedValuesStr.length() -1);

        arrayHere(editStr, att_id, timedValuesStr);
    }

    public void arrayHere(String editStr, String attId, String timedValue){
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("attribute_value",editStr);
            jsonObject.put("player_id", player1IDStr);
            jsonObject.put("attribute_id", attId);
            jsonObject.put("team_id", team1IdStr);
            if(timedValue.equalsIgnoreCase(",")){
                jsonObject.put("timed_value", "");
            }else{
                jsonObject.put("timed_value", timedValue);
            }


            CoachLeagueMatchResultScreen.PlayerPerformanceDataSTR = CoachLeagueMatchResultScreen.PlayerPerformanceDataSTR + jsonObject.toString()+",";
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
