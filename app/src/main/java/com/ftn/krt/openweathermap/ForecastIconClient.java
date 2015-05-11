package com.ftn.krt.openweathermap;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by nikola on 5/11/15.
 */
public class ForecastIconClient extends AsyncTask<String, String, String> {
    private String url = "http://openweathermap.org/img/w/";

    ForecastIconClient() {
    }

    @Override
    protected String doInBackground(String... image) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            String image_url = url + image[0] + ".png";
            response = httpClient.execute(new HttpGet(image_url));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString();
                out.close();
            }

        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }
}