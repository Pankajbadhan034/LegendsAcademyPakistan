package com.lap.application.parent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.lap.application.R;
import com.lap.application.beans.LeagueJoinLeagueBean;
import com.lap.application.beans.LeagueManageCustomEditBean;
import com.lap.application.beans.LeagueTeamDataBean;
import com.lap.application.beans.UserBean;
import com.lap.application.hitPay.HitPayExampleForJoinLeagueScreen;
import com.lap.application.hitPay.HitPayPostWebServiceWithHeadersAsync;
import com.lap.application.league.adapters.LeagueJoinLeagueAdapter;
import com.lap.application.league.adapters.LeagueSelectTeamAdapter;
import com.lap.application.parent.paymentGatewayUtilities.AvenuesParams;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class LeagueManageCustomEditScreen extends AppCompatActivity implements IWebServiceCallback {
    int posFinal;
    String totalAmountStr;
    private final String TERM_COND_SETTING = "TERM_COND_SETTING";
    private final String UPDATE_TERM_COND_SETTING = "UPDATE_TERM_COND_SETTING";
    private final String HITPAY_STATUS = "HITPAY_STATUS";
    private final String HITPAY_WEB_SERVICE = "HITPAY_WEB_SERVICE";
    private final String HITPAY_ID_STORE_SERVICE = "HITPAY_ID_STORE_SERVICE";
    private final String MANAGE_LEAGE_LISTING = "MANAGE_LEAGE_LISTING";
    private final String GET_ORDER_ID = "GET_ORDER_ID";
    private final String GET_SELECTED_CLUB = "GET_SELECTED_CLUB";
    boolean statusGatewayScreen;
    String webviewHitPayurl;

    String ordersId;

    String existingTeamCheck="";
    ArrayList<LeagueTeamDataBean> leagueTeamDataBeanArrayList;
    String totalNumberOfRowsStr;
    DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private final String APPLY_PROMO_CODE = "APPLY_PROMO_CODE";
    private final String BOOK_JOIN_LEAGUE ="BOOK_JOIN_LEAGUE";
    ListView listView;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    private final String JOIN_LEAGUE_SUMMARY = "JOIN_LEAGUE_SUMMARY";
    String academy_currency;
    ImageView backButton;
    TextView title;
    String league_entry_id, groupId, leagueId, fee;
    TextView leagueName;
    ArrayList<LeagueManageCustomEditBean> teamArrayList = new ArrayList<>();
    ArrayList<LeagueManageCustomEditBean> groupArrayList = new ArrayList<>();
    ArrayList<LeagueManageCustomEditBean> clubArrayList = new ArrayList<>();
    EditText totalNumberOfRows;
    Button submit;
    TextView parentNameTV;
    TextView totalAmount;
    EditText promoCodeEditText;
    Button applyPromoCode;
    Button cancelPromoCode;
    TextView promoCodeDiscount;
    TextView netAmount;
    TextView makePayment;
    String strPromoCode = "";
    double promoCodeDeductAmount = 0;
    double dblNetAmount = 0;
    String joinArray = "";
    TotalLinesAdapter totalLinesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.league_manage_custom_edit_activity);
        listView = findViewById(R.id.listView);
        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
        leagueName = findViewById(R.id.leagueName);
        totalNumberOfRows = findViewById(R.id.totalNumberOfRows);
        submit = findViewById(R.id.submit);
        parentNameTV = findViewById(R.id.parentNameTV);
        totalAmount = findViewById(R.id.totalAmount);

        promoCodeEditText = findViewById(R.id.promoCodeEditText);
        applyPromoCode = findViewById(R.id.applyPromoCode);
        cancelPromoCode = findViewById(R.id.cancelPromoCode);
        promoCodeDiscount = findViewById(R.id.promoCodeDiscount);
        netAmount = findViewById(R.id.netAmount);
        makePayment = findViewById(R.id.makePayment);


        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        academy_currency = sharedPreferences.getString("academy_currency", null);

        league_entry_id = getIntent().getStringExtra("league_entry_id");
//        groupId = getIntent().getStringExtra("groupId");
//        leagueId = getIntent().getStringExtra("leagueId");
//        fee = getIntent().getStringExtra("fee");

        parentNameTV.setText(loggedInUser.getFullName());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAmount.setText("0.00 "+academy_currency);
                promoCodeEditText.setText("");
                promoCodeDiscount.setText("0.00 "+academy_currency);
                netAmount.setText("0.00 "+academy_currency);
                listView.setAdapter(null);
                totalNumberOfRowsStr = totalNumberOfRows.getText().toString().trim();
                if(totalNumberOfRowsStr==null || totalNumberOfRowsStr.isEmpty() || totalNumberOfRowsStr.equalsIgnoreCase("0")){
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Please enter number of teams you want to register", Toast.LENGTH_SHORT).show();

                }else{
                    leagueTeamDataBeanArrayList = new ArrayList<>();
                    for(int i=0; i<Integer.parseInt(totalNumberOfRowsStr); i++){
                        LeagueTeamDataBean leagueTeamDataBean = new LeagueTeamDataBean();
                        leagueTeamDataBean.setTeamName("Team Name");
                        leagueTeamDataBean.setSelectTeam("Select Team");
                        leagueTeamDataBean.setSelectGroup("Select Group");
                        leagueTeamDataBean.setSelectClub("Select Club");
                        leagueTeamDataBean.setCheckTeamSelect("");
                        leagueTeamDataBeanArrayList.add(leagueTeamDataBean);
                    }
                    totalLinesAdapter = new TotalLinesAdapter(LeagueManageCustomEditScreen.this, leagueTeamDataBeanArrayList);
                    listView.setAdapter(totalLinesAdapter);
                }

            }
        });

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(totalNumberOfRowsStr==null || totalNumberOfRowsStr.isEmpty() || totalNumberOfRowsStr.equalsIgnoreCase("0")){
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Please enter number of teams you want to register", Toast.LENGTH_SHORT).show();

                }else{

                    for(int i=0; i<leagueTeamDataBeanArrayList.size(); i++){
                        if(leagueTeamDataBeanArrayList.get(i).getTeamName().equalsIgnoreCase("Team Name") &&
                                leagueTeamDataBeanArrayList.get(i).getSelectTeam().equalsIgnoreCase("Select Team")){
                            Toast.makeText(LeagueManageCustomEditScreen.this, "Team and group is required", Toast.LENGTH_SHORT).show();
                            return;
                        }else if(leagueTeamDataBeanArrayList.get(i).getSelectGroup().equalsIgnoreCase("Select Group")){
//                            Toast.makeText(LeagueManageCustomEditScreen.this, "Please select group", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LeagueManageCustomEditScreen.this, "Team and group is required", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }



                     for(int i=0; i<leagueTeamDataBeanArrayList.size(); i++){
                    if(leagueTeamDataBeanArrayList.get(i).getTeamName().equalsIgnoreCase("Team Name") &&
                            leagueTeamDataBeanArrayList.get(i).getSelectTeam().equalsIgnoreCase("Select Team")){
                        Toast.makeText(LeagueManageCustomEditScreen.this, "Team and group is required", Toast.LENGTH_SHORT).show();
                        return;
                    }else if(leagueTeamDataBeanArrayList.get(i).getSelectGroup().equalsIgnoreCase("Select Group")){
//                        Toast.makeText(LeagueManageCustomEditScreen.this, "Please select group", Toast.LENGTH_SHORT).show();
                        Toast.makeText(LeagueManageCustomEditScreen.this, "Team and group is required", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        for(int j=0; j<leagueTeamDataBeanArrayList.size(); j++){
                            System.out.println("dataHereTeamSelected::"+leagueTeamDataBeanArrayList.get(j).getCheckTeamSelect());
                            if(i==j){

                            }else{
                                System.out.println("teamcheck::"+j+" : "+leagueTeamDataBeanArrayList.get(j).getCheckTeamSelect() +", teamcheck:: "+i+" : "+leagueTeamDataBeanArrayList.get(i).getCheckTeamSelect());
                                if(leagueTeamDataBeanArrayList.get(j).getCheckTeamSelect().equalsIgnoreCase("selected") && leagueTeamDataBeanArrayList.get(i).getCheckTeamSelect().equalsIgnoreCase("selected")){
                                    if(leagueTeamDataBeanArrayList.get(i).getSelectTeam().equalsIgnoreCase(leagueTeamDataBeanArrayList.get(j).getSelectTeam()) &&
                                            leagueTeamDataBeanArrayList.get(i).getSelectGroup().equalsIgnoreCase(leagueTeamDataBeanArrayList.get(j).getSelectGroup())){
                                            Toast.makeText(LeagueManageCustomEditScreen.this, "You cannot select the same data", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        }

                            }
                        }


                }



                          joinArray = "[";
                         for(LeagueTeamDataBean leagueTeamDataBean : leagueTeamDataBeanArrayList) {
                             String teamType="";
                             String teamName="";
                             String groupId = leagueTeamDataBean.getGroupId();
                             String existingTeamId = "";
                             String clubId="";

                             if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("")){
                             }else if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("selected")){
                                 teamType = "old";
                                 teamName = leagueTeamDataBean.getSelectTeam();
                                 existingTeamId = leagueTeamDataBean.getTeamId();
                             }else if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("edited")){
                                 teamType = "new";
                                 teamName = leagueTeamDataBean.getTeamName();
                             }

                             try{
                                 clubId = leagueTeamDataBean.getClubId();

                                 if(clubId==null || clubId.equalsIgnoreCase("-1")){
                                     clubId = "";
                                 }else{
                                     clubId = leagueTeamDataBean.getClubId();
                                 }

                             }catch (Exception e){
                                 e.printStackTrace();
                             }

                             joinArray += "{\"team_type\": \""+teamType+"\",\"team_name\": \""+teamName+"\", \"club_id\": \""+clubId+"\", \"existing_team\": \""+existingTeamId+"\",  \"group_id\": \""+groupId+"\"},";

                         }

                         if (joinArray != null && joinArray.length() > 0 && joinArray.charAt(joinArray.length()-1)==',') {
                             joinArray = joinArray.substring(0, joinArray.length()-1);
                         }

                         joinArray += "]";

                    }
                }


                if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {
                    String strNetAmount = dblNetAmount+"";

                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                    nameValuePairList.add(new BasicNameValuePair("team_counter", totalNumberOfRowsStr));
                    nameValuePairList.add(new BasicNameValuePair("join_array", joinArray));
                    nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
                    nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
                    nameValuePairList.add(new BasicNameValuePair("league_id", leagueId));
                    nameValuePairList.add(new BasicNameValuePair("group_id", groupId));
                    nameValuePairList.add(new BasicNameValuePair("fee", fee));
                    nameValuePairList.add(new BasicNameValuePair("net_amt", totalAmountStr));
                    nameValuePairList.add(new BasicNameValuePair("existing_team_record", existingTeamCheck));
                    nameValuePairList.add(new BasicNameValuePair("code_value", strPromoCode));

                    String webServiceUrl = Utilities.BASE_URL + "join_league/join_league_payment";

                    ArrayList<String> headers = new ArrayList<>();
                    headers.add("X-access-uid:"+loggedInUser.getId());
                    headers.add("X-access-token:"+loggedInUser.getToken());

                    PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, BOOK_JOIN_LEAGUE, LeagueManageCustomEditScreen.this, headers);
                    postWebServiceAsync.execute(webServiceUrl);

                } else {
                    Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }





//                for(LeagueTeamDataBean leagueTeamDataBean : leagueTeamDataBeanArrayList) {
//
//                    for(LeagueTeamDataBean leagueTeamDataBean1 : leagueTeamDataBeanArrayList){
//                        if(leagueTeamDataBean1.getCheckTeamSelect().equalsIgnoreCase("selected")){
//                            if(leagueTeamDataBean.getTeamName().equalsIgnoreCase(leagueTeamDataBean1.getTeamName()) && leagueTeamDataBean.getSelectGroup().equalsIgnoreCase(leagueTeamDataBean1.getSelectGroup())){
//                                Toast.makeText(LeagueManageCustomEditScreen.this, "You cannot select the same data", Toast.LENGTH_SHORT).show();
//                                return;
//                            }
//                        }
//                    }
//
//                }




            }
        });


        applyPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(promoCodeEditText.getWindowToken(), 0);

                strPromoCode = promoCodeEditText.getText().toString().trim();

                dblNetAmount = dblNetAmount + promoCodeDeductAmount;
                promoCodeDeductAmount = 0;

                promoCodeDiscount.setText("0.00 "+academy_currency);
                if(dblNetAmount == 0) {
                    netAmount.setText("0.00 "+academy_currency);
                } else {
                    netAmount.setText(decimalFormat.format(dblNetAmount)+" "+academy_currency);
                }

                if(strPromoCode == null || strPromoCode.isEmpty()) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Please enter Promo Code", Toast.LENGTH_SHORT).show();
                } else {
                    if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {

//                        sessionsDetail = "[";
//                        sessionsDetail += "{\"sessions_id\":\""+session_id+"\",\"child_ids\":\""+child_ids+"\",\"is_trial\":\"0\"},";
//                        sessionsDetail += "]";
//

                        List<NameValuePair> nameValuePairList = new ArrayList<>();
                        nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));
                        nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()+""));
                        nameValuePairList.add(new BasicNameValuePair("amount", dblNetAmount+""));
                        nameValuePairList.add(new BasicNameValuePair("module_type", "6"));
                        nameValuePairList.add(new BasicNameValuePair("promo_code", strPromoCode));

                        String webServiceUrl = Utilities.BASE_URL + "join_league/apply_promo_code_league";

                        ArrayList<String> headers = new ArrayList<>();
                        headers.add("X-access-uid:"+loggedInUser.getId());
                        headers.add("X-access-token:"+loggedInUser.getToken());

                        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, APPLY_PROMO_CODE, LeagueManageCustomEditScreen.this, headers);
                        postWebServiceAsync.execute(webServiceUrl);

                    } else {
                        Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               cancelPromo();
            }
        });

        joinLeagueSummary();

    }

    public void cancelPromo(){
        promoCodeEditText.setText("");
        strPromoCode = promoCodeEditText.getText().toString().trim();

        dblNetAmount = dblNetAmount + promoCodeDeductAmount;
        promoCodeDeductAmount = 0;

        promoCodeDiscount.setText("0.00 "+academy_currency);
        if(dblNetAmount == 0) {
            netAmount.setText("0.00 "+academy_currency);
        } else {
            netAmount.setText(decimalFormat.format(dblNetAmount)+" "+academy_currency);
        }
    }

    public void joinLeagueSummary(){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("league_entry_id", league_entry_id));
            String webServiceUrl = Utilities.BASE_URL + "join_league/join_league_summary";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, JOIN_LEAGUE_SUMMARY, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case JOIN_LEAGUE_SUMMARY:
                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            String leagueNameStr = jsonObject.getString("league_name");
                            String id = jsonObject.getString("id");
                            String academy_id = jsonObject.getString("academy_id");
                            leagueId = jsonObject.getString("league_id");
                            groupId = jsonObject.getString("group_id");
                            fee = jsonObject.getString("fee");
                            String state = jsonObject.getString("state");

                            leagueName.setText(leagueNameStr);

                            LeagueManageCustomEditBean leagueManageCustomEditBeanFix = new LeagueManageCustomEditBean();
                            leagueManageCustomEditBeanFix.setId("-1");
                            leagueManageCustomEditBeanFix.setName("Select Team");
                            teamArrayList.add(leagueManageCustomEditBeanFix);

                            JSONArray jsonArray = jsonObject.getJSONArray("existing_team_data");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                LeagueManageCustomEditBean leagueManageCustomEditBean = new LeagueManageCustomEditBean();
                                leagueManageCustomEditBean.setId(jsonObject1.getString("id"));
                                leagueManageCustomEditBean.setName(jsonObject1.getString("name"));
                                teamArrayList.add(leagueManageCustomEditBean);
                            }

                            if(jsonArray.length()==0){
                                existingTeamCheck = "0";
                            }else{
                                existingTeamCheck = "1";
                            }


                            LeagueManageCustomEditBean leagueManageCustomEditBeanFix2 = new LeagueManageCustomEditBean();
                            leagueManageCustomEditBeanFix2.setId("-1");
                            leagueManageCustomEditBeanFix2.setName("Select Group");
                            groupArrayList.add(leagueManageCustomEditBeanFix2);

                            JSONArray jsonArray2 = jsonObject.getJSONArray("group_data");
                            for(int i=0; i<jsonArray2.length(); i++){
                                JSONObject jsonObject1 = jsonArray2.getJSONObject(i);
                                LeagueManageCustomEditBean leagueManageCustomEditBean = new LeagueManageCustomEditBean();
                                leagueManageCustomEditBean.setId(jsonObject1.getString("group_id"));
                                leagueManageCustomEditBean.setName(jsonObject1.getString("name"));
                                leagueManageCustomEditBean.setFee(jsonObject1.getString("fee"));
                                groupArrayList.add(leagueManageCustomEditBean);
                            }


                            LeagueManageCustomEditBean leagueManageCustomEditBeanFix3 = new LeagueManageCustomEditBean();
                            leagueManageCustomEditBeanFix3.setId("-1");
                            leagueManageCustomEditBeanFix3.setName("Select Club");
                            clubArrayList.add(leagueManageCustomEditBeanFix3);

                            JSONArray jsonArray3 = jsonObject.getJSONArray("clubs");
                            for(int i=0; i<jsonArray3.length(); i++){
                                JSONObject jsonObject1 = jsonArray3.getJSONObject(i);
                                LeagueManageCustomEditBean leagueManageCustomEditBean = new LeagueManageCustomEditBean();
                                leagueManageCustomEditBean.setId(jsonObject1.getString("id"));
                                leagueManageCustomEditBean.setName(jsonObject1.getString("name"));
                                clubArrayList.add(leagueManageCustomEditBean);
                            }



                        }

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                }
                break;

            case APPLY_PROMO_CODE:

                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        Toast.makeText(LeagueManageCustomEditScreen.this, message, Toast.LENGTH_LONG).show();

                        if(status) {

                            String strDeductAmount = responseObject.getString("deduct_amt");
                            try{
                                promoCodeDeductAmount = Double.parseDouble(strDeductAmount);
                                System.out.println("promoCodeDeductAmount::"+promoCodeDeductAmount);

                                promoCodeDiscount.setText(strDeductAmount+" "+academy_currency);
                                System.out.println("NET_AMOUNT::"+dblNetAmount+"::PROMO_CODE::"+promoCodeDeductAmount);
                                dblNetAmount = dblNetAmount - promoCodeDeductAmount;

                                if(dblNetAmount == 0) {
                                    netAmount.setText("0.00 "+academy_currency);
                                    makePayment.setText("PROCEED");
                                } else {
                                    netAmount.setText(dblNetAmount+" "+academy_currency);
                                }

                            } catch (NumberFormatException e){
                                Toast.makeText(LeagueManageCustomEditScreen.this, "Invalid discount", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case BOOK_JOIN_LEAGUE:

                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {

                            String net_Amount = responseObject.getString("net_amount");
                            JSONObject dataobj = responseObject.getJSONObject("data");
                            ordersId = responseObject.getString("orders_id");

                            if(netAmount.getText().toString().trim().equals("0.00") || netAmount.getText().toString().trim().equals("0") || netAmount.getText().toString().trim().equals(".0 "+academy_currency)  || netAmount.getText().toString().trim().equals(".00 "+academy_currency) || netAmount.getText().toString().trim().equals("0.00 "+academy_currency) || netAmount.getText().toString().trim().equals("0.0 "+academy_currency) || netAmount.getText().toString().trim().equals("0 "+academy_currency)
                            || netAmount.getText().toString().trim().equals("0.0 "+academy_currency)){
                                System.out.println("in_if_condition");
                                Intent mainScreen = new Intent(LeagueManageCustomEditScreen.this, ParentNetPaymentZeroScreen.class);
                                mainScreen.putExtra("orderID", ordersId);
                                startActivity(mainScreen);

                            }else{
                                System.out.println("netAmount::"+net_Amount+"::OrderID::"+ordersId);
                                hitPaycheck();
                            }



                        } else {
//                            Toast.makeText(ParentBookMidWeekProceedScreen.this, message, Toast.LENGTH_SHORT).show();
                            System.out.println("in_else_2_condition");
                            final Dialog dialog = new Dialog(LeagueManageCustomEditScreen.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.parent_dialog_error);

                            TextView messageTextView = (TextView) dialog.findViewById(R.id.message);
                            TextView okayButton = (TextView) dialog.findViewById(R.id.okayButton);

                            messageTextView.setText(message);

                            okayButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case GET_ORDER_ID:
                if(response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            ordersId = responseObject.getString("orders_id");

                            hitPaycheck();

                        } else {
                            Toast.makeText(LeagueManageCustomEditScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }


                break;
            case HITPAY_STATUS:

                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case HITPAY_WEB_SERVICE:
                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Intent obj = new Intent(LeagueManageCustomEditScreen.this, HitPayExampleForJoinLeagueScreen.class);
                            obj.putExtra("url", webviewHitPayurl);
                            startActivity(obj);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case UPDATE_TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            gatewayScreenIntent(statusGatewayScreen);
                        }else{
                            Toast.makeText(LeagueManageCustomEditScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;


            case GET_SELECTED_CLUB:
                if(response == null) {
                    Toast.makeText(LeagueManageCustomEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                          JSONObject jsonObject = responseObject.getJSONObject("data");
                          String clubId = jsonObject.getString("club");
                          String club = jsonObject.getString("club_name");


//                          for(int i=0; i<clubArrayList.size(); i++){
//                              if(leagueTeamDataBeanArrayList.get(i).getClubId().equalsIgnoreCase(clubId)){
//                                  leagueTeamDataBeanArrayList.get(i).setSelectClub(clubArrayList.get(i).getName());
//                                  leagueTeamDataBeanArrayList.get(i).setClubId(clubArrayList.get(i).getId());
//                                  totalLinesAdapter.notifyDataSetChanged();
//                              }
//                          }

                            leagueTeamDataBeanArrayList.get(posFinal).setClubId(clubId);
                            leagueTeamDataBeanArrayList.get(posFinal).setSelectClub(club);
                            totalLinesAdapter.notifyDataSetChanged();


                        }

//                        else {
//                            Toast.makeText(LeagueManageCustomEditScreen.this, message, Toast.LENGTH_SHORT).show();
//                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueManageCustomEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

        }
    }

    public class TotalLinesAdapter extends BaseAdapter {
        SharedPreferences sharedPreferences;
        UserBean loggedInUser;

        Context context;
        ArrayList<LeagueTeamDataBean> intList;
        String promovalue;
        LayoutInflater layoutInflater;

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions roundOptions;

        public TotalLinesAdapter(Context context, ArrayList<LeagueTeamDataBean> intList) {
            this.context = context;
            this.intList = intList;
            this.promovalue = promovalue;
            this.layoutInflater = LayoutInflater.from(context);

            sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
            if (jsonLoggedInUser != null) {
                loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
            }

            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            roundOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.placeholder)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(1000))
                    .build();
        }

        @Override
        public int getCount() {
            return intList.size();
        }

        @Override
        public Object getItem(int position) {
            return intList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.league_join_total_list_adapter, null);

            TextView teamName = (TextView) convertView.findViewById(R.id.teamName);
            TextView selectTeam = (TextView) convertView.findViewById(R.id.selectTeam);
            TextView selectGroup = (TextView) convertView.findViewById(R.id.selectGroup);
            TextView orTV = (TextView) convertView.findViewById(R.id.orTV);
            TextView selectClub = convertView.findViewById(R.id.selectClub);

            final LeagueTeamDataBean leagueTeamDataBean = intList.get(position);

            teamName.setText(leagueTeamDataBean.getTeamName());
            selectTeam.setText(leagueTeamDataBean.getSelectTeam());
            selectGroup.setText(leagueTeamDataBean.getSelectGroup());
            selectClub.setText(leagueTeamDataBean.getSelectClub());

            if(existingTeamCheck.equalsIgnoreCase("0")){
                selectTeam.setVisibility(View.GONE);
                orTV.setVisibility(View.GONE);
            }

            if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("")){

            }else if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("selected")){
                teamName.setBackgroundColor(context.getResources().getColor(R.color.grey1));
                selectTeam.setBackgroundColor(context.getResources().getColor(R.color.white));
            }else if(leagueTeamDataBean.getCheckTeamSelect().equalsIgnoreCase("edited")){
                teamName.setBackgroundColor(context.getResources().getColor(R.color.white));
                selectTeam.setBackgroundColor(context.getResources().getColor(R.color.grey1));
            }


            double countGroupPrice = 0;
                for(int i=0; i<intList.size(); i++){
                    if(leagueTeamDataBean.getSelectGroup().equalsIgnoreCase("Select Group") || intList.get(i).getFee() == null){
                    }else{
                        try{
                            countGroupPrice = countGroupPrice + Double.parseDouble(intList.get(i).getFee());
                            totalAmount.setText(""+countGroupPrice+" "+academy_currency);
                            totalAmountStr = ""+countGroupPrice;
                            System.out.println("here:: "+countGroupPrice+" :: here:: "+totalAmountStr +" :: here:: "+strPromoCode);

                           if(strPromoCode.equalsIgnoreCase("")){
                               netAmount.setText(""+countGroupPrice+" "+academy_currency);
                               dblNetAmount = countGroupPrice;
                           }



                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }



            teamName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.league_dialog_edit);
                    dialog.setTitle(R.string.ifa_dialog);
                    final EditText parentPassword = (EditText) dialog.findViewById(R.id.parentPassword);
                    Button submit = (Button) dialog.findViewById(R.id.submit);

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(parentPassword.getWindowToken(), 0);

                            String parentPasswordStr = parentPassword.getText().toString();
                            if (parentPasswordStr == null || parentPasswordStr.isEmpty()) {
                                dialog.dismiss();
                                // Toast.makeText(context, "Please enter Parent Password", Toast.LENGTH_LONG).show();
                            } else {
                                intList.get(position).setTeamName(parentPassword.getText().toString().trim());
                                intList.get(position).setCheckTeamSelect("edited");
                                notifyDataSetChanged();
                                dialog.dismiss();
                                cancelPromo();
                            }
                        }
                    });

                    dialog.show();

                }
            });

            selectTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.league_dialog_team);
                    dialog.setTitle(R.string.ifa_dialog);
                    ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

                    LeagueSelectTeamAdapter leagueSelectTeamAdapter = new LeagueSelectTeamAdapter(LeagueManageCustomEditScreen.this, teamArrayList);
                    listViewDialog.setAdapter(leagueSelectTeamAdapter);

                    listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            intList.get(position).setSelectTeam(teamArrayList.get(i).getName());
                            intList.get(position).setTeamId(teamArrayList.get(i).getId());
                            intList.get(position).setCheckTeamSelect("selected");
                             posFinal = position;
                            getSelectedClubAPI(teamArrayList.get(i).getId());
                            notifyDataSetChanged();
                            dialog.dismiss();
                            cancelPromo();

                        }
                    });

                    dialog.show();
                }
            });

            selectGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.league_dialog_team);
                    dialog.setTitle(R.string.ifa_dialog);
                    ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

                    LeagueSelectTeamAdapter leagueSelectTeamAdapter = new LeagueSelectTeamAdapter(LeagueManageCustomEditScreen.this, groupArrayList);
                    listViewDialog.setAdapter(leagueSelectTeamAdapter);

                    listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            intList.get(position).setSelectGroup(groupArrayList.get(i).getName());
                            intList.get(position).setGroupId(groupArrayList.get(i).getId());
                            intList.get(position).setFee(groupArrayList.get(i).getFee());
                            notifyDataSetChanged();
                            dialog.dismiss();
                            cancelPromo();
                        }
                    });

                    dialog.show();
                }
            });

            selectClub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.league_dialog_team);
                    dialog.setTitle(R.string.ifa_dialog);
                    ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

                    LeagueSelectTeamAdapter leagueSelectTeamAdapter = new LeagueSelectTeamAdapter(LeagueManageCustomEditScreen.this, clubArrayList);
                    listViewDialog.setAdapter(leagueSelectTeamAdapter);

                    listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            intList.get(position).setSelectClub(clubArrayList.get(i).getName());
                            intList.get(position).setClubId(clubArrayList.get(i).getId());
                            notifyDataSetChanged();
                            dialog.dismiss();
                            cancelPromo();
                        }
                    });

                    dialog.show();
                }
            });


            return convertView;
        }


    }

//    private void getorderId(){
//        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {
//
//            List<NameValuePair> nameValuePairList = new ArrayList<>();
//            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
//            nameValuePairList.add(new BasicNameValuePair("league_id", leagueId));
//            nameValuePairList.add(new BasicNameValuePair("group_id", groupId));
//            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
//            nameValuePairList.add(new BasicNameValuePair("fee", fee));
//
//            String webServiceUrl = Utilities.BASE_URL + "join_league/join_league_payment";
//
//            ArrayList<String> headers = new ArrayList<>();
//            headers.add("X-access-uid:"+loggedInUser.getId());
//            headers.add("X-access-token:"+loggedInUser.getToken());
//
//            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, GET_ORDER_ID, LeagueManageCustomEditScreen.this, headers);
//            postWebServiceAsync.execute(webServiceUrl);
//
//        } else {
//            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
//        }
//    }

    public void hitPaycheck(){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_status";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, HITPAY_STATUS, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPayIdStoreAPI(String id){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("hitpay_id", id));
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            nameValuePairList.add(new BasicNameValuePair("hitpay_salt", Utilities.SALT_HITPAY_JOIN_LEAGUE_KEY));
            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_refer_id";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, HITPAY_ID_STORE_SERVICE, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void getSelectedClubAPI(String teamId){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));
            nameValuePairList.add(new BasicNameValuePair("team_id", teamId));

            String webServiceUrl = Utilities.BASE_URL + "join_league/get_club_by_team";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, GET_SELECTED_CLUB, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogBoxShow(String text){

        final Dialog dialogAddNotes = new Dialog(LeagueManageCustomEditScreen.this);
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

    public void updateTermSettingAPI(){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/update_order_term_condition";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, UPDATE_TERM_COND_SETTING, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
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
            intent.putExtra(AvenuesParams.AMOUNT, ""+dblNetAmount);
            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
            startActivity(intent);

        }
    }

    public void hitPaysAPI(String api_key){
        if (Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", loggedInUser.getEmail()));
            nameValuePairList.add(new BasicNameValuePair("redirect_url", Utilities.BASE_URL+"payments/hitpay_response"));
            nameValuePairList.add(new BasicNameValuePair("webhook", Utilities.BASE_URL+"payments/hitpay_webhook"));
            nameValuePairList.add(new BasicNameValuePair("amount", ""+dblNetAmount));
            nameValuePairList.add(new BasicNameValuePair("currency", academy_currency));
            nameValuePairList.add(new BasicNameValuePair("reference_number", ordersId +"- (Android)"));


            String webServiceUrl = ""+Utilities.HITPAY_API;

            HitPayPostWebServiceWithHeadersAsync postWebServiceAsync = new HitPayPostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, HITPAY_WEB_SERVICE, LeagueManageCustomEditScreen.this, api_key);
            postWebServiceAsync.execute(webServiceUrl);
        } else {
            Toast.makeText( LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void termSettingAPI(){
        if(Utilities.isNetworkAvailable(LeagueManageCustomEditScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/get_term_condition_settings";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueManageCustomEditScreen.this, nameValuePairList, TERM_COND_SETTING, LeagueManageCustomEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueManageCustomEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }
}