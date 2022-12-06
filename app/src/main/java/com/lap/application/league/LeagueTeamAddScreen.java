package com.lap.application.league;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.LeagueAddPlayerTeamBean;
import com.lap.application.beans.UserBean;
import com.lap.application.league.adapters.LeagueSelectTeamFieldsDataAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LeagueTeamAddScreen extends AppCompatActivity implements IWebServiceCallback {
    private final String TEAM_DATA = "TEAM_DATA";
    ArrayList<LeagueAddPlayerTeamBean>leagueList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>seasonList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>groupList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>clubList = new ArrayList<>();
    ArrayList<LeagueAddPlayerTeamBean>categoryList = new ArrayList<>();

    ImageView backButton;
    TextView title;
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    String academy_currency;

    TextView chooseDate;
    GridView selectedDatesGridView;
    ArrayList<LeagueAddPlayerTeamBean> leagueListSelected = new ArrayList<>();

    TextView chooseSeason;
    GridView selectedSeasonGridView;
    ArrayList<LeagueAddPlayerTeamBean> seasonListSelected = new ArrayList<>();

    String leagueSelected="";
    String seasonSelected="";

    TextView chooseClub;
    TextView chooseCategory;
    TextView chooseGroup;

    TextView browse;
    private Uri fileUri = null;
    public static final int MEDIA_TYPE_IMAGE = 2;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CHOOSE_IMAGE_FROM_GALLERY = 1;
    Uri selectedUri = null;
    String selectedImagePath;
    ImageView thumbnail;

    TextView submit;
    TextView teamName;
    TextView description;
    RadioButton r1;
    RadioButton r2;

    String teamNameStr;
    String descriptionStr;
    String teamType="1";
    String chooseClubStr;
    String chooseCategoryStr;
    String chooseGroupStr;
    String clubIdStr, categoryIdStr, groupIdStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.league_team_add_activity);
        backButton = (ImageView) findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
        chooseDate = (TextView) findViewById(R.id.chooseDate);
        selectedDatesGridView = (GridView) findViewById(R.id.selectedDatesGridView);
        chooseSeason = (TextView) findViewById(R.id.chooseSeason);
        selectedSeasonGridView = (GridView) findViewById(R.id.selectedSeasonGridView);
        chooseClub = findViewById(R.id.chooseClub);
        chooseCategory = findViewById(R.id.chooseCategory);
        chooseGroup = findViewById(R.id.chooseGroup);
        browse = findViewById(R.id.browse);
        thumbnail = findViewById(R.id.thumbnail);
        submit = findViewById(R.id.submit);
        teamName = findViewById(R.id.teamName);
        description = findViewById(R.id.description);
        r1 = findViewById(R.id.r1);
        r2 = findViewById(R.id.r2);

        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamType = "1";
                r1.setChecked(true);
                r2.setChecked(false);
            }
        });

        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamType = "2";
                r1.setChecked(false);
                r2.setChecked(true);
            }
        });

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        academy_currency = sharedPreferences.getString("academy_currency", null);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chooseClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSDKchooseDialog(chooseClub, "Choose Club", clubList);
            }
        });

        chooseCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSDKchooseDialog(chooseCategory, "Choose Category", categoryList);
            }
        });

        chooseGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSDKchooseDialog(chooseGroup, "Choose Group", groupList);
            }
        });

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String[] items = new String[]{"Take from camera", "Select from gallery"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(LeagueTeamAddScreen.this, android.R.layout.select_dialog_item, items);
                AlertDialog.Builder builder = new AlertDialog.Builder(LeagueTeamAddScreen.this);

                builder.setTitle("Choose");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {

                        if (item == 0) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
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
                final Dialog dialog = new Dialog(LeagueTeamAddScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new DatesGridAdapterForPopup(LeagueTeamAddScreen.this));

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
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(leagueListSelected, LeagueTeamAddScreen.this);
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
                final Dialog dialog = new Dialog(LeagueTeamAddScreen.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.league_dialog_select_team);
                dialog.setCancelable(true);

                GridView datesGridView = (GridView) dialog.findViewById(R.id.datesGridView);
                Button done = (Button) dialog.findViewById(R.id.done);

                datesGridView.setAdapter(new SeasonGridAdapterForPopup(LeagueTeamAddScreen.this));

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
                            SelectedDatesAdapter selectedDatesAdapter = new SelectedDatesAdapter(seasonListSelected, LeagueTeamAddScreen.this);
                            selectedSeasonGridView.setAdapter(selectedDatesAdapter);
                            Utilities.setGridViewHeightBasedOnChildren(selectedSeasonGridView, 2);
                        }

                    }
                });

                dialog.show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teamNameStr = teamName.getText().toString().trim();
                descriptionStr = description.getText().toString().trim();
                chooseClubStr = chooseClub.getText().toString().trim();
                chooseCategoryStr = chooseCategory.getText().toString().trim();
                chooseGroupStr = chooseGroup.getText().toString().trim();

                if (teamNameStr == null || teamNameStr.isEmpty()) {
                    Toast.makeText(LeagueTeamAddScreen.this, "Please enter team name", Toast.LENGTH_SHORT).show();
                }
//                else if (descriptionStr == null || descriptionStr.isEmpty()) {
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please enter description", Toast.LENGTH_SHORT).show();
//                }else if (teamType == null || teamType.isEmpty()) {
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please enter team type", Toast.LENGTH_SHORT).show();
//                }else if(leagueListSelected.size()==0){
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please choose league", Toast.LENGTH_SHORT).show();
//                }else if(seasonListSelected.size()==0){
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please choose season", Toast.LENGTH_SHORT).show();
//                }else if (chooseClubStr == null || chooseClubStr.isEmpty() || chooseClubStr.equalsIgnoreCase("Choose Club")) {
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please choose club", Toast.LENGTH_SHORT).show();
//                }else if (chooseCategoryStr == null || chooseCategoryStr.isEmpty() || chooseCategoryStr.equalsIgnoreCase("Choose Category")) {
//                    Toast.makeText(LeagueTeamAddScreen.this, "Please choose category", Toast.LENGTH_SHORT).show();
//                }
                else if (chooseGroupStr == null || chooseGroupStr.isEmpty() || chooseGroupStr.equalsIgnoreCase("Choose Group*")) {
                    Toast.makeText(LeagueTeamAddScreen.this, "Please choose group", Toast.LENGTH_SHORT).show();
                }else{
                     leagueSelected="";
                     seasonSelected="";

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


                    new addAsync(LeagueTeamAddScreen.this).execute();
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


    private void getTeamData(){
        if(Utilities.isNetworkAvailable(LeagueTeamAddScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));

            String webServiceUrl = Utilities.BASE_URL + "join_league/parent_team_data";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(LeagueTeamAddScreen.this, nameValuePairList, TEAM_DATA, LeagueTeamAddScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(LeagueTeamAddScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case TEAM_DATA:
                if(response == null) {
                    Toast.makeText(LeagueTeamAddScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
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
                                leagueAddPlayerTeamBean.setSelected(false);
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

                            JSONArray groupJsonArray = jsonObject.getJSONArray("parent_group_list");
                            for(int i = 0; i<groupJsonArray.length(); i++){
                                JSONObject jsonObject1 = groupJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("group_id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("group_name"));
                                groupList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray clubJsonArray = jsonObject.getJSONArray("club_list");
                            for(int i = 0; i<clubJsonArray.length(); i++){
                                JSONObject jsonObject1 = clubJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                clubList.add(leagueAddPlayerTeamBean);
                            }

                            JSONArray catJsonArray = jsonObject.getJSONArray("category_list");
                            for(int i = 0; i<catJsonArray.length(); i++){
                                JSONObject jsonObject1 = catJsonArray.getJSONObject(i);
                                LeagueAddPlayerTeamBean leagueAddPlayerTeamBean = new LeagueAddPlayerTeamBean();
                                leagueAddPlayerTeamBean.setId(jsonObject1.getString("id"));
                                leagueAddPlayerTeamBean.setName(jsonObject1.getString("name"));
                                categoryList.add(leagueAddPlayerTeamBean);
                            }

                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LeagueTeamAddScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    private void showSDKchooseDialog(final TextView textView, final String title, final ArrayList<LeagueAddPlayerTeamBean> arrayList) {

        final Dialog dialog = new Dialog(LeagueTeamAddScreen.this);
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
                }else if(title.equalsIgnoreCase("Choose Category")){
                    categoryIdStr = arrayList.get(i).getId();
                }else if(title.contains("Choose Group")){
                    groupIdStr = arrayList.get(i).getId();
                }
                dialog.dismiss();
            }
        });

        LeagueSelectTeamFieldsDataAdapter patientAlertAdapter = new LeagueSelectTeamFieldsDataAdapter(LeagueTeamAddScreen.this, arrayList);
        listViewDialog.setAdapter(patientAlertAdapter);

        dialog.show();

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

            case CHOOSE_IMAGE_FROM_GALLERY:
                try {
                    selectedUri = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = LeagueTeamAddScreen.this.getContentResolver().query(selectedUri, filePathColumn, null, null, null);
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

                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;

//                        final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                        browse.setText(fileUri.getPath());

                        // showing thumbnail
                        thumbnail.setImageURI(fileUri);
                        thumbnail.setVisibility(View.VISIBLE);

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

            String uploadUrl = Utilities.BASE_URL + "join_league/parent_team_add";
            String strResponse = null;

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            HttpPost httpPost = new HttpPost(uploadUrl);

            httpPost.addHeader("X-access-uid", loggedInUser.getId());
            httpPost.addHeader("X-access-token", loggedInUser.getToken());

            httpPost.addHeader("x-access-did", Utilities.getDeviceId(context));
            httpPost.addHeader("x-access-dtype", Utilities.DEVICE_TYPE);

            try {

                StringBody academyIdBody = new StringBody(loggedInUser.getAcademiesId());
                StringBody userIdBody = new StringBody(loggedInUser.getId());
                StringBody teamNameBody = new StringBody(teamNameStr);
                StringBody teamGroupBody = new StringBody(groupIdStr);


//                System.out.println("AcademyId :: "+loggedInUser.getAcademiesId() +" User Id:: "+loggedInUser.getId()+
//                " team Name:: "+teamNameStr+ "League:: "+leagueSelected +" seasonSelected:: "+seasonSelected+ " club:: "+clubIdStr+
//                        " choose Category:: "+categoryIdStr+" Group:: "+groupIdStr + " description:: "+descriptionStr);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();

                builder.addPart("user_id", userIdBody);
                builder.addPart("academy_id", academyIdBody);
                builder.addPart("team_name", teamNameBody);
                builder.addPart("team_group", teamGroupBody);

                if (descriptionStr != null || !descriptionStr.isEmpty()) {
                    StringBody teamDescriptionBody = new StringBody(descriptionStr);
                    builder.addPart("team_description", teamDescriptionBody);
                }

                if (teamType != null || !teamType.isEmpty()) {
                    StringBody teamTypeBody = new StringBody(teamType);
                    builder.addPart("team_type", teamTypeBody);
                }

               if(leagueListSelected.size()!=0){
                   StringBody teamLeagueBody = new StringBody(leagueSelected);
                   builder.addPart("team_league", teamLeagueBody);
               }

                if(seasonListSelected.size()!=0){
                    StringBody teamSeasonBody = new StringBody(seasonSelected);
                    builder.addPart("team_season", teamSeasonBody);
                }

                if (chooseClubStr == null || chooseClubStr.isEmpty() || chooseClubStr.equals("Choose Club")){

                }else{
                    System.out.println("here::"+chooseClubStr);
                    StringBody teamClubBody = new StringBody(clubIdStr);
                    builder.addPart("team_club", teamClubBody);
                }

                if (chooseCategoryStr == null || chooseCategoryStr.isEmpty() || chooseCategoryStr.equalsIgnoreCase("Choose Category")) {

                }else{
                    StringBody teamCategoryBody = new StringBody(categoryIdStr);
                    builder.addPart("team_category", teamCategoryBody);
                }


                if (selectedUri != null) {
                    String imageMime = getMimeType(selectedUri);

                    File imageFile = new File(selectedImagePath);

                    //System.out.println("MIME_type__" + imageMime + "--Path__" + selectedImagePath + "--FileName__" + imageFile.getName());

                    if(imageFile != null){
                        FileBody imageFileBody = new FileBody(imageFile, imageFile.getName(), imageMime, "UTF-8");
                        builder.addPart("file", imageFileBody);
                    }

                } else if (fileUri != null) {

                    String fileMime = getMimeType(fileUri);

                    File file = new File(fileUri.getPath());
                    if(file != null){
                        FileBody imageFileBody = new FileBody(file, file.getName(), fileMime, "UTF-8");
                        builder.addPart("file", imageFileBody);
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

            //System.out.println("Response " + response);

            if (response == null) {
                Toast.makeText(LeagueTeamAddScreen.this, "Could not connect server", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject responseObject = new JSONObject(response);

                    boolean status = responseObject.getBoolean("status");
                    String message = responseObject.getString("message");

                    Toast.makeText(LeagueTeamAddScreen.this, message, Toast.LENGTH_SHORT).show();

                    if (status) {
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LeagueTeamAddScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
            ContentResolver cr = LeagueTeamAddScreen.this.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }
}