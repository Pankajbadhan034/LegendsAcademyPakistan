package com.lap.application.coach;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.Chapter;
import com.lap.application.R;
import com.lap.application.beans.CoachViewNotesBean;
import com.lap.application.beans.RecycleExpBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.adapters.CoachMidWeekPackageChildNamesAttendanceAdapter;
import com.lap.application.coach.adapters.CoachMidWeekPackageViewNotesAdapter;
import com.lap.application.utils.NestedListView;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class CoachMidWeekPackageChildNamesAttendanceActivity extends AppCompatActivity implements IWebServiceCallback {
    ArrayList<CoachViewNotesBean> coachViewNotesBeanArrayList = new ArrayList<>();
    String group_name;
    String location_name;
    ImageView backButton;
    NestedListView listView;
    NestedListView listViewUnpaid;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    private final String MARK_ATTENDANCE = "MARK_ATTENDANCE";
    private final String VIEW_NOTES = "VIEW_NOTES";
    private final String ADD_NOTES = "ADD_NOTES";
    Dialog dialogAddNotes;
    private ArrayList<RecycleExpBean>  recycleExpBeanArrayList = new ArrayList<>();
    private ArrayList<RecycleExpBean> recycleExpBeanArrayListUnpaids = new ArrayList<>();
    String midweek_sessionIntent, location_sessionIntent;
    Typeface helvetica;
    Typeface linoType;
    TextView groupName;
//    public static boolean finishCoachMidPackageCHildNamesAtttendanceActivity=false;
    TextView title;
    Button addNotes;
    Button viewNotes;
    Button addUnpaidChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_mid_week_package_child_names_attendance_screen);
        listView = findViewById(R.id.listView);
        listViewUnpaid = findViewById(R.id.listViewUnpaid);
        backButton = findViewById(R.id.backButton);
        groupName = findViewById(R.id.groupName);
        title = findViewById(R.id.title);
        addNotes = findViewById(R.id.addNotes);
        viewNotes = findViewById(R.id.viewNotes);
        addUnpaidChild = findViewById(R.id.addUnpaidChild);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);

        title.setText(verbiage_singular.toUpperCase()+" NAMES");
        addUnpaidChild.setText("ADD UNPAID "+verbiage_singular.toUpperCase());

        helvetica = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        midweek_sessionIntent = getIntent().getStringExtra("midweek_session");
        location_sessionIntent = getIntent().getStringExtra("location_session");

        System.out.println("midweek_sessionIntent:: "+midweek_sessionIntent+" location_sessionIntent:: "+location_sessionIntent);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecycleExpBean recycleExpBean = recycleExpBeanArrayList.get(i);
                Intent intent = new Intent(CoachMidWeekPackageChildNamesAttendanceActivity.this, CoachMidWeekPackageAttendanceListActivity.class);
                intent.putExtra("recycleExpBean", recycleExpBean);
                intent.putExtra("group_name", group_name);
                startActivity(intent);
//                finish();
            }
        });

        listViewUnpaid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RecycleExpBean recycleExpBean = recycleExpBeanArrayListUnpaids.get(i);
                Intent intent = new Intent(CoachMidWeekPackageChildNamesAttendanceActivity.this, CoachMidWeekPackageAttendanceListActivity.class);
                intent.putExtra("recycleExpBean", recycleExpBean);
                intent.putExtra("group_name", group_name);
                startActivity(intent);
//                finish();
            }
        });

        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNotes();
            }
        });

        viewNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNotesListAPI();

            }
        });

        addUnpaidChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent obj = new Intent(CoachMidWeekPackageChildNamesAttendanceActivity.this, CoachMidweekUnpaidPlayersScreen.class);
                obj.putExtra("groupName", group_name);
                obj.putExtra("coachingProgName", "");
                obj.putExtra("coachingProgID", "");
                obj.putExtra("locationName", location_name);
                obj.putExtra("locationId", "");
            //    obj.putExtra("sessionId", clickedOnAgeGroup.getSessionId()); // package id
                obj.putExtra("sessionDate", "");
                obj.putExtra("midweek_sessionIntent", midweek_sessionIntent);
                startActivity(obj);
                finish();
            }
        });



    }



    public void addNotes(){
        dialogAddNotes = new Dialog(CoachMidWeekPackageChildNamesAttendanceActivity.this);
        dialogAddNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddNotes.setContentView(R.layout.coach_midweek_add_notes_dialog);
        dialogAddNotes.setCancelable(true);

        final EditText notes = dialogAddNotes.findViewById(R.id.notes);
        Button submit = dialogAddNotes.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String notesStr = notes.getText().toString().trim();
                if(notesStr.isEmpty() || notesStr==null){
                    Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, "Please add notes", Toast.LENGTH_SHORT).show();
                }else{
                    addNotesAPI(notesStr);
                }
            }
        });


        dialogAddNotes.show();
    }


    private void addNotesAPI(String notes) {
        if(Utilities.isNetworkAvailable(CoachMidWeekPackageChildNamesAttendanceActivity.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("midweek_package_id", midweek_sessionIntent));
            nameValuePairList.add(new BasicNameValuePair("location_id", location_sessionIntent));
            nameValuePairList.add(new BasicNameValuePair("notes", notes));

            String webServiceUrl = Utilities.BASE_URL + "coach/add_midweek_notes";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            System.out.println("uid::"+loggedInUser.getId()+"token::"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidWeekPackageChildNamesAttendanceActivity.this, nameValuePairList, ADD_NOTES, CoachMidWeekPackageChildNamesAttendanceActivity.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    private void viewNotesListAPI() {
        if(Utilities.isNetworkAvailable(CoachMidWeekPackageChildNamesAttendanceActivity.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("package_id", midweek_sessionIntent));
            nameValuePairList.add(new BasicNameValuePair("location_id", location_sessionIntent));

            String webServiceUrl = Utilities.BASE_URL + "coach/get_midweek_notes";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            System.out.println("uid::"+loggedInUser.getId()+"token::"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidWeekPackageChildNamesAttendanceActivity.this, nameValuePairList, VIEW_NOTES, CoachMidWeekPackageChildNamesAttendanceActivity.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    private void getChildrenListingForAttendance() {
        if(Utilities.isNetworkAvailable(CoachMidWeekPackageChildNamesAttendanceActivity.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("midweek_session", midweek_sessionIntent));

            String webServiceUrl = Utilities.BASE_URL + "coach/coach_midweek_attendance_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            System.out.println("uid::"+loggedInUser.getId()+"token::"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidWeekPackageChildNamesAttendanceActivity.this, nameValuePairList, MARK_ATTENDANCE, CoachMidWeekPackageChildNamesAttendanceActivity.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case ADD_NOTES:
                if(response == null) {
                    Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        if(status==true){
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            dialogAddNotes.dismiss();
                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case VIEW_NOTES:
                coachViewNotesBeanArrayList.clear();
                if(response == null) {
                    Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        if(status==true){
                            JSONArray jsonArray = responseObject.getJSONArray("data");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                CoachViewNotesBean coachViewNotesBean = new CoachViewNotesBean();
                                coachViewNotesBean.setNotes(jsonObject.getString("notes"));
                                coachViewNotesBean.setCreatedAt(jsonObject.getString("created"));
                                coachViewNotesBean.setId(jsonObject.getString("id"));
                                coachViewNotesBean.setPackageId(jsonObject.getString("package_id"));
                                coachViewNotesBeanArrayList.add(coachViewNotesBean);
                            }

                            if(coachViewNotesBeanArrayList.size()==0){

                            }else{
                                final Dialog dialog = new Dialog(CoachMidWeekPackageChildNamesAttendanceActivity.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.coach_unpaid_player_participant_dialog);
                                dialog.setCancelable(true);

                                TextView titleDialog = dialog.findViewById(R.id.titleDialog);
                                ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

                                titleDialog.setText("NOTES HISTORY");

                                CoachMidWeekPackageViewNotesAdapter coachUnpaidPlayerParticipantAdapter = new CoachMidWeekPackageViewNotesAdapter(CoachMidWeekPackageChildNamesAttendanceActivity.this, coachViewNotesBeanArrayList);
                                listViewDialog.setAdapter(coachUnpaidPlayerParticipantAdapter);

                                dialog.show();
                            }



                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case MARK_ATTENDANCE:
                recycleExpBeanArrayListUnpaids.clear();
                recycleExpBeanArrayList.clear();
                if(response == null) {
                    Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status==true){
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            int attendance_dates = jsonObject.getInt("attendance_dates");
                             group_name = jsonObject.getString("group_name");
                             location_name = jsonObject.getString("location_name");
                            JSONArray childDetails = jsonObject.getJSONArray("child_details");


                            for(int i=0; i<childDetails.length(); i++){
                                JSONObject jsonObject1 = childDetails.getJSONObject(i);
//                                Subject physics = new Subject();
//                                physics.id = i;
//                                physics.subjectName = jsonObject1.getString("child_name");
//                                physics.chapters = new ArrayList<Chapter>();

                                RecycleExpBean recycleExpBean = new RecycleExpBean();
                                recycleExpBean.setChildName(jsonObject1.getString("child_name"));
                                if(jsonObject1.has("used_credit")){
                                    recycleExpBean.setUsed_credit(jsonObject1.getString("used_credit"));
                                    recycleExpBean.setTotal_credit(jsonObject1.getString("total_credit"));
                                }else{
                                    recycleExpBean.setUsed_credit("");
                                    recycleExpBean.setTotal_credit("");
                                }
                                recycleExpBean.setId(i);
                               // recycleExpBean.setChapters(new ArrayList<Chapter>());

                                ArrayList<Chapter>chapterArrayList = new ArrayList<>();
                                JSONArray attendance = jsonObject1.getJSONArray("attendance");
                                for(int j=0; j<attendance.length(); j++){
                                    JSONObject jsonObject2 = attendance.getJSONObject(j);
                                    Chapter chapter1 = new Chapter();
//                                    chapter1.chapterName = jsonObject2.getString("attendance_date");
//                                    chapter1.status = jsonObject2.getString("status");
//                                    chapter1.id = j;
//                                    chapter1.imageUrl = "http://ashishkudale.com/images/maths/circle.png";
//                                    physics.chapters.add(chapter1);

                                    chapter1.setChapterName(jsonObject2.getString("attendance_date"));
                                    chapter1.setNewDateFormat(jsonObject2.getString("attendance_date"));
                                    chapter1.setStatus(jsonObject2.getString("status"));
                                    chapter1.setId(j);
                                    chapter1.setAttendance_id(jsonObject2.getString("attendance_id"));
                                    chapter1.setTermId(jsonObject2.getString("term_id"));
                                    chapter1.setChildId(jsonObject1.getString("users_id"));

                                    chapter1.setMidweekSessionDetailsId(jsonObject2.getString("midweek_session_details_id"));
                                    chapter1.setOrderMidWeekSessionId(jsonObject2.getString("order_midweek_sessions_id"));
                                    chapter1.setUnpaidFlag(jsonObject2.getString("unpaid_flag"));

                                    if(jsonObject2.getString("attendance_date").equalsIgnoreCase("")){
                                        chapter1.setBoolLocalDelete(true);
                                    }else{
                                        chapter1.setBoolLocalDelete(false);
                                    }


                                    chapterArrayList.add(chapter1);

                                    recycleExpBean.setChapters(chapterArrayList);
                                }

                                if(attendance.length()==attendance_dates){

                                }else{
                                    int loopSize = attendance_dates - attendance.length();
                                    for(int k=0; k<loopSize; k++){
                                        Chapter chapter1 = new Chapter();
//                                        chapter1.chapterName = "N/A";
//                                        chapter1.status = "N/A";
//                                        chapter1.id = k+attendance.length();
//                                        chapter1.imageUrl = "http://ashishkudale.com/images/maths/circle.png";
//                                        physics.chapters.add(chapter1);

                                        chapter1.setChapterName("N/A");
                                        chapter1.setNewDateFormat("N/A");
                                        chapter1.setStatus("N/A");
                                        chapter1.setId(k+attendance.length());
                                        chapter1.setAttendance_id("N/A");
                                        chapter1.setTermId("N/A");
                                        chapter1.setMidweekSessionDetailsId("N/A");
                                        chapter1.setOrderMidWeekSessionId("N/A");
                                        chapter1.setUnpaidFlag("N/A");

                                        chapterArrayList.add(chapter1);
                                        recycleExpBean.setChapters(chapterArrayList);
                                    }
                                }


                                if(jsonObject1.getString("unpaid").equalsIgnoreCase("1")){
                                    recycleExpBeanArrayListUnpaids.add(recycleExpBean);
                                }else{
                                    recycleExpBeanArrayList.add(recycleExpBean);
                                }




                            }



                            groupName.setText(group_name);
                            CoachMidWeekPackageChildNamesAttendanceAdapter coachMidWeekPackageChildNamesAttendanceAdapter = new CoachMidWeekPackageChildNamesAttendanceAdapter(CoachMidWeekPackageChildNamesAttendanceActivity.this, recycleExpBeanArrayList);
                            listView.setAdapter(coachMidWeekPackageChildNamesAttendanceAdapter);
                         //   Utilities.setListViewHeightBasedOnChildren(listView);

                            CoachMidWeekPackageChildNamesAttendanceAdapter coachMidWeekPackageChildNamesAttendanceAdapterUnpaid = new CoachMidWeekPackageChildNamesAttendanceAdapter(CoachMidWeekPackageChildNamesAttendanceActivity.this, recycleExpBeanArrayListUnpaids);
                            listViewUnpaid.setAdapter(coachMidWeekPackageChildNamesAttendanceAdapterUnpaid);
                          //  Utilities.setListViewHeightBasedOnChildren(listViewUnpaid);


                        }else{
                            Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, message, Toast.LENGTH_SHORT).show();
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachMidWeekPackageChildNamesAttendanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getChildrenListingForAttendance();
//        if(finishCoachMidPackageCHildNamesAtttendanceActivity==true){
//            finish();
//            finishCoachMidPackageCHildNamesAtttendanceActivity = false;
//        }

    }
}