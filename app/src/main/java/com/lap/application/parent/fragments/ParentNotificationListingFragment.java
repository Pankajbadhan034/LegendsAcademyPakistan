package com.lap.application.parent.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.ParentNotficationBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.adapters.ParentNotificationAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentNotificationListingFragment extends Fragment implements IWebServiceCallback {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;
    private final String GET_NOTFICATIONS = "GET_NOTFICATIONS";
    ListView listView;
    int offset=0;
    SwipyRefreshLayout mSwipyRefreshLayout;
    ArrayList<ParentNotficationBean> parentNotficationBeanArrayList = new ArrayList<>();
    ParentNotificationAdapter parentNotificationAdapter;
    String appClick;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parent_notification_listing_fragment, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getActivity().getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        listView = view.findViewById(R.id.listView);
        mSwipyRefreshLayout = view.findViewById(R.id.swipyrefreshlayout);

        appClick = getActivity().getIntent().getStringExtra("appClick");

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                offset += 1;
                getNotifications();
            }
        });

        getNotifications();
        parentNotificationAdapter = new ParentNotificationAdapter(getActivity(),parentNotficationBeanArrayList);
        listView.setAdapter(parentNotificationAdapter);

        return view;
    }

    private void getNotifications(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("users_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("offset", "" + offset));

            String webServiceUrl = Utilities.BASE_URL + "account/push_notify_count";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, GET_NOTFICATIONS, ParentNotificationListingFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_NOTFICATIONS:
                if(response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0; i<dataArray.length(); i++){
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                ParentNotficationBean parentNotficationBean = new ParentNotficationBean();
                                parentNotficationBean.setTitle(jsonObject.getString("title"));
                                parentNotficationBean.setMessage(jsonObject.getString("message"));
                                parentNotficationBean.setCreated_at_date(jsonObject.getString("created_at_date"));
                                parentNotficationBean.setCreated_at_time(jsonObject.getString("created_at_time"));
                                parentNotficationBeanArrayList.add(parentNotficationBean);
                            }
                            mSwipyRefreshLayout.setRefreshing(false);
                            parentNotificationAdapter.notifyDataSetChanged();

                        } else {
                            mSwipyRefreshLayout.setRefreshing(false);
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mSwipyRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }


}
