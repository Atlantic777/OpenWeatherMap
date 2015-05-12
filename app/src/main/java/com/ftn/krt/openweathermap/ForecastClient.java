package com.ftn.krt.openweathermap;

import android.media.Image;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nikola on 5/7/15.
 */
public class ForecastClient extends AsyncTask<String, String, String> {
    private ForecastDataListener mListener;
    private final String TAG = "client";
    private String image_url = "http://openweathermap.org/img/w/";
    private final String IMAGE_FORMAT = ".png";
    private ArrayList<Image> imageIcons;

    ForecastClient(ForecastDataListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        JSONObject obj = new JSONObject();

        try {
            response = httpClient.execute(new HttpGet(uri[0]));
            StatusLine statusLine = response.getStatusLine();

            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }

            obj = new JSONObject(responseString);
            JSONArray  list = obj.getJSONArray("list");


            int cnt = obj.getInt("cnt");
            String img;
            JSONObject weather;

            for(int i = 0; i < cnt; i++) {
                weather = list.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                img = weather.getString("icon");

                response = httpClient.execute(new HttpGet(image_url + img + IMAGE_FORMAT));
                statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    list.getJSONObject(i).put("icon_img", Base64.encodeToString(out.toByteArray(), Base64.DEFAULT));
                    out.close();
                    Log.d(TAG, image_url + img + IMAGE_FORMAT);
                }
            }

        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, obj.toString());
        return obj.toString(); //responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mListener.pushForecastData(result);
    }
}
