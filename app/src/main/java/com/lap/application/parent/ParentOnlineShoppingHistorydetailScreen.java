package com.lap.application.parent;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.OnlineShoppingHistoryBean;
import com.lap.application.beans.ParentOnlineShoppingHistoryBean;
import com.lap.application.beans.UserBean;
import com.lap.application.parent.adapters.ParentOnlineShoppingHistoryDetailAdapter;
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

import androidx.appcompat.app.AppCompatActivity;

public class ParentOnlineShoppingHistorydetailScreen extends AppCompatActivity implements IWebServiceCallback {

    String orderId;

    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;

    TextView title;
    ImageView backButton;

    TextView orderIdTV;
    ListView productList;
    TextView totalIncludingFees;
    TextView discount;
    String academy_currency;
    TextView gstAmount;
    TextView dCAmount;
    TextView orderCharges;
    TextView netAmount;
    ArrayList<ParentOnlineShoppingHistoryBean> parentOnlineShoppingHistoryBeanArrayList = new ArrayList<>();

    private final String GET_SHOPPING_PRODUCT_HISTORY_DETAIL = "GET_SHOPPING_PRODUCT_HISTORY_DETAIL";

    ArrayList<OnlineShoppingHistoryBean>subOrderArrayList = new ArrayList<>();
    ArrayList<OnlineShoppingHistoryBean>discountInlineArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_activity_online_shopping_history_detail_screen);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }
        helvetica = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(),"fonts/LinotypeOrdinarRegular.ttf");

        title = (TextView) findViewById(R.id.title);
        backButton = (ImageView) findViewById(R.id.backButton);
        orderIdTV = findViewById(R.id.orderId);
        productList = findViewById(R.id.productList);
        totalIncludingFees = findViewById(R.id.totalIncludingFees);
        discount = findViewById(R.id.discount);
        gstAmount = findViewById(R.id.gstAmount);
        dCAmount = findViewById(R.id.dCAmount);
        orderCharges = findViewById(R.id.orderCharges);
        netAmount = findViewById(R.id.netAmount);

        orderId = getIntent().getStringExtra("orderId");
        orderIdTV.setText("ORDER ID : #"+orderId);

        academy_currency = sharedPreferences.getString("academy_currency", null);

        title.setTypeface(linoType);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getOnlineStoreDetail();

    }


    private void getOnlineStoreDetail() {
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingHistorydetailScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("order_id", orderId));
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));

            String webServiceUrl = Utilities.BASE_URL + "product/booked_products_details";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingHistorydetailScreen.this, nameValuePairList, GET_SHOPPING_PRODUCT_HISTORY_DETAIL, ParentOnlineShoppingHistorydetailScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingHistorydetailScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case GET_SHOPPING_PRODUCT_HISTORY_DETAIL:
                parentOnlineShoppingHistoryBeanArrayList.clear();
                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingHistorydetailScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if (status) {

                            JSONObject object = responseObject.getJSONObject("data");
                            JSONObject object2 = object.getJSONObject("data");
                            String taxStr = object2.getString("tax");
                            String totalCostStr = object2.getString("total_cost");
                            String discount_costStr = object2.getString("discount_cost");
                            String net_amount = object2.getString("net_amount");
                            String delivery_charges = object2.getString("delivery_charges");

                            JSONArray jsonArray = object2.getJSONArray("sub_orders");
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                ParentOnlineShoppingHistoryBean parentOnlineShoppingHistoryBean = new ParentOnlineShoppingHistoryBean();
                                parentOnlineShoppingHistoryBean.setImage_url(jsonObject.getString("image_url"));
                                parentOnlineShoppingHistoryBean.setImage(jsonObject.getString("image"));
                                parentOnlineShoppingHistoryBean.setProduct_name(jsonObject.getString("product_name"));
                                parentOnlineShoppingHistoryBean.setAttributes(jsonObject.getString("attributes"));
                                parentOnlineShoppingHistoryBean.setNet_cost(jsonObject.getString("net_cost"));
                                parentOnlineShoppingHistoryBean.setQuantity(jsonObject.getString("quantity"));
                                parentOnlineShoppingHistoryBean.setTotal_cost(jsonObject.getString("total_cost"));
                                parentOnlineShoppingHistoryBeanArrayList.add(parentOnlineShoppingHistoryBean);
                            }

                            totalIncludingFees.setText(totalCostStr+" "+academy_currency);
                            discount.setText(discount_costStr+" "+academy_currency);
                            gstAmount.setText(taxStr+"%");
                            dCAmount.setText(delivery_charges+" "+academy_currency);
                            orderCharges.setText(net_amount+" "+academy_currency);
                            netAmount.setText(net_amount+" "+academy_currency);

                            ParentOnlineShoppingHistoryDetailAdapter parentOnlineShoppingProductStoreAdapter = new ParentOnlineShoppingHistoryDetailAdapter(ParentOnlineShoppingHistorydetailScreen.this, parentOnlineShoppingHistoryBeanArrayList, academy_currency);
                            productList.setAdapter(parentOnlineShoppingProductStoreAdapter);
                            Utilities.setListViewHeightBasedOnChildren(productList);

                        } else {
                            Toast.makeText(ParentOnlineShoppingHistorydetailScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingHistorydetailScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}