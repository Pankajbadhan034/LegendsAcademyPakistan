package com.lap.application.league;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.login.LoginManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.lap.application.LoginScreen;
import com.lap.application.R;
import com.lap.application.beans.ParentDashboardBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.CoachMainScreen;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceAsync;
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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class LeagueDashboardFragmentNew extends Fragment implements IWebServiceCallback {
    GridView gridView;
    ParentDashboardBean parentDashboardBean;
    public static ArrayList<ParentDashboardBean> parentDashboardBeanArrayList = new ArrayList<>();;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ImageView profilePhoto;
    ImageView profilePhotoSmall;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    private final String DO_LOGOUT = "DO_LOGOUT";
    private final String CHECK_FOR_UPDATES = "CHECK_FOR_UPDATES";
    private final String CUSTOM_DASHBOARD = "CUSTOM_DASHBOARD";
    private final String HIDE_SHOW = "HIDE_SHOW";
    String showTiles="";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
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

        helvetica = Typeface.createFromAsset(getActivity().getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LinotypeOrdinarRegular.ttf");
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.league_fragment_dashboard_new, container, false);

        profilePhoto = (ImageView) view.findViewById(R.id.profilePhoto);
        profilePhotoSmall = (ImageView) view.findViewById(R.id.profilePhotoSmall);
        gridView = view.findViewById(R.id.gridView);

        Button reportAbuse = (Button) view.findViewById(R.id.reportAbuse);
        Button logoutButton = (Button) view.findViewById(R.id.logoutButton);

        reportAbuse.setTypeface(linoType);
        logoutButton.setTypeface(linoType);

        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        imageLoader.displayImage(loggedInUser.getProfilePicPath(), profilePhoto, options);
        imageLoader.displayImage(loggedInUser.getProfilePicPath(), profilePhotoSmall, options);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(loggedInUser.getFullName());
                    toolbar.setBackgroundResource(R.drawable.imgbg);
                    profilePhotoSmall.setVisibility(View.VISIBLE);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    toolbar.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                    profilePhotoSmall.setVisibility(View.GONE);
                    isShow = false;
                }
            }
        });

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  ((CoachMainScreen) getActivity()).showUpdateProfile();
            }
        });

        reportAbuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LeagueMainScreen) getActivity()).showContactCoach("COMPLAINTS");
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
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

        checkForUpdates();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String label = parentDashboardBeanArrayList.get(position).getLabel();

                // System.out.println("sizeHERE::"+parentDashboardBeanArrayList.get(11).getLabel());

                if(label.equalsIgnoreCase("Join League")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayList.get(position).getPreferred_text());
                    editor.commit();
                    ((LeagueMainScreen) getActivity()).showJoinLeague();
                }else if(label.equalsIgnoreCase("Team")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayList.get(position).getPreferred_text());
                    editor.commit();
                    ((LeagueMainScreen) getActivity()).showTeams();
                }else if(label.equalsIgnoreCase("Players")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("prefTitle", parentDashboardBeanArrayList.get(position).getPreferred_text());
                    editor.commit();
                    ((LeagueMainScreen) getActivity()).showPlayers();
                }else {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return  view;
    }

    private void checkForUpdates() {
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("build_version", Utilities.SYSTEM_VERSION));

        String webServiceUrl = Utilities.BASE_URL + "account/get_app_version";

        PostWebServiceAsync postWebServiceAsync = new PostWebServiceAsync(getActivity(), nameValuePairList, CHECK_FOR_UPDATES, LeagueDashboardFragmentNew.this);
        postWebServiceAsync.execute(webServiceUrl);
    }

    private void showUpdateDialog(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String appPackageName = getActivity().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getActivity().finish();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void customNavigsation() {
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("nav_type", "mobileLeague"));

        String webServiceUrl = Utilities.BASE_URL + "account/navigations_items";
        ArrayList<String> headers = new ArrayList<>();
        headers.add("X-access-uid:" + loggedInUser.getId());
        headers.add("X-access-token:" + loggedInUser.getToken());

        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, CUSTOM_DASHBOARD, LeagueDashboardFragmentNew.this, headers);
        postWebServiceAsync.execute(webServiceUrl);
    }

    private void hideShow() {
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
        nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));

        ArrayList<String> headers = new ArrayList<>();
        headers.add("X-access-uid:"+loggedInUser.getId());
        headers.add("X-access-token:"+loggedInUser.getToken());

        String webServiceUrl = Utilities.BASE_URL + "join_league/check_parent_league_registration";

        PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, HIDE_SHOW, LeagueDashboardFragmentNew.this, headers);
        postWebServiceAsync.execute(webServiceUrl);
    }

    private void doLogout(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            String webServiceUrl = Utilities.BASE_URL + "account/logout";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(getActivity(), DO_LOGOUT, LeagueDashboardFragmentNew.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag){
            case HIDE_SHOW:

                if (response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {
                            showTiles = "yes";

                        }else{
                            showTiles = "no";
                        }
                        customNavigsation();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case DO_LOGOUT:

                if (response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

                        if (status) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.commit();

                            // Logout Facebook
//                            LoginManager.getInstance().logOut();

                            Intent loginScreen = new Intent(getActivity(), LoginScreen.class);
                            loginScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginScreen);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case CHECK_FOR_UPDATES:

                if(response == null){
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        hideShow();
                        if(status){
                            String currentSystemUpdateLevel = responseObject.getString("CURRENT_SYS_UPDATE_LEVEL");

                            if(currentSystemUpdateLevel.equalsIgnoreCase("1")){
                                // Show dialog
                                showUpdateDialog(message);
                            } else {
                                // Do nothing
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case CUSTOM_DASHBOARD:

                System.out.println("response::"+response);
                parentDashboardBeanArrayList.clear();
                if(response == null){
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status){
                            JSONObject dataobj1 = responseObject.getJSONObject("data");
                            JSONObject dataobj2 = dataobj1.getJSONObject("data");
                            JSONArray jsonArray = dataobj2.getJSONArray("main");

                            for(int i=0; i<jsonArray.length(); i++){
                                parentDashboardBean = new ParentDashboardBean();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                parentDashboardBean.setId(jsonObject.getString("id"));
                                parentDashboardBean.setNavigation_type(jsonObject.getString("navigation_type"));
                                parentDashboardBean.setLabel(jsonObject.getString("label"));
                                parentDashboardBean.setAcademy_id(jsonObject.getString("academy_id"));
                                parentDashboardBean.setStatus(jsonObject.getString("status"));
                                parentDashboardBean.setPreferred_text(jsonObject.getString("preferred_text"));
                                parentDashboardBean.setSort(jsonObject.getString("sort"));

                                if(jsonObject.getString("status").equalsIgnoreCase("1")){


                                    if(i>0){
                                        if(showTiles.equalsIgnoreCase("yes")){
                                            parentDashboardBeanArrayList.add(parentDashboardBean);
                                        }
                                    }else{
                                        parentDashboardBeanArrayList.add(parentDashboardBean);
                                    }


                                }

                                if(parentDashboardBeanArrayList.size()==0){

                                }else{
                                    gridView.setAdapter(new LeagueDashboardAdapter(getActivity(), parentDashboardBeanArrayList));
                                    Utilities.setGridViewHeightBasedOnChildren(gridView, 2);
                                }
                            }



                            gridView.setAdapter(new LeagueDashboardAdapter(getActivity(), parentDashboardBeanArrayList));
                            Utilities.setGridViewHeightBasedOnChildren(gridView, 2);

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}