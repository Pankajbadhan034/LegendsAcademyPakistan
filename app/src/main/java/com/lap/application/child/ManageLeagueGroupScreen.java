package com.lap.application.child;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.CoachLeagueBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.CoachLeagueDetailOneScreen;
import com.lap.application.coach.adapters.CoachLeagueAdapter;
import com.lap.application.coach.fragments.CoachLeagueFragment;
import com.lap.application.league.LeagueManageLeagueScreen;
import com.lap.application.parent.ParentBookPitchSummaryScreen;
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

public class ManageLeagueGroupScreen extends AppCompatActivity implements IWebServiceCallback {
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;
    ArrayList<CoachLeagueBean> coachLeagueBeanArrayList;
    ListView listView;
    ImageView backButton;
    String leagueId, academyId;
    Button joinLeague;
    private final String LEAGUE_MATCHES = "LEAGUE_MATCHES";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_league_group_screen);

        listView = findViewById(R.id.listView);
        backButton = findViewById(R.id.backButton);
        joinLeague = findViewById(R.id.joinLeague);

        leagueId = getIntent().getStringExtra("league_id");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CoachLeagueBean coachLeagueBean = coachLeagueBeanArrayList.get(i);

                if(coachLeagueBean.getRowStatus().equalsIgnoreCase("false")){
                    final Dialog dialog = new Dialog(ManageLeagueGroupScreen.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.parent_dialog_no_dates_found);
                    TextView text1 = dialog.findViewById(R.id.text1);
                    TextView ok = dialog.findViewById(R.id.ok);
                    text1.setText(coachLeagueBean.getRowMessage());
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                           // finish();
                        }
                    });
                    dialog.show();
                }else{
                    Intent intent = new Intent(ManageLeagueGroupScreen.this, CoachLeagueDetailOneScreen.class);
                    intent.putExtra("league_id", leagueId);
                    intent.putExtra("name", coachLeagueBean.getName());
                    intent.putExtra("sport_id", coachLeagueBean.getSportsId());
                    intent.putExtra("group_id", coachLeagueBean.getId());
                    startActivity(intent);
                }

            }
        });

        joinLeague.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent obj = new Intent(ManageLeagueGroupScreen.this, LeagueManageLeagueScreen.class);
                obj.putExtra("id", leagueId);
                obj.putExtra("sport_id", "");
                obj.putExtra("name", "League Group");
                startActivity(obj);
            }
        });

        getLeaugeNames();

    }

    private void getLeaugeNames() {
        if (Utilities.isNetworkAvailable(ManageLeagueGroupScreen.this)){

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("league_id", leagueId));

            String webServiceUrl = Utilities.BASE_URL + "league_tournament/league_groups";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            System.out.println("uid::" + loggedInUser.getId() + "token::" + loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ManageLeagueGroupScreen.this, nameValuePairList, LEAGUE_MATCHES, ManageLeagueGroupScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else{
            Toast.makeText(ManageLeagueGroupScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case LEAGUE_MATCHES:
                if (response == null) {
                    Toast.makeText(ManageLeagueGroupScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONObject jsonObject1 = responseObject.getJSONObject("data");
                            leagueId = jsonObject1.getString("league_id");
                            academyId = jsonObject1.getString("academy_id");
                            JSONArray dataArray = jsonObject1.getJSONArray("groupsData");
                            coachLeagueBeanArrayList = new ArrayList<>();

                            for(int i=0; i<dataArray.length(); i++){
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                CoachLeagueBean coachLeagueBean = new CoachLeagueBean();
                                coachLeagueBean.setId(jsonObject.getString("group_id"));
                              //  coachLeagueBean.setAcademy_id(jsonObject.getString("academy_id"));
                                coachLeagueBean.setName(jsonObject.getString("name"));
                              //  coachLeagueBean.setDescription(jsonObject.getString("description"));
                                coachLeagueBean.setFileName(jsonObject.getString("file_name"));
                                if(jsonObject.has("sports_id")){
                                    coachLeagueBean.setSportsId(jsonObject.getString("sports_id"));
                                }else{
                                    coachLeagueBean.setSportsId("");
                                }

                             //   coachLeagueBean.setSort(jsonObject.getString("sort"));
//                                coachLeagueBean.setCreatedAt(jsonObject.getString("created"));
//                                coachLeagueBean.setModified(jsonObject.getString("modified"));
//                                coachLeagueBean.setState(jsonObject.getString("state"));
                                if(jsonObject.has("image_url")){
                                    coachLeagueBean.setImageUrl(jsonObject.getString("image_url"));
                                }else{
                                    coachLeagueBean.setImageUrl("no image");
                                }

                                coachLeagueBean.setRowStatus(jsonObject.getString("league_matches_flag"));
                                coachLeagueBean.setRowMessage(jsonObject.getString("league_matches_message"));

                                coachLeagueBeanArrayList.add(coachLeagueBean);
                            }


                            CoachLeagueAdapter coachMidWeekPackageChildNamesAttendanceAdapter = new CoachLeagueAdapter(ManageLeagueGroupScreen.this, coachLeagueBeanArrayList);
                            listView.setAdapter(coachMidWeekPackageChildNamesAttendanceAdapter);

                        }else {
                            Toast.makeText(ManageLeagueGroupScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ManageLeagueGroupScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;


        }
    }


}