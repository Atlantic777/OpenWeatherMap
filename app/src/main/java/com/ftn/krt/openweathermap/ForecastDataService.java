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
 * \file ForecastDataService.java
 * \brief
 *      This file contains ForecastDataService class
 *      which is an Android service for forecast data fetching.
 *
 *      It needs a listener and location ID to function properly,
 *      both of which can be set by corresponding public methods.
 *
 *      When there are clients bound, it will spawn a scheduled task
 *      which will fetch updates every 1 hour. When last client unbinds,
 *      it will stop automatic updates.
 *
 *      It also has a force fetch feature which can be activated by
 *      calling the forceFetch() public method.
 *
 *      On every data fetch request, it spawns a new ForecastClient.
 *
 *      URL request options are hardcoded this way:
 *       - daily forecast
 *       - for 5 days
 *       - in metric units
 *       - and in JSON format
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ForecastDataService extends Service {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final int REFETCH_INTERVAL = 1;
    private String mLocationID;
    private String mQueryURL = "http://api.openweathermap.org/data/2.5/forecast/daily?units=metric&mode=json&cnt=5&id=";

    private final IBinder mBinder = new LocalBinder();
    private final Runnable mBeeper = new Runnable() {
        @Override
        public void run() {
            forceFetch();
        }
    };
    private ScheduledFuture beeperHandle;
    private ForecastDataListener mListener;
    public ForecastDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        ForecastDataService getService() {
            return ForecastDataService.this;
        }
    }

    public void forceFetch() {
        if(mListener != null && mLocationID != null) {
            ForecastClient client = new ForecastClient(mListener);
            client.execute(mQueryURL+mLocationID);
        }
    }

    public void setForecastDataListener(ForecastDataListener listener) {
        beeperHandle =  scheduler.scheduleAtFixedRate(mBeeper,0,REFETCH_INTERVAL,TimeUnit.HOURS);
        mListener = listener;
    }

    public void removeForecastDataListener() {
        if(mListener != null) {
            mListener = null;
            beeperHandle.cancel(true);
        }
    }

    public void setLocation(String locationID) {
        mLocationID = locationID;
        forceFetch();
    }
}
