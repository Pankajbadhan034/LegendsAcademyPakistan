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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.ApplicationContext;
import com.lap.application.R;
import com.lap.application.beans.PitchBookingDateBean;
import com.lap.application.beans.PitchBookingSlotsDataBean;
import com.lap.application.beans.PitchSummaryDataBean;
import com.lap.application.beans.UserBean;
import com.lap.application.hitPay.HitPayExample;
import com.lap.application.hitPay.HitPayPostWebServiceWithHeadersAsync;
import com.lap.application.parent.adapters.ParentPitchSummaryNewForCalendarViewAdapter;
import com.lap.application.parent.paymentGatewayUtilities.AvenuesParams;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

public class ParentBookPitchSummaryNewForCalendarViewScreen extends AppCompatActivity implements IWebServiceCallback {
    private final String TERM_COND_SETTING = "TERM_COND_SETTING";
    private final String UPDATE_TERM_COND_SETTING = "UPDATE_TERM_COND_SETTING";
    boolean statusGatewayScreen;

    String webviewHitPayurl;
    private final String HITPAY_STATUS = "HITPAY_STATUS";
    private final String HITPAY_WEB_SERVICE = "HITPAY_WEB_SERVICE";
    private final String HITPAY_ID_STORE_SERVICE = "HITPAY_ID_STORE_SERVICE";
    String academy_currency;

    String net_Amount;
    String ordersId;
    ApplicationContext applicationContext;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ImageView backButton;
    TextView title;
    TextView clearAll;
    ListView pitchesDetailListView;
    TextView lblAmount;
    TextView overallAmount;
    TextView lblBulkHourDiscount;
    TextView overallBulkHourDiscount;
    TextView lblAdditionalDiscount;
    TextView overallAdditionalDiscount;
    TextView lblNetAmount;
    TextView overallNetAmount;
    Button bookMoreButton;
    Button makePaymentButton;
    RelativeLayout amountRelative;
    RelativeLayout bulkHourDiscountRelative;
    RelativeLayout additionalDiscountRelative;
    RelativeLayout netAmountRelative;
    LinearLayout calculationsLinearLayout;

    LinearLayout promoCodeLinear;
    EditText promoCodeEditText;
    Button applyPromoCode;
    Button cancelPromoCode;
    LinearLayout promoCodeDiscountLinear;
    TextView lblPromoCodeDiscount;
    TextView promoCodeDiscount;

    private final String GET_PITCH_SUMMARY_DATA = "GET_PITCH_SUMMARY_DATA";
    private final String BOOK_PITCH = "BOOK_PITCH";
    private final String CLEAR_ALL = "CLEAR_ALL";

    ArrayList<PitchSummaryDataBean> summaryDataList = new ArrayList<>();

    ParentPitchSummaryNewForCalendarViewAdapter parentPitchSummaryAdapter;

    String strPromoCode = "";
    double promoCodeDeductAmount = 0;
    private final String APPLY_PROMO_CODE = "APPLY_PROMO_CODE";

    double overallInitialAmountValue = 0;
    double overallBulkDiscountValue = 0;
    double overallAdditionalDiscountValue = 0;
    double overallNetAmountValue;
    long roundedOffNetAmount = 0;

    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    boolean isBookMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity_book_pitch_summary_new_for_calendar_view_screen);

        applicationContext = (ApplicationContext) getApplication();
        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
        clearAll = (TextView) findViewById(R.id.clearAll);
        pitchesDetailListView = (ListView) findViewById(R.id.pitchesDetailListView);
        lblAmount = (TextView) findViewById(R.id.lblAmount);
        overallAmount = (TextView) findViewById(R.id.overallAmount);
        lblBulkHourDiscount = (TextView) findViewById(R.id.lblBulkHourDiscount);
        overallBulkHourDiscount = (TextView) findViewById(R.id.overallBulkHourDiscount);
        lblAdditionalDiscount = (TextView) findViewById(R.id.lblAdditionalDiscount);
        overallAdditionalDiscount = (TextView) findViewById(R.id.overallAdditionalDiscount);
        lblNetAmount = (TextView) findViewById(R.id.lblNetAmount);
        overallNetAmount = (TextView) findViewById(R.id.overallNetAmount);
        bookMoreButton = (Button) findViewById(R.id.bookMoreButton);
        makePaymentButton = (Button) findViewById(R.id.makePaymentButton);
        amountRelative = (RelativeLayout) findViewById(R.id.amountRelative);
        bulkHourDiscountRelative = (RelativeLayout) findViewById(R.id.bulkHourDiscountRelative);
        additionalDiscountRelative = (RelativeLayout) findViewById(R.id.additionalDiscountRelative);
        netAmountRelative = (RelativeLayout) findViewById(R.id.netAmountRelative);
        calculationsLinearLayout = findViewById(R.id.calculationsLinearLayout);

        promoCodeLinear = findViewById(R.id.promoCodeLinear);
        promoCodeEditText = findViewById(R.id.promoCodeEditText);
        promoCodeEditText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        applyPromoCode = findViewById(R.id.applyPromoCode);
        cancelPromoCode = findViewById(R.id.cancelPromoCode);
        promoCodeDiscountLinear = findViewById(R.id.promoCodeDiscountLinear);
        lblPromoCodeDiscount = findViewById(R.id.lblPromoCodeDiscount);
        promoCodeDiscount = findViewById(R.id.promoCodeDiscount);

        academy_currency = sharedPreferences.getString("academy_currency", null);
        promoCodeDiscount.setText("0.00 "+academy_currency);

        parentPitchSummaryAdapter = new ParentPitchSummaryNewForCalendarViewAdapter(ParentBookPitchSummaryNewForCalendarViewScreen.this, summaryDataList, ParentBookPitchSummaryNewForCalendarViewScreen.this, pitchesDetailListView);
        pitchesDetailListView.setAdapter(parentPitchSummaryAdapter);

        changeFonts();
        getPitchDetailSummaryData();
//        getPitchDetailData();

        makePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(summaryDataList == null || summaryDataList.isEmpty()) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Please select a Pitch", Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONArray pitchArray = new JSONArray();
                JSONObject pitchObject;
                for(PitchSummaryDataBean pitchSummaryDataBean: summaryDataList) {
                    pitchObject = new JSONObject();

                    try {
                        pitchObject.put("pitch_id", pitchSummaryDataBean.getPitchId());

//                        pitchObject.put("is_full_pitch", pitchSummaryDataBean.getIsFullPitch());

                        if(pitchSummaryDataBean.getIsFullPitch().equalsIgnoreCase("true") || pitchSummaryDataBean.getIsFullPitch().equalsIgnoreCase("1")){
                            pitchObject.put("is_full_pitch", "1");
                        } else {
                            pitchObject.put("is_full_pitch", "0");
                        }

                        JSONArray bookingsArray = new JSONArray();
                        JSONObject bookingObject;
                        for(PitchBookingDateBean pitchBookingDateBean: pitchSummaryDataBean.getBookingDatesList()){
                            bookingObject = new JSONObject();
                            bookingObject.put("booking_date", pitchBookingDateBean.getBookingDate());
                            bookingObject.put("from_time", pitchBookingDateBean.getFromTime());
                            bookingObject.put("to_time", pitchBookingDateBean.getToTime());

                            bookingsArray.put(bookingObject);
                        }
                        pitchObject.put("bookings", bookingsArray);

                        pitchArray.put(pitchObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                    nameValuePairList.add(new BasicNameValuePair("pitch_booking", pitchArray.toString()));
                    nameValuePairList.add(new BasicNameValuePair("promo_code", strPromoCode));

                    if(overallNetAmount.getText().toString().trim().equals("0.00 "+academy_currency)){
                        nameValuePairList.add(new BasicNameValuePair("payment_mode", "offline"));
                    }else{
                        nameValuePairList.add(new BasicNameValuePair("payment_mode", ""));
                    }


                    String webServiceUrl = Utilities.BASE_URL + "pitch/book";

                    ArrayList<String> headers = new ArrayList<>();
                    headers.add("X-access-uid:"+loggedInUser.getId());
                    headers.add("X-access-token:"+loggedInUser.getToken());

                    PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, BOOK_PITCH, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
                    postWebServiceAsync.execute(webServiceUrl);

                } else {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }

            }
        });

        applyPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strPromoCode = promoCodeEditText.getText().toString().trim();

                overallNetAmountValue = overallNetAmountValue + promoCodeDeductAmount;
                promoCodeDeductAmount = 0;

                String academy_currency = sharedPreferences.getString("academy_currency", null);
                promoCodeDiscount.setText("0.00 "+academy_currency);
                if(overallNetAmountValue == 0) {
                    overallNetAmount.setText("0.00 "+academy_currency);
                } else {
                    overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" "+academy_currency);
                }

                if(strPromoCode == null || strPromoCode.isEmpty()) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Please enter Promo Code", Toast.LENGTH_SHORT).show();
                } else {
                    if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

                        List<NameValuePair> nameValuePairList = new ArrayList<>();
                        nameValuePairList.add(new BasicNameValuePair("amount", overallNetAmountValue+""));
                        nameValuePairList.add(new BasicNameValuePair("module_type", "3"));
                        nameValuePairList.add(new BasicNameValuePair("promo_code", strPromoCode));

                        String webServiceUrl = Utilities.BASE_URL + "sessions/apply_promo_code";

                        ArrayList<String> headers = new ArrayList<>();
                        headers.add("X-access-uid:"+loggedInUser.getId());
                        headers.add("X-access-token:"+loggedInUser.getToken());

                        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, APPLY_PROMO_CODE, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
                        postWebServiceAsync.execute(webServiceUrl);

                    } else {
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cancelPromoCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promoCodeEditText.setText("");
                strPromoCode = promoCodeEditText.getText().toString().trim();

                overallNetAmountValue = overallNetAmountValue + promoCodeDeductAmount;
                promoCodeDeductAmount = 0;

                String academy_currency = sharedPreferences.getString("academy_currency", null);
                promoCodeDiscount.setText("0.00 "+academy_currency);
                if(overallNetAmountValue == 0) {
                    overallNetAmount.setText("0.00 "+academy_currency);
                } else {
                    overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" "+academy_currency);
                }
            }
        });

        bookMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBookMore = true;
                clearWebService();
            }
        });

        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ParentBookPitchSummaryNewForCalendarViewScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.parent_dialog_clear_all);

                TextView yes = (TextView) dialog.findViewById(R.id.yes);
                TextView no = (TextView) dialog.findViewById(R.id.no);

                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

//                        clearAllLocalData();
                        isBookMore = false;
                        clearWebService();
                    }
                });

                dialog.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void clearWebService(){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

            String webServiceUrl = Utilities.BASE_URL + "pitch/cache_clear_all";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, CLEAR_ALL, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void clearAllLocalData(){
        applicationContext.getPitchBookingSlotsDataBeanListing().clear();
        summaryDataList.clear();

        pitchesDetailListView.setAdapter(null);
        pitchesDetailListView.setVisibility(View.GONE);

        calculationsLinearLayout.setVisibility(View.GONE);

        /*amountRelative.setVisibility(View.GONE);
        bulkHourDiscountRelative.setVisibility(View.GONE);
        additionalDiscountRelative.setVisibility(View.GONE);
        promoCodeLinear.setVisibility(View.GONE);
        promoCodeDiscountLinear.setVisibility(View.GONE);
        netAmountRelative.setVisibility(View.GONE);*/
    }

    public void updateListView(){
        if(summaryDataList.isEmpty()){
            applicationContext.getPitchBookingSlotsDataBeanListing().clear();

            pitchesDetailListView.setAdapter(null);
            pitchesDetailListView.setVisibility(View.GONE);

            calculationsLinearLayout.setVisibility(View.GONE);

            /*amountRelative.setVisibility(View.GONE);
            bulkHourDiscountRelative.setVisibility(View.GONE);
            additionalDiscountRelative.setVisibility(View.GONE);
            promoCodeLinear.setVisibility(View.GONE);
            promoCodeDiscountLinear.setVisibility(View.GONE);
            netAmountRelative.setVisibility(View.GONE);*/

        }
    }

    public void updateCalculations() {
        overallInitialAmountValue = 0;
        overallBulkDiscountValue = 0;
        overallAdditionalDiscountValue = 0;
        overallNetAmountValue = 0;

        for(PitchSummaryDataBean pitchSummaryDataBean: summaryDataList) {
            overallInitialAmountValue += pitchSummaryDataBean.getInitialAmountValue();
            overallBulkDiscountValue += Double.parseDouble(pitchSummaryDataBean.getBulkHourDiscountAmount());
            overallAdditionalDiscountValue += Double.parseDouble(pitchSummaryDataBean.getAdditionalDiscountAmount());

//            overallBulkDiscountValue += pitchSummaryDataBean.getBulkHourDiscountValue();
//            overallAdditionalDiscountValue += pitchSummaryDataBean.getAdditionalDiscountValue();
        }

        overallNetAmountValue = overallInitialAmountValue - overallBulkDiscountValue - overallAdditionalDiscountValue;

        String academy_currency = sharedPreferences.getString("academy_currency", null);
        overallAmount.setText(decimalFormat.format(overallInitialAmountValue)+" "+academy_currency);

        if(overallBulkDiscountValue == 0) {
            overallBulkHourDiscount.setText("0.00 "+academy_currency);
        } else {
            overallBulkHourDiscount.setText(decimalFormat.format(overallBulkDiscountValue)+" "+academy_currency);
        }

        if(overallAdditionalDiscountValue == 0){
            overallAdditionalDiscount.setText("0.00 "+academy_currency);
        } else {
            overallAdditionalDiscount.setText(decimalFormat.format(overallAdditionalDiscountValue)+" "+academy_currency);
        }

//        overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" AED");
//        roundedOffNetAmount = Math.round(overallNetAmountValue);
//        overallNetAmount.setText(roundedOffNetAmount+" "+academy_currency);

        if(decimalFormat.format(overallNetAmountValue).equals(".00")){
            overallNetAmount.setText("0.00 "+academy_currency);
        }else{
            overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" "+academy_currency);
        }
    }

    private void getPitchDetailSummaryData(){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {
            ArrayList<PitchBookingSlotsDataBean> pitchBookingSlotsListing = applicationContext.getPitchBookingSlotsDataBeanListing();

            try{
//                JSONArray mainArray = new JSONArray();
//
//                for(PitchBookingSlotsDataBean pitchBookingSlotsDataBean: pitchBookingSlotsListing) {
//
//                    JSONObject mainObject = new JSONObject();
////                    mainObject.put("pitch_id", pitchBookingSlotsDataBean.getPitchId());
//                    JSONObject timeSlotsObject = new JSONObject();
//
//                    for(PitchDateBean pitchDateBean: pitchBookingSlotsDataBean.getPitchDateBeenList()) {
//
//                        JSONArray timeSlotsArray = new JSONArray();
//
//                        for(PitchTimeSlotBean pitchTimeSlotBean : pitchDateBean.getTimeSlots()) {
//
//                            JSONObject timeSlotObject = new JSONObject();
//
//                            if(pitchTimeSlotBean.isSelected()) {
////                                timeSlotsArray.put(pitchTimeSlotBean.getTimeSlot());
//                                timeSlotObject.put("time", pitchTimeSlotBean.getTimeSlot());
//                                timeSlotObject.put("pitch_id", pitchTimeSlotBean.getPitchId());
//                                timeSlotsArray.put(timeSlotObject);
//                            }
//                        }
//
//                        timeSlotsObject.put(pitchDateBean.getDate(), timeSlotsArray);
//
//                    }
//
//                    mainObject.put("time_slots", timeSlotsObject);
//                    if(pitchBookingSlotsDataBean.isFullPitch()){
//                        mainObject.put("is_full_pitch", "1");
//                    } else {
//                        mainObject.put("is_full_pitch", "0");
//                    }
//
//                    mainArray.put(mainObject);
//                }
//
//                //System.out.println("array "+mainArray.toString());
//
//                List<NameValuePair> nameValuePairList = new ArrayList<>();
//                nameValuePairList.add(new BasicNameValuePair("temp_pitch_data", mainArray.toString()));
//                String webServiceUrl = Utilities.BASE_URL + "pitch/book_summary";

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
                nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
                String webServiceUrl = Utilities.BASE_URL + "front_calendar/booked_pitches";


                ArrayList<String> headers = new ArrayList<>();
                headers.add("X-access-uid:"+loggedInUser.getId());
                headers.add("X-access-token:"+loggedInUser.getToken());

                PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, GET_PITCH_SUMMARY_DATA, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
                postWebServiceAsync.execute(webServiceUrl);

            } catch(Exception e) {
                Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    /*private void getPitchDetailData() {
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

            ArrayList<PitchBookingData> pitchBookingDataList = applicationContext.getPitchBookingDataList();

            JSONArray pitchesDataArray = new JSONArray();
            JSONObject pitchDataObject;

            for (PitchBookingData pitchBookingData : pitchBookingDataList) {
                pitchDataObject = new JSONObject();
                try {
                    pitchDataObject.put("pitch_id", pitchBookingData.getPitchId());
                    pitchDataObject.put("is_multiple", pitchBookingData.isMultiple() ? "1":"0");
                    pitchDataObject.put("from_date", pitchBookingData.getFromDate());
                    pitchDataObject.put("to_date", pitchBookingData.getToDate());
                    pitchDataObject.put("is_daily", pitchBookingData.isDaily() ? "1" : "0");
                    if (!pitchBookingData.isDaily()) {
                        pitchDataObject.put("weekdays", pitchBookingData.getWeekdays());
                    }
                    pitchDataObject.put("from_time", pitchBookingData.getFromTime());
                    pitchDataObject.put("to_time", pitchBookingData.getToTime());

                    pitchesDataArray.put(pitchDataObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("pitch_detail", pitchesDataArray.toString()));

            String webServiceUrl = Utilities.BASE_URL + "pitch/availability";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, GET_PITCH_SUMMARY_DATA, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }*/

    public void deleteSlot(String pitchId, PitchBookingDateBean bookingDateBean){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {
            try{
                JSONArray mainArray = new JSONArray();

                JSONObject jObject = new JSONObject();
                jObject.put("pitch_id", pitchId);
                jObject.put("bdate", bookingDateBean.getBookingDate());
                jObject.put("from_time", bookingDateBean.getFromTime());
                jObject.put("to_time", bookingDateBean.getToTime());

                mainArray.put(jObject);

                List<NameValuePair> nameValuePairList = new ArrayList<>();
                nameValuePairList.add(new BasicNameValuePair("remove_tslots", mainArray.toString()));

                String webServiceUrl = Utilities.BASE_URL + "pitch/remove_slots";

                ArrayList<String> headers = new ArrayList<>();
                headers.add("X-access-uid:"+loggedInUser.getId());
                headers.add("X-access-token:"+loggedInUser.getToken());

                PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, GET_PITCH_SUMMARY_DATA, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
                postWebServiceAsync.execute(webServiceUrl);
            } catch(JSONException e) {
                Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_PITCH_SUMMARY_DATA:

                summaryDataList.clear();

                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            PitchSummaryDataBean summaryBean;
                            for(int i=0; i<dataArray.length(); i++) {
                                JSONObject summaryObject = dataArray.getJSONObject(i);
                                summaryBean = new PitchSummaryDataBean();

                                summaryBean.setPitchId(summaryObject.getString("pitch_id"));
                                summaryBean.setPitchName(summaryObject.getString("pitch_name"));
                                summaryBean.setLocationName(summaryObject.getString("location_name"));
                                summaryBean.setFromDate(summaryObject.getString("from_date"));
                                summaryBean.setToDate(summaryObject.getString("to_date"));
//                                summaryBean.setPrices(summaryObject.getString("prices"));
//                                summaryBean.setBulkHoursDiscount(summaryObject.getString("bulk_hours_discount"));
//                                summaryBean.setBulkHours(summaryObject.getString("bulk_hours"));
//                                summaryBean.setAdditionalBookingDiscount(summaryObject.getString("additional_booking_discount"));
//                                summaryBean.setShowPrice(summaryObject.getString("price_display"));
                                summaryBean.setIsFullPitch(summaryObject.getString("is_full_pitch"));

                                JSONObject discountsObject = summaryObject.getJSONObject("discounts");
                                summaryBean.setBulkHourDiscountAmount(discountsObject.getString("bulk_hour_discount"));
                                summaryBean.setAdditionalDiscountAmount(discountsObject.getString("additional_discount"));
                                summaryBean.setAdditionalDiscountLabel(discountsObject.getString("additional_discount_label"));

                                JSONArray bookingsArray = summaryObject.getJSONArray("bookings");
                                ArrayList<PitchBookingDateBean> bookingDatesList = new ArrayList<>();
                                PitchBookingDateBean bookingDateBean;
                                for(int j=0; j<bookingsArray.length(); j++) {
                                    JSONObject bookingObject = bookingsArray.getJSONObject(j);
                                    bookingDateBean = new PitchBookingDateBean();

                                    bookingDateBean.setShowBookingDate(bookingObject.getString("booking_date_formatted"));
                                    bookingDateBean.setBookingDate(bookingObject.getString("booking_date"));
                                    bookingDateBean.setFromTime(bookingObject.getString("from_time"));
                                    bookingDateBean.setToTime(bookingObject.getString("to_time"));
                                    bookingDateBean.setTime(bookingObject.getString("time"));
                                    bookingDateBean.setInterval(bookingObject.getString("interval"));
                                    bookingDateBean.setAmount(bookingObject.getString("amount"));

                                    bookingDatesList.add(bookingDateBean);
                                }

                                summaryBean.setBookingDatesList(bookingDatesList);
//                                bookedPitchSummaryBean.setUnavailableDates(summaryObject.getString("unavailable"));

                                summaryDataList.add(summaryBean);
                            }

                            updateListView();

                        } else {

                            final Dialog dialog = new Dialog(ParentBookPitchSummaryNewForCalendarViewScreen.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.parent_dialog_no_dates_found);

                            TextView text1 = (TextView) dialog.findViewById(R.id.text1);
                            TextView ok = (TextView) dialog.findViewById(R.id.ok);

                            text1.setText(message);

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog.dismiss();

                                    finish();

                                }
                            });

                            dialog.show();


//                            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
//                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                parentPitchSummaryAdapter.notifyDataSetChanged();
                Utilities.setListViewHeightBasedOnChildren(pitchesDetailListView);

                break;

            case BOOK_PITCH:

                if(response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            String academy_currency = sharedPreferences.getString("academy_currency", null);
                            System.out.println("netAmount::"+overallNetAmount.getText().toString());
                            System.out.println("stored value:: 0.00 "+academy_currency);

                            net_Amount = responseObject.getString("net_amount");
                            ordersId = responseObject.getString("orders_id");
                            applicationContext.getPitchBookingSlotsDataBeanListing().clear();

                            if(overallNetAmount.getText().toString().trim().equals(".00 "+academy_currency) || overallNetAmount.getText().toString().trim().equals("0.00 "+academy_currency) || overallNetAmount.getText().toString().trim().equals("0 "+academy_currency) || overallNetAmount.getText().toString().trim().equals("0.0 "+academy_currency)  || overallNetAmount.getText().toString().trim().equals(".0 "+academy_currency)){
                                System.out.println("in_if_condition");
                                //Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
                                Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParentNetPaymentZeroScreen.class);
                                mainScreen.putExtra("orderID", ordersId);
                                startActivity(mainScreen);

                            }else {
//                                Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
//                                intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                                intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                                intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//
//                                intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
//
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
//                            /*Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParentMainScreen.class);
//                            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(mainScreen);*/


                                hitPaycheck();

                            }



                        } else {
                            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_STATUS:

                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
//
//                            String api_key = hitpayDetail.getString("api_key");
//
//                            //hitPaysAPI(api_key);
//                            hitPaysAPI(Utilities.HITPAY_CALENDAR_FACILITIES_KEY);
//                        }else{
//                                Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
//                                intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                                intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                                intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//
//                                intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
//
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
//                            /*Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParentMainScreen.class);
//                            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            startActivity(mainScreen);*/
//
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_WEB_SERVICE:
                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Intent obj = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, HitPayExample.class);
                            obj.putExtra("url", webviewHitPayurl);
                            startActivity(obj);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;


            case CLEAR_ALL:
                if(response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            if(isBookMore){
                                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                                    Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParticipantMainScreen.class);
                                    mainScreen.putExtra("screenName","bookPitch");
                                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainScreen);

                                }else{
                                    Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParentMainScreen.class);
                                    mainScreen.putExtra("screenName","bookPitch");
                                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainScreen);
                                }


                            } else {
                                Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
                                clearAllLocalData();
                            }
                        } else {
                            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case APPLY_PROMO_CODE:

                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_LONG).show();

                        if(status) {

                            String strDeductAmount = responseObject.getString("deduct_amt");
                            try{
                                promoCodeDeductAmount = Double.parseDouble(strDeductAmount);

                                String academy_currency = sharedPreferences.getString("academy_currency", null);
                                promoCodeDiscount.setText(strDeductAmount+" "+academy_currency);
                                overallNetAmountValue = overallNetAmountValue - promoCodeDeductAmount;

                                if(overallNetAmountValue == 0) {
                                    overallNetAmount.setText("0.00 "+academy_currency);
                                } else {
//                                    overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" AED");
//                                    roundedOffNetAmount = Math.round(overallNetAmountValue);

                                    DecimalFormat df2 = new DecimalFormat("#.##");
                                    overallNetAmountValue =Double.parseDouble( df2.format(overallNetAmountValue));

                                    overallNetAmount.setText(decimalFormat.format(overallNetAmountValue)+" "+academy_currency);
                                }

                            } catch (NumberFormatException e){
                                Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Invalid discount", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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

                            if(facility_state.equalsIgnoreCase("1")){
                                dialogBoxShow(facility_description);
                            }else{
                                gatewayScreenIntent(statusGatewayScreen);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case UPDATE_TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            gatewayScreenIntent(statusGatewayScreen);
                        }else{
                            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }

    private void changeFonts() {
        title.setTypeface(linoType);
        lblAmount.setTypeface(helvetica);
        overallAmount.setTypeface(helvetica);
        lblBulkHourDiscount.setTypeface(helvetica);
        overallBulkHourDiscount.setTypeface(helvetica);
        lblAdditionalDiscount.setTypeface(helvetica);
        overallAdditionalDiscount.setTypeface(helvetica);
        lblNetAmount.setTypeface(helvetica);
        overallNetAmount.setTypeface(helvetica);
        bookMoreButton.setTypeface(linoType);
        makePaymentButton.setTypeface(linoType);
    }

    public void hitPaycheck(){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_status";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, HITPAY_STATUS, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPaysAPI(String api_key){
        if (Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", loggedInUser.getEmail()));
            nameValuePairList.add(new BasicNameValuePair("redirect_url", Utilities.BASE_URL+"payments/hitpay_response"));
            nameValuePairList.add(new BasicNameValuePair("webhook", Utilities.BASE_URL+"payments/hitpay_webhook"));
            nameValuePairList.add(new BasicNameValuePair("amount", net_Amount));
            nameValuePairList.add(new BasicNameValuePair("currency", academy_currency));
            nameValuePairList.add(new BasicNameValuePair("reference_number", ordersId +"- (Android)"));


            String webServiceUrl = ""+Utilities.HITPAY_API;

            HitPayPostWebServiceWithHeadersAsync postWebServiceAsync = new HitPayPostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, HITPAY_WEB_SERVICE, ParentBookPitchSummaryNewForCalendarViewScreen.this, api_key);
            postWebServiceAsync.execute(webServiceUrl);
        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void hitPayIdStoreAPI(String id){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("hitpay_id", id));
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            nameValuePairList.add(new BasicNameValuePair("hitpay_salt", Utilities.SALT_HITPAY_CALENDAR_FACILITIES_KEY));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_refer_id";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, HITPAY_ID_STORE_SERVICE, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void termSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/get_term_condition_settings";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, TERM_COND_SETTING, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTermSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentBookPitchSummaryNewForCalendarViewScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/update_order_term_condition";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentBookPitchSummaryNewForCalendarViewScreen.this, nameValuePairList, UPDATE_TERM_COND_SETTING, ParentBookPitchSummaryNewForCalendarViewScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentBookPitchSummaryNewForCalendarViewScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogBoxShow(String text){

        final Dialog dialogAddNotes = new Dialog(ParentBookPitchSummaryNewForCalendarViewScreen.this);
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

           // String api_key = hitpayDetail.getString("api_key");

            //hitPaysAPI(api_key);
            hitPaysAPI(Utilities.HITPAY_CALENDAR_FACILITIES_KEY);
        }else{
            Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
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

                            /*Intent mainScreen = new Intent(ParentBookPitchSummaryNewForCalendarViewScreen.this, ParentMainScreen.class);
                            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainScreen);*/

        }
    }

}