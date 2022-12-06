package com.lap.application.hitPay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.UserBean;
import com.lap.application.league.LeagueMainScreen;
import com.lap.application.parent.ParentMainScreen;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.utils.Utilities;

import androidx.appcompat.app.AppCompatActivity;

public class HitPayExampleForJoinLeagueScreen extends AppCompatActivity {
    WebView webView;
    String url;
    ImageView backButton;
    Button goToDashboard;
    TextView title;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hit_pay_example);
        webView = findViewById(R.id.webView);
        backButton= findViewById(R.id.backButton);
        goToDashboard = findViewById(R.id.goToDashboard);
        title = findViewById(R.id.title);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        url = getIntent().getStringExtra("url");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    System.out.println("clickedHERE:: "+url);
                    // do whatever you want to do on a web link click

                    if(url.contains("hitpay_response")){

                        new CountDownTimer(7000, 1000) {

                            public void onTick(long millisUntilFinished) {
                                //here you can have your logic to set text to edittext
                            }

                            public void onFinish() {
                                goToDashboard.setVisibility(View.VISIBLE);
                                title.setText("RESULT");
                            }

                        }.start();

//                        new Handler().postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                goToDashboard.setVisibility(View.VISIBLE);
//                                title.setText("RESULT");
//                            }
//                        }, 5000);



                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParticipantMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }else if(loggedInUser.getRoleCode().equalsIgnoreCase("league_role")){
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, LeagueMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }else{
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParentMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }


            }
        });

        goToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParticipantMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }else if(loggedInUser.getRoleCode().equalsIgnoreCase("league_role")){
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, LeagueMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }else{
                    Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParentMainScreen.class);
                    mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainScreen);
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
            Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParticipantMainScreen.class);
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);
        }else if(loggedInUser.getRoleCode().equalsIgnoreCase("league_role")){
            Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, LeagueMainScreen.class);
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);
        }else{
            Intent mainScreen = new Intent(HitPayExampleForJoinLeagueScreen.this, ParentMainScreen.class);
            mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainScreen);
        }
    }
}