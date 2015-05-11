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
    private String mQueryURL = "http://api.openweathermap.org/data/2.5/forecast/daily?q=London,UK&mode=json";

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
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public class LocalBinder extends Binder {
        ForecastDataService getService() {
            return ForecastDataService.this;
        }
    }

    public void forceFetch() {
        if(mListener != null) {
            ForecastClient client = new ForecastClient(mListener);
            client.execute(mQueryURL);
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
}
