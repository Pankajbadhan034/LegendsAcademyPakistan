package com.lap.application.league;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.LeagueJoinLeagueBean;
import com.lap.application.beans.UserBean;
import com.lap.application.league.adapters.LeagueTeamAdapter;
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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LeaguePlayersFragment extends Fragment implements IWebServiceCallback {
    ArrayList<LeagueJoinLeagueBean> teamListingBeanArrayList = new ArrayList<>();
    private final String TEAM_LISTING = "TEAM_LISTING";
    ListView listView;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Button addTeam;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.league_player_fragment, container, false);
        listView = view.findViewById(R.id.listView);
        addTeam = view.findViewById(R.id.addTeam);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent obj = new Intent(getActivity(), LeaguePlayerEditScreen.class);
                obj.putExtra("id", teamListingBeanArrayList.get(i).getId());
                startActivity(obj);
//                Intent obj = new Intent(getActivity(), LeagueManageLeagueScreen.class);
//                obj.putExtra("id", leagueJoinLeagueBeanArrayList.get(i).getId());
//                obj.putExtra("sport_id", leagueJoinLeagueBeanArrayList.get(i).getSportId());
//                obj.putExtra("name", leagueJoinLeagueBeanArrayList.get(i).getName());
//                startActivity(obj);
            }
        });

        addTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent obj = new Intent(getActivity(), LeaguePlayerAddScreen.class);
                startActivity(obj);
            }
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLeagueList();
    }

    private void getLeagueList(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));

            String webServiceUrl = Utilities.BASE_URL + "join_league/parent_player_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, TEAM_LISTING, LeaguePlayersFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case TEAM_LISTING:
                teamListingBeanArrayList.clear();

                if(response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                LeagueJoinLeagueBean leagueJoinLeagueBean = new LeagueJoinLeagueBean();
                                leagueJoinLeagueBean.setId(jsonObject.getString("id"));
                                leagueJoinLeagueBean.setName(jsonObject.getString("name"));
                                teamListingBeanArrayList.add(leagueJoinLeagueBean);
                            }

                            LeagueTeamAdapter leagueTeamAdapter = new LeagueTeamAdapter(getActivity(), teamListingBeanArrayList);
                            listView.setAdapter(leagueTeamAdapter);

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}