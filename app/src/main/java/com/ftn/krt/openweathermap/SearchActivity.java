package com.ftn.krt.openweathermap;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;


public class SearchActivity extends ActionBarActivity implements CityDataListener {
    private final String TAG = "SEARCH_ACTIVITY";
    private CityDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        Button cancel = (Button)findViewById(R.id.search_negative);
        Button search = (Button)findViewById(R.id.search_positive);

        cancel.setOnClickListener(new NegativeListener());
        search.setOnClickListener(new PositiveListener(this));

        mAdapter = new CityDataAdapter(this);
        ListView list = (ListView)findViewById(R.id.search_list);
        list.setAdapter(mAdapter);

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
    public void pushCityData(String cityData) {
        try {
            JSONObject obj = new JSONObject(cityData);
            mAdapter.setData(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class NegativeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    class PositiveListener implements  View.OnClickListener {
        CityDataListener mListener;

        PositiveListener(CityDataListener listener) {
            mListener = listener;
        }

        @Override
        public void onClick(View v) {
            EditText city = (EditText)findViewById(R.id.input_city);
            EditText country = (EditText)findViewById(R.id.input_country);

            CityClient a = new CityClient(mListener);

            a.execute(city.getText().toString(),country.getText().toString());
        }
    }
}
