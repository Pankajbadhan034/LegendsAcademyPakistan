package com.lap.application.parent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.OnlineHistoryBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.ParentOnlineShoppingHistorydetailScreen;
import com.lap.application.parent.adapters.ParentOnlineStoreHistoryAdapter;
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

public class ParentOnlineShoppingHistoryFragment extends  Fragment implements IWebServiceCallback {

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

   // ImageView backButton;
   // TextView title;
    ListView bookedSessionsListView;

    private final String GET_SESSION_HISTORY = "GET_ONLINE_HISTORY";

    ArrayList<OnlineHistoryBean> sessionHistoryList = new ArrayList<>();
    ParentOnlineStoreHistoryAdapter parentSessionHistoryListingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parent_fragment_online_shopping_history, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getActivity().getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        parentSessionHistoryListingAdapter = new ParentOnlineStoreHistoryAdapter(getActivity(), sessionHistoryList);

      //  backButton = (ImageView) view.findViewById(R.id.backButton);
       // title = (TextView) view.findViewById(R.id.title);
        bookedSessionsListView = (ListView) view.findViewById(R.id.bookedSessionsListView);
        bookedSessionsListView.setAdapter(parentSessionHistoryListingAdapter);

       // title.setTypeface(linoType);

        getSessionHistory();

        bookedSessionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent historyDetail = new Intent(getActivity(), ParentOnlineShoppingHistorydetailScreen.class);
                historyDetail.putExtra("orderId", sessionHistoryList.get(position).getId());
                startActivity(historyDetail);
            }
        });

        return view;
    }
    private void getSessionHistory(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("offset", "0"));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));

            String webServiceUrl = Utilities.BASE_URL + "product/get_booked_products";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, GET_SESSION_HISTORY, ParentOnlineShoppingHistoryFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_SESSION_HISTORY:

                sessionHistoryList.clear();

                if(response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject obj = dataArray.getJSONObject(i);
                                OnlineHistoryBean onlineHistoryBean = new OnlineHistoryBean();

                                onlineHistoryBean.setId(obj.getString("id"));
                                onlineHistoryBean.setNetAmount(obj.getString("net_amount"));
                                onlineHistoryBean.setDisplayTotalCost(obj.getString("display_total_cost"));
                                onlineHistoryBean.setOrderDate(obj.getString("order_date"));
                                onlineHistoryBean.setState(obj.getString("state"));
                                onlineHistoryBean.setRefundAmount(obj.getString("refund_amount"));
                                onlineHistoryBean.setCustomDiscount(obj.getString("custom_discount"));
                                onlineHistoryBean.setDisplayCustomDiscount(obj.getString("display_custom_discount"));
                                onlineHistoryBean.setDisplayRefundAmount(obj.getString("display_refund_amount"));
                                onlineHistoryBean.setStateCode(obj.getString("state_code"));


                                sessionHistoryList.add(onlineHistoryBean);
                            }

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                parentSessionHistoryListingAdapter.notifyDataSetChanged();

                break;
        }
    }
    }
