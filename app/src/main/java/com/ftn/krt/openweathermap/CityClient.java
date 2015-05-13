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
 * \file CityClient.java
 * \brief
 *      This file contains implementation of a HTTP client for
 *      fetching list of data about cities available in remote service.
 *
 *      It has to be initialized with a reference to object which implements
 *      the CityDataListener interface.
 *
 *      This client needs 2 String params to be executed (city and country name),
 *      and country name can be an empty string.
 *
 *      Upon execution, it notifies attached listener and pushes a String
 *      containing downloaded data in JSON format.
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

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

@SuppressWarnings("FieldCanBeLocal")
public class CityClient extends AsyncTask<String, String, String> {
    private String CITY_URL = "http://api.openweathermap.org/data/2.5/find?type=like&mode=json&q=";
    private final String ENCODING = "US-ASCII";
    private CityDataListener mListener;

    CityClient(CityDataListener listener) {
        mListener = listener;
    }

    /**
     * Work needed to be done as background job in a separate thread.
     * @param params - array of strings, two needed (city and country)
     * @return - String containing JSON formatted data
     */
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

    /**
     * Notifies the listener when data downloading is finished
     * @param result - String containing JSON data
     */
    @Override
    protected void onPostExecute(String result) {
        mListener.pushCityData(result);
    }

    /**
     * Helper method for compiling request URL.
     *
     * This method deals with HTTP encoding, removes whitespaces and decides
     * whether or not user specified country.
     *
     * @param city - string containing city name
     * @param country - string containing country name
     * @return - compiled request URL with HTTP encoding escape sequences
     */
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
