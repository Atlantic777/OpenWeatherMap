package com.ftn.krt.openweathermap;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by nikola on 5/13/15.
 */
public class CityClient extends AsyncTask<String,String,String> {
    private String city_url = "http://api.openweathermap.org/data/2.5/find?type=like&mode=json&q=";
    private final String TAG = "CITY_CLIENT";
    private CityDataListener mListener;

    CityClient(CityDataListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;



        try {
            String city_name = URLEncoder.encode(params[0], "US-ASCII");
            String country_name = URLEncoder.encode(params[1], "US-ASCII");

            String complete_url = city_url + city_name;

            if(params[1].length() > 0) {
                complete_url = complete_url + "," + country_name;
            }

            Log.d(TAG, complete_url);

            response = httpClient.execute(new HttpGet(complete_url));
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }
        } catch(Exception e) {
            e.printStackTrace();
            responseString = "";
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        mListener.pushCityData(result);
    }
}
