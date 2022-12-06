package com.lap.application.parent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.UserBean;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.utils.Utilities;

import androidx.appcompat.app.AppCompatActivity;

public class ParentNetPaymentZeroScreen extends AppCompatActivity {
    Button goToDashboard;
    UserBean loggedInUser;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_net_payment_zero_activity);
        goToDashboard = (Button) findViewById(R.id.goToDashboard);
        TextView orderID = (TextView) findViewById(R.id.orderID);

        String orderIdStr = getIntent().getStringExtra("orderID");
        orderID.setText("Order ID : "+orderIdStr);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        goToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                    Intent mainScreen = new Intent(ParentNetPaymentZeroScreen.this, ParticipantMainScreen.class);
                    mainScreen.putExtra("screenName","bookPitch");
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);

                }else{
                    Intent mainScreen = new Intent(ParentNetPaymentZeroScreen.this, ParentMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
            Intent mainScreen = new Intent(ParentNetPaymentZeroScreen.this, ParticipantMainScreen.class);
            mainScreen.putExtra("screenName","bookPitch");
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);

        }else{
            Intent mainScreen = new Intent(ParentNetPaymentZeroScreen.this, ParentMainScreen.class);
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);
        }

    }
}