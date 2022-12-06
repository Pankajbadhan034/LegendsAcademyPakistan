package com.lap.application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lap.application.child.ChildMainScreen;
import com.lap.application.coach.CoachMainScreen;
import com.lap.application.league.LeagueMainScreen;
import com.lap.application.parent.ParentMainScreen;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity implements IWebServiceCallback {

    SharedPreferences sharedPreferences;
    boolean isUserLoggedIn;
    boolean isGuestUser;
    String typeOfUser;
    private final String CHECK_FOR_UPDATES = "CHECK_FOR_UPDATES";
    private final String NEWS_API = "NEWS_API";

    LinearLayout linear;
    ListView modeList;
    ArrayList<ListView> listViewArrayList = new ArrayList<>();
    ArrayList<String> stringArrayList = new ArrayList<>();

    ApplicationContext applicationContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_activity_splash_screen);


        try{
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            System.out.println("here1");
            applicationContext = (ApplicationContext) getApplication();
            applicationContext.getAcademySessionChildBeanListing().clear();
            applicationContext.getPitchBookingSlotsDataBeanListing().clear();
        }catch (Exception e){
            System.out.println("here2");
            e.printStackTrace();
        }

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false);
        isGuestUser = sharedPreferences.getBoolean("guestUser", false);
        typeOfUser = sharedPreferences.getString("typeOfUser", "");
        System.out.println("HERE_onCreate");
        printHashKey(SplashScreen.this);
        checkForUpdates();


//        String webServiceUrl = "https://www.ospreysrugby.com/api/v2/content?path=%2Fnews%2Fgeorge-north-statement";
//        GetWebServiceAsync getWebServiceWithHeadersAsync = new GetWebServiceAsync(SplashScreen.this, NEWS_API, SplashScreen.this);
//        getWebServiceWithHeadersAsync.execute(webServiceUrl);


    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("TAG_1", "printHashKey() Hash Key: " + hashKey);
                System.out.println("HASH::"+hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("TAG_2", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("TAG_3", "printHashKey()", e);
        }
    }

    private void checkForUpdates() {
        System.out.println("checkForUpdates_Method::");
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("build_version", Utilities.SYSTEM_VERSION));
        String webServiceUrl = Utilities.BASE_URL + "account/get_app_version";

        PostWebServiceAsync postWebServiceAsync = new PostWebServiceAsync(SplashScreen.this, nameValuePairList, CHECK_FOR_UPDATES, SplashScreen.this);
        postWebServiceAsync.execute(webServiceUrl);
    }

    private void showUpdateDialog(String message) {
        System.out.println("inDialgoMethod::");
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        builder.setMessage(message);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case CHECK_FOR_UPDATES:
                //System.out.println("Response::" + response);

                if (response == null) {
                    Toast.makeText(SplashScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            String currentSystemUpdateLevel = responseObject.getString("CURRENT_SYS_UPDATE_LEVEL");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String verbiage = responseObject.getString("verbiage");
                            editor.putString("verbiage_singular", verbiage);
                            String academy_id = responseObject.getString("academy_id");
                            editor.putString("academy_id_stored", academy_id);
                            editor.commit();

                            if (currentSystemUpdateLevel.equalsIgnoreCase("1")) {
                                // Show dialog
                                System.out.println("ShowDialogHere::");
                                showUpdateDialog(message);
                            } else {
                                System.out.println("elseCase::");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isUserLoggedIn) {
                                            switch (typeOfUser) {
                                                case "parent":
//                                                    Intent obj = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                                    startActivity(obj);

                                                    Intent parentDashboard = new Intent(SplashScreen.this, ParentMainScreen.class);
                                                    startActivity(parentDashboard);
                                                    break;
                                                case "child":
//                                                    Intent obj1 = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                                    startActivity(obj1);
                                                    Intent childDashboard = new Intent(SplashScreen.this, ChildMainScreen.class);
                                                    startActivity(childDashboard);
                                                    break;
                                                case "coach":
                                                    Intent coachDashboard = new Intent(SplashScreen.this, CoachMainScreen.class);
                                                    startActivity(coachDashboard);
                                                    break;
                                                case "participant":
                                                    Intent participantDashboard = new Intent(SplashScreen.this, ParticipantMainScreen.class);
                                                    startActivity(participantDashboard);
                                                    break;
                                                case "league":
                                                    Intent leagueDashboard = new Intent(SplashScreen.this, LeagueMainScreen.class);
                                                    startActivity(leagueDashboard);
                                                    break;
                                            }
                                        } else {
//                                            if(isGuestUser){
//                                                Intent obj = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                                startActivity(obj);
//                                            }else{
//                                                Intent obj = new Intent(SplashScreen.this, StartModuleEnterEmailScreen.class);
//                                                startActivity(obj);
//                                            }




                                            Intent loginScreen = new Intent(SplashScreen.this, LoginScreen.class);
                                            startActivity(loginScreen);
                                        }
                                        finish();
                                    }
                                }, 1000);
                                Toast.makeText(SplashScreen.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } else if (status == false) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String verbiage = responseObject.getString("verbiage");
                            editor.putString("verbiage_singular", verbiage);
                            String academy_id = responseObject.getString("academy_id");
                            editor.putString("academy_id_stored", academy_id);
                            editor.commit();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isUserLoggedIn) {
                                        switch (typeOfUser) {
                                            case "parent":
//                                                Intent obj = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                                startActivity(obj);
                                                Intent parentDashboard = new Intent(SplashScreen.this, ParentMainScreen.class);
                                                startActivity(parentDashboard);
                                                break;
                                            case "child":
//                                                Intent obj1 = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                                startActivity(obj1);
                                                Intent childDashboard = new Intent(SplashScreen.this, ChildMainScreen.class);
                                                startActivity(childDashboard);
                                                break;
                                            case "coach":
                                                Intent coachDashboard = new Intent(SplashScreen.this, CoachMainScreen.class);
                                                startActivity(coachDashboard);
                                                break;
                                            case "participant":
                                                Intent participantDashboard = new Intent(SplashScreen.this, ParticipantMainScreen.class);
                                                startActivity(participantDashboard);
                                                break;
                                            case "league":
                                                Intent leagueDashboard = new Intent(SplashScreen.this, LeagueMainScreen.class);
                                                startActivity(leagueDashboard);
                                                break;
                                        }
                                    } else {
//                                        if(isGuestUser){
//                                            Intent obj = new Intent(SplashScreen.this, StartModuleDashboardScreen.class);
//                                            startActivity(obj);
//                                        }else{
//                                            Intent obj = new Intent(SplashScreen.this, StartModuleEnterEmailScreen.class);
//                                            startActivity(obj);
//                                        }

                                        Intent loginScreen = new Intent(SplashScreen.this, LoginScreen.class);
                                        startActivity(loginScreen);
                                    }
                                    finish();
                                }
                            }, 1000);
                        } else {
                            Toast.makeText(SplashScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(SplashScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }



}