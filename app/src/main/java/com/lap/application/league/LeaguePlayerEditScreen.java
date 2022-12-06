package com.lap.application.league;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devsmart.android.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.lap.application.R;
import com.lap.application.beans.LeagueAddPlayerTeamBean;
import com.lap.application.beans.LeaguePlayerChooseDocBean;
import com.lap.application.beans.LeaguePlayerEditedDataBean;
import com.lap.application.beans.UserBean;
import com.lap.application.league.adapters.LeagueSelectTeamFieldsDataAdapter;
import com.lap.application.utils.NestedListView;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class LeaguePlayerEditScreen extends AppCompatActivity implements IWebServiceCallback {
    int a=0;
    File f;
    ImageLoader imageLoaderGlobel = ImageLoader.getInstance();
    DisplayImageOptions optionsGlobel;
    int posDoc = 0;
    LeaguePlayerEditedDataBean leaguePlayerEditedDataBean;
    String idStr;
    private final String GET_EDITED_DATA = "GET_EDITED_DATA";
    private final String DELETE_DOC = "DELETE_DOC";

    private final String TEAM_DATA = "TEAM_DATA";
    ArrayList<LeagueAddPlayerTeamBean>leagueList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>seasonList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>teamList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>positionList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>clubList = new ArrayList<>();

    ImageView backButton;
    TextView title;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    String academy_currency;

    TextView chooseClub;
    String chooseClubStr;
    String clubIdStr;
    TextView chooseDate;
    GridView selectedDatesGridView;
    ArrayList<LeagueAddPlayerTeamBean> leagueListSelected = new ArrayList<>();

    TextView chooseSeason;
    GridView selectedSeasonGridView;
    ArrayList<LeagueAddPlayerTeamBean> seasonListSelected = new ArrayList<>();

    TextView chooseTeam;
    GridView selectedTeamGridView;
    ArrayList<LeagueAddPlayerTeamBean> teamListSelected = new ArrayList<>();

    TextView choosePosition;
    GridView selectedPositionGridView;
    ArrayList<LeagueAddPlayerTeamBean> positionListSelected = new ArrayList<>();

    String leagueSelected="";
    String seasonSelected="";
    String teamSelected="";
    String positionSelected="";

    TextView browse;
    private Uri fileUri = null;
    public static final int MEDIA_TYPE_IMAGE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CHOOSE_IMAGE_FROM_GALLERY = 1;
    private static final int CHOOSE_DOC_FROM_GALLERY = 3;
    Uri selectedUri = null;
    String selectedImagePath;

    Uri selectedUriDoc = null;
    String selectedImagePathDoc;

    ImageView thumbnail;

    TextView submit;
    TextView description;
    TextView playerName;
    TextView squadNo;
    TextView nationality;
    TextView height;
    TextView weight;
    static TextView dob;
    String playerNameStr;
    String descriptionStr;
    String squadNumberStr;
    String nationalityStr;
    String heightStr;
    String weightStr;
    String strDob;
    String strSchool;
    String strContact;
    String strParentEmail;
    TextView schoolET;
    TextView contactET;
    TextView parentEmailET;
    Button addDocButton;
    NestedListView listView;
    LeaguePlayerChooseDocAdapter leaguePlayerChooseDocAdapter;
    ArrayList<LeaguePlayerChooseDocBean> leaguePlayerChooseDocBeanArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.league_player_edit_activity);
        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
        playerName = findViewById(R.id.playerName);
        squadNo = findViewById(R.id.squadNo);
        nationality = findViewById(R.id.nationality);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        chooseDate = (TextView) findViewById(R.id.chooseDate);
        selectedDatesGridView = (GridView) findViewById(R.id.selectedDatesGridView);
        chooseSeason = (TextView) findViewById(R.id.chooseSeason);
        selectedSeasonGridView = (GridView) findViewById(R.id.selectedSeasonGridView);
        chooseTeam = (TextView) findViewById(R.id.chooseTeam);
        selectedTeamGridView = (GridView) findViewById(R.id.selectedTeamGridView);
        choosePosition = (TextView) findViewById(R.id.choosePosition);
        selectedPositionGridView = (GridView) findViewById(R.id.selectedPositionGridView);
        browse = findViewById(R.id.browse);
        thumbnail = findViewById(R.id.thumbnail);
        submit = findViewById(R.id.submit);
        description = findViewById(R.id.description);
        dob = (TextView) findViewById(R.id.dob);
        schoolET = findViewById(R.id.schoolET);
        contactET = findViewById(R.id.contactET);
        parentEmailET = findViewById(R.id.parentEmailET);
        addDocButton = findViewById(R.id.addDocButton);
        listView = findViewById(R.id.listView);
        chooseClub = findViewById(R.id.chooseClub);

        imageLoaderGlobel.init(ImageLoaderConfiguration.createDefault(LeaguePlayerEditScreen.this));
        optionsGlobel = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        idStr = getIntent().getStringExtra("id");

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        academy_currency = sharedPreferences.getString("academy_currency", null);

        String verbiage_singular = sharedPreferences.getString("verbiage_singular", null);
        browse.setText("Player Photo");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        leaguePlayerChooseDocAdapter = new LeaguePlayerChooseDocAdapter(LeaguePlayerEditScreen.this, leaguePlayerChooseDocBeanArrayList);
        listView.setAdapter(leaguePlayerChooseDocAdapter);
//        Utilities.setListViewHeightBasedOnChildren(listView);

        chooseClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSDKchooseDialog(chooseClub, "Choose Club", clubList);
            }
        });

        addDocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(leaguePlayerChooseDocBeanArrayList.size() == 0 || leaguePlayerChooseDocBeanArrayList.size() < 5){
                    LeaguePlayerChooseDocBean leaguePlayerChooseDocBean = new LeaguePlayerChooseDocBean();
                    leaguePlayerChooseDocBean.setFileName("Document Title");
                    leaguePlayerChooseDocBean.setFile(null);
                    leaguePlayerChooseDocBean.setFileName("Upload Document");
                    leaguePlayerChooseDocBean.setTitle("");
                    leaguePlayerChooseDocBeanArrayList.add(leaguePlayerChooseDocBean);
                    leaguePlayerChooseDocAdapter.notifyDataSetChanged();
//                    Utilities.setListViewHeightBasedOnChildren(listView);
                }else{
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(LeaguePlayerEditScreen.this);
                    builder1.setMessage("Only five document can be added.");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();
                }


            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[]{"Take from camera", "Select from gallery"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LeaguePlayerEditScreen.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(LeaguePlayerEditScreen.this);

                builder.setTitle("Choose");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

//                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                            File output = new File(dir, "abc.jpg");
//                            fileUri = Uri.fromFile(output);
//                            i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                            startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileUri);
                            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);


                        } else {
                            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, CHOOSE_IMAGE_FROM_GALLERY);
                            dialog.cancel();

                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();



            }
        });

        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LeaguePlayerEditScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new DatesGridAdapterForPopup(LeaguePlayerEditScreen.this));

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        leagueListSelected.clear();
                        selectedDatesGridView.setAdapter(null);

                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : leagueList){
                            if(leagueAddPlayerTeamBean.isSelected()){
                                leagueListSelected.add(leagueAddPlayerTeamBean);
                            }
                        }
                        if(leagueListSelected.size()==0){

                        }else{
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(leagueListSelected, LeaguePlayerEditScreen.this);
                            selectedDatesGridView.setAdapter(selectedDatesAdapter);
                            Utilities.setGridViewHeightBasedOnChildren(selectedDatesGridView, 2);
                        }

                    }
                });

                dialog.show();
            }
        });



        chooseSeason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LeaguePlayerEditScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new SeasonGridAdapterForPopup(LeaguePlayerEditScreen.this));

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        seasonListSelected.clear();
                        selectedSeasonGridView.setAdapter(null);

                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : seasonList){
                            if(leagueAddPlayerTeamBean.isSelected()){
                                seasonListSelected.add(leagueAddPlayerTeamBean);
                            }
                        }
                        if(seasonListSelected.size()==0){

                        }else{
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(seasonListSelected, LeaguePlayerEditScreen.this);
                            selectedSeasonGridView.setAdapter(selectedDatesAdapter);
                            Utilities.setGridViewHeightBasedOnChildren(selectedSeasonGridView, 2);
                        }

                    }
                });

                dialog.show();
            }
        });

        chooseTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LeaguePlayerEditScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new TeamGridAdapterForPopup(LeaguePlayerEditScreen.this));

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        teamListSelected.clear();
                        selectedTeamGridView.setAdapter(null);

                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : teamList){
                            if(leagueAddPlayerTeamBean.isSelected()){
                                teamListSelected.add(leagueAddPlayerTeamBean);
                            }
                        }
                        if(teamListSelected.size()==0){

                        }else{
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(teamListSelected, LeaguePlayerEditScreen.this);
                            selectedTeamGridView.setAdapter(selectedDatesAdapter);
                            Utilities.setGridViewHeightBasedOnChildren(selectedTeamGridView, 2);
                        }

                    }
                });

                dialog.show();
            }
        });

        choosePosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LeaguePlayerEditScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new PositionGridAdapterForPopup(LeaguePlayerEditScreen.this));

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();
                        positionListSelected.clear();
                        selectedPositionGridView.setAdapter(null);

                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : positionList){
                            if(leagueAddPlayerTeamBean.isSelected()){
                                positionListSelected.add(leagueAddPlayerTeamBean);
                            }
                        }
                        if(positionListSelected.size()==0){

                        }else{
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(positionListSelected, LeaguePlayerEditScreen.this);
                            selectedPositionGridView.setAdapter(selectedDatesAdapter);
                            Utilities.setGridViewHeightBasedOnChildren(selectedPositionGridView, 2);
                        }

                    }
                });

                dialog.show();
            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DatePickerJoinLeague();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerNameStr = playerName.getText().toString().trim();
                squadNumberStr = squadNo.getText().toString().trim();
                nationalityStr = nationality.getText().toString().trim();
                heightStr = height.getText().toString().trim();
                weightStr = weight.getText().toString().trim();
                descriptionStr = description.getText().toString().trim();
                strDob = dob.getText().toString().trim();
                strSchool= schoolET.getText().toString().trim();
                strContact = contactET.getText().toString().trim();
                strParentEmail = parentEmailET.getText().toString().trim();
                chooseClubStr = chooseClub.getText().toString().trim();

                Pattern pattern = Pattern.compile(Utilities.EMAIL_PATTERN);
                Matcher matcher = pattern.matcher(strParentEmail);

                if (playerNameStr == null || playerNameStr.isEmpty()) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter player name", Toast.LENGTH_SHORT).show();
                }
//                else if (squadNumberStr == null || squadNumberStr.isEmpty()) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter squad number", Toast.LENGTH_SHORT).show();
//                }else if (nationalityStr == null || nationalityStr.isEmpty()) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter nationality", Toast.LENGTH_SHORT).show();
//                }else if (heightStr == null || heightStr.isEmpty()) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter height", Toast.LENGTH_SHORT).show();
//                }else if (weightStr == null || weightStr.isEmpty()) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter weight", Toast.LENGTH_SHORT).show();
//                } else if (strDob == null || strDob.isEmpty() || strDob.equalsIgnoreCase("Date of Birth")) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please select Date of Birth", Toast.LENGTH_SHORT).show();
//                }else if(leagueListSelected.size()==0){
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please choose league", Toast.LENGTH_SHORT).show();
//                }else if(seasonListSelected.size()==0){
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please choose season", Toast.LENGTH_SHORT).show();
//                }
//                else if (strSchool == null || strSchool.isEmpty()) {
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter School", Toast.LENGTH_SHORT).show();
//                }
                else if (strContact == null || strContact.isEmpty()) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter contact number", Toast.LENGTH_SHORT).show();
                }else if (strParentEmail == null || strParentEmail.isEmpty()) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Please enter contact email", Toast.LENGTH_SHORT).show();
                } else if (!matcher.matches()) {
                    Toast.makeText(getApplicationContext(), "Please enter a valid contact email", Toast.LENGTH_SHORT).show();
                }else if(teamListSelected.size()==0){
                    Toast.makeText(LeaguePlayerEditScreen.this, "Please choose team", Toast.LENGTH_SHORT).show();
                }
//                else if(positionListSelected.size()==0){
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please choose position", Toast.LENGTH_SHORT).show();
//                }else if(descriptionStr == null || descriptionStr.isEmpty()){
//                    Toast.makeText(LeaguePlayerEditScreen.this, "Please choose description", Toast.LENGTH_SHORT).show();
//                }
                else{
                    leagueSelected="";
                    seasonSelected="";
                    teamSelected="";
                    positionSelected="";
                    if(leagueListSelected.size()!=0){
                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : leagueListSelected){
                            leagueSelected = leagueSelected+""+leagueAddPlayerTeamBean.getId()+",";
                        }
                        leagueSelected = leagueSelected.substring(0, leagueSelected.length() -1);
                    }

                    if(seasonListSelected.size()!=0){
                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : seasonListSelected){
                            seasonSelected = seasonSelected+""+leagueAddPlayerTeamBean.getId()+",";
                        }
                        seasonSelected = seasonSelected.substring(0, seasonSelected.length() -1);
                    }

                    if(teamListSelected.size()!=0){
                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : teamListSelected){
                            teamSelected = teamSelected+""+leagueAddPlayerTeamBean.getId()+",";
                        }
                        teamSelected = teamSelected.substring(0, teamSelected.length() -1);
                    }

                    if(positionListSelected.size()!=0){
                        for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : positionListSelected){
                            positionSelected = positionSelected+""+leagueAddPlayerTeamBean.getId()+",";
                        }
                        positionSelected = positionSelected.substring(0, positionSelected.length() -1);
                    }

                    if(leaguePlayerChooseDocBeanArrayList.size()==0){
                        new addAsync(LeaguePlayerEditScreen.this).execute();
                    }else{
                        int j = 0;
                        for(int i=0; i<leaguePlayerChooseDocBeanArrayList.size(); i++){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).getTitle().equalsIgnoreCase("") || leaguePlayerChooseDocBeanArrayList.get(i).getTitle().equalsIgnoreCase("document title") ||
                                    leaguePlayerChooseDocBeanArrayList.get(i).getFileName().equalsIgnoreCase("") || leaguePlayerChooseDocBeanArrayList.get(i).getFileName().equalsIgnoreCase("Upload Document")){
                                j = 1;
                            }
                        }

                        if(j==1){
                            Toast.makeText(LeaguePlayerEditScreen.this, "Please enter document title and upload document", Toast.LENGTH_SHORT).show();
                        }else{
                            new addAsync(LeaguePlayerEditScreen.this).execute();
                        }
                    }


                }
            }
        });


        getTeamData();
    }

    class DatesGridAdapterForPopup extends BaseAdapter {
        LayoutInflater layoutInflater;
        public DatesGridAdapterForPopup(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return leagueList.size();
        }

        @Override
        public Object getItem(int position) {
            return leagueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.parent_adapter_camp_available_date_item, null);

            final CheckBox dateCheckBox = (CheckBox) convertView.findViewById(R.id.dateCheckBox);
            dateCheckBox.setText(leagueList.get(position).getName());


            if (leagueList.get(position).isSelected()) {
                dateCheckBox.setChecked(true);
            }

            dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        leagueList.get(position).setSelected(true);
                    } else {
                        leagueList.get(position).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    class SeasonGridAdapterForPopup extends BaseAdapter {
        LayoutInflater layoutInflater;
        public SeasonGridAdapterForPopup(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return seasonList.size();
        }

        @Override
        public Object getItem(int position) {
            return seasonList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.parent_adapter_camp_available_date_item, null);

            final CheckBox dateCheckBox = (CheckBox) convertView.findViewById(R.id.dateCheckBox);
            dateCheckBox.setText(seasonList.get(position).getName());


            if (seasonList.get(position).isSelected()) {
                dateCheckBox.setChecked(true);
            }

            dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        seasonList.get(position).setSelected(true);
                    } else {
                        seasonList.get(position).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    class TeamGridAdapterForPopup extends BaseAdapter {
        LayoutInflater layoutInflater;
        public TeamGridAdapterForPopup(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return teamList.size();
        }

        @Override
        public Object getItem(int position) {
            return teamList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.parent_adapter_camp_available_date_item, null);

            final CheckBox dateCheckBox = (CheckBox) convertView.findViewById(R.id.dateCheckBox);
            dateCheckBox.setText(teamList.get(position).getName());


            if (teamList.get(position).isSelected()) {
                dateCheckBox.setChecked(true);
            }

            dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        teamList.get(position).setSelected(true);
                    } else {
                        teamList.get(position).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    class PositionGridAdapterForPopup extends BaseAdapter {
        LayoutInflater layoutInflater;
        public PositionGridAdapterForPopup(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return positionList.size();
        }

        @Override
        public Object getItem(int position) {
            return positionList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.parent_adapter_camp_available_date_item, null);

            final CheckBox dateCheckBox = (CheckBox) convertView.findViewById(R.id.dateCheckBox);
            dateCheckBox.setText(positionList.get(position).getName());


            if (positionList.get(position).isSelected()) {
                dateCheckBox.setChecked(true);
            }

            dateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        positionList.get(position).setSelected(true);
                    } else {
                        positionList.get(position).setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });

            return convertView;
        }
    }

    class SelectedDatesAdapter extends BaseAdapter {
        LayoutInflater layoutInflater;
        ArrayList<LeagueAddPlayerTeamBean> selectedDatesList;

        public SelectedDatesAdapter(ArrayList<LeagueAddPlayerTeamBean> selectedDatesList, Context context){
            this.selectedDatesList = selectedDatesList;
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return selectedDatesList.size();
        }

        @Override
        public Object getItem(int position) {
            return selectedDatesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.parent_adapter_camp_selected_date_item, null);

            TextView date = (TextView) convertView.findViewById(R.id.date);
            date.setText(selectedDatesList.get(position).getName());

            return convertView;
        }
    }



    private void getTeamData(){
        if(Utilities.isNetworkAvailable(LeaguePlayerEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));

            String webServiceUrl = Utilities.BASE_URL + "join_league/parent_player_data";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeaguePlayerEditScreen.this, nameValuePairList, TEAM_DATA, LeaguePlayerEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeaguePlayerEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case TEAM_DATA:
                if(response == null) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");

                            JSONArray leagueJsonArray = jsonObject.getJSONArray("parent_league_list");
                            for(int i=0; i<leagueJsonArray.length(); i++){
                                JSONObject jsonObject1 = leagueJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("league_id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("legue_name"));
                                leagueList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray seasonJsonArray = jsonObject.getJSONArray("season_list");
                            for(int i=0; i<seasonJsonArray.length(); i++){
                                JSONObject jsonObject1 = seasonJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                seasonList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray positionJsonArray = jsonObject.getJSONArray("position_list");
                            for(int i = 0; i<positionJsonArray.length(); i++){
                                JSONObject jsonObject1 = positionJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                positionList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray teamJsonArray = jsonObject.getJSONArray("team_list");
                            for(int i = 0; i<teamJsonArray.length(); i++){
                                JSONObject jsonObject1 = teamJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                teamList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray clubJsonArray = jsonObject.getJSONArray("club_list");
                            for(int i = 0; i<clubJsonArray.length(); i++){
                                JSONObject jsonObject1 = clubJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                clubList.add(leagueAddPlayerTeamBean);
                            }

                            getEditedData();

                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeaguePlayerEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case GET_EDITED_DATA:
                if(response == null) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            leaguePlayerEditedDataBean = new LeaguePlayerEditedDataBean();
                            leaguePlayerEditedDataBean.setId(jsonObject.getString("id"));
                            leaguePlayerEditedDataBean.setAcademy_id(jsonObject.getString("academy_id"));
                            leaguePlayerEditedDataBean.setName(jsonObject.getString("name"));
                            leaguePlayerEditedDataBean.setDescription(jsonObject.getString("description"));
                            leaguePlayerEditedDataBean.setLeague(jsonObject.getString("league"));
                            leaguePlayerEditedDataBean.setTeam_id(jsonObject.getString("team_id"));
                            leaguePlayerEditedDataBean.setSquad_number(jsonObject.getString("squad_number"));
                            leaguePlayerEditedDataBean.setNationality(jsonObject.getString("nationality"));
                            leaguePlayerEditedDataBean.setPosition(jsonObject.getString("position"));
                            leaguePlayerEditedDataBean.setTeam(jsonObject.getString("team"));
                            leaguePlayerEditedDataBean.setSeasons(jsonObject.getString("seasons"));
                            leaguePlayerEditedDataBean.setClub(jsonObject.getString("club"));
                            leaguePlayerEditedDataBean.setHeight(jsonObject.getString("height"));
                            leaguePlayerEditedDataBean.setWeight(jsonObject.getString("weight"));
                            leaguePlayerEditedDataBean.setDob(jsonObject.getString("dob"));
                            leaguePlayerEditedDataBean.setImage(jsonObject.getString("image"));
                            leaguePlayerEditedDataBean.setState(jsonObject.getString("state"));
                            leaguePlayerEditedDataBean.setCreated_by(jsonObject.getString("created_by"));
                            leaguePlayerEditedDataBean.setSchool(jsonObject.getString("school"));
                            leaguePlayerEditedDataBean.setParentContactEmail(jsonObject.getString("parent_email"));
                            leaguePlayerEditedDataBean.setParentContactNumber(jsonObject.getString("parent_contact_no"));
                         //   ArrayList<LeaguePlayerChooseDocBean> leaguePlayerChooseDocBeanArrayList = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("player_documents");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                LeaguePlayerChooseDocBean leaguePlayerChooseDocBean = new LeaguePlayerChooseDocBean();
                                leaguePlayerChooseDocBean.setId(jsonObject1.getString("id"));
                                leaguePlayerChooseDocBean.setAcademy_id(jsonObject1.getString("academy_id"));
                                leaguePlayerChooseDocBean.setPlayer_id(jsonObject1.getString("player_id"));
                                leaguePlayerChooseDocBean.setTitle(jsonObject1.getString("doc_name"));
                                leaguePlayerChooseDocBean.setFileName(jsonObject1.getString("file_name"));
                                leaguePlayerChooseDocBean.setState(jsonObject1.getString("state"));
                                leaguePlayerChooseDocBean.setFile_url(jsonObject1.getString("file_url"));
                                leaguePlayerChooseDocBean.setFile(null);
                                leaguePlayerChooseDocBean.setEditBoolDoc(false);
                                leaguePlayerChooseDocBean.setEditBoolTitle(false);
                                leaguePlayerChooseDocBeanArrayList.add(leaguePlayerChooseDocBean);
                            }

                            leaguePlayerChooseDocAdapter.notifyDataSetChanged();
//                            Utilities.setListViewHeightBasedOnChildren(listView);
//
//
                            playerName.setText(leaguePlayerEditedDataBean.getName());

                            if(!leaguePlayerEditedDataBean.getSquad_number().equalsIgnoreCase("0")){
                                squadNo.setText(leaguePlayerEditedDataBean.getSquad_number());
                            }

                            nationality.setText(leaguePlayerEditedDataBean.getNationality());
                            height.setText(leaguePlayerEditedDataBean.getHeight());
                            weight.setText(leaguePlayerEditedDataBean.getWeight());
                            if(leaguePlayerEditedDataBean.getDob().equalsIgnoreCase("")){

                            }else{
                                dob.setText(leaguePlayerEditedDataBean.getDob());
                            }

                            schoolET.setText(leaguePlayerEditedDataBean.getSchool());
                            parentEmailET.setText(leaguePlayerEditedDataBean.getParentContactEmail());
                            contactET.setText(leaguePlayerEditedDataBean.getParentContactNumber());

                            imageLoaderGlobel.displayImage(leaguePlayerEditedDataBean.getImage(), thumbnail, optionsGlobel);

                            if(leaguePlayerEditedDataBean.getLeague().equalsIgnoreCase("") || leaguePlayerEditedDataBean.getLeague().equalsIgnoreCase("null")){

                            }else{
                                System.out.println("YESHERE____"+leaguePlayerEditedDataBean.getLeague()+"sdfs");
                                for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : leagueList){
                                    List<String> items = Arrays.asList(leaguePlayerEditedDataBean.getLeague().split("\\s*,\\s*"));
                                    for(String item : items){
                                        if(item.equalsIgnoreCase(leagueAddPlayerTeamBean.getId())){
                                            leagueAddPlayerTeamBean.setSelected(true);
                                        }
                                    }
                                    if(leagueAddPlayerTeamBean.isSelected()){
                                        leagueListSelected.add(leagueAddPlayerTeamBean);
                                    }
                                }
                                SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(leagueListSelected, LeaguePlayerEditScreen.this);
                                selectedDatesGridView.setAdapter(selectedDatesAdapter);
                                Utilities.setGridViewHeightBasedOnChildren(selectedDatesGridView, 2);
                            }


                            if(leaguePlayerEditedDataBean.getSeasons().equalsIgnoreCase("") || leaguePlayerEditedDataBean.getSeasons().equalsIgnoreCase("null")){

                            }else{
                                for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : seasonList){
                                    List<String> items = Arrays.asList(leaguePlayerEditedDataBean.getSeasons().split("\\s*,\\s*"));
                                    for(String item : items){
                                        if(item.equalsIgnoreCase(leagueAddPlayerTeamBean.getId())){
                                            leagueAddPlayerTeamBean.setSelected(true);
                                        }
                                    }
                                    if(leagueAddPlayerTeamBean.isSelected()){
                                        seasonListSelected.add(leagueAddPlayerTeamBean);
                                    }
                                }
                                SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(seasonListSelected, LeaguePlayerEditScreen.this);
                                selectedSeasonGridView.setAdapter(selectedDatesAdapter);
                                Utilities.setGridViewHeightBasedOnChildren(selectedSeasonGridView, 2);
                            }

                            if(leaguePlayerEditedDataBean.getTeam().equalsIgnoreCase("") || leaguePlayerEditedDataBean.getTeam().equalsIgnoreCase("null")){

                            }else{
                                for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : teamList){
                                    List<String> items = Arrays.asList(leaguePlayerEditedDataBean.getTeam().split("\\s*,\\s*"));
                                    for(String item : items){
                                        if(item.equalsIgnoreCase(leagueAddPlayerTeamBean.getId())){
                                            leagueAddPlayerTeamBean.setSelected(true);
                                        }
                                    }
                                    if(leagueAddPlayerTeamBean.isSelected()){
                                        teamListSelected.add(leagueAddPlayerTeamBean);
                                    }
                                }
                                SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(teamListSelected, LeaguePlayerEditScreen.this);
                                selectedTeamGridView.setAdapter(selectedDatesAdapter);
                                Utilities.setGridViewHeightBasedOnChildren(selectedTeamGridView, 2);
                            }

                            if(leaguePlayerEditedDataBean.getPosition().equalsIgnoreCase("") || leaguePlayerEditedDataBean.getPosition().equalsIgnoreCase("null")){

                            }else{
                                for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : positionList){
                                    List<String> items = Arrays.asList(leaguePlayerEditedDataBean.getPosition().split("\\s*,\\s*"));
                                    for(String item : items){
                                        if(item.equalsIgnoreCase(leagueAddPlayerTeamBean.getId())){
                                            leagueAddPlayerTeamBean.setSelected(true);
                                        }
                                    }
                                    if(leagueAddPlayerTeamBean.isSelected()){
                                        positionListSelected.add(leagueAddPlayerTeamBean);
                                    }
                                }
                                SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(positionListSelected, LeaguePlayerEditScreen.this);
                                selectedPositionGridView.setAdapter(selectedDatesAdapter);
                                Utilities.setGridViewHeightBasedOnChildren(selectedPositionGridView, 2);
                            }

                            if(leaguePlayerEditedDataBean.getClub().equalsIgnoreCase("") || leaguePlayerEditedDataBean.getClub().equalsIgnoreCase("null")){

                            }else{
                                for(LeagueAddPlayerTeamBean leagueAddPlayerTeamBean : clubList){
                                    if(leagueAddPlayerTeamBean.getId().equalsIgnoreCase(leaguePlayerEditedDataBean.getClub())){
                                        clubIdStr = leagueAddPlayerTeamBean.getId();
                                        chooseClub.setText(leagueAddPlayerTeamBean.getName());
                                    }
                                }
                            }



                            if(leaguePlayerEditedDataBean.getDescription().equalsIgnoreCase("")){

                            }else{
                                descriptionStr = leaguePlayerEditedDataBean.getDescription();
                                description.setText(Html.fromHtml(descriptionStr));
                            }

                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LeaguePlayerEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                break;

            case DELETE_DOC:
                if(response == null) {
                    Toast.makeText(LeaguePlayerEditScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {

                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }else{
                           // Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeaguePlayerEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(Environment.getExternalStorageDirectory(), "IMG_" + timeStamp + ".jpg");
        }else {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_DOC_FROM_GALLERY:
                try {
                    selectedUriDoc = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = LeaguePlayerEditScreen.this.getContentResolver().query(selectedUriDoc, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePathDoc = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePathDoc);
                    String nameFile = file.getName();

                    leaguePlayerChooseDocBeanArrayList.get(posDoc).setFileName(nameFile);
                    leaguePlayerChooseDocBeanArrayList.get(posDoc).setFile(selectedUriDoc);
                    leaguePlayerChooseDocBeanArrayList.get(posDoc).setSelectedImagePath(selectedImagePathDoc);
                    leaguePlayerChooseDocBeanArrayList.get(posDoc).setEditBoolDoc(true);
                    leaguePlayerChooseDocAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case CHOOSE_IMAGE_FROM_GALLERY:
                try {
                    selectedUri = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = LeaguePlayerEditScreen.this.getContentResolver().query(selectedUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    selectedImagePath = cursor.getString(columnIndex);
                    cursor.close();
                    File file = new File(selectedImagePath);
                    String nameFile = file.getName();
                    browse.setText(nameFile);


                    // showing thumbnail
                    thumbnail.setImageURI(selectedUri);
                    thumbnail.setVisibility(View.VISIBLE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:



                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    thumbnail.setVisibility(View.VISIBLE);
                    thumbnail.setImageBitmap(imageBitmap);


                    try{
                        //create a file to write bitmap data
                         f = new File(getCacheDir(), "image.jpg");
                        f.createNewFile();

//Convert bitmap to byte array
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
                        byte[] bitmapdata = bos.toByteArray();

//write the bytes in file
                        FileOutputStream fos = new FileOutputStream(f);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                        browse.setText(""+f.getPath());
                        a=1;
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                }


//                //                String videoRotatedFinalPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/IFA-filter-output.mp4";
//                if (resultCode == RESULT_OK) {
//
//                    try {
//
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inSampleSize = 8;
//
////                        final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
//                        browse.setText(fileUri.getPath());
//
//                        // showing thumbnail
//                        thumbnail.setImageURI(fileUri);
//                        thumbnail.setVisibility(View.VISIBLE);
//
//                    } catch (NullPointerException e) {
//                        e.printStackTrace();
//                    }
//
//
//                } else if (resultCode == RESULT_CANCELED) {
//                    // user cancelled Image capture
////                    Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
//                } else {
//                    // failed to capture image
//                    Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
//                }
                break;

        }
    }


    private class addAsync extends AsyncTask<Void, Void, String> {

        ProgressDialog pDialog;
        Context context;

        public addAsync(Context context) {
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

            String uploadUrl = Utilities.BASE_URL + "join_league/parent_player_edit";
            String strResponse = null;

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httpPost = new HttpPost(uploadUrl);

            httpPost.addHeader("X-access-uid", loggedInUser.getId());
            httpPost.addHeader("X-access-token", loggedInUser.getToken());

            httpPost.addHeader("x-access-did", Utilities.getDeviceId(context));
            httpPost.addHeader("x-access-dtype", Utilities.DEVICE_TYPE);

            try {
                StringBody idBody = new StringBody(idStr);
                StringBody academyIdBody = new StringBody(loggedInUser.getAcademiesId());
                StringBody userIdBody = new StringBody(loggedInUser.getId());
                StringBody playerNameBody = new StringBody(playerNameStr);
                StringBody teamDescriptionBody = new StringBody(descriptionStr);
                StringBody teamLeagueBody = new StringBody(leagueSelected);
                StringBody teamSeasonBody = new StringBody(seasonSelected);
                StringBody squadBody = new StringBody(squadNumberStr);
                StringBody nationalityBody = new StringBody(nationalityStr);
                StringBody heightBody = new StringBody(heightStr);
                StringBody weightBody = new StringBody(weightStr);
                StringBody teamBody = new StringBody(teamSelected);
                StringBody positionBody = new StringBody(positionSelected);
                StringBody dobBody = new StringBody(strDob);
                StringBody schoolBody = new StringBody(strSchool);
                StringBody contactBody = new StringBody(strContact);
                StringBody parentEmailBody = new StringBody(strParentEmail);

                System.out.println("id:: "+idStr+" AcademyId :: "+loggedInUser.getAcademiesId() +" User Id:: "+loggedInUser.getId()+
                        " Player Name:: "+playerNameStr+ "League:: "+leagueSelected +" seasonSelected:: "+seasonSelected+ " player Team:: "+teamSelected+
                        " squad number:: "+squadNumberStr+" nationality:: "+nationalityStr + " description:: "+descriptionStr+
                        " height:: "+heightStr+ " weight:: "+weightStr +" position:: "+positionSelected +" DOB:: "+strDob);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addPart("id", idBody);
                builder.addPart("user_id", userIdBody);
                builder.addPart("academy_id", academyIdBody);
                builder.addPart("player_name", playerNameBody);
                builder.addPart("player_description", teamDescriptionBody);
                builder.addPart("squad_number", squadBody);
                builder.addPart("nationality",nationalityBody);
                builder.addPart("height", heightBody);
                builder.addPart("weight", weightBody);
                if(strDob.equalsIgnoreCase("Date of Birth")){

                }else{
                    builder.addPart("dob", dobBody);
                }
                builder.addPart("player_league", teamLeagueBody);
                builder.addPart("player_season", teamSeasonBody);
                builder.addPart("player_team", teamBody);
                builder.addPart("player_position", positionBody);
                builder.addPart("school", schoolBody);
                builder.addPart("parent_contact_no", contactBody);
                builder.addPart("parent_email", parentEmailBody);

                if (chooseClubStr == null || chooseClubStr.isEmpty() || chooseClubStr.equals("Choose Club")){

                }else{
                    StringBody teamClubBody = new StringBody(clubIdStr);
                    builder.addPart("player_club", teamClubBody);
                }

                if (selectedUri != null) {
                    String imageMime = getMimeType(selectedUri);

                    File imageFile = new File(selectedImagePath);

                    System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());

                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file", imageFileBody);
                    }

                }
                else if (a==1){
                    //     String fileMime = getMimeType(fileUri);

                    //   File file = new File(fileUri.getPath());

                    FileBody imageFileBody = new FileBody(f, f.getName(), "image/jpeg", "UTF-8");
                    builder.addPart("file", imageFileBody);
                    a=0;

                }

//                else if (fileUri != null) {
//
//                    String fileMime = getMimeType(fileUri);
//
//                    File file = new File(fileUri.getPath());
//                    if(file != null){
//                        FileBody imageFileBody = new FileBody(file, file.getName(), fileMime, "UTF-8");
//                        builder.addPart("file", imageFileBody);
//                    }
//                }

                if(leaguePlayerChooseDocBeanArrayList.size()==0){

                }else{
                    for(int i=0; i<leaguePlayerChooseDocBeanArrayList.size(); i++){
                        Uri uri = leaguePlayerChooseDocBeanArrayList.get(i).getFile();
                        if(i==0){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_1", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_1", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_1", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_1", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_1", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                               // if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_1", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_1", bodyOne);

                                    System.out.println("hereData:: id:: "+leaguePlayerChooseDocBeanArrayList.get(i).getId() +" , title:: "+leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                              //  }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_1", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_1", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_1", imageFileBody);
                                    }

                                }
                            }


                        }else if(i==1){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_2", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_2", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_2", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_2", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_2", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                              //  if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_2", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_2", bodyOne);
                            //    }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_2", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_2", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_2", imageFileBody);
                                    }

                                }
                            }


                        }else if(i==2){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_3", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_3", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_3", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_3", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_3", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                              //  if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_3", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_3", bodyOne);
                             //   }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_3", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_3", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_3", imageFileBody);
                                    }

                                }
                            }

                        }else if(i==3){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_4", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_4", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_4", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_4", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_4", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                             //   if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_4", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_4", bodyOne);
                            //    }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_4", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_4", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_4", imageFileBody);
                                    }

                                }
                            }

                        }else if(i==4){
                            if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_5", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_5", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==false){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_5", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_5", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_5", imageFileBody);
                                    }

                                }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==false && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                              //  if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_5", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_5", bodyOne);
                              //  }
                            }else if(leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolDoc()==true && leaguePlayerChooseDocBeanArrayList.get(i).isEditBoolTitle()==true){
                                if (uri != null) {
                                    StringBody bodyID = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getId());
                                    builder.addPart("existing_doc_id_5", bodyID);

                                    StringBody bodyOne = new StringBody(leaguePlayerChooseDocBeanArrayList.get(i).getTitle());
                                    builder.addPart("document_title_5", bodyOne);

                                    String imageMime = getMimeType(uri);
                                    File imageFile = new File(leaguePlayerChooseDocBeanArrayList.get(i).getSelectedImagePath());
                                    if(imageFile != null){
                                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                                        builder.addPart("player_doc_5", imageFileBody);
                                    }

                                }
                            }

                        }
                    }
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
                //System.out.println("Exception " + e.getMessage());
            }

            return strResponse;

        }

        @Override
        protected void onPostExecute(String response) {

            System.out.println("Response " + response);

            if (response == null) {
                Toast.makeText(LeaguePlayerEditScreen.this, "Could not connect server", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    boolean status = responseObject.getBoolean("status");
                    String message = responseObject.getString("message");

                    Toast.makeText(LeaguePlayerEditScreen.this, message, Toast.LENGTH_SHORT).show();

                    if (status) {
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LeaguePlayerEditScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = LeaguePlayerEditScreen.this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static class DatePickerJoinLeague extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month++;
            String strDay = day < 10 ? "0"+day : day+"";
            String strMonth = month < 10 ? "0"+month : month+"";
            dob.setText(strDay+"-"+strMonth+"-"+year);
        }
    }

    private void getEditedData(){
        if(Utilities.isNetworkAvailable(LeaguePlayerEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("id", idStr));

            String webServiceUrl = Utilities.BASE_URL + "join_league/parent_player_edit_record";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeaguePlayerEditScreen.this, nameValuePairList, GET_EDITED_DATA, LeaguePlayerEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeaguePlayerEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteDocAPI(String id){
        if(Utilities.isNetworkAvailable(LeaguePlayerEditScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("doc_id", id));

            String webServiceUrl = Utilities.BASE_URL + "join_league/remove_player_document";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeaguePlayerEditScreen.this, nameValuePairList, DELETE_DOC, LeaguePlayerEditScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeaguePlayerEditScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public class LeaguePlayerChooseDocAdapter  extends BaseAdapter {
        SharedPreferences sharedPreferences;
        UserBean loggedInUser;
        Context context;
        ArrayList<LeaguePlayerChooseDocBean> leaguePlayerChooseDocBeanArrayList;
        LayoutInflater layoutInflater;
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options;

        Typeface helvetica;
        Typeface linoType;

        public LeaguePlayerChooseDocAdapter(Context context, ArrayList<LeaguePlayerChooseDocBean> leaguePlayerChooseDocBeanArrayList){
            this.context = context;
            this.leaguePlayerChooseDocBeanArrayList = leaguePlayerChooseDocBeanArrayList;
            layoutInflater = LayoutInflater.from(context);

            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.placeholder)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .cacheInMemory(true)
                    .cacheOnDisc(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();

            helvetica = Typeface.createFromAsset(context.getAssets(),"fonts/HelveticaNeue.ttf");
            linoType = Typeface.createFromAsset(context.getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

            sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
            if (jsonLoggedInUser != null) {
                loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
            }

        }

        @Override
        public int getCount() {
            return leaguePlayerChooseDocBeanArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return leaguePlayerChooseDocBeanArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = layoutInflater.inflate(R.layout.league_adapter_choose_doc_item, null);
            final TextView document1ET = (TextView) convertView.findViewById(R.id.document1ET);
            final TextView browseDoc1 = (TextView) convertView.findViewById(R.id.browseDoc1);
            final ImageView deleteIV= convertView.findViewById(R.id.deleteIV);
            final ImageView imageView = convertView.findViewById(R.id.imageView);


            final LeaguePlayerChooseDocBean leaguePlayerChooseDocBean = leaguePlayerChooseDocBeanArrayList.get(position);
            document1ET.setText(leaguePlayerChooseDocBean.getTitle());
            browseDoc1.setText(leaguePlayerChooseDocBean.getFileName());

            imageView.setVisibility(View.VISIBLE);
            if(leaguePlayerChooseDocBean.getFile()==null){
                imageLoader.displayImage(leaguePlayerChooseDocBean.getFile_url(), imageView, options);
            }else{
                imageView.setImageURI(leaguePlayerChooseDocBean.getFile());
            }


            deleteIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                    builder1.setMessage("Are you sure you want to delete this document?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    deleteDocAPI(leaguePlayerChooseDocBeanArrayList.get(position).getId());
                                    leaguePlayerChooseDocBeanArrayList.remove(position);
                                    notifyDataSetChanged();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    android.app.AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });

            browseDoc1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    posDoc = position;
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, CHOOSE_DOC_FROM_GALLERY);
                }
            });

//            document1ET.setOnKeyListener(new View.OnKeyListener() {
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//
//                        leaguePlayerChooseDocBeanArrayList.get(position).setTitle(document1ET.getText().toString().trim());
//                        leaguePlayerChooseDocBeanArrayList.get(position).setEditBoolTitle(true);
//                        notifyDataSetChanged();
//                        return true;
//                    }
//                    return false;
//                }
//            });

            document1ET.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        leaguePlayerChooseDocBean.setTitle(document1ET.getText().toString().trim());
                        leaguePlayerChooseDocBean.setEditBoolTitle(true);
                        System.out.println("HERE1:: "+document1ET.getText().toString().trim());
                        try {
                            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            if (inputMethodManager != null && getCurrentFocus() != null) {
                                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });

            return convertView;
        }


    }

    private void showSDKchooseDialog(final TextView textView, final String title, final ArrayList<LeagueAddPlayerTeamBean> arrayList) {

        final Dialog dialog = new Dialog(LeaguePlayerEditScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.league_team_dialog);
        dialog.setCancelable(true);

        TextView titleDialog = dialog.findViewById(R.id.titleDialog);
        ListView listViewDialog = dialog.findViewById(R.id.listViewDialog);

        titleDialog.setText(title);

        listViewDialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                textView.setText(arrayList.get(i).getName());

                if(title.equalsIgnoreCase("Choose Club")){
                    clubIdStr = arrayList.get(i).getId();
                }
                dialog.dismiss();
            }
        });

        LeagueSelectTeamFieldsDataAdapter patientAlertAdapter = new LeagueSelectTeamFieldsDataAdapter(LeaguePlayerEditScreen.this, arrayList);
        listViewDialog.setAdapter(patientAlertAdapter);

        dialog.show();

    }
}
