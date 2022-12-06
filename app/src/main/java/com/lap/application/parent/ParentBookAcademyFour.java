package com.lap.application.parent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.ApplicationContext;
import com.lap.application.R;
import com.lap.application.beans.AcademyLocationBean;
import com.lap.application.beans.AcademySessionChildBean;
import com.lap.application.beans.AcademySessionDetailBean;
import com.lap.application.beans.ChildBean;
import com.lap.application.beans.CoachingAcademyBean;
import com.lap.application.beans.TermBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.adapters.ParentAcademyChildrenListingAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ParentBookAcademyFour extends AppCompatActivity implements IWebServiceCallback {

    ApplicationContext applicationContext;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ImageView backButton;
    TextView title;
    //    TextView continueBelow;
//    TextView four;
//    TextView easySteps;
    TextView selectChild;
    TextView ageGroup;
    ListView childrenListView;
    TextView proceed;
    TextView addChild;
    TextView playerList;

    CoachingAcademyBean clickedOnAcademy;
    TermBean clickedOnTerm;
    AcademyLocationBean clickedOnLocation;
    AcademySessionDetailBean clickedOnSession;

    private final String GET_CHILDREN_LISTING = "GET_CHILDREN_LISTING";

    ArrayList<ChildBean> childrenListing = new ArrayList<>();
    ParentAcademyChildrenListingAdapter parentAcademyChildrenListingAdapter;

    public static boolean shouldRefreshChildrenListing = false;

    String defaultChecked = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity_book_academy_four);

        applicationContext = (ApplicationContext) getApplication();
        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
//        continueBelow = (TextView) findViewById(R.id.continueBelow);
//        four = (TextView) findViewById(R.id.four);
//        easySteps = (TextView) findViewById(R.id.easySteps);
        selectChild = (TextView) findViewById(R.id.selectChild);
        ageGroup = (TextView) findViewById(R.id.ageGroup);
        childrenListView = (ListView) findViewById(R.id.childrenListView);
        proceed = (TextView) findViewById(R.id.proceed);
        addChild = (TextView) findViewById(R.id.addChild);
        playerList = (TextView) findViewById(R.id.playerList);

        changeFonts();

        parentAcademyChildrenListingAdapter = new ParentAcademyChildrenListingAdapter(ParentBookAcademyFour.this, childrenListing, loggedInUser.getRoleCode());
        childrenListView.setAdapter(parentAcademyChildrenListingAdapter);

        Intent intent = getIntent();
        if (intent != null) {
            clickedOnAcademy = (CoachingAcademyBean) intent.getSerializableExtra("clickedOnAcademy");
            clickedOnTerm = (TermBean) intent.getSerializableExtra("clickedOnTerm");
            clickedOnLocation = (AcademyLocationBean) intent.getSerializableExtra("clickedOnLocation");
            clickedOnSession = (AcademySessionDetailBean) intent.getSerializableExtra("clickedOnSession");

            ageGroup.setText(clickedOnSession.getFromAge() + " years to " + clickedOnSession.getToAge() + " years");
//            ageGroup.setText(clickedOnSession.getGroupName());
        }

        getChildrenListing();

        playerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent obj = new Intent(ParentBookAcademyFour.this, ParentMainScreen.class);
                obj.putExtra("showFriendList", "showFriendList");
                startActivity(obj);*/

                Intent showManageChildren = new Intent(ParentBookAcademyFour.this, ParentShowManageChildren.class);
                startActivity(showManageChildren);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean atLeastOneSelected = false;
                for (ChildBean childBean : childrenListing) {
                    if (childBean.isSelected()) {
                        atLeastOneSelected = true;
                        break;
                    }
                }

                if (!atLeastOneSelected) {
                    Toast.makeText(ParentBookAcademyFour.this, "Please select at least one child", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Make CSV of selected children ids
                String selectedChildrenIds = "";
                for(ChildBean childBean: childrenListing){
                    if(childBean.isSelected()){
                        selectedChildrenIds += childBean.getId() + ",";
                    }
                }
                if (selectedChildrenIds != null && selectedChildrenIds.length() > 0 && selectedChildrenIds.charAt(selectedChildrenIds.length() - 1) == ',') {
                    selectedChildrenIds = selectedChildrenIds.substring(0, selectedChildrenIds.length() - 1);
                }

                // Check if session already exists in ApplicationContext
                int sessionExistsAtPosition = -1;
                ArrayList<AcademySessionChildBean> existingListing = applicationContext.getAcademySessionChildBeanListing();
                for(int i=0;i<existingListing.size();i++){
                    AcademySessionChildBean existingBean = existingListing.get(i);
                    if(existingBean.getSessionId().equalsIgnoreCase(clickedOnSession.getSessionId())){
                        sessionExistsAtPosition = i;
                        break;
                    }
                }

                if(sessionExistsAtPosition == -1){
                    // Does not exist, add to application context
                    AcademySessionChildBean academySessionChildBean = new AcademySessionChildBean();
                    academySessionChildBean.setSessionId(clickedOnSession.getSessionId());
                    academySessionChildBean.setChildrenIdsCSV(selectedChildrenIds);

                    applicationContext.getAcademySessionChildBeanListing().add(academySessionChildBean);
                } else {
                    // Session already exists, replace children
                    applicationContext.getAcademySessionChildBeanListing().get(sessionExistsAtPosition).setChildrenIdsCSV(selectedChildrenIds);
                }




                /*for (ChildBean childBean : childrenListing) {
                    if (childBean.isSelected()) {

                        // Check if already exists in ArrayList
                        ArrayList<AcademySessionChildBean> existingListing = applicationContext.getAcademySessionChildBeanListing();

                        boolean alreadyExists = false;

                        for(AcademySessionChildBean existingBean : existingListing) {
                            String csvIds = existingBean.getChildrenIdsCSV();

                            String arr[] = csvIds.split(",");

                            for(int i=0;i<arr.length;i++){
                                if(clickedOnSession.getSessionId().equalsIgnoreCase(existingBean.getSessionId()) && childBean.getId().equalsIgnoreCase(arr[i])){
                                    alreadyExists = true;
                                }
                            }
                        }

                        // If does not exist, then add
                        if(!alreadyExists) {
                            selectedChildrenIds += childBean.getId() + ",";
                        }
                    }
                }

                if (selectedChildrenIds != null && selectedChildrenIds.length() > 0 && selectedChildrenIds.charAt(selectedChildrenIds.length() - 1) == ',') {
                    selectedChildrenIds = selectedChildrenIds.substring(0, selectedChildrenIds.length() - 1);
                }

//                applicationContext.getBookAcademySummaryData().put(clickedOnSession.getSessionId(), selectedChildrenIds);
                if(selectedChildrenIds != null && !selectedChildrenIds.isEmpty()){

                    AcademySessionChildBean academySessionChildBean = new AcademySessionChildBean();
                    academySessionChildBean.setSessionId(clickedOnSession.getSessionId());
                    academySessionChildBean.setChildrenIdsCSV(selectedChildrenIds);

                    applicationContext.getAcademySessionChildBeanListing().add(academySessionChildBean);
                }*/

                Intent summaryScreen = new Intent(ParentBookAcademyFour.this, ParentBookAcademySummaryScreen.class);
                startActivity(summaryScreen);

            }
        });

        if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
            addChild.setVisibility(View.INVISIBLE);
        }else{
            addChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent addChild = new Intent(ParentBookAcademyFour.this, ParentAddChildScreen.class);
                    addChild.putExtra("isEditMode", false);
                    addChild.putExtra("comingFromBookAcademy", true);
                    addChild.putExtra("defaultChecked", defaultChecked);
                    startActivity(addChild);
                }
            });
        }



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(shouldRefreshChildrenListing){
            getChildrenListing();
            // Change back to false
            shouldRefreshChildrenListing = false;
        }
    }

    private void getChildrenListing() {
        if (Utilities.isNetworkAvailable(ParentBookAcademyFour.this)) {

            String webServiceUrl = Utilities.BASE_URL + "children/children_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(ParentBookAcademyFour.this, GET_CHILDREN_LISTING, ParentBookAcademyFour.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookAcademyFour.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_CHILDREN_LISTING:

                childrenListing.clear();

                if (response == null) {
                    Toast.makeText(ParentBookAcademyFour.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            defaultChecked = responseObject.getString("default_checked");

                            ChildBean childBean;
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject childObject = dataArray.getJSONObject(i);
                                childBean = new ChildBean();

                                childBean.setId(childObject.getString("id"));
                                childBean.setAcademiesId(childObject.getString("academies_id"));
                                childBean.setUsername(childObject.getString("username"));
                                childBean.setEmail(childObject.getString("email"));
                                childBean.setGender(childObject.getString("gender"));
                                childBean.setCreatedAt(childObject.getString("created_at"));
                                childBean.setState(childObject.getString("state"));
                                childBean.setFirstName(childObject.getString("first_name"));
                                childBean.setLastName(childObject.getString("last_name"));
                                childBean.setFullName(childObject.getString("full_name"));
                                childBean.setAge(childObject.getString("age"));
                                childBean.setAgeValue(childObject.getInt("age_value"));
                                childBean.setDateOfBirth(childObject.getString("dob"));
                                childBean.setMedicalCondition(childObject.getString("medical_conditions"));
                                childBean.setGenderValue(childObject.getString("gender_value"));

                                /*

                                define('SESSION_BOYS', '0');
                                define('SESSION_GIRL', '1');
                                define('SESSION_BOTH', '2');

                                */

                                if (childBean.getAgeValue() >= clickedOnSession.getFromAge() && childBean.getAgeValue() <= clickedOnSession.getToAge()) {
                                    if (clickedOnSession.getSessionGender().equalsIgnoreCase("0") && childBean.getGenderValue().equalsIgnoreCase("0")) {
                                        childrenListing.add(childBean);
                                    } else if (clickedOnSession.getSessionGender().equalsIgnoreCase("1") && childBean.getGenderValue().equalsIgnoreCase("1")) {
                                        childrenListing.add(childBean);
                                    } else if (clickedOnSession.getSessionGender().equalsIgnoreCase("2")) {
                                        childrenListing.add(childBean);
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(ParentBookAcademyFour.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookAcademyFour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                parentAcademyChildrenListingAdapter.notifyDataSetChanged();

//                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
//                    proceed.performClick();
//                }

                break;
        }
    }

    private void changeFonts() {
        title.setTypeface(linoType);
//        continueBelow.setTypeface(helvetica);
//        four.setTypeface(helvetica);
//        easySteps.setTypeface(helvetica);
        selectChild.setTypeface(linoType);
        ageGroup.setTypeface(linoType);
        proceed.setTypeface(linoType);
        addChild.setTypeface(linoType);
        playerList.setTypeface(linoType);
    }

}