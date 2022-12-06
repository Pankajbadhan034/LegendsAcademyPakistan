package com.lap.application.parent.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.CampBean;
import com.lap.application.beans.CampGalleryBean;
import com.lap.application.beans.CampLocationBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.adapters.ParentCampsListingAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentCampsListingFragment extends Fragment implements IWebServiceCallback{

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    ListView campsListView;

    private final String GET_CAMPS_LISTING = "GET_CAMPS_LISTING";

    ArrayList<CampBean> campsList = new ArrayList<>();
    ParentCampsListingAdapter parentCampsListingAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getActivity().getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        parentCampsListingAdapter = new ParentCampsListingAdapter(getActivity(), campsList);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.parent_fragment_book_camp, container, false);

        campsListView = (ListView) view.findViewById(R.id.campsListView);
        campsListView.setAdapter(parentCampsListingAdapter);

        getCampsListing();

        return view;
    }

    private void getCampsListing(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("offset", "0"));

            String webServiceUrl = Utilities.BASE_URL + "camps/list";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(getActivity(), nameValuePairList, GET_CAMPS_LISTING, ParentCampsListingFragment.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_CAMPS_LISTING:

                if (response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {

                            JSONArray dataArray = responseObject.getJSONArray("data");

                            CampBean campBean;

                            campsList.clear();
                            for(int i=0; i<dataArray.length(); i++) {

                                JSONObject campObject = dataArray.getJSONObject(i);
                                campBean = new CampBean();

                                campBean.setCampId(campObject.getString("id"));
                                campBean.setCampName(campObject.getString("camp_name"));
                                campBean.setFileTitle(campObject.getString("file_title"));
                                campBean.setFilePath(campObject.getString("file_path"));

                                JSONArray galleryArray = campObject.getJSONArray("camp_gallery");
                                ArrayList<CampGalleryBean> galleryList = new ArrayList<>();
                                CampGalleryBean galleryBean;

                                for (int j=0; j<galleryArray.length(); j++) {
                                    JSONObject galleryObject = galleryArray.getJSONObject(j);
                                    galleryBean = new CampGalleryBean();

                                    galleryBean.setGalleryId(galleryObject.getString("camp_gallery_id"));
                                    galleryBean.setFileTitle(galleryObject.getString("file_title"));
                                    galleryBean.setFilePath(galleryObject.getString("file_path"));

                                    galleryList.add(galleryBean);
                                }
                                campBean.setGalleryList(galleryList);

                                JSONArray locationsArray = campObject.getJSONArray("camp_to_locations");
                                ArrayList<CampLocationBean> locationsList = new ArrayList<>();
                                CampLocationBean locationBean;
                                for(int j=0; j<locationsArray.length(); j++) {
                                    JSONObject locationObject = locationsArray.getJSONObject(j);
                                    locationBean = new CampLocationBean();

                                    locationBean.setLocationId(locationObject.getString("locations_id"));
                                    locationBean.setLocationName(locationObject.getString("l_name"));

                                    locationsList.add(locationBean);
                                }
                                campBean.setLocationList(locationsList);

                                campsList.add(campBean);
                            }

                            parentCampsListingAdapter.notifyDataSetChanged();


                        } else {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }
}