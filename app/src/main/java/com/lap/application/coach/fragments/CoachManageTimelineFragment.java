package com.lap.application.coach.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.PostBean;
import com.lap.application.beans.PostBeanMultiplImages;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.CoachCreatePostScreen;
import com.lap.application.coach.adapters.CoachPostsAdapter;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.GetWebServiceWithHeadersAsync;
import com.lap.application.webservices.IWebServiceCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CoachManageTimelineFragment extends Fragment implements IWebServiceCallback{

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Button createNewPost;
    ListView postsListView;

    private final String GET_POSTS_LISTING = "GET_POSTS_LISTING";

    ArrayList<PostBean> postsList = new ArrayList<>();
    CoachPostsAdapter coachPostsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        coachPostsAdapter = new CoachPostsAdapter(getActivity(), postsList, CoachManageTimelineFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.coach_fragment_manage_timeline, container, false);

        createNewPost = (Button) view.findViewById(R.id.createNewPost);
        postsListView = (ListView) view.findViewById(R.id.postsListView);
        postsListView.setAdapter(coachPostsAdapter);

        createNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createPost = new Intent(getActivity(), CoachCreatePostScreen.class);
                startActivity(createPost);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPostsListing();
    }

    public void getPostsListing(){
        if(Utilities.isNetworkAvailable(getActivity())) {

            String webServiceUrl = Utilities.BASE_URL + "user_posts/status_list_new?offset=0";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            GetWebServiceWithHeadersAsync getWebServiceWithHeadersAsync = new GetWebServiceWithHeadersAsync(getActivity(), GET_POSTS_LISTING, CoachManageTimelineFragment.this, headers);
            getWebServiceWithHeadersAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(getActivity(), R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_POSTS_LISTING:

                postsList.clear();

                if(response == null) {
                    Toast.makeText(getActivity(), "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        JSONArray dataArray = responseObject.getJSONArray("data");
                        PostBean postBean;
                        for(int i=0;i<dataArray.length();i++) {
                            JSONObject postObject = dataArray.getJSONObject(i);
                            postBean = new PostBean();

                            postBean.setPostId(postObject.getString("post_id"));
                            postBean.setTitle(postObject.getString("title"));
                            postBean.setPstatus(postObject.getString("pstatus"));
                            postBean.setPostedDate(postObject.getString("posted_date"));
                           // postBean.setFileName(postObject.getString("file_name"));
                            //postBean.setFilePath(postObject.getString("file_path"));
                           // postBean.setFileType(postObject.getString("file_type"));
                            postBean.setPostedBy(postObject.getString("posted_by"));
                            postBean.setPostedOn(postObject.getString("posted_on"));
                            postBean.setStatusLabel(postObject.getString("status_label"));
                            postBean.setVideoThumb(postObject.getString("video_thumb"));

                            JSONArray multipleJsonArray = postObject.getJSONArray("multipleFilesData");
                            ArrayList<PostBeanMultiplImages> postBeanMultiplImagesArrayList = new ArrayList<>();
                            if(multipleJsonArray.length()==0){
                                PostBeanMultiplImages postBeanMultiplImages = new PostBeanMultiplImages();
                                postBeanMultiplImages.setFileName("");
                                postBeanMultiplImages.setFilePath("");
                                postBeanMultiplImages.setFileType("");
                                postBeanMultiplImagesArrayList.add(postBeanMultiplImages);
                            }else{
                                for(int j=0; j<multipleJsonArray.length(); j++){
                                    JSONObject jsonObject = multipleJsonArray.getJSONObject(j);
                                    PostBeanMultiplImages postBeanMultiplImages = new PostBeanMultiplImages();
                                    postBeanMultiplImages.setFileName(jsonObject.getString("file_name"));
                                    postBeanMultiplImages.setFilePath(jsonObject.getString("file_path"));
                                    postBeanMultiplImages.setFileType(jsonObject.getString("file_type"));
                                    postBeanMultiplImagesArrayList.add(postBeanMultiplImages);
                                }
                            }

                            postBean.setPostBeanMultiplImagesArrayList(postBeanMultiplImagesArrayList);

                            postsList.add(postBean);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                coachPostsAdapter.notifyDataSetChanged();
                break;
        }
    }

}