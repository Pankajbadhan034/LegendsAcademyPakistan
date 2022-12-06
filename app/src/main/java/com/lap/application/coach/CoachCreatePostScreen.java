package com.lap.application.coach;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.share.Sharer;
//import com.facebook.share.model.ShareLinkContent;
//import com.facebook.share.widget.ShareDialog;
import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.AgeGroupBean;
import com.lap.application.beans.CampDaysBean;
import com.lap.application.beans.CampLocationBean;
import com.lap.application.beans.ChildBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.adapters.CoachChildNamesSpinnerAdapter;
import com.lap.application.coach.adapters.CoachLocationListingAdapter;
import com.lap.application.coach.adapters.CoachOnlyAgeGroupListingAdapter;
import com.lap.application.coach.adapters.CoachSessionListingAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
//import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
//import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

public class CoachCreatePostScreen extends AppCompatActivity implements IWebServiceCallback {

    ProgressDialog pd;
    //FFmpeg ffmpeg;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    ImageView backButton;
    Spinner locationSpinner;
    Spinner sessionSpinner;
    Spinner ageGroupSpinner;
    Spinner childrenSpinner;
    EditText postText;
    EditText youtubeUrl;
    TextView browse;
    CheckBox sendMessageToParent;
    Button submit;
    ImageView thumbnail1;
    ImageView thumbnail2;
    ImageView thumbnail3;
    ImageView thumbnail4;
    ImageView thumbnail5;

    private final String GET_LOCATION_LISTING = "GET_LOCATION_LISTING";
    private final String GET_SESSIONS_LISTING = "GET_SESSIONS_LISTING";
    private final String GET_AGE_GROUP_LISTING = "GET_AGE_GROUP_LISTING";
    private final String GET_ALL_CHILDREN_LISTING = "GET_ALL_CHILDREN_LISTING";

    ArrayList<CampLocationBean> locationsList = new ArrayList<>();
    ArrayList<CampDaysBean> daysListing = new ArrayList<>();
    ArrayList<AgeGroupBean> ageGroupsListing = new ArrayList<>();
    ArrayList<ChildBean> childrenListing = new ArrayList<>();

    CoachLocationListingAdapter coachLocationListingAdapter;
    CoachSessionListingAdapter coachSessionListingAdapter;
    CoachOnlyAgeGroupListingAdapter coachOnlyAgeGroupListingAdapter;
    CoachChildNamesSpinnerAdapter coachChildNamesSpinnerAdapter;

    String clickedOnLocationIds;
    String clickedOnDayIds;
    String clickedOnSessionIds;
    String clickedOnChildIds;
    String strPostText;
    String strSendMessageToParent;
    String strYoutubeUrl;

    private final int CHOOSE_IMAGE_FROM_GALLERY = 1;
    private final int CHOOSE_IMAGE_FROM_GALLERY2 = 12;
    private final int CHOOSE_IMAGE_FROM_GALLERY3 = 13;
    private final int CHOOSE_IMAGE_FROM_GALLERY4 = 14;
    private final int CHOOSE_IMAGE_FROM_GALLERY5 = 15;
    private static final int CHOOSE_VIDEO_FROM_GALLERY = 1234;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static final int MEDIA_TYPE_IMAGE = 2;
    public static final int MEDIA_TYPE_VIDEO = 3;
    private Uri fileUri = null;
    Uri selectedUri;
    String selectedImagePath;

    Uri selectedUri2;
    String selectedImagePath2;
    Uri selectedUri3;
    String selectedImagePath3;
    Uri selectedUri4;
    String selectedImagePath4;
    Uri selectedUri5;
    String selectedImagePath5;

    // Run time permission
    public static final int MULTIPLE_PERMISSIONS = 10;
    String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

//    ShareDialog shareDialog;
//    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coach_activity_create_post_screen);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        backButton = (ImageView) findViewById(R.id.backButton);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);
        sessionSpinner = (Spinner) findViewById(R.id.sessionSpinner);
        ageGroupSpinner = (Spinner) findViewById(R.id.ageGroupSpinner);
        childrenSpinner = (Spinner) findViewById(R.id.childrenSpinner);
        locationSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        sessionSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        ageGroupSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        childrenSpinner.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

        postText = (EditText) findViewById(R.id.postText);
        youtubeUrl = findViewById(R.id.youtubeUrl);
        sendMessageToParent = (CheckBox) findViewById(R.id.sendMessageToParent);
        browse = (TextView) findViewById(R.id.browse);
        submit = (Button) findViewById(R.id.submit);
        thumbnail1 = findViewById(R.id.thumbnail1);
        thumbnail2 = findViewById(R.id.thumbnail2);
        thumbnail3 = findViewById(R.id.thumbnail3);
        thumbnail4 = findViewById(R.id.thumbnail4);
        thumbnail5 = findViewById(R.id.thumbnail5);

        thumbnail1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoVIdeoDialog();
            }
        });

        thumbnail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY2);
            }
        });

        thumbnail3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY3);
            }
        });

        thumbnail4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY4);
            }
        });

        thumbnail5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY5);
            }
        });

//        shareDialog = new ShareDialog(CoachCreatePostScreen.this);
//        callbackManager = CallbackManager.Factory.create();
//        shareDialog.registerCallback(callbackManager, new
//
//                FacebookCallback<Sharer.Result>() {
//                    @Override
//                    public void onSuccess(Sharer.Result result) {}
//
//                    @Override
//                    public void onCancel() {}
//
//                    @Override
//                    public void onError(FacebookException error) {}
//                });

        getLocationListing();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Utilities.isNetworkAvailable(CoachCreatePostScreen.this)) {
                    Toast.makeText(CoachCreatePostScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }

                int locationPosition = locationSpinner.getSelectedItemPosition();
                int sessionPosition = sessionSpinner.getSelectedItemPosition();
                int ageGroupPosition = ageGroupSpinner.getSelectedItemPosition();
                int childrenPosition = childrenSpinner.getSelectedItemPosition();
                strPostText = postText.getText().toString().trim();
                if(sendMessageToParent.isChecked()){
                    strSendMessageToParent = "1";
                } else {
                    strSendMessageToParent = "0";
                }
                strYoutubeUrl = youtubeUrl.getText().toString().trim();

                if(locationPosition == 0) {
                    Toast.makeText(CoachCreatePostScreen.this, "Please select Location", Toast.LENGTH_SHORT).show();
                } else if (sessionPosition == 0) {
                    Toast.makeText(CoachCreatePostScreen.this, "Please select Session", Toast.LENGTH_SHORT).show();
                } else if (ageGroupPosition == 0) {
                    Toast.makeText(CoachCreatePostScreen.this, "Please select Age Group", Toast.LENGTH_SHORT).show();
                } else if (childrenPosition == 0) {
                    String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
                    String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);
                    Toast.makeText(CoachCreatePostScreen.this, "Please select "+verbiage_singular, Toast.LENGTH_SHORT).show();

                } else if (strPostText == null || strPostText.isEmpty()) {
                    Toast.makeText(CoachCreatePostScreen.this, "Please write Post", Toast.LENGTH_SHORT).show();
                } else {
                    new UpdateImageVideoAsync(CoachCreatePostScreen.this).execute();
                }
            }
        });

        youtubeUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!youtubeUrl.getText().toString().trim().isEmpty()){
                    thumbnail1.setVisibility(View.GONE);
                    thumbnail2.setVisibility(View.GONE);
                    thumbnail3.setVisibility(View.GONE);
                    thumbnail4.setVisibility(View.GONE);
                    thumbnail5.setVisibility(View.GONE);
                    selectedUri = null;
                    fileUri = null;
                    browse.setText("Upload Photo/Video");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        browse.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                final String[] items = new String[]{"Take from camera", "Select from gallery"};
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachCreatePostScreen.this, android.R.layout.select_dialog_item, items);
//                AlertDialog.Builder builder = new AlertDialog.Builder(CoachCreatePostScreen.this);
//
//                builder.setTitle("Choose");
//                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int item) {
//
//                        if (item == 0) {
//
//                            final String[] items = new String[]{"Image", "Video"};
//                            ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachCreatePostScreen.this, android.R.layout.select_dialog_item, items);
//                            AlertDialog.Builder builder = new AlertDialog.Builder(CoachCreatePostScreen.this);
//
//                            builder.setTitle("Choose");
//                            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int item) {
//
//                                    if (item == 0) {
//                                        // Code to get image
//                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                                        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
//
//                                    } else {
//                                        // Code to get video
//                                        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                                        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
//                                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                                        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
//                                    }
//                                }
//                            });
//                            AlertDialog innerDialog = builder.create();
//                            innerDialog.show();
//
//
//                        } else {
//                            /*Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
//                            mediaChooser.setType("video*//*, images*//*");
//                            startActivityForResult(mediaChooser, CHOOSE_IMAGE_FROM_GALLERY);*/
//
//                            android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(CoachCreatePostScreen.this);
//                            builder1.setMessage("Choose your option:");
//                            builder1.setCancelable(true);
//
//                            builder1.setPositiveButton(
//                                    "Image",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                            startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY);
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            builder1.setNegativeButton(
//                                    "Video",
//                                    new DialogInterface.OnClickListener() {
//                                        public void onClick(DialogInterface dialog, int id) {
//                                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//                                            startActivityForResult(i, CHOOSE_VIDEO_FROM_GALLERY);
//                                            dialog.cancel();
//                                        }
//                                    });
//
//                            android.app.AlertDialog alert11 = builder1.create();
//                            alert11.show();
//                        }
//                    }
//                });
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position == 0) {
                    daysListing.clear();

                    // 0th Element Choose Session
                    CampDaysBean daysBean = new CampDaysBean();
                    daysBean.setDay("-1");
                    daysBean.setDayLabel("Choose Session");

                    daysListing.add(daysBean);

                    coachSessionListingAdapter = new CoachSessionListingAdapter(CoachCreatePostScreen.this, daysListing);
                    sessionSpinner.setAdapter(coachSessionListingAdapter);
                } /*else if (position == 1) {

                    clickedOnLocationIds = "";
                    for(CampLocationBean locationBean : locationsList) {

                        if(Integer.parseInt(locationBean.getLocationId()) > 0) {
                            clickedOnLocationIds += locationBean.getLocationId()+",";
                        }

                    }

                    if (clickedOnLocationIds != null && clickedOnLocationIds.length() > 0 && clickedOnLocationIds.charAt(clickedOnLocationIds.length()-1)==',') {
                        clickedOnLocationIds = clickedOnLocationIds.substring(0, clickedOnLocationIds.length()-1);
                    }

                    getSessionDaysListing();

                }*/ else {
//                    clickedOnLocation = locationsList.get(position);

                    clickedOnLocationIds = locationsList.get(position).getLocationId();

                    getSessionDaysListing();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {
                    ageGroupsListing.clear();

                    // 0th Element Choose Session
                    AgeGroupBean ageGroupBean = new AgeGroupBean();
                    ageGroupBean.setAgeGroupId("-1");
                    ageGroupBean.setGroupName("Choose Age Group");

                    ageGroupsListing.add(ageGroupBean);

                    coachOnlyAgeGroupListingAdapter = new CoachOnlyAgeGroupListingAdapter(CoachCreatePostScreen.this, ageGroupsListing);
                    ageGroupSpinner.setAdapter(coachOnlyAgeGroupListingAdapter);
                } /*else if (position == 1) {

                    clickedOnDayIds = "";
                    for(CampDaysBean campDaysBean : daysListing) {
                        if(Integer.parseInt(campDaysBean.getDay()) >= 0) {
                            clickedOnDayIds += campDaysBean.getDay()+",";
                        }
                    }

                    if (clickedOnDayIds != null && clickedOnDayIds.length() > 0 && clickedOnDayIds.charAt(clickedOnDayIds.length()-1)==',') {
                        clickedOnDayIds = clickedOnDayIds.substring(0, clickedOnDayIds.length()-1);
                    }
                    getAgeGroupsListing();

                }*/ else {
//                    clickedOnDay = daysListing.get(position);
                    clickedOnDayIds = daysListing.get(position).getDay();
                    getAgeGroupsListing();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ageGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    childrenListing.clear();

                    ChildBean childBean = new ChildBean();
                    childBean.setId("-1");

                    String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
                    String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);

                    childBean.setFullName("Choose "+verbiage_singular);

                    childrenListing.add(childBean);

                    coachChildNamesSpinnerAdapter = new CoachChildNamesSpinnerAdapter(CoachCreatePostScreen.this, childrenListing);
                    childrenSpinner.setAdapter(coachChildNamesSpinnerAdapter);

                } /*else if (position == 1) {

                    clickedOnSessionIds = "";
                    for(AgeGroupBean ageGroupBean : ageGroupsListing) {

                        if(Integer.parseInt(ageGroupBean.getSessionId()) > 0) {
                            clickedOnSessionIds += ageGroupBean.getSessionId()+",";
                        }

                    }

                    if (clickedOnSessionIds != null && clickedOnSessionIds.length() > 0 && clickedOnSessionIds.charAt(clickedOnSessionIds.length()-1)==',') {
                        clickedOnSessionIds = clickedOnSessionIds.substring(0, clickedOnSessionIds.length()-1);
                    }
                    getAllChildrenListing();

                }*/ else {
//                    clickedOnAgeGroup = ageGroupsListing.get(position);

                    clickedOnSessionIds = ageGroupsListing.get(position).getSessionId();

                    getAllChildrenListing();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        childrenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) {

                    /*categoriesListing.clear();

                    ChallengeCategoryBean categoryBean = new ChallengeCategoryBean();
                    categoryBean.setCategoryId("-1");
                    categoryBean.setName("Choose Category");
                    categoriesListing.add(categoryBean);

                    coachChallengeCategorySpinnerAdapter = new CoachChallengeCategorySpinnerAdapter(CoachCreatePostScreen.this, categoriesListing);
                    categorySpinner.setAdapter(coachChallengeCategorySpinnerAdapter);*/

                } else if (position == 1) {

                    clickedOnChildIds = "";

                    /*for(ChildBean childBean : childrenListing) {
                        if(Integer.parseInt(childBean.getId()) > 0) {
                            clickedOnChildIds += childBean.getId()+",";
                        }
                    }

                    if (clickedOnChildIds != null && clickedOnChildIds.length() > 0 && clickedOnChildIds.charAt(clickedOnChildIds.length()-1)==',') {
                        clickedOnChildIds = clickedOnChildIds.substring(0, clickedOnChildIds.length()-1);
                    }*/

                } else {
                    clickedOnChildIds = childrenListing.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void photoVIdeoDialog(){
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachCreatePostScreen.this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(CoachCreatePostScreen.this);

        builder.setTitle("Choose");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {

                    final String[] items = new String[]{"Image", "Video"};
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(CoachCreatePostScreen.this, android.R.layout.select_dialog_item, items);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CoachCreatePostScreen.this);

                    builder.setTitle("Choose");
                    builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            if (item == 0) {
                                browse.setText("Upload Photo/Video");

                                // Code to get image
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

                            } else {
                                thumbnail2.setImageResource(R.drawable.coach_gallery);
                                thumbnail3.setImageResource(R.drawable.coach_gallery);
                                thumbnail4.setImageResource(R.drawable.coach_gallery);
                                thumbnail5.setImageResource(R.drawable.coach_gallery);
                                thumbnail2.setVisibility(View.GONE);
                                thumbnail3.setVisibility(View.GONE);
                                thumbnail4.setVisibility(View.GONE);
                                thumbnail5.setVisibility(View.GONE);
                                selectedUri2 = null;
                                selectedUri3 = null;
                                selectedUri4 = null;
                                selectedUri5 = null;
                                // Code to get video
                                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                                fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
                                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
                            }
                        }
                    });
                    AlertDialog innerDialog = builder.create();
                    innerDialog.show();


                } else {
                            /*Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
                            mediaChooser.setType("video*//*, images*//*");
                            startActivityForResult(mediaChooser, CHOOSE_IMAGE_FROM_GALLERY);*/

                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(CoachCreatePostScreen.this);
                    builder1.setMessage("Choose your option:");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Image",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    browse.setText("Upload Photo/Video");

                                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY);
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "Video",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    thumbnail2.setImageResource(R.drawable.coach_gallery);
                                    thumbnail3.setImageResource(R.drawable.coach_gallery);
                                    thumbnail4.setImageResource(R.drawable.coach_gallery);
                                    thumbnail5.setImageResource(R.drawable.coach_gallery);
                                    thumbnail2.setVisibility(View.GONE);
                                    thumbnail3.setVisibility(View.GONE);
                                    thumbnail4.setVisibility(View.GONE);
                                    thumbnail5.setVisibility(View.GONE);
                                    selectedUri2 = null;
                                    selectedUri3 = null;
                                    selectedUri4 = null;
                                    selectedUri5 = null;

                                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                    startActivityForResult(i, CHOOSE_VIDEO_FROM_GALLERY);
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermissions()){
//                Toast.makeText(ChildTrackWorkoutScreen.this, "Permissions already granted", Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(ChildTrackWorkoutScreen.this, "Initially permission not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(CoachCreatePostScreen.this, p);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permissions granted.
                } else {
                    // no permissions granted.
                    Toast.makeText(CoachCreatePostScreen.this, "This feature cannot work without Permissions. \nRelaunch the App or allow permissions in Applications Settings", Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }

    private void getLocationListing(){
        if(Utilities.isNetworkAvailable(CoachCreatePostScreen.this)) {

            String webServiceUrl = Utilities.BASE_URL + "coach/locations_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(CoachCreatePostScreen.this, GET_LOCATION_LISTING, CoachCreatePostScreen.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachCreatePostScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getSessionDaysListing(){
        if(Utilities.isNetworkAvailable(CoachCreatePostScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("locations_ids", clickedOnLocationIds));
            nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnLocationIds));

//            String webServiceUrl = Utilities.BASE_URL + "coach/all_session_days_list";
            String webServiceUrl = Utilities.BASE_URL + "coach/session_days_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachCreatePostScreen.this, nameValuePairList, GET_SESSIONS_LISTING, CoachCreatePostScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachCreatePostScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getAgeGroupsListing(){
        if(Utilities.isNetworkAvailable(CoachCreatePostScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            /*nameValuePairList.add(new BasicNameValuePair("locations_ids", clickedOnLocationIds));
            nameValuePairList.add(new BasicNameValuePair("session_days", clickedOnDayIds));

            String webServiceUrl = Utilities.BASE_URL + "coach/all_age_group_list";*/

            nameValuePairList.add(new BasicNameValuePair("locations_ids", clickedOnLocationIds));
            nameValuePairList.add(new BasicNameValuePair("locations_id", clickedOnLocationIds));
            nameValuePairList.add(new BasicNameValuePair("session_days", clickedOnDayIds));

            String webServiceUrl = Utilities.BASE_URL + "coach/age_group_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachCreatePostScreen.this, nameValuePairList, GET_AGE_GROUP_LISTING, CoachCreatePostScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachCreatePostScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void getAllChildrenListing(){
        if(Utilities.isNetworkAvailable(CoachCreatePostScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("sessions_ids", clickedOnSessionIds));

            String webServiceUrl = Utilities.BASE_URL + "coach/all_children_booked_session_list";
//            String webServiceUrl = Utilities.BASE_URL + "coach/children_booked_session_list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(CoachCreatePostScreen.this, nameValuePairList, GET_ALL_CHILDREN_LISTING, CoachCreatePostScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(CoachCreatePostScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_LOCATION_LISTING:

                locationsList.clear();

                if (response == null) {
                    Toast.makeText(CoachCreatePostScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        // 0th Element Choose Location
                        CampLocationBean locationBean = new CampLocationBean();
                        locationBean.setLocationId("-1");
                        locationBean.setLocationName("Choose Location");

                        locationsList.add(locationBean);

                        if (status) {

                            /*locationBean = new CampLocationBean();
                            locationBean.setLocationId("-2");
                            locationBean.setLocationName("All");

                            locationsList.add(locationBean);*/

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for (int i = 0; i < dataArray.length(); i++) {
                                JSONObject locationObject = dataArray.getJSONObject(i);
                                locationBean = new CampLocationBean();

                                locationBean.setLocationId(locationObject.getString("locations_id"));
                                locationBean.setLocationName(locationObject.getString("locations_name"));
                                locationBean.setDescription(locationObject.getString("description"));
                                locationBean.setLatitude(locationObject.getString("latitude"));
                                locationBean.setLongitude(locationObject.getString("longitude"));

                                locationsList.add(locationBean);
                            }

                        } else {
                            Toast.makeText(CoachCreatePostScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachLocationListingAdapter = new CoachLocationListingAdapter(CoachCreatePostScreen.this, locationsList);
                locationSpinner.setAdapter(coachLocationListingAdapter);
                break;

            case GET_SESSIONS_LISTING:

                daysListing.clear();

                if(response == null) {
                    Toast.makeText(CoachCreatePostScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        // 0th Element Choose Session
                        CampDaysBean daysBean = new CampDaysBean();
                        daysBean.setDay("-1");
                        daysBean.setDayLabel("Choose Session");

                        daysListing.add(daysBean);

                        if(status) {

                            /*daysBean = new CampDaysBean();
                            daysBean.setDay("-2");
                            daysBean.setDayLabel("All");

                            daysListing.add(daysBean);*/

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0; i<dataArray.length(); i++) {
                                JSONObject daysObject = dataArray.getJSONObject(i);
                                daysBean = new CampDaysBean();
                                daysBean.setDay(daysObject.getString("day"));
                                daysBean.setDayLabel(daysObject.getString("day_label"));

                                daysListing.add(daysBean);
                            }

                        } else {
                            Toast.makeText(CoachCreatePostScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachSessionListingAdapter = new CoachSessionListingAdapter(CoachCreatePostScreen.this, daysListing);
                sessionSpinner.setAdapter(coachSessionListingAdapter);

                break;

            case GET_AGE_GROUP_LISTING:

                ageGroupsListing.clear();

                if (response == null) {
                    Toast.makeText(CoachCreatePostScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        // 0th Element Choose Age Group
                        AgeGroupBean ageGroupBean = new AgeGroupBean();
                        ageGroupBean.setAgeGroupId("-1");
                        ageGroupBean.setSessionId("-1");
                        ageGroupBean.setGroupName("Choose Age Group");

                        ageGroupsListing.add(ageGroupBean);

                        if(status) {

                            /*ageGroupBean = new AgeGroupBean();
                            ageGroupBean.setAgeGroupId("-2");
                            ageGroupBean.setSessionId("-2");
                            ageGroupBean.setGroupName("All");

                            ageGroupsListing.add(ageGroupBean);*/

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            for(int i=0; i<dataArray.length(); i++) {
                                JSONObject ageGroupObject = dataArray.getJSONObject(i);
                                ageGroupBean = new AgeGroupBean();
                                ageGroupBean.setSessionId(ageGroupObject.getString("sessions_id"));
                                ageGroupBean.setAgeGroupId(ageGroupObject.getString("age_groups_id"));
                                ageGroupBean.setGroupName(ageGroupObject.getString("group_name"));

                                ageGroupsListing.add(ageGroupBean);
                            }


                        } else {
                            Toast.makeText(CoachCreatePostScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachOnlyAgeGroupListingAdapter = new CoachOnlyAgeGroupListingAdapter(CoachCreatePostScreen.this, ageGroupsListing);
                ageGroupSpinner.setAdapter(coachOnlyAgeGroupListingAdapter);

                break;

            case GET_ALL_CHILDREN_LISTING:

                childrenListing.clear();

                if(response == null) {
                    Toast.makeText(CoachCreatePostScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {

                            ChildBean childBean = new ChildBean();
                            childBean.setId("-1");
                         //   childBean.setFullName("Choose Child");
                            String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
                            String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);

                            childBean.setFullName("Choose "+verbiage_singular);

                            childrenListing.add(childBean);

                            childBean = new ChildBean();
                            childBean.setId("-2");
                            childBean.setFullName("All");
                            childrenListing.add(childBean);

                            JSONArray dataArray = responseObject.getJSONArray("data");

                            for(int i=0; i<dataArray.length(); i++) {
                                JSONObject childObject = dataArray.getJSONObject(i);
                                childBean = new ChildBean();

                                childBean.setId(childObject.getString("id"));
                                childBean.setFullName(childObject.getString("full_name"));

                                childrenListing.add(childBean);
                            }

                        } else {
                            Toast.makeText(CoachCreatePostScreen.this, message, Toast.LENGTH_SHORT).show();

                            ChildBean childBean = new ChildBean();
                            childBean.setId("-1");
                            //childBean.setFullName("Choose Child");
                            String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
                            String verbiage_plural = sharedPreferences.getString("verbiage_plural", null);

                            childBean.setFullName("Choose "+verbiage_singular);
                            childrenListing.add(childBean);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachChildNamesSpinnerAdapter = new CoachChildNamesSpinnerAdapter(CoachCreatePostScreen.this, childrenListing);
                childrenSpinner.setAdapter(coachChildNamesSpinnerAdapter);

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case CHOOSE_IMAGE_FROM_GALLERY:
                try {
                    selectedUri = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath);
                    String nameFile = file.getName();
                 //   browse.setText(nameFile);

                    // showing thumbnail
                    thumbnail1.setImageURI(selectedUri);
                    thumbnail1.setVisibility(View.VISIBLE);
                    thumbnail1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

//                    thumbnail2.setImageURI(selectedUri);
                    thumbnail2.setVisibility(View.VISIBLE);
//
//                    thumbnail3.setImageURI(selectedUri);
//                    thumbnail3.setVisibility(View.VISIBLE);
//
//                    thumbnail4.setImageURI(selectedUri);
//                    thumbnail4.setVisibility(View.VISIBLE);
//
//                    thumbnail5.setImageURI(selectedUri);
//                    thumbnail5.setVisibility(View.VISIBLE);

                    // clearing youtube url
                    youtubeUrl.setText("");

                }catch (Exception e){
      //              Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CHOOSE_IMAGE_FROM_GALLERY2:
                try {
                    selectedUri2 = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri2, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath2 = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath2);
                    String nameFile = file.getName();
                    //   browse.setText(nameFile);

                    // showing thumbnail
                    thumbnail2.setImageURI(selectedUri2);
                    thumbnail2.setVisibility(View.VISIBLE);
                    thumbnail2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    thumbnail3.setVisibility(View.VISIBLE);
                    youtubeUrl.setText("");

                }catch (Exception e){
            //        Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CHOOSE_IMAGE_FROM_GALLERY3:
                try {
                    selectedUri3 = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri3, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath3 = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath3);
                    String nameFile = file.getName();
                    //   browse.setText(nameFile);

                    // showing thumbnail
                    thumbnail3.setImageURI(selectedUri3);
                    thumbnail3.setVisibility(View.VISIBLE);
                    thumbnail3.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    thumbnail4.setVisibility(View.VISIBLE);
                    youtubeUrl.setText("");

                }catch (Exception e){
             //       Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CHOOSE_IMAGE_FROM_GALLERY4:
                try {
                    selectedUri4 = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri4, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath4 = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath4);
                    String nameFile = file.getName();
                    //   browse.setText(nameFile);

                    // showing thumbnail
                    thumbnail4.setImageURI(selectedUri4);
                    thumbnail4.setVisibility(View.VISIBLE);
                    thumbnail4.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    thumbnail5.setVisibility(View.VISIBLE);
                    youtubeUrl.setText("");

                }catch (Exception e){
              //      Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CHOOSE_IMAGE_FROM_GALLERY5:
                try {
                    selectedUri5 = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri5, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath5 = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath5);
                    String nameFile = file.getName();
                    //   browse.setText(nameFile);

                    // showing thumbnail
                    thumbnail5.setImageURI(selectedUri5);
                    thumbnail5.setVisibility(View.VISIBLE);
                    thumbnail5.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    youtubeUrl.setText("");

                }catch (Exception e){
               //     Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CHOOSE_VIDEO_FROM_GALLERY:
                try {
                    selectedUri = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = CoachCreatePostScreen.this.getContentResolver().query(selectedUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath);
                    String nameFile = file.getName();
                    browse.setText(nameFile);

                    // showing thumbnail
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                    thumbnail1.setImageBitmap(bMap);
                    thumbnail1.setVisibility(View.VISIBLE);

                    thumbnail2.setVisibility(View.GONE);
                    thumbnail3.setVisibility(View.GONE);
                    thumbnail4.setVisibility(View.GONE);
                    thumbnail5.setVisibility(View.GONE);

                    // clearing youtube url
                    youtubeUrl.setText("");
                }catch (Exception e){
              //      Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

                break;

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

//                        browse.setText(fileUri.getPath());

                        // showing thumbnail
                        thumbnail1.setImageURI(fileUri);
                        thumbnail1.setVisibility(View.VISIBLE);

//                        thumbnail2.setImageURI(fileUri);
                        thumbnail2.setVisibility(View.VISIBLE);
//                        thumbnail3.setImageURI(fileUri);
//                        thumbnail3.setVisibility(View.VISIBLE);
//                        thumbnail4.setImageURI(fileUri);
//                        thumbnail4.setVisibility(View.VISIBLE);
//                        thumbnail5.setImageURI(fileUri);
//                        thumbnail5.setVisibility(View.VISIBLE);


                        // clearing youtube url
                        youtubeUrl.setText("");
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }


                } else if (resultCode == RESULT_CANCELED) {
                    // user cancelled Image capture
//                    Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
                } else {
                    // failed to capture image
                    Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
                }
                break;

            case CAMERA_CAPTURE_VIDEO_REQUEST_CODE:

                if (resultCode == RESULT_OK) {
                    try {
                         browse.setText(fileUri.getPath());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Sorry! Failed to record video", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    private class UpdateImageVideoAsync extends AsyncTask<Void, Void, String> {

        ProgressDialog pDialog;
        Context context;

        public UpdateImageVideoAsync(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            pDialog = Utilities.createProgressDialog(context);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... voids) {

            //System.out.println("Uploading starting");

            String uploadUrl = Utilities.BASE_URL + "user_posts/create_new_timeline_coach";
            String strResponse = null;

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httpPost = new HttpPost(uploadUrl);

            httpPost.addHeader("X-access-uid", loggedInUser.getId());
            httpPost.addHeader("X-access-token", loggedInUser.getToken());

            httpPost.addHeader("x-access-did", Utilities.getDeviceId(context));
            httpPost.addHeader("x-access-dtype", Utilities.DEVICE_TYPE);

            try {
                StringBody titleBody = new StringBody(strPostText);
                StringBody location_id = new StringBody(clickedOnLocationIds);
                StringBody session_day = new StringBody(clickedOnDayIds);
                StringBody select_age_group = new StringBody(clickedOnSessionIds);
                StringBody timeline_select_child = new StringBody(clickedOnChildIds);
                StringBody sendMessageToParentBody = new StringBody(strSendMessageToParent);

                //System.out.println("URL "+Utilities.BASE_URL + "user_posts/create_new_timeline");
                //System.out.println("clicked on child ids "+clickedOnChildIds);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                builder.addPart("timeline_description", titleBody);
                builder.addPart("location_id", location_id);
                builder.addPart("session_day", session_day);
                builder.addPart("select_age_group", select_age_group);
                builder.addPart("timeline_select_child", timeline_select_child);
                builder.addPart("mail_check", sendMessageToParentBody);

                /*if(selectedUri != null) {
                    String imageMime = getMimeType(selectedUri);
                    File imageFile = new File(selectedImagePath);
                    //System.out.println("MIME_type__"+imageMime+"--Path__"+selectedImagePath+"--FileName__"+imageFile.getName());
                    FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");

                    builder.addPart("file", imageFileBody);
                }*/

                if (selectedUri != null) {
                    String imageMime = getMimeType(selectedUri);
                    File imageFile = new File(selectedImagePath);
                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());
                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file_1", imageFileBody);
                    }

                } else if (fileUri != null) {
                    String fileMime = getMimeType(fileUri);
                    File file = new File(fileUri.getPath());
                    if(file != null){
                        FileBody imageFileBody = new FileBody(file, file.getName(), fileMime, "UTF-8");
                        builder.addPart("file_1", imageFileBody);
                    }
                }

                if (selectedUri2 != null) {
                    String imageMime = getMimeType(selectedUri2);
                    File imageFile = new File(selectedImagePath2);
                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());
                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file_2", imageFileBody);
                    }

                }

                if (selectedUri3 != null) {
                    String imageMime = getMimeType(selectedUri3);
                    File imageFile = new File(selectedImagePath3);
                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());
                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file_3", imageFileBody);
                    }

                }

                if (selectedUri4 != null) {
                    String imageMime = getMimeType(selectedUri4);
                    File imageFile = new File(selectedImagePath4);
                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());
                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file_4", imageFileBody);
                    }

                }

                if (selectedUri5 != null) {
                    String imageMime = getMimeType(selectedUri5);
                    File imageFile = new File(selectedImagePath5);
                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());
                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file_5", imageFileBody);
                    }

                }

                if(strYoutubeUrl != null && !strYoutubeUrl.isEmpty()){
                    StringBody youtubeUrlBody = new StringBody(strYoutubeUrl);
                    builder.addPart("youtube_url", youtubeUrlBody);
                }

                HttpEntity entity = builder.build();
                httpPost.setEntity(entity);

                HttpResponse response = httpClient.execute(httpPost);
                HttpEntity resEntity = response.getEntity();

                if (resEntity != null) {
                    try {
                        strResponse = EntityUtils.toString(resEntity).trim();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                //System.out.println("File upload end");

            } catch (Exception e) {
                e.printStackTrace();
                //System.out.println("Exception "+e.getMessage());
            }

            return strResponse;

        }

        @Override
        protected void onPostExecute(String response) {

            System.out.println("Response "+response);

            if(response == null) {
                Toast.makeText(CoachCreatePostScreen.this, "Could not connect server", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    boolean status = responseObject.getBoolean("status");
                    String message = responseObject.getString("message");

                    Toast.makeText(CoachCreatePostScreen.this, message, Toast.LENGTH_SHORT).show();

                    if(status) {
//                        finish();
                        showShareOption();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CoachCreatePostScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            if(pDialog != null && pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }
    public String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = CoachCreatePostScreen.this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(Environment.getExternalStorageDirectory(), "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(Environment.getExternalStorageDirectory(), "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    void showShareOption(){
        final Dialog dialog = new Dialog(CoachCreatePostScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.coach_dialog_share_post);
        dialog.setCancelable(false);

        final Button facebookShare = (Button) dialog.findViewById(R.id.facebookShare);
        final Button twitterShare = (Button) dialog.findViewById(R.id.twitterShare);
        ImageView closeDialog = (ImageView) dialog.findViewById(R.id.closeDialog);

        facebookShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (ShareDialog.canShow(ShareLinkContent.class)) {
//                    try{
//                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                                .setContentTitle(strPostText)
////                                .setContentUrl(Uri.parse(childPostsBean.getFileUrl()))
//                                .build();
//
//                        shareDialog.show(linkContent);
//                    }catch (Exception e){
//                        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                                .setContentTitle(strPostText)
//                                .build();
//
//                        shareDialog.show(linkContent);
//                    }
//
//                }
//                dialog.cancel();
            }
        });

        twitterShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    TweetComposer.Builder builder = new TweetComposer.Builder(CoachCreatePostScreen.this)
                            .text(strPostText);
                    builder.show();
//                    dialog.cancel();
                }catch (Exception e){
                    TweetComposer.Builder builder = new TweetComposer.Builder(CoachCreatePostScreen.this)
                            .text(strPostText);
                    builder.show();
//                    dialog.cancel();
                }

            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                finish();
            }
        });

//        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
//    public void ffmpegComressVideo(){
//        pd = new ProgressDialog(CoachCreatePostScreen.this);
//        pd.setMessage("Loading...");
//        pd.show();
//
//        ffmpeg = FFmpeg.getInstance(CoachCreatePostScreen.this);
//        try {
//            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    //System.out.println("FFMPEG onStart");
//                }
//
//                @Override
//                public void onFailure() {
//                    //System.out.println("FFMPEG onFailure");
//                }
//
//                @Override
//                public void onSuccess() {
//                    //System.out.println("FFMPEG onSuccess");
//                }
//
//                @Override
//                public void onFinish() {
//                    //System.out.println("FFMPEG onFinish");
//                }
//            });
//        } catch (FFmpegNotSupportedException e) {
//            // Handle if FFmpeg is not supported by device
//            //System.out.println("FFMPEG Exception");
//            e.printStackTrace();
//        }
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
//        final String output = Environment.getExternalStorageDirectory()+"/VIDEO_" + timeStamp + ".mp4";
//
//
//        //System.out.println("input"+fileUri.getPath()+"output::"+output);
//
//
//        // Merge files
//
//        String[] command1 = new String[3];
//        command1[0] = "-i";
//        command1[1] = fileUri.getPath();
//        command1[2] = output;
//
//        try {
//            // to execute "ffmpeg -version" command you just need to pass "-version"
//            ffmpeg.execute(command1, new ExecuteBinaryResponseHandler() {
//
//                @Override
//                public void onStart() {
//                    //System.out.println("FFMPEG onStart");
//                }
//
//                @Override
//                public void onProgress(String message) {
//                    //System.out.println("FFMPEG onProgress "+message);
//                }
//
//                @Override
//                public void onFailure(String message) {
//                    //System.out.println("FFMPEG onFailure "+message);
//                }
//
//                @Override
//                public void onSuccess(String message) {
//                    //System.out.println("FFMPEG onSuccess "+message);
//                }
//
//                @Override
//                public void onFinish() {
//                    //System.out.println("FFMPEG onFinish");
//
//                    File file = new File(output);
//
//                    fileUri = Uri.fromFile(file);
//                    browse.setText(fileUri.getPath());
//                    pd.dismiss();
//
//                    // showing thumbnail
//                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//                    thumbnail.setImageBitmap(bMap);
//                    thumbnail.setVisibility(View.VISIBLE);
//
//                    // clearing youtube url
//                    youtubeUrl.setText("");
//                }
//            });
//        } catch (FFmpegCommandAlreadyRunningException e) {
//            // Handle if FFmpeg is already running
//            //System.out.println("FFMPEG Exception");
//            e.printStackTrace();
//        }
//    }
}