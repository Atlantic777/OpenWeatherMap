/**********************************************************************
 *
 * Copyright (c) 2015 Fakultet tehnickih nauka
 * Trg Dositeja Obradovica 6, Novi Sad Srbija
 *
 * All Rights Reserved
 *
 * P R O P R I E T A R Y    &    C O N F I D E N T I A L
 *
 *
 * -----------------------------------------------------
 * http://www.ftn.uns.ac.rs/
 *
 * --------------------------------------- --------------
 *
 *
 * \file ForecastClient.java
 * \brief
 *      This file contains implementation of ForecastClient class,
 *      a HTTP client for fetching 5 days daily forecast data for
 *      particular city by it's ID.
 *
 *      It also fetched weather icons from OpenWeatherMap service
 *      by ID stored in forecast data.
 *
 *      It notifies a listener and pushes a string containing
 *      JSON formatted data.
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

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