package com.ftn.krt.openweathermap;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by nikola on 5/13/15.
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public class CityClient extends AsyncTask<String, String, String> {
    private String CITY_URL = "http://api.openweathermap.org/data/2.5/find?type=like&mode=json&q=";
    private final String ENCODING = "US-ASCII";
    private CityDataListener mListener;

    CityClient(CityDataListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        StatusLine statusLine;

        String completeURL = composeURL(params[0], params[1]);

        try {
            response = httpClient.execute(new HttpGet(completeURL));
            statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseString = "";
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        mListener.pushCityData(result);
    }

    private String composeURL(String city, String country) {
        String cityName = "";
        String countryName = "";
        String completeURL;

        try {
            cityName = URLEncoder.encode(city, ENCODING);
            countryName = URLEncoder.encode(country, ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        completeURL = CITY_URL + cityName;

        if (country.length() > 0) {
            completeURL = completeURL + "," + countryName;
        }

        return completeURL;
    }
}
