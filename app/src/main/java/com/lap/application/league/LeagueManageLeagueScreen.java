package com.lap.application.league;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.LeagueManageLeagueBean;
import com.lap.application.beans.UserBean;
import com.lap.application.hitPay.HitPayExampleForJoinLeagueScreen;
import com.lap.application.hitPay.HitPayPostWebServiceWithHeadersAsync;
import com.lap.application.league.adapters.LeagueManageLeagueAdapter;
import com.lap.application.parent.LeagueManageCustomEditScreen;
import com.lap.application.parent.ParentBookAcademySummaryScreen;
import com.lap.application.parent.ParentPaymentGatewayWebViewScreen;
import com.lap.application.parent.paymentGatewayUtilities.AvenuesParams;
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

public class LeagueManageLeagueScreen extends AppCompatActivity implements IWebServiceCallback {
    private final String TERM_COND_SETTING = "TERM_COND_SETTING";
    private final String UPDATE_TERM_COND_SETTING = "UPDATE_TERM_COND_SETTING";
    boolean statusGatewayScreen;

    String webviewHitPayurl;
    private final String HITPAY_STATUS = "HITPAY_STATUS";
    private final String HITPAY_WEB_SERVICE = "HITPAY_WEB_SERVICE";
    private final String HITPAY_ID_STORE_SERVICE = "HITPAY_ID_STORE_SERVICE";

    ArrayList<LeagueManageLeagueBean> leagueManageLeagueBeanArrayList = new ArrayList<>();
    private final String MANAGE_LEAGE_LISTING = "MANAGE_LEAGE_LISTING";
    private final String GET_ORDER_ID = "GET_ORDER_ID";
    ListView listView;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    String nameStr;
    String idStr;
    String sportIdStr;
    ImageView backButton;
    TextView title;
    String academy_currency;
    String net_Amount, leagueId, fee, groupId, ordersId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.league_manage_league_activity);
        listView = findViewById(R.id.listView);
        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);

        idStr = getIntent().getStringExtra("id");
        sportIdStr = getIntent().getStringExtra("sport_id");
        nameStr = getIntent().getStringExtra("name");

        title.setText(nameStr);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        academy_currency = sharedPreferences.getString("academy_currency", null);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                net_Amount = leagueManageLeagueBeanArrayList.get(i).getFee();
//                leagueId = leagueManageLeagueBeanArrayList.get(i).getLeagueId();
//                fee = leagueManageLeagueBeanArrayList.get(i).getFee();
//                groupId = leagueManageLeagueBeanArrayList.get(i).getGroupId();
//                getorderId();


                Intent obj = new Intent(LeagueManageLeagueScreen.this, LeagueManageCustomEditScreen.class);
                obj.putExtra("league_entry_id",leagueManageLeagueBeanArrayList.get(i).getId());
                obj.putExtra("groupId",groupId);
                obj.putExtra("leagueId",leagueId);
                obj.putExtra("fee",fee);
                startActivity(obj);
            }
        });

        getLeagueList();

    }


    private void getLeagueList(){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("league_id", idStr));

            String webServiceUrl = Utilities.BASE_URL + "join_league/join_league_group";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, MANAGE_LEAGE_LISTING, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getorderId(){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("league_id", leagueId));
            nameValuePairList.add(new BasicNameValuePair("group_id", groupId));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("fee", fee));

            String webServiceUrl = Utilities.BASE_URL + "join_league/join_league_payment";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, GET_ORDER_ID, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPaycheck(){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_status";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, HITPAY_STATUS, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPaysAPI(String api_key){
        if (Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", loggedInUser.getEmail()));
            nameValuePairList.add(new BasicNameValuePair("redirect_url", Utilities.BASE_URL+"payments/hitpay_response"));
            nameValuePairList.add(new BasicNameValuePair("webhook", Utilities.BASE_URL+"payments/hitpay_webhook"));
            nameValuePairList.add(new BasicNameValuePair("amount", net_Amount));
            nameValuePairList.add(new BasicNameValuePair("currency", academy_currency));
            nameValuePairList.add(new BasicNameValuePair("reference_number", ordersId +"- (Android)"));


            String webServiceUrl = ""+Utilities.HITPAY_API;

            HitPayPostWebServiceWithHeadersAsync postWebServiceAsync = new HitPayPostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, HITPAY_WEB_SERVICE, LeagueManageLeagueScreen.this, api_key);
            postWebServiceAsync.execute(webServiceUrl);
        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void hitPayIdStoreAPI(String id){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("hitpay_id", id));
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            nameValuePairList.add(new BasicNameValuePair("hitpay_salt", Utilities.SALT_HITPAY_JOIN_LEAGUE_KEY));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_refer_id";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, HITPAY_ID_STORE_SERVICE, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case MANAGE_LEAGE_LISTING:
                leagueManageLeagueBeanArrayList.clear();

                if(response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject jsonObject = dataArray.getJSONObject(i);
                                LeagueManageLeagueBean leagueManageLeagueBean = new LeagueManageLeagueBean();
                                leagueManageLeagueBean.setId(jsonObject.getString("id"));
                                leagueManageLeagueBean.setAcademyId(jsonObject.getString("academy_id"));
                                leagueManageLeagueBean.setName(jsonObject.getString("name"));
                                leagueManageLeagueBean.setLeagueId(jsonObject.getString("league_id"));
                                leagueManageLeagueBean.setGroupId(jsonObject.getString("group_id"));
                                leagueManageLeagueBean.setState(jsonObject.getString("state"));
                                leagueManageLeagueBean.setFee(jsonObject.getString("fee"));
                                leagueManageLeagueBeanArrayList.add(leagueManageLeagueBean);
                            }

                            LeagueManageLeagueAdapter leagueManageLeagueAdapter = new LeagueManageLeagueAdapter(LeagueManageLeagueScreen.this, leagueManageLeagueBeanArrayList);
                            listView.setAdapter(leagueManageLeagueAdapter);

                        } else {
                            Toast.makeText(LeagueManageLeagueScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case GET_ORDER_ID:
                if(response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            ordersId = responseObject.getString("orders_id");

                            hitPaycheck();

                        } else {
                            Toast.makeText(LeagueManageLeagueScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


                break;

            case HITPAY_STATUS:

                if (response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        System.out.println("REsponseHERE::"+response);
                        JSONObject responseObject = new JSONObject(response);

                        statusGatewayScreen = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        termSettingAPI();

//                        if(status) {
//
//                            JSONObject hitpayDetail = responseObject.getJSONObject("hitpay_detail");
//                            //String api_key = hitpayDetail.getString("api_key");
//                            hitPaysAPI(Utilities.HITPAY_JOIN_LEAGUE_KEY);
//
//                        }else{
//
//                            Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
//                            intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                            intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                            intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//                            intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
//                            intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
//                            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
//                            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
//                            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
//                            startActivity(intent);
//
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_WEB_SERVICE:
                if (response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject jsonObject = new JSONObject(response);

                        webviewHitPayurl = jsonObject.getString("url");
                        String redirect_url = jsonObject.getString("redirect_url");
                        String id = jsonObject.getString("id");
                        System.out.println("id:: "+id+" urlHER:: "+webviewHitPayurl+"::redirectURL:: "+redirect_url);
                        hitPayIdStoreAPI(id);


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                break;

            case HITPAY_ID_STORE_SERVICE:
                if (response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Intent obj = new Intent(LeagueManageLeagueScreen.this, HitPayExampleForJoinLeagueScreen.class);
                            obj.putExtra("url", webviewHitPayurl);
                            startActivity(obj);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            String sessionDescription = jsonObject.getString("session_url");
                            String facility_description = jsonObject.getString("facility_url");
                            String camp_description = jsonObject.getString("camp_url");
                            String midweek_session_description = jsonObject.getString("midweek_session_url");
                            String product_description = jsonObject.getString("product_url");
                            String league_description = jsonObject.getString("league_url");

                            String session_state = jsonObject.getString("session_state");
                            String facility_state = jsonObject.getString("facility_state");
                            String camp_state = jsonObject.getString("camp_state");
                            String midweek_session_state = jsonObject.getString("midweek_session_state");
                            String product_state = jsonObject.getString("product_state");
                            String league_state = jsonObject.getString("league_state");

                            if(league_state.equalsIgnoreCase("1")){
                                dialogBoxShow(league_description);
                            }else{
                                gatewayScreenIntent(statusGatewayScreen);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case UPDATE_TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(LeagueManageLeagueScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            gatewayScreenIntent(statusGatewayScreen);
                        }else{
                            Toast.makeText(LeagueManageLeagueScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageLeagueScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
    public void termSettingAPI(){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/get_term_condition_settings";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, TERM_COND_SETTING, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTermSettingAPI(){
        if(Utilities.isNetworkAvailable(LeagueManageLeagueScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/update_order_term_condition";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageLeagueScreen.this, nameValuePairList, UPDATE_TERM_COND_SETTING, LeagueManageLeagueScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageLeagueScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogBoxShow(String text){

        final Dialog dialogAddNotes = new Dialog(LeagueManageLeagueScreen.this);
        dialogAddNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddNotes.setContentView(R.layout.dialog_box_term_conditions);
        dialogAddNotes.setCancelable(true);

        Button submit = dialogAddNotes.findViewById(R.id.submit);
        Button cancel = dialogAddNotes.findViewById(R.id.cancel);
        //        TextView termText = dialogAddNotes.findViewById(R.id.termText);
//        termText.setText(Html.fromHtml(text));
//        termText.setMovementMethod(LinkMovementMethod.getInstance());

        WebView termText = dialogAddNotes.findViewById(R.id.termText);

//        termText.getSettings().setLoadWithOverviewMode(true);
//        termText.getSettings().setUseWideViewPort(true);

        termText.getSettings().setUseWideViewPort(true);
        termText.getSettings().setLoadWithOverviewMode(true);

        termText.getSettings().setSupportZoom(true);
        termText.getSettings().setBuiltInZoomControls(true);
        termText.getSettings().setDisplayZoomControls(false);

        System.out.println("urlHERE::"+text);
        termText.loadUrl(text);
        termText.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //System.out.println("Should override " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                //System.out.println("page finished  " + url);
                super.onPageFinished(view, url);
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNotes.dismiss();
                updateTermSettingAPI();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNotes.dismiss();
            }
        });

        dialogAddNotes.show();

    }

    public void gatewayScreenIntent(boolean status){
        if(status) {

        //    JSONObject hitpayDetail = responseObject.getJSONObject("hitpay_detail");
            //String api_key = hitpayDetail.getString("api_key");
            hitPaysAPI(Utilities.HITPAY_JOIN_LEAGUE_KEY);

        }else{

            Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
            intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
            intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
            intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
            intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
            startActivity(intent);

        }
    }
}