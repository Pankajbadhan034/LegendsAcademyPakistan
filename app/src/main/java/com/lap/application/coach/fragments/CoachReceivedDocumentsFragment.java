package com.lap.application.coach.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.DocumentBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.adapters.CoachReceivedDocumentsListingAdapter;
import com.lap.application.parent.ParentPreviewDocumentScreen;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CoachReceivedDocumentsFragment extends Fragment implements IWebServiceCallback{

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ListView documentsListView;

    private final String GET_DOCUMENTS = "GET_DOCUMENTS";

    ArrayList<DocumentBean> documentsList = new ArrayList<>();
    CoachReceivedDocumentsListingAdapter coachReceivedDocumentsListingAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coach_fragment_received_documents, container, false);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getActivity().getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        documentsListView = (ListView) view.findViewById(R.id.documentsListView);

        coachReceivedDocumentsListingAdapter = new CoachReceivedDocumentsListingAdapter(getActivity(), documentsList);
        documentsListView.setAdapter(coachReceivedDocumentsListingAdapter);

        documentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocumentBean clickedOnDocument = documentsList.get(position);
                Intent previewDocument = new Intent(getActivity(), ParentPreviewDocumentScreen.class);
                previewDocument.putExtra("clickedOnDocument", clickedOnDocument);
                startActivity(previewDocument);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDocumentsListing();
    }

    public void getDocumentsListing() {
        if(Utilities.isNetworkAvailable(getActivity())) {

            /*List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("users_id", loggedInUser.getId()));

            String webServiceUrl = Utilities.BASE_URL + "account/get_documents";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, GET_DOCUMENTS, ParentDocumentsListingFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);*/

            String webServiceUrl = Utilities.BASE_URL + "account/get_received_documents";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(getActivity(), GET_DOCUMENTS, CoachReceivedDocumentsFragment.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_DOCUMENTS:

                documentsList.clear();

                if (response == null) {
                    Toast.makeText(getActivity(), "Could not connect server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");
                            DocumentBean documentBean;
                            for(int i=0;i<dataArray.length();i++) {
                                JSONObject documentObject = dataArray.getJSONObject(i);
                                documentBean = new DocumentBean();

                                documentBean.setDocumentId(documentObject.getString("id"));
                                documentBean.setType(documentObject.getString("type"));
                                documentBean.setSharedBy(documentObject.getString("shared_by"));
                                documentBean.setSharedByName(documentObject.getString("shared_by_name"));
                                documentBean.setCreatedAt(documentObject.getString("created_at"));
                                documentBean.setCreatedAtFormatted(documentObject.getString("created_at_formatted"));
                                documentBean.setTitle(documentObject.getString("title"));
                                documentBean.setComments(documentObject.getString("comments"));
                                documentBean.setFilePath(documentObject.getString("file_path"));

//                                documentBean.setState(documentObject.getString("state"));
//                                documentBean.setUserId(documentObject.getString("users_id"));
//                                documentBean.setFileURL(documentObject.getString("file_url"));
//                                documentBean.setFileName(documentObject.getString("file_name"));

                                documentsList.add(documentBean);
                            }

                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachReceivedDocumentsListingAdapter.notifyDataSetChanged();

                break;
        }
    }

}