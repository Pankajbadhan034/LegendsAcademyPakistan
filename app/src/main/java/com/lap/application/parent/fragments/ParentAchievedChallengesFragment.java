package com.lap.application.parent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.ChallengeBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.ParentChallengeDetailScreen;
import com.lap.application.parent.adapters.ParentActiveChallengesAdapter;
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

public class ParentAchievedChallengesFragment extends Fragment implements IWebServiceCallback {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    TextView achieved;
    ListView achievedChallengesListView;
    ParentActiveChallengesAdapter parentActiveChallengesAdapter;
    ArrayList<ChallengeBean> challengesList = new ArrayList<>();

    private final String GET_ACHIEVED_CHALLENGES = "GET_ACHIEVED_CHALLENGES";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        helvetica = Typeface.createFromAsset(getActivity().getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        parentActiveChallengesAdapter = new ParentActiveChallengesAdapter(getActivity(), challengesList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parent_fragment_achieved_challeges, container, false);

        achieved = (TextView) view.findViewById(R.id.achieved);
        achievedChallengesListView = (ListView) view.findViewById(R.id.achievedChallengesListView);
        achievedChallengesListView.setAdapter(parentActiveChallengesAdapter);

        achieved.setTypeface(linoType);

        getAchievedChallengesListing();

        achievedChallengesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChallengeBean clickedOnChallenge = challengesList.get(position);
                Intent challengeDetail = new Intent(getActivity(), ParentChallengeDetailScreen.class);
                challengeDetail.putExtra("clickedOnChallenge", clickedOnChallenge);
                startActivity(challengeDetail);
            }
        });

        return view;
    }

    private void getAchievedChallengesListing(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("parent_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("status", "2"));

            String webServiceUrl = Utilities.BASE_URL + "challenges/list_for_parent";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, GET_ACHIEVED_CHALLENGES, ParentAchievedChallengesFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_ACHIEVED_CHALLENGES:

                challengesList.clear();

                if(response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            ChallengeBean challengeBean;
                            for (int i=0;i<dataArray.length();i++) {
                                JSONObject challengeObject = dataArray.getJSONObject(i);
                                challengeBean = new ChallengeBean();

                                challengeBean.setChildId(challengeObject.getString("child_id"));
                                challengeBean.setChildName(challengeObject.getString("child_name"));
//                                challengeBean.setChildImageUrl(challengeObject.getString("child_image_url"));
                                challengeBean.setChallengeId(challengeObject.getString("challenge_id"));
//                                challengeBean.setChallengeImageUrl(challengeObject.getString("challenge_image_url"));
//                                challengeBean.setChallengeVideoUrl(challengeObject.getString("challenge_video_url"));
                                challengeBean.setChallengeTitle(challengeObject.getString("challenge_title"));
                                challengeBean.setChallengeDescription(challengeObject.getString("challenge_description"));
                                challengeBean.setOwnerId(challengeObject.getString("owner_id"));
                                challengeBean.setCoachName(challengeObject.getString("coach_name"));
                                challengeBean.setCategoryId(challengeObject.getString("category_id"));
                                challengeBean.setCategoryName(challengeObject.getString("category_name"));
                                challengeBean.setChallengersId(challengeObject.getString("challengers_id"));
                                challengeBean.setExpiration(challengeObject.getString("expiration"));
                                challengeBean.setShowExpiration(challengeObject.getString("expiration_formatted"));
                                challengeBean.setTargetScore(challengeObject.getString("target_score"));
                                challengeBean.setTargetTime(challengeObject.getString("target_time"));
                                challengeBean.setTargetTimeType(challengeObject.getString("target_time_type"));
                                challengeBean.setApprovalRequired(challengeObject.getString("approval_required"));
                                challengeBean.setAchievedScore(challengeObject.getString("achieved_score"));
                                challengeBean.setAchievedTime(challengeObject.getString("achieved_time"));
                                challengeBean.setChallengeResult(challengeObject.getString("challenge_result"));
                                challengeBean.setApprovedStatus(challengeObject.getString("approved_status"));

                                challengeBean.setExpirationTimeFormatted(challengeObject.getString("expiration_time_formatted"));
                                challengeBean.setTargetTimeTypeFormatted(challengeObject.getString("target_time_type_formatted"));
                                challengeBean.setChallengeResultFormatted(challengeObject.getString("challenge_result_formatted"));

                                challengesList.add(challengeBean);
                            }

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                parentActiveChallengesAdapter.notifyDataSetChanged();

                break;
        }
    }
}