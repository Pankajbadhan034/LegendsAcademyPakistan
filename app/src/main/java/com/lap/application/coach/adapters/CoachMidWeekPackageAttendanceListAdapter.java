package com.lap.application.coach.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.Chapter;
import com.lap.application.R;
import com.lap.application.beans.UserBean;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoachMidWeekPackageAttendanceListAdapter extends BaseAdapter implements IWebServiceCallback {
    private final String NOTES_DELETE = "NOTES_DELETE";

    int notesDeleteIntPosition;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<Chapter> membersList;
    LayoutInflater layoutInflater;

    public CoachMidWeekPackageAttendanceListAdapter(Context context, ArrayList<Chapter> membersList) {
        this.context = context;
        this.membersList = membersList;
        this.layoutInflater = LayoutInflater.from(context);

        sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
    }

    @Override
    public int getCount() {
        return membersList.size();
    }

    @Override
    public Object getItem(int position) {
        return membersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_midweek_package_attendance_item, null);

        TextView childName = (TextView) convertView.findViewById(R.id.childName);
        TextView srNo = convertView.findViewById(R.id.srNo);
        ImageView arrow = convertView.findViewById(R.id.arrow);
        ImageView delete = convertView.findViewById(R.id.delete);
        RelativeLayout mainRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.mainRelativeLayout);
        LinearLayout linearUnpaidLine = convertView.findViewById(R.id.linearUnpaidLine);

        final Chapter childBean = membersList.get(position);

        childName.setText(childBean.getNewDateFormat());

        int i = childBean.getId()+1;
        srNo.setText(""+i+".");

        if(childBean.getUnpaidFlag().equalsIgnoreCase("1")){
            linearUnpaidLine.setBackgroundColor(context.getResources().getColor(R.color.white));
            childName.setBackgroundColor(context.getResources().getColor(R.color.greyBorder));
        }else{
            linearUnpaidLine.setBackgroundColor(context.getResources().getColor(R.color.white));
            childName.setBackgroundColor(context.getResources().getColor(R.color.white));
        }

        if(childBean.getStatus().equalsIgnoreCase("1")){
            childName.setText(childBean.getNewDateFormat());
//            childName.setBackgroundColor(context.getResources().getColor(R.color.greyBorder));
            arrow.setVisibility(View.VISIBLE);
        }else if(childBean.getStatus().equalsIgnoreCase("0")){
            childName.setText(childBean.getNewDateFormat());
//            childName.setBackgroundColor(context.getResources().getColor(R.color.greyBorder));
            arrow.setVisibility(View.VISIBLE);
        }else{
            childName.setText(childBean.getNewDateFormat());
            childName.setBackgroundColor(context.getResources().getColor(R.color.red_trans));
            arrow.setVisibility(View.GONE);
            mainRelativeLayout.setVisibility(View.GONE);
        }

        if(childBean.getNewDateFormat().equalsIgnoreCase("")){
            delete.setVisibility(View.INVISIBLE);
        }else{
            delete.setVisibility(View.VISIBLE);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesDeleteIntPosition = position;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure you want to delete this Midweek Attendance?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if(childBean.isBoolLocalDelete()){
//                                    membersList.remove(notesDeleteIntPosition);
                                    membersList.get(notesDeleteIntPosition).setNewDateFormat("");
                                    membersList.get(notesDeleteIntPosition).setChapterName("");
                                    membersList.get(notesDeleteIntPosition).setBoolLocalDelete(true);
                                    notifyDataSetChanged();

                                }else{
                                    if (Utilities.isNetworkAvailable(context)) {
                                        List<NameValuePair> nameValuePairList = new ArrayList<>();
                                        nameValuePairList.add(new BasicNameValuePair("attendance_id", childBean.getAttendance_id()));
                                        nameValuePairList.add(new BasicNameValuePair("child_id", childBean.getChildId()));

                                        String webServiceUrl = Utilities.BASE_URL + "coach/midweek_attendance_remove";

                                        ArrayList<String> headers = new ArrayList<>();
                                        headers.add("X-access-uid:" + loggedInUser.getId());
                                        headers.add("X-access-token:" + loggedInUser.getToken());

                                        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(context, nameValuePairList, NOTES_DELETE, CoachMidWeekPackageAttendanceListAdapter.this, headers);
                                        postWebServiceAsync.execute(webServiceUrl);
                                    } else {
                                        Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });

//        childName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//            }
//        });


//        if(position % 2 == 0){
//            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
//            childName.setTextColor(context.getResources().getColor(R.color.white));
//        } else {
//            mainRelativeLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
//            childName.setTextColor(context.getResources().getColor(R.color.black));
//        }

        return convertView;
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag){
            case NOTES_DELETE:

                if (response == null) {
                    Toast.makeText(context, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status){
//                            membersList.remove(notesDeleteIntPosition);
                            membersList.get(notesDeleteIntPosition).setNewDateFormat("");
                            membersList.get(notesDeleteIntPosition).setChapterName("");
                            membersList.get(notesDeleteIntPosition).setBoolLocalDelete(true);
                            notifyDataSetChanged();
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }


}
