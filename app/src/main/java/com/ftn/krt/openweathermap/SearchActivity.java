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
 * \file SearchActivity.java
 * \brief
 *      This file contains implementation of SearchActivity class.
 *      This activity is used for searching for cities and changing.
 *
 *      Country input field and search button are by default disabled and
 *      will be enabled when user enters at least city name.
 *
 *      When search button is clicked, it will spawn a CityClient, connect
 *      itself as a listener and start refresh animation. When data arrives,
 *      refresh animation will stop and information about cities will be present
 *      in a ListView in bottom part of the screen.
 *
 *      Choosing an item in list by pressing it will send that city ID to the
 *      main activity, stop search activity and start main activity where
 *      forecast data will be queued for fetching and showing.
 *
 *      Another way for leaving this activity is by pressing the cancel button
 *      which will stop this activity and start again the main activity with
 *      last known location ID.
 *
 *      WARNING: empty list probably means that search didn't return any results.
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;


@SuppressWarnings("FieldCanBeLocal")
public class SearchActivity extends ActionBarActivity implements CityDataListener {
    private CityDataAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private Button mSearch;
    private Button mCancel;
    private ListView mList;
    private EditText mCityInput;
    private EditText mCountryInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mCancel = (Button)findViewById(R.id.search_negative);
        mSearch = (Button)findViewById(R.id.search_positive);
        mList = (ListView)findViewById(R.id.search_list);
        mSwipeLayout = (SwipeRefreshLayout)findViewById(R.id.cities_swipe_container);
        mCityInput = (EditText)findViewById(R.id.input_city);
        mCountryInput = (EditText)findViewById(R.id.input_country);

        mAdapter = new CityDataAdapter(this);

        mCancel.setOnClickListener(new NegativeListener());
        mSearch.setOnClickListener(new PositiveListener(this));
        mSearch.setEnabled(false);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(new CityItemClickListener());
        mList.setEmptyView(findViewById(R.id.cities_empty));
        mCountryInput.setEnabled(false);

        mCityInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mCountryInput.setEnabled(true);
                    mSearch.setEnabled(true);
                } else {
                    mCountryInput.setEnabled(false);
                    mSearch.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void pushCityData(String cityData) {
        mSwipeLayout.setRefreshing(false);

        try {
            JSONObject obj = new JSONObject(cityData);
            mAdapter.setData(obj);
        } catch (Exception e) {
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
            CityClient cityClient = new CityClient(mListener);
            cityClient.execute(mCityInput.getText().toString(), mCountryInput.getText().toString());
            mSwipeLayout.setRefreshing(true);
        }
    }

    class CityItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView cityID = (TextView)view.findViewById(R.id.city_id);
            TextView cityName = (TextView)view.findViewById(R.id.city_name);
            TextView countryName = (TextView)view.findViewById(R.id.country_name);
            Intent i = new Intent();

            setResult(RESULT_OK, i);
            i.putExtra("city_id", cityID.getText().toString());
            i.putExtra("city_name", cityName.getText().toString());
            i.putExtra("country_name", countryName.getText().toString());

            finish();
        }
    }
}
