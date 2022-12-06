package com.lap.application.parent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.CampBean;
import com.lap.application.beans.CampSummaryBean;
import com.lap.application.beans.CampSummarySelectedChildBean;
import com.lap.application.beans.DiscountBean;
import com.lap.application.beans.UserBean;
import com.lap.application.hitPay.HitPayExample;
import com.lap.application.hitPay.HitPayPostWebServiceWithHeadersAsync;
import com.lap.application.parent.adapters.ParentCampSummaryAdapter;
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

public class ParentBookCampSummaryScreen extends AppCompatActivity implements IWebServiceCallback {
    private final String TERM_COND_SETTING = "TERM_COND_SETTING";
    private final String UPDATE_TERM_COND_SETTING = "UPDATE_TERM_COND_SETTING";
    boolean statusGatewayScreen;

    String webviewHitPayurl;
    private final String HITPAY_STATUS = "HITPAY_STATUS";
    private final String HITPAY_WEB_SERVICE = "HITPAY_WEB_SERVICE";
    private final String HITPAY_ID_STORE_SERVICE = "HITPAY_ID_STORE_SERVICE";

    String net_Amount;
    String ordersId;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ImageView backButton;
    TextView title;
    ListView selectedChildrenListView;
    TextView lblTotalAmount;
    TextView totalAmount;
    TextView lblTotalDiscount;
    TextView totalDiscount;
    TextView lblNetPayable;
    TextView netAmount;
    Button makeAPayment;

    LinearLayout promoCodeLinear;
    EditText promoCodeEditText;
    Button applyPromoCode;
    Button cancelPromoCode;
    LinearLayout promoCodeDiscountLinear;
    TextView lblPromoCodeDiscount;
    TextView promoCodeDiscount;

    CampBean clickedOnCamp;
    int sessionPosition;
    int locationPosition;
    String strBookingType;
    String selectedChildrenArray;
    String strComments;

    private final String GET_CAMP_SUMMARY = "GET_CAMP_SUMMARY";
    private final String BOOK_CAMP = "BOOK_CAMP";
    private final String APPLY_PROMO_CODE = "APPLY_PROMO_CODE";

    CampSummaryBean campSummaryBean;

    String strPromoCode = "";
    String academy_currency;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity_book_camp_summary_screen);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        backButton = findViewById(R.id.backButton);
        title = findViewById(R.id.title);
        selectedChildrenListView = findViewById(R.id.selectedChildrenListView);
        lblTotalAmount = findViewById(R.id.lblTotalAmount);
        totalAmount = findViewById(R.id.totalAmount);
        lblTotalDiscount = findViewById(R.id.lblTotalDiscount);
        totalDiscount = findViewById(R.id.totalDiscount);
        lblNetPayable = findViewById(R.id.lblNetPayable);
        netAmount = findViewById(R.id.netAmount);
        makeAPayment = findViewById(R.id.makeAPayment);
        promoCodeLinear = findViewById(R.id.promoCodeLinear);
        promoCodeEditText = findViewById(R.id.promoCodeEditText);
        promoCodeEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        applyPromoCode = findViewById(R.id.applyPromoCode);
        cancelPromoCode = findViewById(R.id.cancelPromoCode);
        promoCodeDiscountLinear = findViewById(R.id.promoCodeDiscountLinear);
        lblPromoCodeDiscount = findViewById(R.id.lblPromoCodeDiscount);
        promoCodeDiscount = findViewById(R.id.promoCodeDiscount);

          academy_currency = sharedPreferences.getString("academy_currency", null);
        promoCodeDiscount.setText("0.00 " + academy_currency);

        Intent intent = getIntent();
        if (intent != null) {
            clickedOnCamp = (CampBean) intent.getSerializableExtra("clickedOnCamp");
            sessionPosition = intent.getIntExtra("sessionPosition", -1);
            locationPosition = intent.getIntExtra("locationPosition", -1);
            strBookingType = intent.getStringExtra("strBookingType");
            selectedChildrenArray = intent.getStringExtra("selectedChildrenArray");
            strComments = intent.getStringExtra("strComments");

            getCampSummary();
        }

        changeFonts();

        makeAPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {

                    List<NameValuePair> nameValuePairList = new ArrayList<>();

                    nameValuePairList.add(new BasicNameValuePair("book_type", strBookingType));
                    nameValuePairList.add(new BasicNameValuePair("camp_id", clickedOnCamp.getCampId()));
                    nameValuePairList.add(new BasicNameValuePair("camp_sessions_id", clickedOnCamp.getSessionsList().get(sessionPosition).getSessionId()));
                    if (clickedOnCamp.getLocationList().size() > 1) {
                        nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnCamp.getLocationList().get(locationPosition).getLocationId()));
                    } else {
                        nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnCamp.getLocationList().get(0).getLocationId()));

                    }
                    nameValuePairList.add(new BasicNameValuePair("sending_data", selectedChildrenArray));
                    nameValuePairList.add(new BasicNameValuePair("comments", strComments));
                    nameValuePairList.add(new BasicNameValuePair("promo_code", strPromoCode));

                    if(netAmount.getText().toString().trim().equals("0.00 "+academy_currency) || netAmount.getText().toString().trim().equals("0 "+academy_currency)){
                        nameValuePairList.add(new BasicNameValuePair("payment_mode", "offline"));
                    }else{
                        nameValuePairList.add(new BasicNameValuePair("payment_mode", ""));
                    }

                    // String webServiceUrl = Utilities.BASE_URL + "camps/booking_new";
String webServiceUrl = Utilities.BASE_URL + Utilities.bookingNew;

                    ArrayList<String> headers = new ArrayList<>();
                    headers.add("X-access-uid:" + loggedInUser.getId());
                    headers.add("X-access-token:" + loggedInUser.getToken());

                    PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, BOOK_CAMP, ParentBookCampSummaryScreen.this, headers);
                    postWebServiceAsync.execute(webServiceUrl);

                } else {
                    Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });

        applyPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPromoCode = promoCodeEditText.getText().toString().trim();

                if (strPromoCode == null || strPromoCode.isEmpty()) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Please enter Promo Code", Toast.LENGTH_SHORT).show();
                } else {
                    getCampSummary();
                }
            }
        });

        cancelPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promoCodeEditText.setText("");
                strPromoCode = promoCodeEditText.getText().toString().trim();

                getCampSummary();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void showData() {
        String academy_currency = sharedPreferences.getString("academy_currency", null);
        totalAmount.setText(campSummaryBean.getTotalCost() + " " + academy_currency);
        totalDiscount.setText(campSummaryBean.getTotalDiscount() + " " + academy_currency);
        promoCodeDiscount.setText(campSummaryBean.getPromoCodeDiscount() + " " + academy_currency);
//        netAmount.setText(campSummaryBean.getNetPayable()+" AED");
        netAmount.setText(campSummaryBean.getRoundedNetPayable() + " " + academy_currency);

        selectedChildrenListView.setAdapter(new ParentCampSummaryAdapter(ParentBookCampSummaryScreen.this, campSummaryBean.getCampSummarySelectedChildList()));
        Utilities.setListViewHeightBasedOnChildren(selectedChildrenListView);
    }

    void getCampSummary() {
        if (Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();

//            nameValuePairList.add(new BasicNameValuePair("booking_type", strBookingType));
//            nameValuePairList.add(new BasicNameValuePair("camps_id", clickedOnCamp.getCampId()));

            nameValuePairList.add(new BasicNameValuePair("book_type", strBookingType));
            nameValuePairList.add(new BasicNameValuePair("camp_id", clickedOnCamp.getCampId()));
            nameValuePairList.add(new BasicNameValuePair("camp_sessions_id", clickedOnCamp.getSessionsList().get(sessionPosition).getSessionId()));
            if (clickedOnCamp.getLocationList().size() > 1) {
                nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnCamp.getLocationList().get(locationPosition).getLocationId()));

            } else {
                nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnCamp.getLocationList().get(0).getLocationId()));
            }
            nameValuePairList.add(new BasicNameValuePair("sending_data", selectedChildrenArray));
            nameValuePairList.add(new BasicNameValuePair("comments", strComments));
            nameValuePairList.add(new BasicNameValuePair("promo_code", strPromoCode));

            //String webServiceUrl = Utilities.BASE_URL + "camps/calculate_summary_amount";
            String webServiceUrl = Utilities.BASE_URL + Utilities.campSession;

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, GET_CAMP_SUMMARY, ParentBookCampSummaryScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_CAMP_SUMMARY:

                campSummaryBean = new CampSummaryBean();

                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONObject dataObject = responseObject.getJSONObject("data");

                            campSummaryBean.setTotalCost(dataObject.getString("total_cost"));
                            campSummaryBean.setTotalDiscount(dataObject.getString("total_discount"));
                            campSummaryBean.setNetPayable(dataObject.getString("net_payable"));
                            campSummaryBean.setRoundedNetPayable(dataObject.getString("rounded_net_payable"));
                            campSummaryBean.setCampPerDayCost(dataObject.getString("camp_per_day_cost"));
                            campSummaryBean.setNumOfDays(dataObject.getString("num_of_days"));
                            campSummaryBean.setTotalWeeklyDiscount(dataObject.getString("total_weekly_discount"));
                            campSummaryBean.setPromoCodeDiscount(dataObject.getString("promo_code_discount"));

                            JSONArray summaryArray = dataObject.getJSONArray("summary");
                            ArrayList<CampSummarySelectedChildBean> selectedChildList = new ArrayList<>();
                            CampSummarySelectedChildBean campSummarySelectedChildBean;
                            for (int i = 0; i < summaryArray.length(); i++) {
                                JSONObject summaryObject = summaryArray.getJSONObject(i);

                                campSummarySelectedChildBean = new CampSummarySelectedChildBean();
                                campSummarySelectedChildBean.setTotalCost(summaryObject.getString("total_cost"));
                                campSummarySelectedChildBean.setPerDayCost(summaryObject.getString("per_day_cost"));
                                campSummarySelectedChildBean.setBookingDates(summaryObject.getString("booking_dates"));
                                campSummarySelectedChildBean.setWeeklyDiscount(summaryObject.getString("weekly_discount"));
                                campSummarySelectedChildBean.setCampCost(summaryObject.getString("camp_cost"));
                                campSummarySelectedChildBean.setName(summaryObject.getString("name"));
                                campSummarySelectedChildBean.setNetPay(summaryObject.getString("net_pay"));
                                campSummarySelectedChildBean.setDiscountCost(summaryObject.getString("discount_cost"));
                                campSummarySelectedChildBean.setChildId(summaryObject.getString("child_id"));

                                Object dObject = summaryObject.get("discount");

                                if (dObject instanceof JSONObject) {
                                    JSONObject discountObject = summaryObject.getJSONObject("discount");
                                    DiscountBean discountBean = new DiscountBean();
                                    discountBean.setDiscountLabel(discountObject.getString("discount_label"));
                                    discountBean.setDiscountDescription(discountObject.getString("discount_desc"));
                                    discountBean.setDiscountValue(discountObject.getString("discount_value"));
                                    discountBean.setDiscountCode(discountObject.getString("discount_code"));

                                    campSummarySelectedChildBean.setDiscountBean(discountBean);
                                } else {
                                    campSummarySelectedChildBean.setDiscountBean(null);
                                }

                                selectedChildList.add(campSummarySelectedChildBean);
                            }

                            campSummaryBean.setCampSummarySelectedChildList(selectedChildList);

                            showData();
                        } else {
                            Toast.makeText(ParentBookCampSummaryScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case BOOK_CAMP:

                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not reach server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
//                            Toast.makeText(ParentBookCampScreenOLD.this, message, Toast.LENGTH_SHORT).show();
//                            finish();

                            String academy_currency = sharedPreferences.getString("academy_currency", null);
                            System.out.println("netAmount::"+netAmount.getText().toString());
                            System.out.println("stored value:: 0.00 "+academy_currency);

                            net_Amount = responseObject.getString("net_amount");
                            ordersId = responseObject.getString("orders_id");

                            if(netAmount.getText().toString().trim().equals(".00 "+academy_currency) || netAmount.getText().toString().trim().equals("0.00 "+academy_currency) || netAmount.getText().toString().trim().equals("0 "+academy_currency) || netAmount.getText().toString().trim().equals(".0 "+academy_currency) || netAmount.getText().toString().trim().equals("0.0 "+academy_currency)){
//                            if(netAmount.getText().toString().trim().equals(".00 "+academy_currency) || netAmount.getText().toString().trim().equals("0.00 "+academy_currency) || netAmount.getText().toString().trim().equals("0 "+academy_currency)){
                                System.out.println("in_if_condition");
                                //Toast.makeText(ParentBookCampSummaryScreen.this, message, Toast.LENGTH_SHORT).show();
                                Intent mainScreen = new Intent(ParentBookCampSummaryScreen.this, ParentNetPaymentZeroScreen.class);
                                mainScreen.putExtra("orderID", ordersId);
                                startActivity(mainScreen);

                            }else{

//
//                                Intent intent = new Intent(ParentBookCampSummaryScreen.this, ParentPaymentGatewayWebViewScreen.class);
//                                intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                                intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                                intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//
//                                intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
////                            intent.putExtra(AvenuesParams.CURRENCY, Utilities.CURRENCY);
//                                intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
//
////                            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.BASE_URL + "payments/response_handler");
//                                intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
////                            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.BASE_URL + "payments/response_handler");
//                                intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
//                                intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
//
//                                startActivity(intent);

                                hitPaycheck();
                            }


                        } else {
                            Toast.makeText(ParentBookCampSummaryScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_STATUS:

                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
//                            String api_key = hitpayDetail.getString("api_key");
////                            hitPaysAPI(api_key);
//                            hitPaysAPI(Utilities.HITPAY_CAMP_KEY);
//                        }else{
//
//                                Intent intent = new Intent(ParentBookCampSummaryScreen.this, ParentPaymentGatewayWebViewScreen.class);
//                                intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                                intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                                intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//
//                                intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
////                            intent.putExtra(AvenuesParams.CURRENCY, Utilities.CURRENCY);
//                                intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
//
////                            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.BASE_URL + "payments/response_handler");
//                                intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
////                            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.BASE_URL + "payments/response_handler");
//                                intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
//                                intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
//
//                                startActivity(intent);
//
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_WEB_SERVICE:
                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Intent obj = new Intent(ParentBookCampSummaryScreen.this, HitPayExample.class);
                            obj.putExtra("url", webviewHitPayurl);
                            startActivity(obj);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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

                            if(camp_state.equalsIgnoreCase("1")){
                                dialogBoxShow(camp_description);
                            }else{
                                gatewayScreenIntent(statusGatewayScreen);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case UPDATE_TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentBookCampSummaryScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            gatewayScreenIntent(statusGatewayScreen);
                        }else{
                            Toast.makeText(ParentBookCampSummaryScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookCampSummaryScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

        }
    }

    private void changeFonts() {
        title.setTypeface(linoType);
        lblTotalAmount.setTypeface(helvetica);
        totalAmount.setTypeface(helvetica);
        lblTotalDiscount.setTypeface(helvetica);
        totalDiscount.setTypeface(helvetica);
        lblNetPayable.setTypeface(helvetica);
        netAmount.setTypeface(helvetica);
        makeAPayment.setTypeface(linoType);
    }


    public void hitPaycheck(){
        if(Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_status";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, HITPAY_STATUS, ParentBookCampSummaryScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPaysAPI(String api_key){
        if (Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", loggedInUser.getEmail()));
            nameValuePairList.add(new BasicNameValuePair("redirect_url", Utilities.BASE_URL+"payments/hitpay_response"));
            nameValuePairList.add(new BasicNameValuePair("webhook", Utilities.BASE_URL+"payments/hitpay_webhook"));
            nameValuePairList.add(new BasicNameValuePair("amount", net_Amount));
            nameValuePairList.add(new BasicNameValuePair("currency", academy_currency));
            nameValuePairList.add(new BasicNameValuePair("reference_number", ordersId +"- (Android)"));


            String webServiceUrl = ""+Utilities.HITPAY_API;

            HitPayPostWebServiceWithHeadersAsync postWebServiceAsync = new HitPayPostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, HITPAY_WEB_SERVICE, ParentBookCampSummaryScreen.this, api_key);
            postWebServiceAsync.execute(webServiceUrl);
        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void hitPayIdStoreAPI(String id){
        if(Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("hitpay_id", id));
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            nameValuePairList.add(new BasicNameValuePair("hitpay_salt", Utilities.SALT_HITPAY_CAMP_KEY));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_refer_id";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, HITPAY_ID_STORE_SERVICE, ParentBookCampSummaryScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }
    public void termSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/get_term_condition_settings";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, TERM_COND_SETTING, ParentBookCampSummaryScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTermSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentBookCampSummaryScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/update_order_term_condition";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookCampSummaryScreen.this, nameValuePairList, UPDATE_TERM_COND_SETTING, ParentBookCampSummaryScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookCampSummaryScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogBoxShow(String text){

        final Dialog dialogAddNotes = new Dialog(ParentBookCampSummaryScreen.this);
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

          //  JSONObject hitpayDetail = responseObject.getJSONObject("hitpay_detail");
          //  String api_key = hitpayDetail.getString("api_key");
//                            hitPaysAPI(api_key);
            hitPaysAPI(Utilities.HITPAY_CAMP_KEY);
        }else{

            Intent intent = new Intent(ParentBookCampSummaryScreen.this, ParentPaymentGatewayWebViewScreen.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
            intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
            intent.putExtra(AvenuesParams.ORDER_ID, ordersId);

            intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
//                            intent.putExtra(AvenuesParams.CURRENCY, Utilities.CURRENCY);
            intent.putExtra(AvenuesParams.AMOUNT, net_Amount);

//                            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.BASE_URL + "payments/response_handler");
            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
//                            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.BASE_URL + "payments/response_handler");
            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");

            startActivity(intent);

        }
    }
}
