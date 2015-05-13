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
 * \file MainActivity.java
 * \brief
 *      This file contains implementation of MainActivity of
 *      OpenWeatherMap application.
 *
 *      This activity shows current city and it's country,
 *      has search Floating Action Button which leads user to
 *      the city search activity, and a ListView with
 *      daily forecast info.
 *
 *      Data can be forced for re-fetching by pulling down the
 *      forecast data list view. Data will be automatically updated
 *      every hour, while application is running and has focus.
 *
 *      Implements ForecastDataListener and forwards forecast data
 *      to the ForecastDataAdapter.
 *
 *      Pressing search button will start search activity and when
 *      user chooses a city, result will be returned to this activity.
 *      After that, new location ID will be set for ForecastDataService.
 *
 *      Start/stop of this activity does connect to/disconnect from service.
 *
 *      By default it will try to show weather forecast for Novi Sad.
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends ActionBarActivity implements ForecastDataListener, SwipeRefreshLayout.OnRefreshListener {
    private boolean mBound = false;
    private final String TAG = "MAIN_ACTIVITY";
    private String mLocationID = "3194360"; // Novi Sad ID

    private ForecastDataService mService;
    private ForecastDataAdapter mAdapter;

    private TextView mCity;
    private TextView mCountry;
    private ListView mList;
    private SwipeRefreshLayout mSwipeLayout;
    private ServiceConnection mConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mList = (ListView)findViewById(R.id.list);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.forecast_swipe_container);
        mCity = (TextView)findViewById(R.id.city);
        mCountry = (TextView)findViewById(R.id.country);
        mAdapter = new ForecastDataAdapter(this);
        mConnection = new ForecastServiceConnection(this);

        mList.setAdapter(mAdapter);
        mSwipeLayout.setOnRefreshListener(this);

        ImageButton fabImageButton = (ImageButton)findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new FabListener());

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent(this, ForecastDataService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            unbindService(mConnection);
            mService.removeForecastDataListener();
            mBound = false;
        }
    }


    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        if (mBound) {
            mService.setLocation(mLocationID);
            mService.forceFetch();
        }
    }


    @Override
    public void pushForecastData(String forecastData) {
        try {
            JSONObject o = new JSONObject(forecastData);
            JSONObject c = o.getJSONObject("city");

            mCity.setText(c.getString("name"));
            mCountry.setText(c.getString("country"));

            mAdapter.setData(o);
            mSwipeLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Ok, I got the data");
    }


    protected void setLocation(String locationID) {
        mLocationID = locationID;

        if(mBound) {
            mService.setLocation(mLocationID);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                setLocation(data.getStringExtra("city_id"));

                mCity.setText(data.getStringExtra("city_name"));
                mCountry.setText(data.getStringExtra("country_name"));

                mSwipeLayout.setRefreshing(true);
            }
            else if(resultCode == RESULT_CANCELED) {
                Log.d(TAG, "User canceled search");
            }
        }
    }

    class FabListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent i = new Intent(getApplicationContext(), SearchActivity.class);
            startActivityForResult(i, 0);
        }
    }

    class ForecastServiceConnection implements ServiceConnection {
        private ForecastDataListener mListener;

        ForecastServiceConnection(ForecastDataListener listener) {
            super();
            mListener = listener;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ForecastDataService.LocalBinder binder = (ForecastDataService.LocalBinder)service;

            mService = binder.getService();
            mService.setForecastDataListener(mListener);

            mBound = true;
            mService.setLocation(mLocationID);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    }
}
