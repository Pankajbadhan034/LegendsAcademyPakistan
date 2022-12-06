package com.lap.application.participant.adapter;

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
import com.lap.application.participant.fragment.ParticipantDashboardFragmentNew;

import java.util.ArrayList;

public class ParticipantDashboardAdapter extends BaseAdapter {

    Context context;
    ArrayList<ParentDashboardBean> parentDashboardBeanArrayList;
    LayoutInflater layoutInflater;

    Typeface helvetica;
    Typeface linoType;

    public ParticipantDashboardAdapter(Context context, ArrayList<ParentDashboardBean> parentDashboardBeanArrayList){
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
        convertView = layoutInflater.inflate(R.layout.participant_dashboard_adapter, null);
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
        if(label.equalsIgnoreCase("My Participants")){
            image.setBackgroundResource(R.drawable.team);
            try{
                if(ParticipantDashboardFragmentNew.requestCount.equalsIgnoreCase("0")){
                    badge.setVisibility(View.INVISIBLE);
                }else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(ParticipantDashboardFragmentNew.requestCount);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if(label.equalsIgnoreCase("Book Now")){
            image.setBackgroundResource(R.drawable.booknow);
        }else if(label.equalsIgnoreCase("Participant Newsfeed")){
            image.setBackgroundResource(R.drawable.timeline);
        }else if(label.equalsIgnoreCase("Participant Career")){
            image.setBackgroundResource(R.drawable.career);
        }else if(label.equalsIgnoreCase("Posts")){
            image.setBackgroundResource(R.drawable.writeapost);
        }else if(label.equalsIgnoreCase("Booking History")){
            image.setBackgroundResource(R.drawable.bookinghistory);
        }else if(label.equalsIgnoreCase("Join Leauge")){
            image.setBackgroundResource(R.drawable.league);
        }else if(label.equalsIgnoreCase("Parent Profile")){
            image.setBackgroundResource(R.drawable.profile);
        }else if(label.equalsIgnoreCase("Contact Us")){
            image.setBackgroundResource(R.drawable.contact_new);
        }else if(label.equalsIgnoreCase("Documents")){
            image.setBackgroundResource(R.drawable.uploaddocs_new);
            try{
                if(ParticipantDashboardFragmentNew.challengeCount.equalsIgnoreCase("0")){
                    badge.setVisibility(View.INVISIBLE);
                }else{
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(ParticipantDashboardFragmentNew.challengeCount);
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }else if(label.equalsIgnoreCase("Rugby Education")){
            image.setBackgroundResource(R.drawable.rugby);
        }else if(label.equalsIgnoreCase("Book Midweek Package")){
            image.setBackgroundResource(R.drawable.profile);
        }else if(label.equalsIgnoreCase("Online Store")){
            image.setBackgroundResource(R.drawable.cart_d);
        }else if(label.equalsIgnoreCase("Order History")){
            image.setBackgroundResource(R.drawable.cart_d);
        }else if(label.equalsIgnoreCase("Manage League")){
            image.setBackgroundResource(R.drawable.tournament);
        }else if(label.equalsIgnoreCase("Newsfeed")){
            image.setBackgroundResource(R.drawable.timeline);
        }else if(label.equalsIgnoreCase("Performance")){
            image.setBackgroundResource(R.drawable.workout);
        }else if(label.equalsIgnoreCase("Chats")){
            image.setBackgroundResource(R.drawable.messages);
            if(ParticipantDashboardFragmentNew.messageCount.equalsIgnoreCase("0")){
                badge.setVisibility(View.INVISIBLE);
            }else{
                badge.setVisibility(View.VISIBLE);
                badge.setText(ParticipantDashboardFragmentNew.messageCount);
            }
        }else if(label.equalsIgnoreCase("Friends")){
            image.setBackgroundResource(R.drawable.friends);
            if(ParticipantDashboardFragmentNew.requestCount.equalsIgnoreCase("0")){
                badge.setVisibility(View.INVISIBLE);
            }else{
                badge.setVisibility(View.VISIBLE);
                badge.setText(ParticipantDashboardFragmentNew.requestCount);
            }
        }else if(label.equalsIgnoreCase("Create Film")){
            image.setBackgroundResource(R.drawable.video);
        }else if(label.equalsIgnoreCase("Gallery")){
            image.setBackgroundResource(R.drawable.gallery);
        }else if(label.equalsIgnoreCase("Posts")){
            image.setBackgroundResource(R.drawable.writeapost);
        }else if(label.equalsIgnoreCase("Challenges")){
            image.setBackgroundResource(R.drawable.challenges);
            if(ParticipantDashboardFragmentNew.challengesCount.equalsIgnoreCase("0")){
                badge.setVisibility(View.INVISIBLE);
            }else{
                badge.setVisibility(View.VISIBLE);
                badge.setText(ParticipantDashboardFragmentNew.challengesCount);
            }
        }else if(label.equalsIgnoreCase("Career")){
            image.setBackgroundResource(R.drawable.career);
        }else if(label.equalsIgnoreCase("Reports")){
            image.setBackgroundResource(R.drawable.scores);
        }else if(label.equalsIgnoreCase("About Me")){
            image.setBackgroundResource(R.drawable.updateprofile);
        }else if(label.equalsIgnoreCase("Profile")){
            image.setBackgroundResource(R.drawable.viewprofile);
        }else if(label.equalsIgnoreCase("Login As Parent")){
            image.setBackgroundResource(R.drawable.parent);
        }else if(label.equalsIgnoreCase("Manage League")){
            image.setBackgroundResource(R.drawable.tournament);
        }else if(label.equalsIgnoreCase("Report Abuse")){
            image.setBackgroundResource(R.drawable.abuse);
        }else if(label.equalsIgnoreCase("Team")){
            image.setBackgroundResource(R.drawable.team);
        }else if(label.equalsIgnoreCase("Players")){
            image.setBackgroundResource(R.drawable.scores);
        }else if(label.equalsIgnoreCase("Notifications")){
            image.setBackgroundResource(R.drawable.bookinghistory);
        }


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = display.getWidth();
        int sixtyPercent = (screenWidth * 80) / 100;
        float textWidthForTitle = name.getPaint().measureText(name.getText().toString());
        int numberOfLines = ((int) textWidthForTitle / sixtyPercent) + 1;
        name.setLines(numberOfLines);


        return convertView;
    }
}