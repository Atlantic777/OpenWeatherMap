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
 * \file CityDataAdapter.java
 * \brief
 *      This file contains...
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by nikola on 5/13/15.
 *
 */

public class CityDataAdapter extends BaseAdapter {
    private JSONObject mCityData;
    private Context mContext;

    public CityDataAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public int getCount() {
        int val = 0;

        if (mCityData != null) {
            try {
                val = mCityData.getInt("count");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    @Override
    public Object getItem(int position) {
        Object rv = null;

        if(mCityData != null) {
            try {
                rv = mCityData.getJSONArray("list").get(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return rv;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.city_data_row, null);
        }

        TextView city    = (TextView) view.findViewById(R.id.city_name);
        TextView country = (TextView) view.findViewById(R.id.country_name);
        TextView city_id = (TextView) view.findViewById(R.id.city_id);

        JSONObject obj = (JSONObject) getItem(position);
        String sCountry;

        if (obj != null) {
            try {
                city.setText(obj.getString("name"));

                sCountry = obj.getJSONObject("sys").getString("country");
                country.setText(sCountry);

                city_id.setText(Integer.toString(obj.getInt("id")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    public void setData(JSONObject data) {
        mCityData = data;
        notifyDataSetChanged();
    }
}
