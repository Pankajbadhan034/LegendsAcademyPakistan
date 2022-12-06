package com.lap.application.coach;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.CoachChildUnpaidPlayerBean;
import com.lap.application.beans.CoachUnpaidParticipantBean;
import com.lap.application.beans.CountryBean;
import com.lap.application.beans.LeagueAddPlayerTeamBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.adapters.CoachSelectMidweekPackageAdapter;
import com.lap.application.coach.adapters.CoachUnpaidPlayerParticipantAdapter;
import com.lap.application.coach.adapters.CoachUnpaidPlayersAdapter;
import com.lap.application.coach.fragments.CoachManageAttendanceFragment;
import com.lap.application.league.LeaguePlayerAddScreen;
import com.lap.application.league.adapters.LeagueSelectTeamFieldsDataAdapter;
import com.lap.application.parent.adapters.ParentCountryCodeAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceAsync;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class CoachMidweekUnpaidPlayersScreen extends AppCompatActivity implements IWebServiceCallback {
    String titleStr="";
    LinearLayout linearParticipant;
    RelativeLayout relative1;
    EditText partNameET;
    TextView parentEmail;
    Button partsearchBT;
    RadioButton parentChoose;
    RadioButton partChoose;
    private final String SEARCH_PART_NAME = "SEARCH_PART_NAME";
    ArrayList<CoachUnpaidParticipantBean> coachUnpaidParticipantBeanArrayList = new ArrayList<>();
    private final String PARENT_EMAIL_GET = "PARENT_EMAIL_GET";

    String usersID;
    String dobLabelStr,gendername1, gendername2, fNameLabelStr, lNameLabelStr, NameLabelStr, genderLabelStr, genderInputTypeMultFieldStr;
    private final String GET_REG = "GET_REG";
    private final String GET_COUNTRY_CODES = "GET_COUNTRY_CODES";
    Button addNewChild;
    LinearLayout linearForChildFileds;
    String finalDate;
    String child_ids;
    EditText emailET;
    Button searchBT;
    ImageView backButton;
    private final String SEARCH_EMAIL = "SEARCH_EMAIL";
    private final String SESSION_DATE = "SESSION_DATE";
    private final String ADD_UNPAID = "ADD_UNPAID";
    String strEmail;
    String strCheckEmail;
    ArrayList<CoachChildUnpaidPlayerBean> coachChildUnpaidPlayerBeanArrayList;
    GridView gridView;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    String groupName;
    String coachingProgName;
    String coachingProgID;
    String locationName;
    String locationId;
    String sessionId;
    String sessionDate;
    TextView sessionInfo;
    TextView sessionAttendedDate;
    Button submitSessionDate;
    Button resetSessionDate;
    boolean addNewChildBool;
    EditText parentNameET;
    EditText mobileNumberET;
    TextInputLayout parentName;
    TextInputLayout mobileTextInputLayout;
    LinearLayout linearPhone;
    TextView countryCodeOneTextView;
    ArrayList<CountryBean> countryList = new ArrayList<>();
    String defaultCountryCodeFromServer = "";
    EditText fullNameET;
    boolean emailExistBool;
    static TextView dob;
    String strDob;
    TextView sex;
    RadioButton male;
    RadioButton female;
    RadioGroup radioGroup;
    String strGender = "";
    TextInputLayout fullName;
    TextView title;
    TextView chooseClub;
    String chooseClubStr;
    ArrayList<LeagueAddPlayerTeamBean>clubList = new ArrayList<>();
    String midweek_sessionIntent;
    private final String TEAM_DATA = "TEAM_DATA";
    String packageIdStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_midweek_unpaid_players_screen_activity);
        emailET = findViewById(R.id.emailET);
        searchBT = findViewById(R.id.searchBT);
        backButton = findViewById(R.id.backButton);
        gridView = findViewById(R.id.gridView);
        sessionInfo = findViewById(R.id.sessionInfo);
        sessionAttendedDate = findViewById(R.id.sessionAttendedDate);
        submitSessionDate = findViewById(R.id.submitSessionDate);
        resetSessionDate = findViewById(R.id.resetSessionDate);
        linearForChildFileds = findViewById(R.id.linearForChildFileds);
        addNewChild = findViewById(R.id.addNewChild);
        parentNameET = findViewById(R.id.parentNameET);
        mobileNumberET = findViewById(R.id.mobileNumberET);
        parentName = findViewById(R.id.parentName);
        mobileTextInputLayout = findViewById(R.id.mobileTextInputLayout);
        linearPhone = findViewById(R.id.linearPhone);
        countryCodeOneTextView = findViewById(R.id.countryCodeOneTextView);
        fullNameET = findViewById(R.id.fullNameET);
        dob = findViewById(R.id.dob);
        sex = (TextView) findViewById(R.id.sex);
        radioGroup = findViewById(R.id.radioGroup);
        fullName = findViewById(R.id.fullName);
        title = findViewById(R.id.title);
        chooseClub = findViewById(R.id.chooseClub);

        linearParticipant = findViewById(R.id.linearParticipant);
        partNameET = findViewById(R.id.partNameET);
        parentEmail = findViewById(R.id.parentEmail);
        partsearchBT= findViewById(R.id.partsearchBT);
        parentChoose = findViewById(R.id.parentChoose);
        partChoose = findViewById(R.id.partChoose);
        relative1 = findViewById(R.id.relative1);


        male = (RadioButton) findViewById(R.id.male);
        female = (RadioButton) findViewById(R.id.female);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        final String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);
        title.setText("ADD UNPAID "+verbiage_singular.toUpperCase());
        addNewChild.setText("Add New "+verbiage_singular);

        partChoose.setText(verbiage_singular);
        partNameET.setHint("Enter "+verbiage_singular+" Name");

        groupName = getIntent().getStringExtra("groupName");
        coachingProgName = getIntent().getStringExtra("coachingProgName");
        coachingProgID = getIntent().getStringExtra("coachingProgID");
        locationName = getIntent().getStringExtra("locationName");
        locationId = getIntent().getStringExtra("locationId");
        sessionId = getIntent().getStringExtra("sessionId");
        sessionDate = getIntent().getStringExtra("sessionDate");
        midweek_sessionIntent = getIntent().getStringExtra("midweek_sessionIntent");

        sessionInfo.setText("SESSION INFORMATION:\n\n" +
                "Session Attended Date : "+sessionDate+"\n\n" +
                ""+groupName+"\n\n" +
                ""+coachingProgName+"\n\n" +
                ""+locationName+"");

        chooseClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSDKchooseDialog(chooseClub, "Choose Midweek Package", clubList);
            }
        });

        parentChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailET.setText("");
                parentNameET.setText("");
                mobileNumberET.setText("");
                fullNameET.setText("");
                gridView.setAdapter(null);
                gridView.setVisibility(View.GONE);
                linearForChildFileds.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);
                addNewChildBool = false;
                strGender = "";
                strEmail = "";
                strCheckEmail = "";
                strDob = "";
                partNameET.setText("");

                linearParticipant.setVisibility(View.GONE);
                relative1.setVisibility(View.VISIBLE);

            }
        });

        partChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailET.setText("");
                parentNameET.setText("");
                mobileNumberET.setText("");
                fullNameET.setText("");
                gridView.setAdapter(null);
                gridView.setVisibility(View.GONE);
                linearForChildFileds.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);
                addNewChildBool = false;
                strGender = "";
                strEmail = "";
                strCheckEmail = "";
                strDob = "";
                partNameET.setText("");

                linearParticipant.setVisibility(View.VISIBLE);
                relative1.setVisibility(View.GONE);
                parentEmail.setVisibility(View.GONE);
                parentEmail.setText("Parent Email");



            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CoachMidweekUnpaidPlayersScreen.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {

                                month = month+1;

                                String strDay = day < 10 ? "0"+day : day+"";
                                String strMonth = month < 10 ? "0"+month : month+"";
                                strDob = strDay+"-"+strMonth+"-"+year;
                                dob.setText(strDob);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        countryCodeOneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CoachMidweekUnpaidPlayersScreen.this);
                ListView countriesListView = new ListView(CoachMidweekUnpaidPlayersScreen.this);
                alertDialog.setView(countriesListView);
                alertDialog.setTitle("Select Country");
                countriesListView.setAdapter(new ParentCountryCodeAdapter(CoachMidweekUnpaidPlayersScreen.this, countryList));

                final AlertDialog dialog = alertDialog.create();

                countriesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        CountryBean clickedOnCountry = countryList.get(i);
                        countryCodeOneTextView.setText(clickedOnCountry.getDialingCode());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        addNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearForChildFileds.setVisibility(View.VISIBLE);
                parentName.setVisibility(View.GONE);
                linearPhone.setVisibility(View.GONE);
                gridView.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);
                addNewChildBool = true;
            }
        });


        resetSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailET.setText("");
                parentNameET.setText("");
                mobileNumberET.setText("");
                fullNameET.setText("");
                gridView.setAdapter(null);
                gridView.setVisibility(View.GONE);
                linearForChildFileds.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);
                addNewChildBool = false;
                strGender = "";
                strEmail = "";
                strCheckEmail = "";
                strDob = "";
                linearParticipant.setVisibility(View.GONE);
                relative1.setVisibility(View.VISIBLE);
                parentChoose.setChecked(true);


            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strGender = "1";
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strGender = "2";
            }
        });

        submitSessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if(partChoose.isChecked()){

                }else{
                    strEmail = emailET.getText().toString().trim();
                }



                Pattern pattern = Pattern.compile(Utilities.EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(strEmail);

                if (strEmail == null || strEmail.isEmpty()) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please search with parent email or "+verbiage_singular+" name to add "+verbiage_singular+".", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please search parent email to add "+verbiage_singular, Toast.LENGTH_SHORT).show();
                    return;
                }else if (!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                    if(addNewChildBool==true){

                        System.out.println("strCheckEmail:: "+strCheckEmail+" strEmail:: "+strEmail);

                        if(strCheckEmail.equalsIgnoreCase(strEmail)){
                            if(emailExistBool==true){
                                String fullNameStr = fullNameET.getText().toString().trim();
                                strDob = dob.getText().toString().trim();
                                if(fullNameStr == null || fullNameStr.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter "+fNameLabelStr, Toast.LENGTH_SHORT).show();
                                } else if (strDob == null || strDob.isEmpty() || strDob.equalsIgnoreCase(dobLabelStr)) {
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please select "+dobLabelStr, Toast.LENGTH_SHORT).show();
                                }else if(strGender == null || strGender.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please select "+genderLabelStr, Toast.LENGTH_SHORT).show();
                                }else{
                                    apiAddUnpaidNoChildExistYesParentExists();
                                }
                            }else{
                                String parentNameStr = parentNameET.getText().toString().trim();
                                String phoneNoStr = mobileNumberET.getText().toString().trim();
                                String fullNameStr = fullNameET.getText().toString().trim();
                                strDob = dob.getText().toString().trim();


                                if(parentNameStr == null || parentNameStr.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter parent name", Toast.LENGTH_SHORT).show();
                                }else if(phoneNoStr == null || phoneNoStr.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                                }else if(phoneNoStr == null || phoneNoStr.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                                }else if(fullNameStr == null || fullNameStr.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter "+fNameLabelStr, Toast.LENGTH_SHORT).show();
                                } else if (strDob == null || strDob.isEmpty() || strDob.equalsIgnoreCase(dobLabelStr)) {
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please select "+dobLabelStr, Toast.LENGTH_SHORT).show();
                                }else if(strGender == null || strGender.isEmpty()){
                                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please select "+genderLabelStr, Toast.LENGTH_SHORT).show();
                                }else{
                                    apiAddUnpaidNoChildExistNoParentExists("", parentNameStr, phoneNoStr, defaultCountryCodeFromServer);
                                }


                            }

                        }else{
                            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please search again parent email or "+verbiage_singular+" name", Toast.LENGTH_SHORT).show();
                        }


                    }else{
                        child_ids = "";
                        for(int i=0; i<coachChildUnpaidPlayerBeanArrayList.size(); i++){

                            if(coachChildUnpaidPlayerBeanArrayList.get(i).getCheck().equalsIgnoreCase("true")){
                                if(child_ids.equalsIgnoreCase("")){
                                    child_ids = child_ids + coachChildUnpaidPlayerBeanArrayList.get(i).getId();
                                }else {
                                    child_ids = child_ids + ","+coachChildUnpaidPlayerBeanArrayList.get(i).getId();
                                }

                            }
                        }

                        try{
                            System.out.println("child_IDS::"+child_ids);

                            if(child_ids.equalsIgnoreCase("")){
                                Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please select "+verbiage_singular, Toast.LENGTH_SHORT).show();
                            }else{
                                apiAddUnpaidChildExist();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                }


            }
        });

        sessionAttendedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(CoachMidweekUnpaidPlayersScreen.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {

                                month = month+1;

                                String strDay = day < 10 ? "0"+day : day+"";
                                String strMonth = month < 10 ? "0"+month : month+"";
                                finalDate = strDay+"-"+strMonth+"-"+year;

                                apiSessionDate(finalDate);

                                // txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, year, month, day);
                datePickerDialog.show();

//                DialogFragment newFragment = new DatePickerFrag();
//                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        partsearchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                gridView.setVisibility(View.GONE);
                linearForChildFileds.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);

                String strName = partNameET.getText().toString().trim();

                if (strName == null || strName.isEmpty()) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                }else{
                    if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

                        List<NameValuePair> nameValuePairList = new ArrayList<>();
                        nameValuePairList.add(new BasicNameValuePair("player_name", strName));

                        String webServiceUrl = Utilities.BASE_URL + "coach/search_by_player_name";

                        ArrayList<String> headers = new ArrayList<>();
                        headers.add("X-access-uid:"+loggedInUser.getId());
                        headers.add("X-access-token:"+loggedInUser.getToken());

                        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, SEARCH_PART_NAME, CoachMidweekUnpaidPlayersScreen.this, headers);
                        postWebServiceAsync.execute(webServiceUrl);

                    } else {
                        Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        searchBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                gridView.setVisibility(View.GONE);
                linearForChildFileds.setVisibility(View.GONE);
                addNewChild.setVisibility(View.GONE);

                strEmail = emailET.getText().toString().trim();

                Pattern pattern = Pattern.compile(Utilities.EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(strEmail);

                if (strEmail == null || strEmail.isEmpty()) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                } else if (!matcher.matches()) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Please enter a valid Email", Toast.LENGTH_SHORT).show();
                }else{
                    submitParentEmail();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        apiSessionDate(sessionDate);

        getTeamData();

    }

    public void submitParentEmail(){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", strEmail));

            String webServiceUrl = Utilities.BASE_URL + "coach/search_email";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, SEARCH_EMAIL, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getCountryCodes(){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            String webServiceUrl = Utilities.BASE_URL + "account/phoneCode_list";

            GetWebServiceAsync getWebServiceWithHeadersAsync = new GetWebServiceAsync(CoachMidweekUnpaidPlayersScreen.this, GET_COUNTRY_CODES, CoachMidweekUnpaidPlayersScreen.this);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    private void getParentEmail(String id){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("id", id));

            String webServiceUrl = Utilities.BASE_URL + "coach/get_parent_email_data";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, PARENT_EMAIL_GET, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case PARENT_EMAIL_GET:
                if(response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            strEmail = responseObject.getString("data");
                            parentEmail.setVisibility(View.VISIBLE);
                            parentEmail.setText("Parent Email: "+strEmail);
                            partNameET.setSelection(partNameET.getText().length());
                            submitParentEmail();

                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;

            case SEARCH_PART_NAME:
                coachUnpaidParticipantBeanArrayList.clear();
                if(response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0; i<dataArray.length(); i++){
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                CoachUnpaidParticipantBean coachUnpaidParticipantBean = new CoachUnpaidParticipantBean();
                                coachUnpaidParticipantBean.setChildId(jsonObject.getString("child_id"));
                                coachUnpaidParticipantBean.setParentId(jsonObject.getString("parent_id"));
                                coachUnpaidParticipantBean.setFullName(jsonObject.getString("full_name"));
                                coachUnpaidParticipantBeanArrayList.add(coachUnpaidParticipantBean);
                            }

                            if(coachUnpaidParticipantBeanArrayList.size()==0){

                            }else{
                                final Dialog dialog = new Dialog(CoachMidweekUnpaidPlayersScreen.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.coach_unpaid_player_participant_dialog);
                                dialog.setCancelable(true);

                                TextView titleDialog = dialog.findViewById(R.id.titleDialog);
                                ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

                                String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
                                titleDialog.setText("Choose " +verbiage_singular);

                                listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        partNameET.setText(coachUnpaidParticipantBeanArrayList.get(i).getFullName());
                                        dialog.dismiss();
                                        getParentEmail(coachUnpaidParticipantBeanArrayList.get(i).getChildId());
                                    }
                                });

                                listViewDialog.setAdapter(null);
                                CoachUnpaidPlayerParticipantAdapter coachUnpaidPlayerParticipantAdapter = new CoachUnpaidPlayerParticipantAdapter(CoachMidweekUnpaidPlayersScreen.this, coachUnpaidParticipantBeanArrayList);
                                listViewDialog.setAdapter(coachUnpaidPlayerParticipantAdapter);

                                dialog.show();
                            }


                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case SEARCH_EMAIL:

                if(response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            coachChildUnpaidPlayerBeanArrayList = new ArrayList<>();
                            usersID = responseObject.getString("users_id");
                            for(int i=0; i<dataArray.length(); i++){
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                CoachChildUnpaidPlayerBean coachChildUnpaidPlayerBean = new CoachChildUnpaidPlayerBean();
                                coachChildUnpaidPlayerBean.setEmail(responseObject.getString("email"));
                                coachChildUnpaidPlayerBean.setUserId(responseObject.getString("users_id"));
                                coachChildUnpaidPlayerBean.setId(jsonObject.getString("id"));
                                coachChildUnpaidPlayerBean.setFullName(jsonObject.getString("full_name"));
                                coachChildUnpaidPlayerBean.setGender(jsonObject.getString("gender"));
                                coachChildUnpaidPlayerBean.setAge(jsonObject.getString("age"));
                                coachChildUnpaidPlayerBean.setDob(jsonObject.getString("dob"));
                                coachChildUnpaidPlayerBean.setGender_value(jsonObject.getString("gender_value"));
                                coachChildUnpaidPlayerBean.setDateOfBirth(jsonObject.getString("date_of_birth"));
                                coachChildUnpaidPlayerBean.setFirstName(jsonObject.getString("first_name"));
                                coachChildUnpaidPlayerBean.setLastName(jsonObject.getString("last_name"));
                                coachChildUnpaidPlayerBean.setWeight(jsonObject.getString("weight"));
                                coachChildUnpaidPlayerBean.setHeight(jsonObject.getString("height"));

                                if(jsonObject.getString("full_name").equalsIgnoreCase(partNameET.getText().toString().trim())){
                                    coachChildUnpaidPlayerBean.setCheck("true");
                                }else{
                                    coachChildUnpaidPlayerBean.setCheck("false");
                                }


                                coachChildUnpaidPlayerBeanArrayList.add(coachChildUnpaidPlayerBean);
                            }


                            emailExistBool = true;
                            addNewChildBool = false;
                            if(parentChoose.isChecked()){
                                strCheckEmail = emailET.getText().toString().trim();
                            }else{
                                strCheckEmail = strEmail;
                            }

                            addNewChild.setVisibility(View.VISIBLE);



                            if(!(coachChildUnpaidPlayerBeanArrayList.size()==0)){
                                CoachUnpaidPlayersAdapter coachUnpaidPlayersAdapter = new CoachUnpaidPlayersAdapter(CoachMidweekUnpaidPlayersScreen.this, coachChildUnpaidPlayerBeanArrayList);
                                gridView.setAdapter(coachUnpaidPlayersAdapter);
                                Utilities.setGridViewHeightBasedOnChildren(gridView,2);
                                gridView.setVisibility(View.VISIBLE);
                            }



                        }else{
                            emailExistBool = false;
                            addNewChildBool = true;
                            if(parentChoose.isChecked()){
                                strCheckEmail = emailET.getText().toString().trim();
                            }else{
                                strCheckEmail = strEmail;
                            }

                            gridView.setVisibility(View.GONE);
                            linearForChildFileds.setVisibility(View.VISIBLE);
                            parentName.setVisibility(View.VISIBLE);
                            linearPhone.setVisibility(View.VISIBLE);
                            // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                        getCountryCodes();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case TEAM_DATA:
                if(response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            titleStr = jsonObject.getString("title");
                            JSONArray clubJsonArray = jsonObject.getJSONArray("sessions");
                            for(int i = 0; i<clubJsonArray.length(); i++){
                                JSONObject jsonObject1 = clubJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("session_count"));
                                clubList.add(leagueAddPlayerTeamBean);
                            }
                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                        getCountryCodes();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case ADD_UNPAID:
                if(response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//                            Intent mainScreen = new Intent(this, CoachMainScreen.class);
//                            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(mainScreen);
                     //       CoachManageAttendanceFragment.variableTrue = true;
                            finish();
                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case GET_COUNTRY_CODES:

                countryList.clear();

                if(response == null){
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status){
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            CountryBean countryBean;
                            for(int i=0;i<dataArray.length();i++){
                                JSONObject countryObject = dataArray.getJSONObject(i);
                                countryBean = new CountryBean();

                                countryBean.setId(countryObject.getString("id"));
                                countryBean.setCountry(countryObject.getString("country"));
//                                countryBean.setCountryCode(countryObject.getString("country_code"));
                                countryBean.setDialingCode(countryObject.getString("dialing_code"));

                                countryList.add(countryBean);
                            }

                            String defaultCodeId = responseObject.getString("default_codeId");

                            for(CountryBean country : countryList){
                                if(country.getId().equalsIgnoreCase(defaultCodeId)){

                                    defaultCountryCodeFromServer = country.getDialingCode();
                                    countryCodeOneTextView.setText(country.getDialingCode());
                                    break;
                                }
                            }

//                            parentCountryCodeAdapter.notifyDataSetChanged();
                            getRegForm();
                        } else {
                            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case GET_REG:

                System.out.println("RES::"+response);

                if (response == null) {
                    Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray jsonArray = new JSONArray(responseObject.getString("data"));
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if(jsonObject.getString("slug").equalsIgnoreCase("fname")){
//                                    fNameIdStr = jsonObject.getString("id");
                                    fNameLabelStr = jsonObject.getString("label_name");
//                                    fNameSlugStr = jsonObject.getString("slug");
//                                    fNameIsShowStr = jsonObject.getString("is_show");
//                                    fNameIsReqStr = jsonObject.getString("is_required");
//                                    fNameFieldTypeStr = jsonObject.getString("field_type");
//                                    fNameInputTypeMultFieldStr = jsonObject.getString("input_type_multible_field");


//                                    if(fNameIsShowStr.equalsIgnoreCase("1")){
                                    // firstNameTIL.setHint(fNameLabelStr);
//                                        firstNameTIL.setVisibility(View.VISIBLE);
//                                    }
                                    fullName.setHint(fNameLabelStr);


                                }else if(jsonObject.getString("slug").equalsIgnoreCase("lname")){
//                                    lNameIdStr = jsonObject.getString("id");
                                    lNameLabelStr = jsonObject.getString("label_name");
//                                    lNameSlugStr = jsonObject.getString("slug");
//                                    lNameIsShowStr = jsonObject.getString("is_show");
//                                    lNameIsReqStr = jsonObject.getString("is_required");
//                                    lNameFieldTypeStr = jsonObject.getString("field_type");
//                                    lNameInputTypeMultFieldStr = jsonObject.getString("input_type_multible_field");

//                                    if(lNameIsShowStr.equalsIgnoreCase("1")){
//                                        fullName.setHint(fNameLabelStr+" & "+lNameLabelStr);
//                                        lastNameTIL.setVisibility(View.VISIBLE);
//                                    }



                                }
                                else if(jsonObject.getString("slug").equalsIgnoreCase("gender")){
//                                    genderIdStr = jsonObject.getString("id");
                                    genderLabelStr = jsonObject.getString("label_name");
//                                    genderSlugStr = jsonObject.getString("slug");
//                                    genderIsShowStr = jsonObject.getString("is_show");
//                                    genderIsReqStr = jsonObject.getString("is_required");
//                                    genderFieldTypeStr = jsonObject.getString("field_type");
                                    genderInputTypeMultFieldStr = jsonObject.getString("input_type_multible_field");


                                    //  if(genderIsShowStr.equalsIgnoreCase("1")){
                                    String[] namesList = genderInputTypeMultFieldStr.split(",");
                                    gendername1 = namesList [0];
                                    gendername2 = namesList [1];

                                    sex.setText(genderLabelStr);
                                    //  genderLinear.setVisibility(View.VISIBLE);
                                    male.setText(gendername1);
                                    female.setText(gendername2);
                                    //}



                                }else if(jsonObject.getString("slug").equalsIgnoreCase("dob")){
//                                    dobIdStr = jsonObject.getString("id");
                                    dobLabelStr = jsonObject.getString("label_name");
//                                    dobSlugStr = jsonObject.getString("slug");
//                                    dobIsShowStr = jsonObject.getString("is_show");
//                                    dobIsReqStr = jsonObject.getString("is_required");
//                                    dobFieldTypeStr = jsonObject.getString("field_type");
//                                    dobInputTypeMultFieldStr = jsonObject.getString("input_type_multible_field");

                                    //if(dobIsShowStr.equalsIgnoreCase("1")){
                                    // if(isEditMode){
                                    //   dateOfBirth.setText(childToEdit.getDateOfBirth());
                                    //dateOfBirth.setVisibility(View.VISIBLE);
                                    // }else{
                                    dob.setText(dobLabelStr);
                                    // dateOfBirth.setVisibility(View.VISIBLE);
                                    // }

                                    // }



                                }



                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }


    }

    public void apiSessionDate(String finalDate){
        //sessionAttendedDate.setText(finalDate);
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("session_attended", finalDate));
            nameValuePairList.add(new BasicNameValuePair("session_id", sessionId));

            String webServiceUrl = Utilities.BASE_URL + "coach/check_seat_ava";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, SESSION_DATE, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    public void apiAddUnpaidNoChildExistYesParentExists(){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            if(partChoose.isChecked()){
                nameValuePairList.add(new BasicNameValuePair("email", strEmail));
            }else{
                nameValuePairList.add(new BasicNameValuePair("email", emailET.getText().toString().trim()));
            }

            nameValuePairList.add(new BasicNameValuePair("email_exist", "1"));
            nameValuePairList.add(new BasicNameValuePair("child_name", fullNameET.getText().toString().trim()));
            nameValuePairList.add(new BasicNameValuePair("dob", dob.getText().toString().trim()));
            nameValuePairList.add(new BasicNameValuePair("gender", strGender));
            nameValuePairList.add(new BasicNameValuePair("session_attended", sessionDate));
            nameValuePairList.add(new BasicNameValuePair("session_id", sessionId));
            nameValuePairList.add(new BasicNameValuePair("users_id", usersID));
            nameValuePairList.add(new BasicNameValuePair("package_id", packageIdStr));
            String webServiceUrl = Utilities.BASE_URL + "coach/add_unpaid_player_midweek";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, ADD_UNPAID, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    public void apiAddUnpaidNoChildExistNoParentExists(String emailExits, String parentname,  String phoneNo, String phoneCode){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            if(partChoose.isChecked()){
                nameValuePairList.add(new BasicNameValuePair("email", strEmail));
            }else{
                nameValuePairList.add(new BasicNameValuePair("email", emailET.getText().toString().trim()));
            }

            nameValuePairList.add(new BasicNameValuePair("child_name", fullNameET.getText().toString().trim()));
            nameValuePairList.add(new BasicNameValuePair("dob", dob.getText().toString().trim()));
            nameValuePairList.add(new BasicNameValuePair("gender", strGender));
            nameValuePairList.add(new BasicNameValuePair("session_attended", sessionDate));
            nameValuePairList.add(new BasicNameValuePair("session_id", sessionId));
            nameValuePairList.add(new BasicNameValuePair("parent_name", parentname));
            nameValuePairList.add(new BasicNameValuePair("ph_code", phoneCode));
            nameValuePairList.add(new BasicNameValuePair("phone_1", phoneNo));
            nameValuePairList.add(new BasicNameValuePair("package_id", packageIdStr));
            String webServiceUrl = Utilities.BASE_URL + "coach/add_unpaid_player_midweek";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, ADD_UNPAID, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    public void apiAddUnpaidChildExist(){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            if(partChoose.isChecked()){
                nameValuePairList.add(new BasicNameValuePair("email", strEmail));
            }else{
                nameValuePairList.add(new BasicNameValuePair("email", emailET.getText().toString().trim()));
            }
            nameValuePairList.add(new BasicNameValuePair("email_exist", "1"));
            nameValuePairList.add(new BasicNameValuePair("check", "1"));
            nameValuePairList.add(new BasicNameValuePair("child_ids", child_ids));
            nameValuePairList.add(new BasicNameValuePair("session_attended", sessionDate));
            nameValuePairList.add(new BasicNameValuePair("session_id", sessionId));
            nameValuePairList.add(new BasicNameValuePair("users_id", usersID));
            nameValuePairList.add(new BasicNameValuePair("package_id", packageIdStr));
            String webServiceUrl = Utilities.BASE_URL + "coach/add_unpaid_player_midweek";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, ADD_UNPAID, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getRegForm() {
        if (Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            String webServiceUrl = Utilities.BASE_URL + "children/get_child_reg_form";
            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, GET_REG, CoachMidweekUnpaidPlayersScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void showSDKchooseDialog(final TextView textView, final String title, final ArrayList<LeagueAddPlayerTeamBean> arrayList) {

        final Dialog dialog = new Dialog(CoachMidweekUnpaidPlayersScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.league_team_dialog);
        dialog.setCancelable(true);

        TextView titleDialog = dialog.findViewById(R.id.titleDialog);
        ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

        titleDialog.setText(title);

        listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                textView.setText(titleStr+", "+arrayList.get(i).getName()+" Sessions");
                packageIdStr = arrayList.get(i).getId();

                dialog.dismiss();
            }
        });

        CoachSelectMidweekPackageAdapter patientAlertAdapter = new CoachSelectMidweekPackageAdapter(CoachMidweekUnpaidPlayersScreen.this, arrayList, titleStr);
        listViewDialog.setAdapter(patientAlertAdapter);

        dialog.show();

    }

    private void getTeamData(){
        if(Utilities.isNetworkAvailable(CoachMidweekUnpaidPlayersScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("package_id", midweek_sessionIntent));

            String webServiceUrl = Utilities.BASE_URL + "coach/get_midweek_packages_details";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachMidweekUnpaidPlayersScreen.this, nameValuePairList, TEAM_DATA, CoachMidweekUnpaidPlayersScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachMidweekUnpaidPlayersScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }
}