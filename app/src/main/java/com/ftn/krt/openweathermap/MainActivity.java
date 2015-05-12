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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements ForecastDataListener, SwipeRefreshLayout.OnRefreshListener {
    ForecastDataService mService;
    boolean mBound = false;
    ForecastDataAdapter mAdapter;
    ListView mList;
    SwipeRefreshLayout mSwipeLayout;
    private final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAdapter = new ForecastDataAdapter(this);

        mList = (ListView)findViewById(R.id.list);
        mList.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);

        ImageButton fabImageButton = (ImageButton)findViewById(R.id.fab_image_button);
        fabImageButton.setOnClickListener(new FabListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ForecastDataService.class);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mBound) {
            Log.d(TAG, "Should unbind");
            unbindService(mConnection);
            mService.removeForecastDataListener();
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ForecastServiceConnection(this);

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(true);
        Log.d(TAG, Boolean.toString(mBound));
        if (mBound) {
            mService.forceFetch();
            Log.d(TAG, "Called the ForecastDataService");
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    }

    @Override
    public void pushForecastData(String forecastData) {
        try {
            JSONObject o = new JSONObject(forecastData);
            mAdapter.setData(o);
            mSwipeLayout.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Ok, I got the data");
    }

    class FabListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "FAB clicked!");
        }
    }
}
