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
 * Created by nikola on 5/7/15.
 */
public class ForecastClient extends AsyncTask<String, String, String> {
    private ForecastDataListener mListener;

    ForecastClient(ForecastDataListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;

        try {
            response = httpClient.execute(new HttpGet(uri[0]));
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
        mListener.pushForecastData(result);
    }
}
