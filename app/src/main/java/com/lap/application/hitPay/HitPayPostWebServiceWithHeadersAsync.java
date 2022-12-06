package com.lap.application.hitPay;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lap.application.utils.Utilities;
import com.lap.application.webservices.IWebServiceCallback;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class HitPayPostWebServiceWithHeadersAsync extends AsyncTask<String, Void, String> {

    Context context;
    ProgressDialog pDialog;
    List<NameValuePair> nameValuePairs;
    String tag;
    IWebServiceCallback webServiceCallback;
    String api_key;

    public HitPayPostWebServiceWithHeadersAsync(Context context, List<NameValuePair> nameValuePairs, String tag, IWebServiceCallback webServiceCallback, String api_key){
        this.context = context;
        this.nameValuePairs = nameValuePairs;
        this.tag = tag;
        this.webServiceCallback = webServiceCallback;
        this.api_key = api_key;
    }

    @Override
    public void onPreExecute() {
        pDialog = Utilities.createProgressDialog(context);
    }

    @Override
    public String doInBackground(String... urls) {

        String strResponse = null;

        try {
            String url = urls[0];
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            httpPost.addHeader("X-BUSINESS-API-KEY", api_key);
            httpPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            System.out.println("x-access-did :: "+Utilities.getDeviceId(context));
            System.out.println("URL :: "+url);
            System.out.println("Data :: "+nameValuePairs);

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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strResponse;
    }

    @Override
    public void onPostExecute(String response) {

        if(response != null){
            int maxLogSize = 1000;
            for(int i = 0; i <= response.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = end > response.length() ? response.length() : end;
                Log.v("Complete_Response::", response.substring(start, end));
            }
        }

//        System.out.println("Response :: "+response);
        webServiceCallback.onWebServiceResponse(response, tag);

        if(pDialog != null && pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
}