package com.lap.application.startModule;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.SliderImagesBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.CoachMainScreen;
import com.lap.application.parent.ParentAcademyListingWithFiltersScreen;
import com.lap.application.parent.ParentForgotPassword;
import com.lap.application.parent.ParentSignUpScreen;
import com.lap.application.participant.ParticipantMainScreen;
import com.lap.application.startModule.adapter.ShopSliderAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceAsync;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class StartModuleDashboardScreen extends AppCompatActivity implements IWebServiceCallback {
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.

    UserBean loggedInUser;
    LinearLayout forgotPasswordLinear;
    LinearLayout signUpLinear;
    Button login;
    EditText email;
    EditText password;
    TextInputLayout emailTextInputLayout;
    TextInputLayout passwordTextInputLayout;
    TextView forgotPassword;
    TextView clickHere;
    TextView notAMember;
    TextView signUpNow;
    String strEmail;
    String strPassword;

    private final String LOGIN_WEB_SERVICE = "LOGIN_WEB_SERVICE";
    private final String DO_LOGOUT = "DO_LOGOUT";

    // Run time permission
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[]{
            Manifest.permission.READ_CALENDAR,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
         //   android.Manifest.permission.RECORD_AUDIO
    };


    boolean isGuestUser;
    boolean isUserLoggedIn;
    String typeOfUser;
    SharedPreferences sharedPreferences;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    private final String ONLINE_PRODUCT_DATA = "ONLINE_PRODUCT_DATA";

    LinearLayout homeLinear;
    ImageView home;
    LinearLayout ospreysLinear;
    ImageView ospreys;
    LinearLayout strategyLinear;
    ImageView strategy;
    LinearLayout rugbyLinear;
    ImageView rugby;
    TextView title;
    LinearLayout linearView1;
    LinearLayout linearView2;
    LinearLayout linearView3;
    LinearLayout linearView4;
    LinearLayout linearView4Login;

    // tab 1
    String linkStr;
    ViewPager adImage;
    LinearLayout linear1Tab1;
    LinearLayout linear2Tab1;
    LinearLayout linear3Tab1;
    LinearLayout linear4Tab1;
    LinearLayout linear5Tab1;
    LinearLayout linear6Tab1;
    String clickIdStr, titleStr;


    // tab 2
    LinearLayout contactUs2;
    LinearLayout linear4Tab2;
    LinearLayout linear5Tab2;
    LinearLayout tickets;
    LinearLayout merchandise;

    // tab 4
    LinearLayout schedule;
    LinearLayout contactUs4;
    LinearLayout newsfeed;
    LinearLayout pic;
    LinearLayout reports;
    TextView attendSessionTV4;
    TextView reportsTV4;
    TextView newsfeedTV4;
    TextView programsTV4;
    TextView scheduleTV4;
    TextView picTV4;
    TextView contactTV4;

    ImageView logout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_module_dashboard_activity);
        homeLinear = findViewById(R.id.homeLinear);
        home = findViewById(R.id.home);
        ospreysLinear = findViewById(R.id.ospreysLinear);
        ospreys = findViewById(R.id.ospreys);
        strategyLinear = findViewById(R.id.strategyLinear);
        strategy = findViewById(R.id.strategy);
        rugbyLinear = findViewById(R.id.rugbyLinear);
        rugby = findViewById(R.id.rugby);
        title = findViewById(R.id.title);
        linearView1 = findViewById(R.id.linearView1);
        linearView2 = findViewById(R.id.linearView2);
        linearView3 = findViewById(R.id.linearView3);
        linearView4 = findViewById(R.id.linearView4);
        linearView4Login = findViewById(R.id.linearView4Login);
        logout = findViewById(R.id.logout);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        isUserLoggedIn = sharedPreferences.getBoolean("isUserLoggedIn", false);
        isGuestUser = sharedPreferences.getBoolean("guestUser", false);
        typeOfUser = sharedPreferences.getString("typeOfUser", "");

        if(isGuestUser && isUserLoggedIn){
            logout.setVisibility(View.VISIBLE);
            Gson gson = new Gson();
            String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
            if (jsonLoggedInUser != null) {
                loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
            }
        }else{
            logout.setVisibility(View.GONE);
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(StartModuleDashboardScreen.this));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(StartModuleDashboardScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.parent_dialog_logout);
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

                        doLogout();

                    }
                });

                dialog.show();
            }
        });

        // tab 1
        linear1Tab1 = findViewById(R.id.linear1Tab1);
        linear2Tab1 = findViewById(R.id.linear2Tab1);
        linear3Tab1 = findViewById(R.id.linear3Tab1);
        linear4Tab1 = findViewById(R.id.linear4Tab1);
        linear5Tab1 = findViewById(R.id.linear5Tab1);
        linear6Tab1 = findViewById(R.id.linear6Tab1);
        adImage = findViewById(R.id.adImage);

        linear1Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "1";
                titleStr = "ANALYSIS";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear2Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "6";
                titleStr = "VIDEO";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear3Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "4";
                titleStr = "SESSION PLANS";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear4Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "5";
                titleStr = "SKILLS CURRICULUM";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear5Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "2";
                titleStr = "HEALTH AND WELL BEING";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear6Tab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "3";
                titleStr = "NUTRITION";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleResourceScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });

//        adImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
////                try{
////                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(linkStr));
////                    startActivity(browserIntent);
////                }catch (Exception e){
////                    Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
////                }
//                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleWebViewScreen.class);
//                obj.putExtra("link",linkStr);
//                startActivity(obj);
//            }
//        });

        // tab 2
        contactUs2 = findViewById(R.id.contactUs2);
        linear4Tab2 = findViewById(R.id.linear4Tab2);
        linear5Tab2 = findViewById(R.id.linear5Tab2);
        tickets = findViewById(R.id.tickets);
        merchandise = findViewById(R.id.merchandise);
        tickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://www.eticketing.co.uk/ospreysrugby/"));
                    startActivity(browserIntent);
                }catch (Exception e){
                    Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


//                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleWebViewScreen.class);
//                obj.putExtra("link", "https://www.eticketing.co.uk/ospreysrugby/");
//                startActivity(obj);

            }
        });
        merchandise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://shop.ospreysrugby.com/"));
                    startActivity(browserIntent);
                }catch (Exception e){
                    Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }



//                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleWebViewScreen.class);
//                obj.putExtra("link", "https://shop.ospreysrugby.com/");
//                startActivity(obj);

            }
        });
        contactUs2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleContactUsScreen.class);
                startActivity(obj);
            }
        });
        linear4Tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "Offers";
                titleStr = "OFFERS";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartOfferAndLegacyScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });
        linear5Tab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickIdStr = "Legacy";
                titleStr = "LEGACY";
                Intent obj = new Intent(StartModuleDashboardScreen.this, StartOfferAndLegacyScreen.class);
                obj.putExtra("id",clickIdStr);
                obj.putExtra("title",titleStr);
                startActivity(obj);
            }
        });

        // tab 4 login screen
        forgotPasswordLinear = (LinearLayout) findViewById(R.id.forgotPasswordLinear);
        signUpLinear = (LinearLayout) findViewById(R.id.signUpLinear);
        login = (Button) findViewById(R.id.login);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        clickHere = (TextView) findViewById(R.id.clickHere);
        notAMember = (TextView) findViewById(R.id.notAMember);
        signUpNow = (TextView) findViewById(R.id.signUpNow);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strEmail = email.getText().toString().trim();
                strPassword = password.getText().toString().trim();
                String fcmToken = sharedPreferences.getString("fcmToken", "");

                Pattern pattern = Pattern.compile(Utilities.EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(strEmail);

                if (strEmail == null || strEmail.isEmpty()) {
                    Toast.makeText(StartModuleDashboardScreen.this, "Please enter Email", Toast.LENGTH_SHORT).show();
                }
//                else if (!matcher.matches()) {
//                    Toast.makeText(getApplicationContext(), "Please enter a valid Email", Toast.LENGTH_SHORT).show();
//                }
                else if (strPassword == null || strPassword.isEmpty()) {
                    Toast.makeText(StartModuleDashboardScreen.this, "Please enter Password", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utilities.isNetworkAvailable(StartModuleDashboardScreen.this)) {
                        List<NameValuePair> nameValuePairList = new ArrayList<>();
                        nameValuePairList.add(new BasicNameValuePair("lemail", strEmail));
                        nameValuePairList.add(new BasicNameValuePair("lpassword", strPassword));
                        nameValuePairList.add(new BasicNameValuePair("fcm_device_token", fcmToken));
                        nameValuePairList.add(new BasicNameValuePair("device_type", "1"));

                        String webServiceUrl = Utilities.BASE_URL + "account/login";

                        PostWebServiceAsync postWebServiceAsync = new PostWebServiceAsync(StartModuleDashboardScreen.this, nameValuePairList, LOGIN_WEB_SERVICE, StartModuleDashboardScreen.this);
                        postWebServiceAsync.execute(webServiceUrl);
                    } else {
                        Toast.makeText(StartModuleDashboardScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        forgotPasswordLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPassword = new Intent(StartModuleDashboardScreen.this, ParentForgotPassword.class);
                startActivity(forgotPassword);
            }
        });

        signUpLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp = new Intent(StartModuleDashboardScreen.this, ParentSignUpScreen.class);
                startActivity(signUp);
            }
        });





        // tab 4 colorful dashboard
        schedule = findViewById(R.id.schedule);
        contactUs4 = findViewById(R.id.contactUs4);
        newsfeed = findViewById(R.id.newsfeed);
        reports = findViewById(R.id.reports);
        pic = findViewById(R.id.pic);
        attendSessionTV4 = findViewById(R.id.attendSessionTV4);
        reportsTV4 = findViewById(R.id.reportsTV4);
        newsfeedTV4 = findViewById(R.id.newsfeedTV4);
        programsTV4 = findViewById(R.id.programsTV4);
        scheduleTV4 = findViewById(R.id.scheduleTV4);
        picTV4 = findViewById(R.id.picTV4);
        contactTV4 = findViewById(R.id.contactTV4);

      //  if(!isGuestUser){
            if(typeOfUser.equalsIgnoreCase("parent")){
                attendSessionTV4.setText("");
                programsTV4.setText("");
                picTV4.setText("");
                contactTV4.setText("");

                reports.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleParentViewMarksScreen.class);
//                        startActivity(obj);

                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleReportsTypeScreen.class);
                        obj.putExtra("typeViewMarks", "parent");
                        startActivity(obj);


                    }
                });
                newsfeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
                        obj.putExtra("type", "NEWSFEED");
                        startActivity(obj);
                    }
                });
                schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4ScheduleScreen.class);
                        startActivity(obj);
                    }
                });

            }else if(typeOfUser.equalsIgnoreCase("child")){

//                if(loggedInUser.getUser_type().equalsIgnoreCase("5")){
//                   // picTV4.setText("BOOK CAMP");
//                }else{
//                   // picTV4.setText("");
//                }

                attendSessionTV4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(loggedInUser.getUser_type().equalsIgnoreCase("5")){
//                            Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
//                            obj.putExtra("type", "BOOK NOW");
//                            startActivity(obj);
                            Intent intent = new Intent(StartModuleDashboardScreen.this, ParentAcademyListingWithFiltersScreen.class);
                            startActivity(intent);
                        }else{
//                            Intent intent = new Intent(StartModuleDashboardScreen.this, ParentAcademyListingWithFiltersScreen.class);
//                            startActivity(intent);
                            Toast.makeText(StartModuleDashboardScreen.this, "You are not authorized to access this functionality", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                programsTV4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4MonitorAndCovidQaScreen.class);
                        startActivity(obj);
                    }
                });




                reports.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(loggedInUser.getUser_type().equalsIgnoreCase("5")){

                            Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleReportsTypeScreen.class);
                            obj.putExtra("typeViewMarks", "child");
                            startActivity(obj);

//                            Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleParentViewMarksScreen.class);
//                            startActivity(obj);
                        }else{
                            Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4ReportsScreen.class);
                            startActivity(obj);
                        }

                    }
                });
                newsfeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
                        obj.putExtra("type", "NEWSFEED + POST");
                        startActivity(obj);
//                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
//                        obj.putExtra("type", "NEWSFEED");
//                        startActivity(obj);
                    }
                });
                schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4ScheduleScreen.class);
                        startActivity(obj);
                    }
                });
                pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4PicScreen.class);
//                        startActivity(obj);
                    }
                });
                contactUs4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleContactUs.class);
                        startActivity(obj);
                    }
                });
            }else if(typeOfUser.equalsIgnoreCase("coach")){
                reportsTV4.setText("IDP's");
                attendSessionTV4.setText("ATTENDANCE");
                programsTV4.setText("COACH AREA");
                attendSessionTV4.setTextSize(11);
                picTV4.setText("");
                contactTV4.setText("CONTACT US");

                attendSessionTV4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
                        obj.putExtra("type", "ATTENDANCE");
                        startActivity(obj);

                    }
                });

                programsTV4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // http://202.164.57.202/ospreysrugby/public/api/v1/coach/timeline
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
                        obj.putExtra("type", "COACH AREA");
                        startActivity(obj);
                    }
                });

                reportsTV4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4AttendanceScreen.class);
                        obj.putExtra("type", "IDP's");
                        startActivity(obj);
                    }
                });

                newsfeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4NewsFeedScreen.class);
                        obj.putExtra("type", "NEWSFEED");
                        startActivity(obj);
                    }
                });
                schedule.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleTab4ScheduleScreen.class);
                        startActivity(obj);
                    }
                });
                contactUs4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleContactUsScreen.class);
                            startActivity(obj);


                    }
                });
            }

//        }





        adList();

        homeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adList();
                title.setText("LATEST NEWS");

                linearView1.setVisibility(View.VISIBLE);
                linearView2.setVisibility(View.GONE);
                linearView3.setVisibility(View.GONE);
                linearView4.setVisibility(View.GONE);
                linearView4Login.setVisibility(View.GONE);

                homeLinear.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                ospreysLinear.setBackgroundColor(getResources().getColor(R.color.black));
                strategyLinear.setBackgroundColor(getResources().getColor(R.color.black));
                rugbyLinear.setBackgroundColor(getResources().getColor(R.color.black));

                home.setBackgroundResource(R.drawable.home_c_hover);
                ospreys.setBackgroundResource(R.drawable.oesprays);
                strategy.setBackgroundResource(R.drawable.strategy_c);
                rugby.setBackgroundResource(R.drawable.rugby_c);

            }
        });

        ospreysLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("OSPREYS");

                linearView1.setVisibility(View.GONE);
                linearView2.setVisibility(View.VISIBLE);
                linearView3.setVisibility(View.GONE);
                linearView4.setVisibility(View.GONE);
                linearView4Login.setVisibility(View.GONE);

                homeLinear.setBackgroundColor(getResources().getColor(R.color.black));
                ospreysLinear.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                strategyLinear.setBackgroundColor(getResources().getColor(R.color.black));
                rugbyLinear.setBackgroundColor(getResources().getColor(R.color.black));

                home.setBackgroundResource(R.drawable.home_c);
                ospreys.setBackgroundResource(R.drawable.oesprays_hover);
                strategy.setBackgroundResource(R.drawable.strategy_c);
                rugby.setBackgroundResource(R.drawable.rugby_c);

            }
        });

        strategyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title.setText("RESOURCES");

                linearView1.setVisibility(View.GONE);
                linearView2.setVisibility(View.GONE);
                linearView3.setVisibility(View.VISIBLE);
                linearView4.setVisibility(View.GONE);
                linearView4Login.setVisibility(View.GONE);

                homeLinear.setBackgroundColor(getResources().getColor(R.color.black));
                ospreysLinear.setBackgroundColor(getResources().getColor(R.color.black));
                strategyLinear.setBackgroundColor(getResources().getColor(R.color.darkBlue));
                rugbyLinear.setBackgroundColor(getResources().getColor(R.color.black));

                home.setBackgroundResource(R.drawable.home_c);
                ospreys.setBackgroundResource(R.drawable.oesprays);
                strategy.setBackgroundResource(R.drawable.strategy_c_hover);
                rugby.setBackgroundResource(R.drawable.rugby_c);

            }
        });

        rugbyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                linearView1.setVisibility(View.GONE);
                linearView2.setVisibility(View.GONE);
                linearView3.setVisibility(View.GONE);

                homeLinear.setBackgroundColor(getResources().getColor(R.color.black));
                ospreysLinear.setBackgroundColor(getResources().getColor(R.color.black));
                strategyLinear.setBackgroundColor(getResources().getColor(R.color.black));
                rugbyLinear.setBackgroundColor(getResources().getColor(R.color.darkBlue));

                home.setBackgroundResource(R.drawable.home_c);
                ospreys.setBackgroundResource(R.drawable.oesprays);
                strategy.setBackgroundResource(R.drawable.strategy_c);
                rugby.setBackgroundResource(R.drawable.rugby_c_hover);


                if(isGuestUser && isUserLoggedIn){

                    System.out.println("HERE::"+loggedInUser.getUser_type());

                    if(loggedInUser.getRoleCode().equalsIgnoreCase("parent_role")){
                        title.setText("PERFORM - PARENT VIEW");
                    }else if(loggedInUser.getRoleCode().equalsIgnoreCase("child_role")){
                        title.setText("PERFORM - CHILD VIEW");
                    }else{
                        title.setText("PERFORM - COACH VIEW");
                    }


                    linearView4.setVisibility(View.VISIBLE);
                    linearView4Login.setVisibility(View.GONE);
                    logout.setVisibility(View.VISIBLE);

                    Gson gson = new Gson();
                    String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
                    if (jsonLoggedInUser != null) {
                        loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
                    }

                }else{
                    title.setText("LOGIN");
                    linearView4.setVisibility(View.GONE);
                    linearView4Login.setVisibility(View.VISIBLE);
                    logout.setVisibility(View.GONE);

                }



            }
        });

    }

    private void adList() {
        if (Utilities.isNetworkAvailable(StartModuleDashboardScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();

            nameValuePairList.add(new BasicNameValuePair("academy_id", "1"));

            String webServiceUrl = Utilities.BASE_URL + "osp_aca/ad_list";

            PostWebServiceAsync postWebServiceAsync = new PostWebServiceAsync(StartModuleDashboardScreen.this, nameValuePairList, ONLINE_PRODUCT_DATA, StartModuleDashboardScreen.this);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(StartModuleDashboardScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void doLogout() {
        if (Utilities.isNetworkAvailable(StartModuleDashboardScreen.this)) {

//            String webServiceUrl = Utilities.BASE_URL + "children/get_child_reg_form";
            String webServiceUrl = Utilities.BASE_URL + "account/logout";
            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(StartModuleDashboardScreen.this, DO_LOGOUT, StartModuleDashboardScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(StartModuleDashboardScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case DO_LOGOUT:

                System.out.println("RES::"+response);

                if (response == null) {
                    Toast.makeText(StartModuleDashboardScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        Toast.makeText(StartModuleDashboardScreen.this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.putBoolean("guestUser", false);
                            editor.commit();

                            Intent loginScreen = new Intent(StartModuleDashboardScreen.this, StartModuleEnterEmailScreen.class);
                            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginScreen);
                        } else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.putBoolean("guestUser", false);
                            editor.commit();

                            Intent loginScreen = new Intent(StartModuleDashboardScreen.this, StartModuleEnterEmailScreen.class);
                            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginScreen);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case LOGIN_WEB_SERVICE:

                if (response == null) {
                    Toast.makeText(StartModuleDashboardScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        System.out.println("response::"+responseObject);

                        if (status) {

                            String token = responseObject.getString("token");
                            JSONObject userData = responseObject.getJSONObject("user_data");

                            UserBean userBean = new UserBean();
                            userBean.setToken(token);

                            userBean.setId(userData.getString("id"));
                            userBean.setAcademiesId(userData.getString("academies_id"));
                            userBean.setUsername(userData.getString("username"));
                            userBean.setEmail(userData.getString("email"));
                            userBean.setGender(userData.getString("gender"));
//                            userBean.setCreatedAt(userData.getString("created_at"));
//                            userBean.setState(userData.getString("state"));
                            userBean.setFirstName(userData.getString("fname"));
                            userBean.setLastName(userData.getString("lname"));
                            userBean.setFullName(userData.getString("full_name"));
                            userBean.setMobileNumber(userData.getString("phone_1"));
                            userBean.setSecondMobileNumber(userData.getString("phone_2"));
//                            userBean.setTotalChildren(userData.getInt("total_children"));
                            userBean.setRoleCode(userData.getString("role_code"));
                            userBean.setAddress(userData.getString("address"));
                            userBean.setProfilePicPath(userData.getString("profile_picture_path"));

                            //added for child module
                            userBean.setUser_type(userData.getString("user_type"));

                            //added for child module
                            if (userBean.getRoleCode().equalsIgnoreCase("child_role") || userBean.getUser_type().equalsIgnoreCase("5")) {
                                userBean.setFavoritePlayer(userData.getString("favourite_player"));
                                userBean.setFavoriteTeam(userData.getString("favourite_team"));
                                userBean.setFavoritePosition(userData.getString("favourite_position"));
                                userBean.setFavoriteFootballBoot(userData.getString("favourite_football_boot"));
                                userBean.setFavoritefood(userData.getString("favourite_food"));
                                userBean.setSchool(userData.getString("school"));
                                userBean.setNationality(userData.getString("nationality"));
                                userBean.setHeight(userData.getString("height"));
                                userBean.setWeight(userData.getString("weight"));
                                userBean.setDobformatted(userData.getString("dob_formatted"));

                                userBean.setHeightNumeric(userData.getString("height"));
                                userBean.setWeightNumeric(userData.getString("weight"));
                                userBean.setFavoritePlayerPicture(userData.getString("favourite_player_picture"));
                                userBean.setFavoriteTemaPicture(userData.getString("favourite_team_picture"));
                                userBean.setHeightFormatted(userData.getString("height_formatted"));
                                userBean.setWeightFormatted(userData.getString("weight_formatted"));

                                userBean.setParentUsername(userData.getString("parent_username"));
                                userBean.setAge(userData.getString("age"));

                                userBean.setFieldCLub(userData.getString("club"));
                                userBean.setClassName(userData.getString("classname"));

                                // userBean.setCategoryId(userData.getString("category_id"));
                            }

                            // added for Coach Module
                            if(userBean.getRoleCode().equalsIgnoreCase("coach_role")){
                                userBean.setCanMoveChild(userData.getString("can_move_child"));
                                userBean.setReportSubmissionType(userData.getString("report_submission_type"));
                            }

                            if(userBean.getRoleCode().equalsIgnoreCase("parent_role")){
                                userBean.setPaymentCard(userData.getString("payment_card"));
                            }

                            userBean.setPhoneCodeOne(userData.getString("phone_code_1"));
                            userBean.setPhoneCodeTwo(userData.getString("phone_code_2"));

                            Gson gson = new Gson();
                            String jsonParentUserBean = gson.toJson(userBean);

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", true);
                            editor.putBoolean("guestUser", true);

                            if (userBean.getRoleCode().equalsIgnoreCase("parent_role")) {
                                editor.putString("typeOfUser", "parent");
                                if(userData.has("academy_currency")){
                                    editor.putString("academy_currency", userData.getString("academy_currency"));
                                    editor.putString("verbiage_singular", userData.getString("verbiage_singular"));
                                    editor.putString("verbiage_plural", userData.getString("verbiage_plural"));
                                    editor.putString("day_start_num", userData.getString("day_start_num"));
                                }else{
                                    editor.putString("academy_currency", "AED");
                                    editor.putString("verbiage_singular", userData.getString("Child"));
                                    editor.putString("verbiage_plural", userData.getString("Children"));
                                    editor.putString("day_start_num", "1");
                                }
                            } else if (userBean.getRoleCode().equalsIgnoreCase("child_role")) {
                                editor.putString("typeOfUser", "child");
                                if(userData.has("club")){
                                    editor.putString("club", userData.getString("club"));
                                    editor.putString("classname", userData.getString("classname"));
                                }
                                if(userData.has("academy_currency")){
                                    editor.putString("academy_currency", userData.getString("academy_currency"));
                                    editor.putString("verbiage_singular", userData.getString("verbiage_singular"));
                                    editor.putString("verbiage_plural", userData.getString("verbiage_plural"));
                                    editor.putString("day_start_num", userData.getString("day_start_num"));
                                }else{
                                    editor.putString("academy_currency", "AED");
                                    editor.putString("verbiage_singular", userData.getString("Child"));
                                    editor.putString("verbiage_plural", userData.getString("Children"));
                                    editor.putString("day_start_num", "1");
                                }
                            } else if (userBean.getRoleCode().equalsIgnoreCase("coach_role")) {
                                editor.putString("typeOfUser", "coach");
                                if(userData.has("academy_currency")){
                                    editor.putString("academy_currency", userData.getString("academy_currency"));
                                    editor.putString("verbiage_singular", userData.getString("verbiage_singular"));
                                    editor.putString("verbiage_plural", userData.getString("verbiage_plural"));
                                    editor.putString("day_start_num", userData.getString("day_start_num"));
                                }else{
                                    editor.putString("academy_currency", "AED");
                                    editor.putString("verbiage_singular", userData.getString("Child"));
                                    editor.putString("verbiage_plural", userData.getString("Children"));
                                    editor.putString("day_start_num", "1");
                                }
                            }

                            editor.putString("loggedInUser", jsonParentUserBean);
                            editor.commit();

                            switch (userBean.getRoleCode()) {
                                case "parent_role":
                                    Intent obj = new Intent(StartModuleDashboardScreen.this, StartModuleDashboardScreen.class);
                                    obj.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(obj);

//                                    Intent parentDashboard = new Intent(LoginScreen.this, ParentMainScreen.class);
//                                    parentDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(parentDashboard);

                                    break;
                                case "child_role":
                                    if(loggedInUser.getRoleCode().equalsIgnoreCase("participant_role")){
                                        Intent mainScreen = new Intent(StartModuleDashboardScreen.this, ParticipantMainScreen.class);
                                        mainScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainScreen);
                                    }else{
                                        Intent obj1 = new Intent(StartModuleDashboardScreen.this, StartModuleDashboardScreen.class);
                                        obj1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(obj1);
                                    }


//                                    Intent childDashboard = new Intent(LoginScreen.this, ChildMainScreen.class);
//                                    childDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                    startActivity(childDashboard);

                                    break;
                                case "coach_role":

                                    Intent coachDashboard = new Intent(StartModuleDashboardScreen.this, CoachMainScreen.class);
                                    coachDashboard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(coachDashboard);

                                    break;
                            }


                        } else {
                            Toast.makeText(StartModuleDashboardScreen.this, message, Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case ONLINE_PRODUCT_DATA:

                if (response == null) {
                    Toast.makeText(StartModuleDashboardScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray jsonArray = responseObject.getJSONArray("data");
                            final ArrayList<SliderImagesBean> sliderBeanArrayList = new ArrayList<>();
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                SliderImagesBean sliderImagesBean = new SliderImagesBean();
                                sliderImagesBean.setImage(jsonObject.getString("image"));
                                sliderImagesBean.setImage_url(jsonObject.getString("image_url"));
                                sliderImagesBean.setUrl(jsonObject.getString("url"));
                                linkStr = jsonObject.getString("url");
                                sliderBeanArrayList.add(sliderImagesBean);
                              //  imageLoader.displayImage(imageUrl+""+fileName, adImage, options);
                            }

                            ShopSliderAdapter shopSliderAdapter = new ShopSliderAdapter(StartModuleDashboardScreen.this, sliderBeanArrayList);
                            adImage.setAdapter(shopSliderAdapter);

                            /*After setting the adapter use the timer */
                            final Handler handler = new Handler();
                            final Runnable Update = new Runnable() {
                                public void run() {
                                    if (currentPage == sliderBeanArrayList.size()) {
                                        currentPage = 0;
                                    }
                                    adImage.setCurrentItem(currentPage++, true);
                                }
                            };

                            timer = new Timer(); // This will create a new Thread
                            timer.schedule(new TimerTask() { // task to be scheduled
                                @Override
                                public void run() {
                                    handler.post(Update);
                                }
                            }, DELAY_MS, PERIOD_MS);


                        } else {
                            Toast.makeText(StartModuleDashboardScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(StartModuleDashboardScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissions()) {
//                Toast.makeText(ChildTrackWorkoutScreen.this, "Permissions already granted", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(ChildTrackWorkoutScreen.this, "Initially permission not available", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(StartModuleDashboardScreen.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int grantResults[]) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    // no permissions granted.
                    Toast.makeText(StartModuleDashboardScreen.this, "This feature cannot work without Permissions. \nRelaunch the App or allow permissions in Applications Settings", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }

    }
}