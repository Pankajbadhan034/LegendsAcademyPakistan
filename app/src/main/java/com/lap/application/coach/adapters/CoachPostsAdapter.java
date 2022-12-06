package com.lap.application.coach.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.ZoomImagesActivity;
import com.lap.application.beans.PostBean;
import com.lap.application.beans.UserBean;
import com.lap.application.coach.fragments.CoachManageTimelineFragment;
import com.lap.application.parent.ParentViewVideoInFullScreen;
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

public class CoachPostsAdapter extends BaseAdapter implements IWebServiceCallback{

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;

    Context context;
    ArrayList<PostBean> postsList;
    CoachManageTimelineFragment coachManageTimelineFragment;
    LayoutInflater layoutInflater;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions roundOptions;
    DisplayImageOptions options;

    private final String APPROVE_POST = "APPROVE_POST";
    private final String DELETE_POST = "DELETE_POST";

    public CoachPostsAdapter(Context context, ArrayList<PostBean> postsList, CoachManageTimelineFragment coachManageTimelineFragment) {
        this.context = context;
        this.postsList = postsList;
        this.coachManageTimelineFragment = coachManageTimelineFragment;
        layoutInflater = LayoutInflater.from(context);

        sharedPreferences = context.getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if(jsonLoggedInUser != null) {
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

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public int getCount() {
        return postsList.size();
    }

    @Override
    public Object getItem(int position) {
        return postsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.coach_adapter_post_item, null);

        LinearLayout mainLinearLayout = (LinearLayout) convertView.findViewById(R.id.mainLinearLayout);
        ImageView profilePhoto = (ImageView) convertView.findViewById(R.id.profilePhoto);
        TextView postedBy = (TextView) convertView.findViewById(R.id.postedBy);
        TextView postedOn = (TextView) convertView.findViewById(R.id.postedOn);
        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView status = (TextView) convertView.findViewById(R.id.status);
        ImageView approvePost = (ImageView) convertView.findViewById(R.id.approvePost);
        ImageView deletePost = (ImageView) convertView.findViewById(R.id.deletePost);
        ImageView postImage = (ImageView) convertView.findViewById(R.id.postImage);
        ImageView videoThumb = (ImageView) convertView.findViewById(R.id.videoThumb);

        final PostBean postBean = postsList.get(position);

        imageLoader.displayImage(postBean.getFilePath(), profilePhoto, roundOptions);

        postedBy.setText("Posted by: "+postBean.getPostedBy());
        postedOn.setText("Posted on: "+postBean.getPostedOn());
        date.setText(postBean.getPostedDate());
        status.setText(Html.fromHtml(postBean.getTitle()));

//        imageLoader.displayImage(postBean.getFilePath(), postImage, options);
        imageLoader.displayImage(postBean.getPostBeanMultiplImagesArrayList().get(0).getFilePath(), postImage, options);
        imageLoader.displayImage(postBean.getVideoThumb(), videoThumb, options);

        postImage.setVisibility(View.GONE);
        videoThumb.setVisibility(View.GONE);

        if(postBean.getPostBeanMultiplImagesArrayList().get(0).getFileType().contains("image")){
            postImage.setVisibility(View.VISIBLE);
        }

        if(postBean.getPostBeanMultiplImagesArrayList().get(0).getFileType().contains("video")){
            videoThumb.setVisibility(View.VISIBLE);
        }

//        if(postBean.getFileType().contains("image")){
//            postImage.setVisibility(View.VISIBLE);
//        }
//
//        if(postBean.getFileType().contains("video")){
//            videoThumb.setVisibility(View.VISIBLE);
//        }

        if(postBean.getPstatus().equalsIgnoreCase("0")) {
            approvePost.setVisibility(View.VISIBLE);
        } else {
            approvePost.setVisibility(View.INVISIBLE);
        }

        if(position % 2 == 0) {
            mainLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.darkBlue));
            postedBy.setTextColor(context.getResources().getColor(R.color.white));
            postedOn.setTextColor(context.getResources().getColor(R.color.white));
            date.setTextColor(context.getResources().getColor(R.color.white));
            status.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            mainLinearLayout.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            postedBy.setTextColor(context.getResources().getColor(R.color.black));
            postedOn.setTextColor(context.getResources().getColor(R.color.black));
            date.setTextColor(context.getResources().getColor(R.color.black));
            status.setTextColor(context.getResources().getColor(R.color.black));
        }

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent viewImageInFullScreen = new Intent(context, ParentViewImageInFullScreen.class);
////                viewImageInFullScreen.putExtra("imageUrl", postBean.getFilePath());
//                viewImageInFullScreen.putExtra("imageUrl", postBean.getPostBeanMultiplImagesArrayList().get(0).getFilePath());
//                context.startActivity(viewImageInFullScreen);

                Intent viewImageInFullScreen = new Intent(context, ZoomImagesActivity.class);
                viewImageInFullScreen.putExtra("images", postBean.getPostBeanMultiplImagesArrayList());
                context.startActivity(viewImageInFullScreen);
            }
        });

        videoThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewImageInFullScreen = new Intent(context, ParentViewVideoInFullScreen.class);
//                viewImageInFullScreen.putExtra("videoUrl", postBean.getFilePath());
                viewImageInFullScreen.putExtra("videoUrl", postBean.getPostBeanMultiplImagesArrayList().get(0).getFilePath());
                context.startActivity(viewImageInFullScreen);
            }
        });

        approvePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.isNetworkAvailable(context)) {

                    List<NameValuePair> nameValuePairList = new ArrayList<>();
                    nameValuePairList.add(new BasicNameValuePair("post_id", postBean.getPostId()));
                    nameValuePairList.add(new BasicNameValuePair("post_status", "1"));

                    String webServiceUrl = Utilities.BASE_URL + "user_posts/change_status";

                    ArrayList<String> headers = new ArrayList<>();
                    headers.add("X-access-uid:"+loggedInUser.getId());
                    headers.add("X-access-token:"+loggedInUser.getToken());

                    PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(context, nameValuePairList, APPROVE_POST, CoachPostsAdapter.this, headers);
                    postWebServiceAsync.execute(webServiceUrl);

                } else {
                    Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.isNetworkAvailable(context)) {

                    final Dialog dialog = new Dialog(context);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.parent_dialog_delete_post);

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

                            if(Utilities.isNetworkAvailable(context)) {

                                List<NameValuePair> nameValuePairList = new ArrayList<>();
                                nameValuePairList.add(new BasicNameValuePair("post_id", postBean.getPostId()));
                                nameValuePairList.add(new BasicNameValuePair("post_status", "-1"));

                                String webServiceUrl = Utilities.BASE_URL + "user_posts/change_status";

                                ArrayList<String> headers = new ArrayList<>();
                                headers.add("X-access-uid:"+loggedInUser.getId());
                                headers.add("X-access-token:"+loggedInUser.getToken());

                                PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(context, nameValuePairList, DELETE_POST, CoachPostsAdapter.this, headers);
                                postWebServiceAsync.execute(webServiceUrl);

                            } else {
                                Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();

                } else {
                    Toast.makeText(context, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case APPROVE_POST:

                if(response == null) {
                    Toast.makeText(context, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            coachManageTimelineFragment.getPostsListing();
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case DELETE_POST:

                if(response == null) {
                    Toast.makeText(context, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            coachManageTimelineFragment.getPostsListing();
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