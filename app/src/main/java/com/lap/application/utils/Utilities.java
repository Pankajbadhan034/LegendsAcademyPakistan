package com.lap.application.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lap.application.R;

import java.util.Date;

public class Utilities {

    public static String SHARED_PREFERENCES_NAME = "com.lap.shared";

    // old stage keys not working
//    public static String HITPAY_JOIN_LEAGUE_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";
//    public static String HITPAY_ONLINE_SHOPPING_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";
//    public static String HITPAY_CALENDAR_FACILITIES_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";
//    public static String HITPAY_ACADEMY_SESSION_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";
//    public static String HITPAY_CAMP_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";
//    public static String HITPAY_MIDWEEK_KEY = "ef044540ea22a9fd6f9febb14c8b5acc8e54f827b36eed618ddc9d53d4ab3ef9";

        // stage hit pay api
    public static String HITPAY_API = "https://api.sandbox.hit-pay.com/v1/payment-requests";
    public static String HITPAY_JOIN_LEAGUE_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String HITPAY_ONLINE_SHOPPING_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String HITPAY_CALENDAR_FACILITIES_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String HITPAY_ACADEMY_SESSION_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String HITPAY_CAMP_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String HITPAY_MIDWEEK_KEY = "dbcc0e2a8c6f3600c1abe0ad4c2773f5e10e81d99f54b4f79c957dd313ead9a6";
    public static String SALT_HITPAY_JOIN_LEAGUE_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";
    public static String SALT_HITPAY_ONLINE_SHOPPING_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";
    public static String SALT_HITPAY_CALENDAR_FACILITIES_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";
    public static String SALT_HITPAY_ACADEMY_SESSION_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";
    public static String SALT_HITPAY_CAMP_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";
    public static String SALT_HITPAY_MIDWEEK_KEY = "UWhkziCU5DFGPP7c8dj8vVYIfOmQG09rjiHKqsJuwgdcNq0tYPXdnfs1mSMJBVcH";

//    // Live hit pay api
//    public static String HITPAY_API = "https://api.hit-pay.com/v1/payment-requests";
//    public static String HITPAY_JOIN_LEAGUE_KEY = "7c447e0065f2f49f55546213ef68d77a72f9d438a23a4527ee21201b5e52866a";
//    public static String HITPAY_ONLINE_SHOPPING_KEY = "96fdd808702d8b7cc2a3b27e7e8a3e22b0fc481d8d76aa62d7ec5e04dcdf704a";
//    public static String HITPAY_CALENDAR_FACILITIES_KEY = "96fdd808702d8b7cc2a3b27e7e8a3e22b0fc481d8d76aa62d7ec5e04dcdf704a";
//    public static String HITPAY_ACADEMY_SESSION_KEY = "939b5bdbecec1c4daedb582ae485a1b051513f4bc5bb804af805207a58fa41fb";
//    public static String HITPAY_CAMP_KEY = "7c447e0065f2f49f55546213ef68d77a72f9d438a23a4527ee21201b5e52866a";
//    public static String HITPAY_MIDWEEK_KEY = "939b5bdbecec1c4daedb582ae485a1b051513f4bc5bb804af805207a58fa41fb";
//    public static String SALT_HITPAY_JOIN_LEAGUE_KEY = "lQQW0ed9PLUQFbIOb4A1XndKRsbsIAVO2m4vTJVbxP66psmrOpFK8Or0pOFIOn2B";
//    public static String SALT_HITPAY_ONLINE_SHOPPING_KEY = "uNRTNaDlSaQZtsXmWkfC7buOWjsZnxcC31Y5ZcrzU8YBHfAbRRTkcjwXHliB4fzX";
//    public static String SALT_HITPAY_CALENDAR_FACILITIES_KEY = "uNRTNaDlSaQZtsXmWkfC7buOWjsZnxcC31Y5ZcrzU8YBHfAbRRTkcjwXHliB4fzX";
//    public static String SALT_HITPAY_ACADEMY_SESSION_KEY = "giDBuT7gvlahs75UXMtdvu0fZTetwxEDaTJMLOkLcCVkNqQFQt1N9TYJ78MUF9S4";
//    public static String SALT_HITPAY_CAMP_KEY = "lQQW0ed9PLUQFbIOb4A1XndKRsbsIAVO2m4vTJVbxP66psmrOpFK8Or0pOFIOn2B";
//    public static String SALT_HITPAY_MIDWEEK_KEY = "giDBuT7gvlahs75UXMtdvu0fZTetwxEDaTJMLOkLcCVkNqQFQt1N9TYJ78MUF9S4";

//        // new Live hit pay api
//    public static String HITPAY_API = "https://api.hit-pay.com/v1/payment-requests";
//    public static String HITPAY_JOIN_LEAGUE_KEY = "939b5bdbecec1c4daedb582ae485a1b051513f4bc5bb804af805207a58fa41fb";
//    public static String HITPAY_ONLINE_SHOPPING_KEY = "96fdd808702d8b7cc2a3b27e7e8a3e22b0fc481d8d76aa62d7ec5e04dcdf704a";
//    public static String HITPAY_CALENDAR_FACILITIES_KEY = "96fdd808702d8b7cc2a3b27e7e8a3e22b0fc481d8d76aa62d7ec5e04dcdf704a";
//    public static String HITPAY_ACADEMY_SESSION_KEY = "939b5bdbecec1c4daedb582ae485a1b051513f4bc5bb804af805207a58fa41fb";
//    public static String HITPAY_CAMP_KEY = "96fdd808702d8b7cc2a3b27e7e8a3e22b0fc481d8d76aa62d7ec5e04dcdf704a";
//    public static String HITPAY_MIDWEEK_KEY = "939b5bdbecec1c4daedb582ae485a1b051513f4bc5bb804af805207a58fa41fb";
//    public static String SALT_HITPAY_JOIN_LEAGUE_KEY = "giDBuT7gvlahs75UXMtdvu0fZTetwxEDaTJMLOkLcCVkNqQFQt1N9TYJ78MUF9S4";
//    public static String SALT_HITPAY_ONLINE_SHOPPING_KEY = "uNRTNaDlSaQZtsXmWkfC7buOWjsZnxcC31Y5ZcrzU8YBHfAbRRTkcjwXHliB4fzX";
//    public static String SALT_HITPAY_CALENDAR_FACILITIES_KEY = "uNRTNaDlSaQZtsXmWkfC7buOWjsZnxcC31Y5ZcrzU8YBHfAbRRTkcjwXHliB4fzX";
//    public static String SALT_HITPAY_ACADEMY_SESSION_KEY = "giDBuT7gvlahs75UXMtdvu0fZTetwxEDaTJMLOkLcCVkNqQFQt1N9TYJ78MUF9S4";
//    public static String SALT_HITPAY_CAMP_KEY = "uNRTNaDlSaQZtsXmWkfC7buOWjsZnxcC31Y5ZcrzU8YBHfAbRRTkcjwXHliB4fzX";
//    public static String SALT_HITPAY_MIDWEEK_KEY = "giDBuT7gvlahs75UXMtdvu0fZTetwxEDaTJMLOkLcCVkNqQFQt1N9TYJ78MUF9S4";



    // Google Play Console
    // hamza@virgilsports.com
    // Sebbatical123

    // live
    public static  String getAvailabileSlotesSerivce = "pitch/get_available_slots_adc";
    public static String listAdcService = "pitch/list_adc";
    public static String detailAdcService = "pitch/detail_adc";
    public static String bookSession = "sessions/book_session_adc";
    public static String campSession = "camps/calculate_summary_amount_adc";
    public static String bookingNew = "camps/booking_new_adc";

//    //  stage
//    public static String BASE_URL = "https://stage.ifasport.com/api/v3.23/"; // Staging Server v3.8 by me
//    public static String SYSTEM_VERSION = "3.23"; // by me stage

        //  stage1
//    public static String BASE_URL = "https://stage1.ifasport.com/api/v3.8/"; // Staging Server v3.8 by me
//    public static String SYSTEM_VERSION = "3.8"; // by me stage

//        // live  - last live versions 3.13, 3.17, 3.18, 3.19, 3.22, 3.23
    public static String BASE_URL = "https://lap.ifasport.com/api/v3.23/"; // Live Server
    public static String SYSTEM_VERSION = "3.23"; // Live

//    // IJF
//        public static String BASE_URL = "https://ijf.ifasport.com/api/v3.23/"; // Live Server
//    public static String SYSTEM_VERSION = "3.23"; // Live

    public static String RUGBY_DOC_URL = "https://lap.ifasport.com/uploads/documents/";
    public static final String REDIRECT_URL = "https://lap.ifasport.com/payments/response_handler";

    public static String BASE_DOC_URL = "https://lap.ifasport.com/";
    public static String TERMS_AND_CONDITIONS = "https://lap.ifasport.com/content/index/terms_and_conditions";

    public static final String ACCESS_CODE = "AVOO02ED52AW90OOWA"; // Live
    public static final String MERCHANT_ID = "44109";              // Live

//    public static final String ACCESS_CODE = "AVGD03HH53CA10DGAC"; // Live
//    public static final String MERCHANT_ID = "45990";              // Live

    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String DEVICE_TYPE = "android";

    public static String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon_notification : R.drawable.icon_notification_white;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static ProgressDialog createProgressDialog(Context context) {

        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setCancelable(false);

        pDialog.show();
        pDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.common_progress_dialog, null);
        ProgressBar pBar = (ProgressBar) v.findViewById(R.id.progressBar);
        pBar.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
        pDialog.setContentView(v);

        return pDialog;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void setGridViewHeightBasedOnChildren(GridView gridView, int columns) {
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int items = listAdapter.getCount();
        int rows = 0;

        View listItem = listAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalHeight = listItem.getMeasuredHeight();

        float x = 1;
        if (items > columns) {
            x = items / columns;
            rows = (int) (x + 1);
            totalHeight *= rows;
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + 40;
        gridView.setLayoutParams(params);

    }

    public static String getDifferenceBtwTime(Date dateTime) {

        long timeDifferenceMilliseconds = dateTime.getTime() - new Date().getTime();
        long diffSeconds = timeDifferenceMilliseconds / 1000;
        long diffMinutes = timeDifferenceMilliseconds / (60 * 1000);
        long diffHours = timeDifferenceMilliseconds / (60 * 60 * 1000);
        long diffDays = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24);
        long diffWeeks = timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 7);
        long diffMonths = (long) (timeDifferenceMilliseconds / (60 * 60 * 1000 * 24 * 30.41666666));
        long diffYears = (long) timeDifferenceMilliseconds / (1000 * 60 * 60 * 24 * 365);

        if (diffSeconds < 1) {
            return "1 sec";
        } else if (diffMinutes < 1) {
            return diffSeconds + " seconds";
        } else if (diffHours < 1) {
            return diffMinutes + " minutes";
        } else if (diffDays < 1) {
            return diffHours + " hours";
        } else if (diffWeeks < 1) {
            return diffDays + " days";
        } else if (diffMonths < 1) {
            return diffWeeks + " weeks";
        } else if (diffYears < 12) {
            return diffMonths + " months";
        } else {
            return diffYears + " years";
        }
    }

    public static void fontImageText(TextView textView, String iconName){

        if(iconName.equalsIgnoreCase("sp-icon-archive")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-availability")){
            long valLong = Long.parseLong("f508",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-blocks")){
            long valLong = Long.parseLong("f164",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-book")){
            long valLong = Long.parseLong("f330",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-bracket")){
            long valLong = Long.parseLong("f325",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-bracket-center")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-buddypress")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-calculator")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-calendar")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-sp_event")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-cake")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("ssp-icon-cancel")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-chart")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-clipboard")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-color")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-copy")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-edit")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-export")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-eye")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-friendly")){
            long valLong = Long.parseLong("f328",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-globe")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-history")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-import")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-league")){
            long valLong = Long.parseLong("f332",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-list")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-location")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-matrix")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-megaphone")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-menu")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-minimal")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-popup")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-shield")){
            long valLong = Long.parseLong("f334",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-sponsor")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-sportspress")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-staff")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-statistics")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-time")){
            long valLong = Long.parseLong("f469",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-timeline")){
            long valLong = Long.parseLong("f203",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-ticket")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-tournament")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-scoreboard")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-tshirt")){
            long valLong = Long.parseLong("f307",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-sp_player")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-trash")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-whistle")){
            long valLong = Long.parseLong("f227",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-key")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-user-scores")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-woo")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-wordpay")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("p-icon-yoast")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-sub")){
            long valLong = Long.parseLong("f503",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("p-icon-star-filled")){
            long valLong = Long.parseLong("f322",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-update")){
            long valLong = Long.parseLong("f113",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-undo")){
            long valLong = Long.parseLong("f171",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-redo")){
            long valLong = Long.parseLong("f172",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-marker")){
            long valLong = Long.parseLong("f159",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-no")){
            long valLong = Long.parseLong("f158",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-heart")){
            long valLong = Long.parseLong("f487",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-soccerball")){
            long valLong = Long.parseLong("e700",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-baseball")){
            long valLong = Long.parseLong("e701",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-basketball")){
            long valLong = Long.parseLong("e602",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-golfball")){
            long valLong = Long.parseLong("e603",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-cricketball")){
            long valLong = Long.parseLong("e604",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-bowling")){
            long valLong = Long.parseLong("e605",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-ice-hockey")){
            long valLong = Long.parseLong("e606",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-football")){
            long valLong = Long.parseLong("e607",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-poolball")){
            long valLong = Long.parseLong("e608",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-table-tennis")){
            long valLong = Long.parseLong("e609",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-tennis")){
            long valLong = Long.parseLong("e610",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-racing-flag")){
            long valLong = Long.parseLong("e611",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-volleyball")){
            long valLong = Long.parseLong("e612",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-soccerball-alt")){
            long valLong = Long.parseLong("e600",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-baseball-alt")){
            long valLong = Long.parseLong("e601",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-shoe")){
            long valLong = Long.parseLong("e800",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-card")){
            long valLong = Long.parseLong("e801",16);
            textView.setText((char) valLong + "");
        }else if(iconName.equalsIgnoreCase("sp-icon-card")){
            long valLong = Long.parseLong("e801",16);
            textView.setText((char) valLong + "");
        }

    }
}