package com.lap.application.parent;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.lap.application.R;
import com.lap.application.beans.UserBean;
import com.lap.application.hitPay.HitPayExample;
import com.lap.application.hitPay.HitPayPostWebServiceWithHeadersAsync;
import com.lap.application.parent.paymentGatewayUtilities.AvenuesParams;
import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;
import com.lap.application.webservices.PostWebServiceWithHeadersAsync;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;

public class ParentOnlineShoppingBillingShippingAddressScreen extends AppCompatActivity implements IWebServiceCallback {
    private final String TERM_COND_SETTING = "TERM_COND_SETTING";
    private final String UPDATE_TERM_COND_SETTING = "UPDATE_TERM_COND_SETTING";
    boolean statusGatewayScreen;

    String webviewHitPayurl;
    private final String HITPAY_STATUS = "HITPAY_STATUS";
    private final String HITPAY_WEB_SERVICE = "HITPAY_WEB_SERVICE";
    private final String HITPAY_ID_STORE_SERVICE = "HITPAY_ID_STORE_SERVICE";

    String academy_currency;
    String net_Amount,ordersId;
    private final String SUBMIT_API = "SUBMIT_API";
    SharedPreferences sharedPreferences;
    UserBean loggedInUser;
    Typeface helvetica;
    Typeface linoType;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    ImageView backButton;
    TextView title;
    TextInputLayout nameTextInputLayout;
    EditText name;
    TextInputLayout emailTextInputLayout;
    EditText email;
    TextInputLayout phoneTextInputLayout;
    EditText phone;
    TextInputLayout addressTextInputLayout;
    EditText address;
    TextInputLayout cityTextInputLayout;
    EditText city;
    TextInputLayout stateTextInputLayout;
    EditText state;
    TextInputLayout countryTextInputLayout;
    EditText country;
    TextInputLayout zipTextInputLayout;
    EditText zip;
    TextInputLayout nameTextInputLayoutShip;
    EditText nameShip;
    TextInputLayout emailTextInputLayoutShip;
    EditText emailShip;
    TextInputLayout phoneTextInputLayoutShip;
    EditText phoneShip;
    TextInputLayout addressTextInputLayoutShip;
    EditText addressShip;
    TextInputLayout cityTextInputLayoutShip;
    EditText cityShip;
    TextInputLayout stateTextInputLayoutShip;
    EditText stateShip;
    TextInputLayout countryTextInputLayoutShip;
    EditText countryShip;
    TextInputLayout zipTextInputLayoutShip;
    EditText zipShip;
    CheckBox noNeedToShip;
    CheckBox billShipSame;
    LinearLayout shipLinear;
    LinearLayout billLinear;
    Button makePayment;
    String nameStr, emailStr, phoneStr, addressStr, cityStr, stateStr, countryStr, zipStr;
    String nameShipStr, emailShipStr, phoneShipStr, addressShipStr, cityShipStr, stateShipStr, countryShipStr, zipShipStr;
    String amount, gst, product_data, module_type, promo_code, delCharges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_online_shopping_billing_shipping_address_activity);
        backButton = findViewById(R.id.backButton);
        title = (TextView) findViewById(R.id.title);
        nameTextInputLayout = (TextInputLayout) findViewById(R.id.nameTextInputLayout);
        name = (EditText) findViewById(R.id.name);
        emailTextInputLayout = (TextInputLayout) findViewById(R.id.emailTextInputLayout);
        email = (EditText) findViewById(R.id.email);
        phoneTextInputLayout = (TextInputLayout) findViewById(R.id.phoneTextInputLayout);
        phone = (EditText) findViewById(R.id.phone);
        addressTextInputLayout = (TextInputLayout) findViewById(R.id.addressTextInputLayout);
        address = (EditText) findViewById(R.id.address);
        cityTextInputLayout = (TextInputLayout) findViewById(R.id.cityTextInputLayout);
        city = (EditText) findViewById(R.id.city);
        stateTextInputLayout = (TextInputLayout) findViewById(R.id.stateTextInputLayout);
        state = (EditText) findViewById(R.id.state);
        countryTextInputLayout = (TextInputLayout) findViewById(R.id.countryTextInputLayout);
        country = (EditText) findViewById(R.id.country);
        zipTextInputLayout = (TextInputLayout) findViewById(R.id.zipTextInputLayout);
        zip = (EditText) findViewById(R.id.zip);
        nameTextInputLayoutShip = (TextInputLayout) findViewById(R.id.nameTextInputLayoutShip);
        nameShip = (EditText) findViewById(R.id.nameShip);
        emailTextInputLayoutShip = (TextInputLayout) findViewById(R.id.emailTextInputLayoutShip);
        emailShip = (EditText) findViewById(R.id.emailShip);
        phoneTextInputLayoutShip = (TextInputLayout) findViewById(R.id.phoneTextInputLayoutShip);
        phoneShip = (EditText) findViewById(R.id.phoneShip);
        addressTextInputLayoutShip = (TextInputLayout) findViewById(R.id.addressTextInputLayoutShip);
        addressShip = (EditText) findViewById(R.id.addressShip);
        cityTextInputLayoutShip = (TextInputLayout) findViewById(R.id.cityTextInputLayoutShip);
        cityShip = (EditText) findViewById(R.id.cityShip);
        stateTextInputLayoutShip = (TextInputLayout) findViewById(R.id.stateTextInputLayoutShip);
        stateShip = (EditText) findViewById(R.id.stateShip);
        countryTextInputLayoutShip = (TextInputLayout) findViewById(R.id.countryTextInputLayoutShip);
        countryShip = (EditText) findViewById(R.id.countryShip);
        zipTextInputLayoutShip = (TextInputLayout) findViewById(R.id.zipTextInputLayoutShip);
        zipShip = (EditText) findViewById(R.id.zipShip);
        noNeedToShip = findViewById(R.id.noNeedToShip);
        billShipSame = findViewById(R.id.billShipSame);
        shipLinear = findViewById(R.id.shipLinear);
        billLinear = findViewById(R.id.billLinear);
        makePayment = findViewById(R.id.makePayment);

        sharedPreferences = getSharedPreferences(Utilities.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonLoggedInUser = sharedPreferences.getString("loggedInUser", null);
        if (jsonLoggedInUser != null) {
            loggedInUser = gson.fromJson(jsonLoggedInUser, UserBean.class);
        }

        helvetica = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        linoType = Typeface.createFromAsset(getAssets(), "fonts/LinotypeOrdinarRegular.ttf");

        imageLoader.init(ImageLoaderConfiguration.createDefault(ParentOnlineShoppingBillingShippingAddressScreen.this));
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        amount = getIntent().getStringExtra("amount");
        gst = getIntent().getStringExtra("gst");
        product_data = getIntent().getStringExtra("product_data");
        module_type = getIntent().getStringExtra("module_type");
        promo_code = getIntent().getStringExtra("promo_code");
        delCharges = getIntent().getStringExtra("delivery_charges");

         academy_currency = sharedPreferences.getString("academy_currency", null);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        noNeedToShip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noNeedToShip.isChecked()){
                    billLinear.setVisibility(View.GONE);
                    shipLinear.setVisibility(View.GONE);
                }else{
                    billLinear.setVisibility(View.VISIBLE);
                    shipLinear.setVisibility(View.VISIBLE);
                }
            }
        });

        billShipSame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(billShipSame.isChecked()){
                    shipLinear.setVisibility(View.GONE);

                }else{
                    shipLinear.setVisibility(View.VISIBLE);
                }
            }
        });

        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(noNeedToShip.isChecked()){
                    submitShipDataAPI();
                }else{
                    nameStr = name.getText().toString().trim();
                    emailStr = email.getText().toString().trim();
                    phoneStr = phone.getText().toString().trim();
                    addressStr = address.getText().toString().trim();
                    cityStr = city.getText().toString().trim();
                    stateStr = state.getText().toString().trim();
                    countryStr = country.getText().toString().trim();
                    zipStr = zip.getText().toString().trim();

                    Pattern pattern = Pattern.compile(Utilities.EMAIL_PATTERN);
                    Matcher matcher = pattern.matcher(emailStr);

                    if(nameStr == null || nameStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Name in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (emailStr == null || emailStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Email in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (!matcher.matches()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter a valid Email in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (phoneStr == null || phoneStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Phone in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (addressStr == null || addressStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Address in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (cityStr == null || cityStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter City in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (stateStr == null || stateStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter State in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (countryStr == null || countryStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Country in Billing Form", Toast.LENGTH_SHORT).show();
                    } else if (zipStr == null || zipStr.isEmpty()) {
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Zip Code in Billing Form", Toast.LENGTH_SHORT).show();
                    }else{
                        if(billShipSame.isChecked()){

                            submitShipDataAPI();

                        }else{

                            nameShipStr = nameShip.getText().toString().trim();
                            emailShipStr = emailShip.getText().toString().trim();
                            phoneShipStr = phoneShip.getText().toString().trim();
                            addressShipStr = addressShip.getText().toString().trim();
                            cityShipStr = cityShip.getText().toString().trim();
                            stateShipStr = stateShip.getText().toString().trim();
                            countryShipStr = countryShip.getText().toString().trim();
                            zipShipStr = zipShip.getText().toString().trim();

                            Pattern patterns = Pattern.compile(Utilities.EMAIL_PATTERN);
                            Matcher matchers = patterns.matcher(emailShipStr);

                            if(nameShipStr == null || nameShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Name in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (emailShipStr == null || emailShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Email in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (!matchers.matches()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter a valid Email in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (phoneShipStr == null || phoneShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Phone in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (addressShipStr == null || addressShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Address in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (cityShipStr == null || cityShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter City in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (stateShipStr == null || stateShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter State in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (countryShipStr == null || countryShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Country in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else if (zipShipStr == null || zipShipStr.isEmpty()) {
                                Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Please enter Zip Code in Shipping Form", Toast.LENGTH_SHORT).show();
                            } else{
                                submitShipDataAPI();
                            }
                        }
                    }
                }



            }
        });

        changeFonts();

    }

    private void changeFonts() {
        title.setTypeface(linoType);
    }

    public void submitShipDataAPI(){
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            nameValuePairList.add(new BasicNameValuePair("user_id", loggedInUser.getId()));
            nameValuePairList.add(new BasicNameValuePair("amount", amount));
            nameValuePairList.add(new BasicNameValuePair("gst", gst));
            nameValuePairList.add(new BasicNameValuePair("product_data", product_data));
            nameValuePairList.add(new BasicNameValuePair("delivery_charges", delCharges));

            if(!module_type.equalsIgnoreCase("")){
                nameValuePairList.add(new BasicNameValuePair("module_type", module_type));
                nameValuePairList.add(new BasicNameValuePair("promo_code", promo_code));
            }

            if(noNeedToShip.isChecked()){
                nameValuePairList.add(new BasicNameValuePair("no_shipping", "1"));
            }else{
                if(billShipSame.isChecked()){
                    nameValuePairList.add(new BasicNameValuePair("same_address", "1"));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_name", nameStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_email", emailStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_phone", phoneStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_address", addressStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_city", cityStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_state", stateStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_country", countryStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_zip", zipStr));
                }else{
                    nameValuePairList.add(new BasicNameValuePair("bill_to_name", nameStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_email", emailStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_phone", phoneStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_address", addressStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_city", cityStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_state", stateStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_country", countryStr));
                    nameValuePairList.add(new BasicNameValuePair("bill_to_zip", zipStr));

                    nameValuePairList.add(new BasicNameValuePair("ship_to_name", nameShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_email", emailShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_phone", phoneShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_address", addressShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_city", cityShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_state", stateShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_country", countryShipStr));
                    nameValuePairList.add(new BasicNameValuePair("ship_to_zip", zipShipStr));
                }
            }

            String webServiceUrl = Utilities.BASE_URL + "product/order_request";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:" + loggedInUser.getId());
            headers.add("X-access-token:" + loggedInUser.getToken());


            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, SUBMIT_API, ParentOnlineShoppingBillingShippingAddressScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onWebServiceResponse(String response, String tag) {
        switch (tag) {
            case SUBMIT_API:

                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                             net_Amount = responseObject.getString("net_amount");
                             ordersId = responseObject.getString("orders_id");

                            if(net_Amount.equals(".00") || (net_Amount.equals("0.0") || net_Amount.equals("0.00") || net_Amount.equals("0") || net_Amount.equals(".0"))){
                                Intent mainScreen = new Intent(ParentOnlineShoppingBillingShippingAddressScreen.this, ParentNetPaymentZeroScreen.class);
                                mainScreen.putExtra("orderID", ordersId);
                                startActivity(mainScreen);

                            }else {

                                hitPaycheck();

                            }

                        } else {
                            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            case HITPAY_STATUS:

                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        System.out.println("REsponseHERE::"+response);
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");
                        termSettingAPI();
//                        if(status) {
//
//                            JSONObject hitpayDetail = responseObject.getJSONObject("hitpay_detail");
//                            //String api_key = hitpayDetail.getString("api_key");
//                            hitPaysAPI(Utilities.HITPAY_ONLINE_SHOPPING_KEY);
//
//                        }else{
//
//                            Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
//                            intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
//                            intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
//                            intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
//                            intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
//                            intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
//                            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
//                            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
//                            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
//                            startActivity(intent);
//
//                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case HITPAY_WEB_SERVICE:
                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try{
                        JSONObject jsonObject = new JSONObject(response);

                        webviewHitPayurl = jsonObject.getString("url");
                        String redirect_url = jsonObject.getString("redirect_url");
                        String id = jsonObject.getString("id");
                        System.out.println("id:: "+id+" urlHER:: "+webviewHitPayurl+"::redirectURL:: "+redirect_url);
                        hitPayIdStoreAPI(id);


                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }

                break;

            case HITPAY_ID_STORE_SERVICE:
                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            Intent obj = new Intent(ParentOnlineShoppingBillingShippingAddressScreen.this, HitPayExample.class);
                            obj.putExtra("url", webviewHitPayurl);
                            startActivity(obj);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            JSONObject jsonObject = responseObject.getJSONObject("data");
                            String sessionDescription = jsonObject.getString("session_url");
                            String facility_description = jsonObject.getString("facility_url");
                            String camp_description = jsonObject.getString("camp_url");
                            String midweek_session_description = jsonObject.getString("midweek_session_url");
                            String product_description = jsonObject.getString("product_url");
                            String league_description = jsonObject.getString("league_url");

                            String session_state = jsonObject.getString("session_state");
                            String facility_state = jsonObject.getString("facility_state");
                            String camp_state = jsonObject.getString("camp_state");
                            String midweek_session_state = jsonObject.getString("midweek_session_state");
                            String product_state = jsonObject.getString("product_state");
                            String league_state = jsonObject.getString("league_state");

                            if(product_state.equalsIgnoreCase("1")){
                                dialogBoxShow(product_description);
                            }else{
                                gatewayScreenIntent(statusGatewayScreen);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;

            case UPDATE_TERM_COND_SETTING:

                if (response == null) {
                    Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, "Could not connect to server", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject responseObject = new JSONObject(response);

                        boolean status = responseObject.getBoolean("status");
                        String message = responseObject.getString("message");

                        if(status) {
                            gatewayScreenIntent(statusGatewayScreen);
                        }else{
                            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }

        }

    public void hitPaycheck(){
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()+""));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_status";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, HITPAY_STATUS, ParentOnlineShoppingBillingShippingAddressScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void hitPaysAPI(String api_key){
        if (Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {
            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("email", loggedInUser.getEmail()));
            nameValuePairList.add(new BasicNameValuePair("redirect_url", Utilities.BASE_URL+"payments/hitpay_response"));
            nameValuePairList.add(new BasicNameValuePair("webhook", Utilities.BASE_URL+"payments/hitpay_webhook"));
            nameValuePairList.add(new BasicNameValuePair("amount", net_Amount));
            nameValuePairList.add(new BasicNameValuePair("currency", academy_currency));
            nameValuePairList.add(new BasicNameValuePair("reference_number", ordersId +"- (Android)"));


            String webServiceUrl = ""+Utilities.HITPAY_API;

            HitPayPostWebServiceWithHeadersAsync postWebServiceAsync = new HitPayPostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, HITPAY_WEB_SERVICE, ParentOnlineShoppingBillingShippingAddressScreen.this, api_key);
            postWebServiceAsync.execute(webServiceUrl);
        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_LONG).show();
        }
    }

    public void hitPayIdStoreAPI(String id){
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {

            List<NameValuePair> nameValuePairList = new ArrayList<>();
            nameValuePairList.add(new BasicNameValuePair("hitpay_id", id));
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            nameValuePairList.add(new BasicNameValuePair("hitpay_salt", Utilities.SALT_HITPAY_ONLINE_SHOPPING_KEY));

            String webServiceUrl = Utilities.BASE_URL + "payments/hitpay_refer_id";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, HITPAY_ID_STORE_SERVICE, ParentOnlineShoppingBillingShippingAddressScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void termSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("academy_id", loggedInUser.getAcademiesId()));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/get_term_condition_settings";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, TERM_COND_SETTING, ParentOnlineShoppingBillingShippingAddressScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateTermSettingAPI(){
        if(Utilities.isNetworkAvailable(ParentOnlineShoppingBillingShippingAddressScreen.this)) {


            List<NameValuePair> nameValuePairList = new ArrayList<>();
            System.out.println("idHERE::"+loggedInUser.getAcademiesId());
            nameValuePairList.add(new BasicNameValuePair("order_id", ordersId));
            String webServiceUrl = Utilities.BASE_URL + "bookings_term_settings/update_order_term_condition";

            ArrayList<String> headers = new ArrayList<>();
            headers.add("X-access-uid:"+loggedInUser.getId());
            headers.add("X-access-token:"+loggedInUser.getToken());

            PostWebServiceWithHeadersAsync postWebServiceAsync = new PostWebServiceWithHeadersAsync(ParentOnlineShoppingBillingShippingAddressScreen.this, nameValuePairList, UPDATE_TERM_COND_SETTING, ParentOnlineShoppingBillingShippingAddressScreen.this, headers);
            postWebServiceAsync.execute(webServiceUrl);

        } else {
            Toast.makeText(ParentOnlineShoppingBillingShippingAddressScreen.this, R.string.internet_not_available, Toast.LENGTH_SHORT).show();
        }
    }

    public void dialogBoxShow(String text){

        final Dialog dialogAddNotes = new Dialog(ParentOnlineShoppingBillingShippingAddressScreen.this);
        dialogAddNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddNotes.setContentView(R.layout.dialog_box_term_conditions);
        dialogAddNotes.setCancelable(true);

        Button submit = dialogAddNotes.findViewById(R.id.submit);
        Button cancel = dialogAddNotes.findViewById(R.id.cancel);
//           TextView termText = dialogAddNotes.findViewById(R.id.termText);
//        termText.setText(Html.fromHtml(text));
//        termText.setMovementMethod(LinkMovementMethod.getInstance());

        WebView termText = dialogAddNotes.findViewById(R.id.termText);

//        termText.getSettings().setLoadWithOverviewMode(true);
//        termText.getSettings().setUseWideViewPort(true);

        termText.getSettings().setUseWideViewPort(true);
        termText.getSettings().setLoadWithOverviewMode(true);

        termText.getSettings().setSupportZoom(true);
        termText.getSettings().setBuiltInZoomControls(true);
        termText.getSettings().setDisplayZoomControls(false);

        System.out.println("urlHERE::"+text);
        termText.loadUrl(text);
        termText.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //System.out.println("Should override " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                //System.out.println("page finished  " + url);
                super.onPageFinished(view, url);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNotes.dismiss();
                updateTermSettingAPI();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddNotes.dismiss();
            }
        });

        dialogAddNotes.show();

    }

    public void gatewayScreenIntent(boolean status){
        if(status) {

          //  JSONObject hitpayDetail = responseObject.getJSONObject("hitpay_detail");
            //String api_key = hitpayDetail.getString("api_key");
            hitPaysAPI(Utilities.HITPAY_ONLINE_SHOPPING_KEY);

        }else{

            Intent intent = new Intent(this, ParentPaymentGatewayWebViewScreen.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, Utilities.ACCESS_CODE);
            intent.putExtra(AvenuesParams.MERCHANT_ID, Utilities.MERCHANT_ID);
            intent.putExtra(AvenuesParams.ORDER_ID, ordersId);
            intent.putExtra(AvenuesParams.CURRENCY, academy_currency);
            intent.putExtra(AvenuesParams.AMOUNT, net_Amount);
            intent.putExtra(AvenuesParams.REDIRECT_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.CANCEL_URL, Utilities.REDIRECT_URL);
            intent.putExtra(AvenuesParams.RSA_KEY_URL, Utilities.BASE_URL + "payments/get_RSA");
            startActivity(intent);

        }
    }
}