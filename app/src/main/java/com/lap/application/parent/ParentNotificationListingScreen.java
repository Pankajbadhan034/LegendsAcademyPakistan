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

import androidx.appcompat.app.AppCompatActivity;

public class ParentNotificationListingScreen extends AppCompatActivity implements IWebServiceCallback {
    TextView title;
    ImageView backButton;

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;
    private final String GET_NOTFICATIONS = "GET_NOTFICATIONS";
    ListView listView;
    int offset=0;
    SwipyRefreshLayout  mSwipyRefreshLayout;
    ArrayList<ParentNotficationBean> parentNotficationBeanArrayList = new ArrayList<>();
    ParentNotificationAdapter parentNotificationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_notification_listing_activity);
        title = (TextView) findViewById(R.id.title);
        backButton = (ImageView) findViewById(R.id.backButton);
        listView = findViewById(R.id.listView);
        mSwipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swipyrefreshlayout);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        title.setTypeface(linoType);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent parentDashboard = new Intent(ParentNotificationListingScreen.this, ParentMainScreen.class);
                parentDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(parentDashboard);

            }
        });

        mSwipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                offset += 1;
                getNotifications();
            }
        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                    final Dialog dialog = new Dialog(ParentNotificationListingScreen.this);
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.setContentView(R.layout.parent_dialog_notfication);
//
//                    TextView text =  dialog.findViewById(R.id.text);
//                    text.setText(Html.fromHtml(parentNotficationBeanArrayList.get(i).getMessage()));
//                    dialog.show();
//            }
//        });

        getNotifications();
        parentNotificationAdapter = new ParentNotificationAdapter(ParentNotificationListingScreen.this,parentNotficationBeanArrayList);
        listView.setAdapter(parentNotificationAdapter);

    }

    @Override
    public void onBackPressed() {
        Intent parentDashboard = new Intent(ParentNotificationListingScreen.this, ParentMainScreen.class);
        parentDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(parentDashboard);
    }

    private void getNotifications(){
        if(Utilities.isNetworkAvailable(ParentNotificationListingScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("users_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("offset", "" + offset));

            String webServiceUrl = Utilities.BASE_URL + "account/push_notify_count";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentNotificationListingScreen.this, nameValuePairList, GET_NOTFICATIONS, ParentNotificationListingScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentNotificationListingScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_NOTFICATIONS:
                if(response == null) {
                    Toast.makeText(ParentNotificationListingScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ParentNotificationListingScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mSwipyRefreshLayout.setRefreshing(false);
                        Toast.makeText(ParentNotificationListingScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}