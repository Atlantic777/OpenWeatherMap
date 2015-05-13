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
 * \file ForecastDataAdapter.java
 * \brief
 *      This file contains...
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by nikola on 5/9/15.
 *
 */
public class ForecastDataAdapter extends BaseAdapter {
    private JSONObject mDailyForecast;
    private Context mContext;
    private final char DEG = (char) 0x00B0;

    public ForecastDataAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public int getCount() {
        int val = 0;

        if (mDailyForecast != null) {
            try {
                val = mDailyForecast.getInt("cnt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return val;
    }

    @Override
    public Object getItem(int position) {
        Object rv = null;

        if (mDailyForecast != null) {
            try {
                rv = mDailyForecast.getJSONArray("list").get(position);
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
            view = inflater.inflate(R.layout.forecast_data_row, null);
        }

        ImageView image = (ImageView) view.findViewById(R.id.image);
        TextView text = (TextView) view.findViewById(R.id.text);
        TextView min = (TextView) view.findViewById(R.id.min);
        TextView max = (TextView) view.findViewById(R.id.max);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView morning = (TextView) view.findViewById(R.id.morn);
        TextView evening = (TextView) view.findViewById(R.id.eve);
        TextView night = (TextView) view.findViewById(R.id.night);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM ''yy");


        JSONObject obj = (JSONObject) getItem(position);

        if (obj == null) {
            return view;
        }

        try {
            String s_image;
            byte[] b_image;
            s_image = obj.getString("icon_img");
            b_image = Base64.decode(s_image, Base64.DEFAULT);
            image.setImageBitmap(BitmapFactory.decodeByteArray(b_image, 0, b_image.length));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject temp = obj.getJSONObject("temp");
            JSONObject weather = obj.getJSONArray("weather").getJSONObject(0);

            min.setText(Integer.toString(temp.getInt("min")) + DEG);
            max.setText(Integer.toString(temp.getInt("max")) + DEG);
            morning.setText(Integer.toString(temp.getInt("morn")) + DEG);
            evening.setText(Integer.toString(temp.getInt("eve")) + DEG);
            night.setText(Integer.toString(temp.getInt("night")) + DEG);

            Date d = new Date(obj.getLong("dt") * 1000);
            date.setText(df.format(d));

            text.setText(weather.getString("description"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public void setData(JSONObject data) {
        this.mDailyForecast = data;
        notifyDataSetChanged();
    }
}
