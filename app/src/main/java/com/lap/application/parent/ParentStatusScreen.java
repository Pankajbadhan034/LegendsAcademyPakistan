package com.lap.application.parent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.UserBean;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;

public class ParentStatusScreen extends AppCompatActivity {
    UserBean loggedInUser;
    SharedPreferences sharedPreferences;

    Typeface helvetica;
    Typeface linoType;

    TextView title;
    TextView status;
    TextView orderId;
    TextView trackingId;
    TextView bankRefNo;
    TextView orderStatus;
    TextView failureMessage;
    TextView paymentMode;
    TextView cardName;
    TextView statusMessage;
    TextView currency;
    TextView amount;
    TextView billingName;
    TextView billingAddress;
    TextView billingTel;
    TextView billingEmail;
    Button goToDashboard;
    TextView txtOrderId;
    TextView txtTrackingId;
    TextView txtBankRefNo;
    TextView txtOrderStatus;
    TextView txtFailureMessage;
    TextView txtPaymentMode;
    TextView txtCardName;
    TextView txtStatusMessage;
    TextView txtCurrency;
    TextView txtAmount;
    TextView txtBillingName;
    TextView txtBillingAddress;
    TextView txtBillingTel;
    TextView txtBillingEmail;

    String strOrderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity_status_screen);

        helvetica = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        title = (TextView) findViewById(R.id.title);
        status = (TextView) findViewById(R.id.status);
        orderId = (TextView) findViewById(R.id.orderId);
        trackingId = (TextView) findViewById(R.id.trackingId);
        bankRefNo = (TextView) findViewById(R.id.bankRefNo);
        orderStatus = (TextView) findViewById(R.id.orderStatus);
        failureMessage = (TextView) findViewById(R.id.failureMessage);
        paymentMode = (TextView) findViewById(R.id.paymentMode);
        cardName = (TextView) findViewById(R.id.cardName);
        statusMessage = (TextView) findViewById(R.id.statusMessage);
        currency = (TextView) findViewById(R.id.currency);
        amount = (TextView) findViewById(R.id.amount);
        billingName = (TextView) findViewById(R.id.billingName);
        billingAddress = (TextView) findViewById(R.id.billingAddress);
        billingTel = (TextView) findViewById(R.id.billingTel);
        billingEmail = (TextView) findViewById(R.id.billingEmail);
        goToDashboard = (Button) findViewById(R.id.goToDashboard);
        txtOrderId = (TextView) findViewById(R.id.txtOrderId);
        txtTrackingId = (TextView) findViewById(R.id.txtTrackingId);
        txtBankRefNo = (TextView) findViewById(R.id.txtBankRefNo);
        txtOrderStatus = (TextView) findViewById(R.id.txtOrderStatus);
        txtFailureMessage = (TextView) findViewById(R.id.txtFailureMessage);
        txtPaymentMode = (TextView) findViewById(R.id.txtPaymentMode);
        txtCardName = (TextView) findViewById(R.id.txtCardName);
        txtStatusMessage = (TextView) findViewById(R.id.txtStatusMessage);
        txtCurrency = (TextView) findViewById(R.id.txtCurrency);
        txtAmount = (TextView) findViewById(R.id.txtAmount);
        txtBillingName = (TextView) findViewById(R.id.txtBillingName);
        txtBillingAddress = (TextView) findViewById(R.id.txtBillingAddress);
        txtBillingTel = (TextView) findViewById(R.id.txtBillingTel);
        txtBillingEmail = (TextView) findViewById(R.id.txtBillingEmail);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }


        changeFonts();

        Intent intent = getIntent();

        String response = intent.getStringExtra("response");

        try {
            JSONObject responseObject = new JSONObject(response);

            strOrderStatus = responseObject.getString("order_status");
            JSONArray decryptArray = responseObject.getJSONArray("decryptValues");

            status.setText(strOrderStatus);
            if(strOrderStatus.equalsIgnoreCase("success")) {
                status.setBackgroundColor(getResources().getColor(R.color.darkGreen));
            } else {
                status.setBackgroundColor(getResources().getColor(R.color.red));
            }

            orderId.setText(findValue(decryptArray.getString(0)));
            trackingId.setText(findValue(decryptArray.getString(1)));
            bankRefNo.setText(findValue(decryptArray.getString(2)));
            orderStatus.setText(findValue(decryptArray.getString(3)));
            failureMessage.setText(findValue(decryptArray.getString(4)));
            paymentMode.setText(findValue(decryptArray.getString(5)));
            cardName.setText(findValue(decryptArray.getString(6)));
            statusMessage.setText(findValue(decryptArray.getString(8)));
            currency.setText(findValue(decryptArray.getString(9)));
            amount.setText(findValue(decryptArray.getString(10)));
            billingName.setText(findValue(decryptArray.getString(11)));
            billingAddress.setText(findValue(decryptArray.getString(12)));
            billingTel.setText(findValue(decryptArray.getString(17)));
            billingEmail.setText(findValue(decryptArray.getString(18)));

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(ParentStatusScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }


        goToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                    Intent mainScreen = new Intent(ParentStatusScreen.this, ParticipantMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }else{

                    Intent mainScreen = new Intent(ParentStatusScreen.this, ParentMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }

            }
        });
    }

    private String findValue(String value){
        String[] arr = value.split("=");
        if(arr.length == 2) {
            return arr[1];
        }
        return "";
    }

    @Override
    public void onBackPressed() {
        if(strOrderStatus.equalsIgnoreCase("success")) {
            // Do Nothing
        } else {
            super.onBackPressed();
        }
    }

    private void changeFonts(){
        title.setTypeface(linoType);
        status.setTypeface(helvetica);
        orderId.setTypeface(helvetica);
        trackingId.setTypeface(helvetica);
        bankRefNo.setTypeface(helvetica);
        orderStatus.setTypeface(helvetica);
        failureMessage.setTypeface(helvetica);
        paymentMode.setTypeface(helvetica);
        cardName.setTypeface(helvetica);
        statusMessage.setTypeface(helvetica);
        currency.setTypeface(helvetica);
        amount.setTypeface(helvetica);
        billingName.setTypeface(helvetica);
        billingAddress.setTypeface(helvetica);
        billingTel.setTypeface(helvetica);
        billingEmail.setTypeface(helvetica);
        goToDashboard.setTypeface(linoType);
        txtOrderId.setTypeface(helvetica);
        txtTrackingId.setTypeface(helvetica);
        txtBankRefNo.setTypeface(helvetica);
        txtOrderStatus.setTypeface(helvetica);
        txtFailureMessage.setTypeface(helvetica);
        txtPaymentMode.setTypeface(helvetica);
        txtCardName.setTypeface(helvetica);
        txtStatusMessage.setTypeface(helvetica);
        txtCurrency.setTypeface(helvetica);
        txtAmount.setTypeface(helvetica);
        txtBillingName.setTypeface(helvetica);
        txtBillingAddress.setTypeface(helvetica);
        txtBillingTel.setTypeface(helvetica);
        txtBillingEmail.setTypeface(helvetica);
    }
}