package com.lap.application.participant;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lap.application.LoginScreen;
import com.lap.application.R;
import com.lap.application.beans.ChildBean;
import com.lap.application.beans.ParentDashboardBean;
import com.lap.application.beans.UserBean;
import com.lap.application.child.ChildMainScreen;
import com.lap.application.child.fragments.ChildCreatePostFragment;
import com.lap.application.child.fragments.ChildMyChallengesFragment;
import com.lap.application.child.fragments.ChildMyFriendsFragment;
import com.lap.application.child.fragments.ChildMyGalleryFragment;
import com.lap.application.child.fragments.ChildMyMessagesFragment;
import com.lap.application.child.fragments.ChildPostFragment;
import com.lap.application.child.fragments.ChildUpdteProfileFragment;
import com.lap.application.child.fragments.ChildViewMarksFragment;
import com.lap.application.child.smartBand.ChildHomeSmartBandScreen;
import com.lap.application.coach.fragments.CoachLeagueFragment;
import com.lap.application.coach.fragments.CoachRugbyFragment;
import com.lap.application.league.LeaguePlayersFragment;
import com.lap.application.league.LeagueTeamsFragment;
import com.lap.application.parent.ParentAcademyListingScreen;
import com.lap.application.parent.ParentChildTimeline;
import com.lap.application.parent.ParentFacilityListingScreen;
import com.lap.application.parent.ParentMainScreen;
import com.lap.application.parent.ParentOnlineShoppingViewCartScreen;
import com.lap.application.parent.fragments.ParentAddCardFragment;
import com.lap.application.parent.fragments.ParentBookNowFragment;
import com.lap.application.parent.fragments.ParentBookingHistoryFragment;
import com.lap.application.parent.fragments.ParentContactCoachFragment;
import com.lap.application.parent.fragments.ParentDocumentsListingFragment;
import com.lap.application.parent.fragments.ParentHelpDocFragment;
import com.lap.application.parent.fragments.ParentLeaguesListingFragment;
import com.lap.application.parent.fragments.ParentManageChildrenFragment;
import com.lap.application.parent.fragments.ParentManageTimelineFragment;
import com.lap.application.parent.fragments.ParentNotificationListingFragment;
import com.lap.application.parent.fragments.ParentOnlineShoppingFragment;
import com.lap.application.parent.fragments.ParentOnlineShoppingHistoryFragment;
import com.lap.application.parent.fragments.ParentUpdateProfileFragment;
import com.lap.application.parent.fragments.ParentViewAttendanceFragment;
import com.lap.application.parent.fragments.ParentViewChildrenActivitiesFragment;
import com.lap.application.parent.fragments.ParentViewMarksFragment;
import com.lap.application.participant.adapter.ParticipantMainAdapter;
import com.lap.application.participant.fragment.ParticipantDashboardFragmentNew;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class ParticipantMainScreen extends AppCompatActivity implements IWebServiceCallback {
    ArrayList<ParentDashboardBean> parentDashboardBeanArrayListDuplicate = new ArrayList<>();;

    SharedPreferences sharedPreferences;
    Typeface helvetica;
    Typeface linoType;

    SlidingMenu slidingMenu;
    TextView title;

    ImageView backButton;
    ImageView searchIcon;
    ImageView menuIcon;
    ImageView profilePhoto;
    UserBean loggedInUser;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    TextView fullName;

    private final String DO_LOGOUT = "DO_LOGOUT";
    private final String GET_CHILDREN_LISTING = "GET_CHILDREN_LISTING";

    ArrayList<ChildBean> childrenListing = new ArrayList<>();
    String strChoice = "";
    RelativeLayout relativeCart;
    TextView badgeText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.parent_activity_parent_main_screen);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(ParticipantMainScreen.this));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(1000))
                .build();

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(linoType);
        backButton = (ImageView) findViewById(R.id.backButton);
        searchIcon = (ImageView) findViewById(R.id.searchIcon);
        menuIcon = (ImageView) findViewById(R.id.slideMenu);
        relativeCart = findViewById(R.id.relativeCart);
        badgeText = findViewById(R.id.badgeText);

        relativeCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent obj = new Intent(ParticipantMainScreen.this, ParentOnlineShoppingViewCartScreen.class);
                startActivity(obj);
            }
        });

        setUpRightSlidingMenuNew();

        String showFriendList = getIntent().getStringExtra("showFriendList");

        if(showFriendList != null){
            showManageChildren();
        }else{
            Intent intent = getIntent();
            if (intent != null) {

                Bundle bundle = intent.getExtras();

                if(bundle == null) {
                    showDashboardScreen();
                } else {

                    if(bundle.containsKey("screenName")) {
                        String screenName = intent.getStringExtra("screenName");

                        switch (screenName) {
                            case "bookSession":
//                            showBookSession();
                                showDashboardScreen();
                                Intent academyListing = new Intent(ParticipantMainScreen.this, ParentAcademyListingScreen.class);
                                startActivity(academyListing);
                                break;
                            case "bookPitch":
//                            showBookPitch();
                                showDashboardScreen();
                                Intent facilityListing = new Intent(ParticipantMainScreen.this, ParentFacilityListingScreen.class);
                                startActivity(facilityListing);
                                break;
                            default:
                                showDashboardScreen();
                        }
                    } else if (bundle.containsKey("notification_data")) {
                        String jsonData = intent.getStringExtra("notification_data");

                        try {
                            JSONObject jsonObject = new JSONObject(jsonData);

                            JSONObject notificationObject = jsonObject.getJSONObject("notification");

                            String title = notificationObject.getString("title");
                            String body = notificationObject.getString("body");

                            showNotificationDialog(title, body);
                            showDashboardScreen();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ParticipantMainScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // Added new else block
                        showDashboardScreen();
                    }
                }
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDashboardScreen();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
                if (f instanceof ParentViewAttendanceFragment) {
                    ((ParentViewAttendanceFragment) f).showSearchLinearLayout();
                } else if (f instanceof ParentViewMarksFragment) {
                    ((ParentViewMarksFragment) f).showSearchLinearLayout();
                } else if (f instanceof ParentViewChildrenActivitiesFragment) {
                    ((ParentViewChildrenActivitiesFragment) f).showSearchLinearLayout();
                }
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingMenu.toggle();
            }
        });
    }

    public void showNotificationDialog(String title, String message) {
        final Dialog dialog = new Dialog(ParticipantMainScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.common_notification_dialog);

        TextView notificationTitle = (TextView) dialog.findViewById(R.id.notificationTitle);
        TextView notificationBody = (TextView) dialog.findViewById(R.id.notificationBody);
        ImageView close = (ImageView) dialog.findViewById(R.id.close);

        notificationTitle.setText(title);
        notificationBody.setText(message);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //System.out.println("On Resume :: Parent Main Screen");

        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);

            imageLoader.displayImage(loggedInUser.getProfilePicPath(), profilePhoto, options);
        }
    }


    private void setUpRightSlidingMenuNew() {
        slidingMenu = new SlidingMenu(this);
        slidingMenu.setMode(SlidingMenu.RIGHT);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(1.0f);
        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        View view = getLayoutInflater().inflate(R.layout.participant_sliding_menu_new, null);
        slidingMenu.setMenu(view);

        profilePhoto = (ImageView) view.findViewById(R.id.profilePhoto);
        fullName = (TextView) view.findViewById(R.id.fullName);

        TextView logout = (TextView) view.findViewById(R.id.logout);
        final ListView listView = view.findViewById(R.id.listView);

        logout.setTypeface(helvetica);

        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);

        imageLoader.displayImage(loggedInUser.getProfilePicPath(), profilePhoto, options);
        fullName.setText(loggedInUser.getFullName());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(ParticipantMainScreen.this);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String label = parentDashboardBeanArrayListDuplicate.get(i).getLabel();

                if(label.equalsIgnoreCase("Dashboard")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showDashboardScreen();
                }else if(label.equalsIgnoreCase("My Participants")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showManageChildren();
                }else if(label.equalsIgnoreCase("Book Now")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showBookNow();
                }else if(label.equalsIgnoreCase("Participant Newsfeed")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showPlayerNewsfeed();
                }else if(label.equalsIgnoreCase("Participant Career")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showPlayerCareer();
                }else if(label.equalsIgnoreCase("Posts")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showManageTimeline();
                }else if(label.equalsIgnoreCase("Booking History")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showBookingHistory();
                }else if(label.equalsIgnoreCase("Join Leauge")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showBookLeague();
                }else if(label.equalsIgnoreCase("Parent Profile")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showUpdateProfile();
                }else if(label.equalsIgnoreCase("Contact Us")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showContactParent("CONTACT US");
                }else if(label.equalsIgnoreCase("Documents")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showDocumentListing();
                }else if(label.equalsIgnoreCase("Help")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showHelp();
                }else if(label.equalsIgnoreCase("Rugby Education")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    rugbyEducation();
                }else if(label.equalsIgnoreCase("Online Store")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    onlineShoppingStore();
                }else if(label.equalsIgnoreCase("Order History")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    orderHistoryStore();
                }else if(label.equalsIgnoreCase("Manage League")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    leagueFrag();
                }else if(label.equalsIgnoreCase("Newsfeed")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showChildPost();
                }else if(label.equalsIgnoreCase("Performance")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitleForPerformance", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showTrackWorkout();
                }else if(label.equalsIgnoreCase("Chats")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showMyMessages();
                }else if(label.equalsIgnoreCase("Friends")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showMyFriends();
                }else if(label.equalsIgnoreCase("Create Film")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showCaptureVideo();
                }else if(label.equalsIgnoreCase("Gallery")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showMyGallery();
                }else if(label.equalsIgnoreCase("Posts")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showCreatePost();
                }else if(label.equalsIgnoreCase("Challenges")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showMyChallenges();
                }else if(label.equalsIgnoreCase("Career")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showMyIFAcareer();
                }else if(label.equalsIgnoreCase("Reports")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showViewMarks();
                }else if(label.equalsIgnoreCase("About Me")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showUpdateProfile();
                }else if(label.equalsIgnoreCase("Profile")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showViewYourProfile();
                }else if(label.equalsIgnoreCase("Login As Parent")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                  //  showLoginAsParent();
                }else if(label.equalsIgnoreCase("Manage League")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    leagueFrag();
                }else if(label.equalsIgnoreCase("Report Abuse")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showContactCoach();
                }else if(label.equalsIgnoreCase("Help")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showHelp();
                }else if(label.equalsIgnoreCase("Team")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showTeams();
                }else if(label.equalsIgnoreCase("Players")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    showPlayers();
                }else if(label.equalsIgnoreCase("Notifications")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayListDuplicate.get(i).getPreferred_text());
                    editor.commit();
                    notificationHistory();
                } else {
                    Toast.makeText(ParticipantMainScreen.this, "Server Error", Toast.LENGTH_SHORT).show();
                }
                slidingMenu.toggle();
            }
        });


        fullName.setTypeface(helvetica);



        slidingMenu.setOnOpenedListener(new SlidingMenu.OnOpenedListener() {
            @Override
            public void onOpened() {

                try{
                    parentDashboardBeanArrayListDuplicate.clear();

                    ParentDashboardBean parentDashboardBean = new ParentDashboardBean();
                    parentDashboardBean.setId("-1");
                    parentDashboardBean.setNavigation_type("Dashboard");
                    parentDashboardBean.setLabel("Dashboard");
                    parentDashboardBean.setAcademy_id("-1");
                    parentDashboardBean.setStatus("-1");
                    parentDashboardBean.setPreferred_text("Dashboard");
                    parentDashboardBean.setSort("-1");
                    parentDashboardBeanArrayListDuplicate.add(parentDashboardBean);

                    listView.setAdapter(null);
                    parentDashboardBeanArrayListDuplicate.addAll(ParticipantDashboardFragmentNew.parentDashboardBeanArrayList);

                    ParentDashboardBean parentDashboardBean2 = new ParentDashboardBean();
                    parentDashboardBean2.setId("-11");
                    parentDashboardBean2.setNavigation_type("Help");
                    parentDashboardBean2.setLabel("Help");
                    parentDashboardBean2.setAcademy_id("-11");
                    parentDashboardBean2.setStatus("-11");
                    parentDashboardBean2.setPreferred_text("Help");
                    parentDashboardBean2.setSort("-11");
                    parentDashboardBeanArrayListDuplicate.add(parentDashboardBean2);

//                    parentDashboardBeanArrayListDuplicate.remove(parentDashboardBeanArrayListDuplicate.size()-1);
//                    parentDashboardBeanArrayListDuplicate.remove(parentDashboardBeanArrayListDuplicate.size()-1);
                    listView.setAdapter(new ParticipantMainAdapter(ParticipantMainScreen.this, parentDashboardBeanArrayListDuplicate));
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });
    }

    private void doLogout() {
        if (Utilities.isNetworkAvailable(ParticipantMainScreen.this)) {

//            String webServiceUrl = Utilities.BASE_URL + "children/get_child_reg_form";
            String webServiceUrl = Utilities.BASE_URL + "account/logout";
            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(ParticipantMainScreen.this, DO_LOGOUT, ParticipantMainScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParticipantMainScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUserName() {
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        fullName.setText(loggedInUser.getFullName());
    }

    public void showDashboardScreen() {
        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        title.setText(verbiage_singular.toUpperCase()+" DASHBOARD");

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.INVISIBLE);

        ParticipantDashboardFragmentNew parentDashboardFragment = new ParticipantDashboardFragmentNew();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentDashboardFragment)
                .commit();
    }

    public void showPlayerNewsfeed(){
        strChoice = "newsfeed";
        getChildrenListing();
    }

    public void showPlayerCareer(){
        strChoice = "career";
        getChildrenListing();
    }

    private void getChildrenListing(){
        if(Utilities.isNetworkAvailable(ParticipantMainScreen.this)) {

            String webServiceUrl = Utilities.BASE_URL + "children/children_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(ParticipantMainScreen.this, GET_CHILDREN_LISTING, ParticipantMainScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParticipantMainScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void showBookNow() {
//        title.setText("BOOKINGS");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentBookNowFragment parentBookNowFragment = new ParentBookNowFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentBookNowFragment)
                .commit();
    }

    public void showManageChildren() {
//        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
//        String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);
//        title.setText(verbiage_singular.toUpperCase()+" INFO");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentManageChildrenFragment parentManageChildrenFragment = new ParentManageChildrenFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentManageChildrenFragment)
                .commit();

    }

    public void showUpdateProfile() {
//        title.setText("PROFILE");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentUpdateProfileFragment parentUpdateProfileFragment = new ParentUpdateProfileFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentUpdateProfileFragment)
                .commit();
    }

    public void showViewYourProfile() {
//        title.setText("VIEW YOUR PROFILE");
//        title.setText(loggedInUser.getFullName());

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildUpdteProfileFragment childUpdateProfileFragment = new ChildUpdteProfileFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childUpdateProfileFragment)
                .commit();
    }

    public void showManageTimeline(){
//        title.setText("WRITE A POST");
        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentManageTimelineFragment parentManageTimelineFragment = new ParentManageTimelineFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentManageTimelineFragment)
                .commit();
    }

    /*public void showPaymentDetail() {
        title.setText("PAYMENT DETAILS");

        searchIcon.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentPaymentDetailFragment parentPaymentDetailFragment = new ParentPaymentDetailFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentPaymentDetailFragment)
                .commit();
    }*/

    public void showDocumentListing() {
//        title.setText("DOCUMENTS");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentDocumentsListingFragment parentDocumentsListingFragment = new ParentDocumentsListingFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentDocumentsListingFragment)
                .commit();
    }



    public void showBookLeague() {
//        title.setText("LEAGUES");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentLeaguesListingFragment parentLeaguesListingFragment = new ParentLeaguesListingFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentLeaguesListingFragment)
                .commit();
    }


    public void showBookingHistory() {
//        title.setText("HISTORY");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentBookingHistoryFragment parentBookingHistoryFragment = new ParentBookingHistoryFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentBookingHistoryFragment)
                .commit();
    }

    public void showContactParent(String strTitle) {
        title.setText(strTitle);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentContactCoachFragment parentContactCoachFragment = new ParentContactCoachFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentContactCoachFragment)
                .commit();
    }

    public void showContactCoach() {
//        title.setText(strTitle);

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentContactCoachFragment parentContactCoachFragment = new ParentContactCoachFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentContactCoachFragment)
                .commit();
    }

    public void showAddCard() {
//        title.setText("ADD CARD");
        title.setText("PAYMENT INFORMATION");

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentAddCardFragment parentAddCardFragment = new ParentAddCardFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentAddCardFragment)
                .commit();
    }

    public void showHelp() {
        title.setText("HELP");

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        ParentHelpDocFragment parentHelpDocFragment = new ParentHelpDocFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentHelpDocFragment)
                .commit();
    }

    public void showTeams() {
//        title.setText("PROFILE");
        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        LeagueTeamsFragment leagueJoinLeagueFragment = new LeagueTeamsFragment();

        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, leagueJoinLeagueFragment)
                .commit();
    }

    public void showPlayers() {
//        title.setText("PROFILE");
        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        LeaguePlayersFragment leagueJoinLeagueFragment = new LeaguePlayersFragment();

        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, leagueJoinLeagueFragment)
                .commit();
    }

    public void rugbyEducation(){
//        title.setText("RUGBY EDUCATION");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        CoachRugbyFragment coachRugbyFragment = new CoachRugbyFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, coachRugbyFragment)
                .commit();
    }

    public void onlineShoppingStore(){
//        title.setText("RUGBY EDUCATION");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.VISIBLE);
        backButton.setVisibility(View.VISIBLE);


        ParentOnlineShoppingFragment parentOnlineShoppingFragment = new ParentOnlineShoppingFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentOnlineShoppingFragment)
                .commit();
    }

    public void notificationHistory(){
//        title.setText("RUGBY EDUCATION");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);


        ParentNotificationListingFragment parentNotificationListingFragment = new ParentNotificationListingFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentNotificationListingFragment)
                .commit();
    }

    public void orderHistoryStore(){
//        title.setText("RUGBY EDUCATION");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);


        ParentOnlineShoppingHistoryFragment parentOnlineShoppingHistoryFragment = new ParentOnlineShoppingHistoryFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, parentOnlineShoppingHistoryFragment)
                .commit();
    }

    public void leagueFrag(){
        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        searchIcon.setVisibility(View.GONE);
        relativeCart.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);

        CoachLeagueFragment coachLeagueFragment = new CoachLeagueFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, coachLeagueFragment)
                .commit();
    }

    public void showChildPost() {
        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);
        ChildMainScreen.CHILDPOST = "";

        ChildPostFragment childPostFragment = new ChildPostFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childPostFragment)
                .commit();
    }

    public void showTrackWorkout() {
        String prefTextStr = sharedPreferences.getString("prefTitleForPerformance", null);
        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);
        Intent trackWorkout = new Intent(ParticipantMainScreen.this, ChildHomeSmartBandScreen.class);
        trackWorkout.putExtra("prefText", prefTextStr);
        startActivity(trackWorkout);
    }

    public void showMyMessages() {
//        title.setText("CHATS");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildMyMessagesFragment childMyMessagesFragment = new ChildMyMessagesFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childMyMessagesFragment)
                .commit();
    }

    public void showMyFriends() {
//        title.setText("FRIENDS");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildMyFriendsFragment childMyFriendsFragment = new ChildMyFriendsFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childMyFriendsFragment)
                .commit();
    }

    public void showCaptureVideo() {
//        title.setText("CAPTURE VIDEO");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        Toast.makeText(this, "This function is not compliant with the Google Play 64-bit requirement.", Toast.LENGTH_SHORT).show();

//        ChildCaptureVideoFragment childCaptureVideoFragment = new ChildCaptureVideoFragment();
//        ChildMainScreen.this.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.mainFrameLayout, childCaptureVideoFragment)
//                .commit();
    }

    public void showMyGallery() {
//        title.setText("GALLERY");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildMyGalleryFragment childMyGalleryFragment = new ChildMyGalleryFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childMyGalleryFragment)
                .commit();
    }

    public void showCreatePost() {
//        title.setText("CREATE A POST");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildCreatePostFragment childCreatePostFragment = new ChildCreatePostFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childCreatePostFragment)
                .commit();
    }

    public void showMyChallenges() {
//        title.setText("CHALLENGES");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);

        ChildMyChallengesFragment childMyChallengesFragment = new ChildMyChallengesFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childMyChallengesFragment)
                .commit();
    }

    public void showMyIFAcareer() {
//        title.setText(loggedInUser.getFullName());

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.GONE);
        ChildMainScreen.CHILDPOST = "myIfaCareer";

        ChildPostFragment childPostFragment = new ChildPostFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childPostFragment)
                .commit();
    }

    public void showViewMarks() {
//        title.setText("STATS");
//        title.setText("REPORTS");

        String prefTextStr = sharedPreferences.getString("prefTitle", null);
        title.setText(prefTextStr);

        backButton.setVisibility(View.VISIBLE);
        relativeCart.setVisibility(View.GONE);
        searchIcon.setVisibility(View.VISIBLE);

        ChildViewMarksFragment childViewMarksFragment = new ChildViewMarksFragment();
        ParticipantMainScreen.this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFrameLayout, childViewMarksFragment)
                .commit();
    }

    public void loadCountShoppingStore(String count){
        badgeText.setText(count);
    }

    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout);
        if (f instanceof ParticipantDashboardFragmentNew) {
            final Dialog dialog = new Dialog(ParticipantMainScreen.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.parent_dialog_exit_app);

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
                    finish();
                }
            });

            dialog.show();

        } else {
            showDashboardScreen();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case DO_LOGOUT:

                System.out.println("RES::"+response);

                if (response == null) {
                    Toast.makeText(ParticipantMainScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        Toast.makeText(ParticipantMainScreen.this, message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.commit();

                            // Logout Facebook
//                            LoginManager.getInstance().logOut();

                            Intent loginScreen = new Intent(ParticipantMainScreen.this, LoginScreen.class);
                            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginScreen);
                        } else {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.commit();

                            // Logout Facebook
//                            LoginManager.getInstance().logOut();

                            Intent loginScreen = new Intent(ParticipantMainScreen.this, LoginScreen.class);
                            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginScreen);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParticipantMainScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case GET_CHILDREN_LISTING:

                childrenListing.clear();

                if (response == null) {
                    Toast.makeText(ParticipantMainScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            JSONArray dataArray = responseObject.getJSONArray("data");

                            ChildBean childBean;
                            for (int i=0; i<dataArray.length(); i++) {
                                JSONObject childObject = dataArray.getJSONObject(i);
                                childBean = new ChildBean();

                                childBean.setId(childObject.getString("id"));
                                childBean.setAcademiesId(childObject.getString("academies_id"));
                                childBean.setUsername(childObject.getString("username"));
                                childBean.setEmail(childObject.getString("email"));
                                childBean.setGender(childObject.getString("gender"));
                                childBean.setCreatedAt(childObject.getString("created_at"));
                                childBean.setState(childObject.getString("state"));
                                childBean.setFirstName(childObject.getString("first_name"));
                                childBean.setLastName(childObject.getString("last_name"));
                                childBean.setFullName(childObject.getString("full_name"));
                                childBean.setAge(childObject.getString("age"));
                                childBean.setDateOfBirth(childObject.getString("dob"));
                                childBean.setMedicalCondition(childObject.getString("medical_conditions"));
                                childBean.setIsPrivate(childObject.getString("is_private"));
                                childBean.setSchool(childObject.getString("school"));
                                childBean.setFavPlayer(childObject.getString("favourite_player"));
                                childBean.setFavTeam(childObject.getString("favourite_team"));
                                childBean.setFavPosition(childObject.getString("favourite_position"));
                                childBean.setFavFootballBoot(childObject.getString("favourite_football_boot"));
                                childBean.setFavFood(childObject.getString("favourite_food"));
                                childBean.setNationality(childObject.getString("nationality"));
                                childBean.setHeight(childObject.getString("height"));
                                childBean.setWeight(childObject.getString("weight"));

                                childrenListing.add(childBean);
                            }

                            if(childrenListing.isEmpty()) {
                                Toast.makeText(ParticipantMainScreen.this, "No data", Toast.LENGTH_SHORT).show();
                            } else if(childrenListing.size() == 1) {
                                switch (strChoice) {
                                    case "newsfeed":
                                        Intent timeline = new Intent(ParticipantMainScreen.this, ParentChildTimeline.class);
                                        timeline.putExtra("currentChild", childrenListing.get(0));
                                        timeline.putExtra("type", "timeline");
                                        startActivity(timeline);

                                        break;

                                    case "career":
                                        timeline = new Intent(ParticipantMainScreen.this, ParentChildTimeline.class);
                                        timeline.putExtra("currentChild", childrenListing.get(0));
                                        timeline.putExtra("type", "career");
                                        startActivity(timeline);

                                        break;
                                }
                            } else {
                                showManageChildren();
                            }

                        } else {
                            Toast.makeText(ParticipantMainScreen.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParticipantMainScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}