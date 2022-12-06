package com.lap.application.coach.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.CoachViewNotesBean;
import com.lap.application.beans.UserBean;
import com.lap.application.child.adapters.ChildPostAdapterNew;
import com.lap.application.coach.CoachMidWeekPackageChildNamesAttendanceActivity;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CoachMidWeekPackageViewNotesAdapter extends BaseAdapter implements IWebServiceCallback {
    String notesStr;
    int notesDeleteIntPosition;
    int notesEditIntPosition;
    Dialog dialogAddNotes;
    private final String NOTES_DELETE = "NOTES_DELETE";
    private final String NOTES_UPDATE = "NOTES_UPDATE";
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<CoachViewNotesBean> leagueJoinLeagueBeanArrayList;
    LayoutInflater layoutInflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions roundOptions;

    public CoachMidWeekPackageViewNotesAdapter(Context context, ArrayList<CoachViewNotesBean> leagueJoinLeagueBeanArrayList) {
        this.context = context;
        this.leagueJoinLeagueBeanArrayList = leagueJoinLeagueBeanArrayList;
        this.layoutInflater = LayoutInflater.from(context);

        sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        roundOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new RoundedBitmapDisplayer(1000))
                .build();
    }

    @Override
    public int getCount() {
        return leagueJoinLeagueBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return leagueJoinLeagueBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_midwek_view_notes_adapter, null);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView notes = (TextView) convertView.findViewById(R.id.notes);
        ImageView editIcon = convertView.findViewById(R.id.editIcon);
        ImageView deleteIcon = convertView.findViewById(R.id.deleteIcon);

        final CoachViewNotesBean leagueJoinLeagueBean = leagueJoinLeagueBeanArrayList.get(position);
        date.setText(leagueJoinLeagueBean.getCreatedAt());
        notes.setText(Html.fromHtml(leagueJoinLeagueBean.getNotes()));
        notes.setLinksClickable(true);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesEditIntPosition = position;
                addNotes(leagueJoinLeagueBean.getNotes(), leagueJoinLeagueBean.getId(), leagueJoinLeagueBean.getPackageId());
            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesDeleteIntPosition = position;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                builder1.setMessage("Are you sure you want to delete this Notes?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                if (Utilities.isNetworkAvailable(context)) {
                                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                                    nameValuePairList.add(new BasicNameValuePair("id", leagueJoinLeagueBean.getId()));

                                    String webServiceUrl = Utilities.BASE_URL + "coach/remove_midweek_notes";

                                    ArrayList<String> headers = new ArrayList<>();
                                    headers.add("X-access-uid:" + loggedInUser.getId());
                                    headers.add("X-access-token:" + loggedInUser.getToken());

                                    PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(context, nameValuePairList, NOTES_DELETE, CoachMidWeekPackageViewNotesAdapter.this, headers);
                                    postWebServiceAsync.execute(webServiceUrl);
                                } else {
                                    Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
            }
        });


        return convertView;
    }

    public void addNotes(String getNotes, final String id, final String packageId){
        dialogAddNotes = new Dialog(context);
        dialogAddNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddNotes.setContentView(R.layout.coach_midweek_add_notes_dialog);
        dialogAddNotes.setCancelable(true);

        final EditText notes = dialogAddNotes.findViewById(R.id.notes);
        Button submit = dialogAddNotes.findViewById(R.id.submit);
        TextView titleDialog = dialogAddNotes.findViewById(R.id.titleDialog);
        TextView titletext = dialogAddNotes.findViewById(R.id.titletext);
        notes.setText(getNotes);

        try{
            notes.setSelection(notes.getText().length());
        }catch (Exception e){
            e.printStackTrace();
        }

        titleDialog.setText("Edit Midweek Notes");
        titletext.setText("Edit Notes");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notesStr = notes.getText().toString().trim();
                if(notesStr.isEmpty() || notesStr==null){
                    Toast.makeText(context, "Please add notes", Toast.LENGTH_SHORT).show();
                }else{
                    addNotesAPI(notesStr, id, packageId);
                }
            }
        });


        dialogAddNotes.show();
    }

    private void addNotesAPI(String notes, String id, String packageId) {
        if(Utilities.isNetworkAvailable(context)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("id", id));
            nameValuePairList.add(new BasicNameValuePair("package_id", packageId));
            nameValuePairList.add(new BasicNameValuePair("notes_text", notes));

            String webServiceUrl = Utilities.BASE_URL + "coach/edit_midweek_notes";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            System.out.println("uid::"+loggedInUser.getId()+"token::"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(context, nameValuePairList, NOTES_UPDATE, CoachMidWeekPackageViewNotesAdapter.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag){
            case NOTES_DELETE:

                if (response == null) {
                    Toast.makeText(context, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status){
                                leagueJoinLeagueBeanArrayList.remove(notesDeleteIntPosition);
                                notifyDataSetChanged();
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case NOTES_UPDATE:

                if (response == null) {
                    Toast.makeText(context, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status){
                            dialogAddNotes.dismiss();
                            leagueJoinLeagueBeanArrayList.get(notesEditIntPosition).setNotes(notesStr);
                                notifyDataSetChanged();
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

}
