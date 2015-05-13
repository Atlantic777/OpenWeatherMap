package com.ftn.krt.openweathermap;

import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by nikola on 5/7/15.
 *
 */
@SuppressWarnings("FieldCanBeLocal")
public class ForecastClient extends AsyncTask<String, String, String> {
    private ForecastDataListener mListener;
    private final String IMAGE_URL = "http://openweathermap.org/img/w/";
    private final String IMAGE_FORMAT = ".png";

    ForecastClient(ForecastDataListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpClient = new DefaultHttpClient();

        HttpResponse response;
        String responseString = "";
        StatusLine statusLine;

        JSONObject obj = new JSONObject();
        JSONArray list;
        JSONObject weather;
        String sImg;

        try {
            response = httpClient.execute(new HttpGet(uri[0]));
            statusLine = response.getStatusLine();

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }

            obj = new JSONObject(responseString);
            list = obj.getJSONArray("list");

            int cnt = obj.getInt("cnt");


            for (int i = 0; i < cnt; i++) {
                weather = list.getJSONObject(i).getJSONArray("weather").getJSONObject(0);
                sImg = weather.getString("icon");

                response = httpClient.execute(new HttpGet(IMAGE_URL + sImg + IMAGE_FORMAT));
                statusLine = response.getStatusLine();

                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    list.getJSONObject(i).put("icon_img", Base64.encodeToString(out.toByteArray(), Base64.DEFAULT));
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mListener.pushForecastData(result);
    }
}
