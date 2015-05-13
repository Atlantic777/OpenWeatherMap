package com.ftn.krt.openweathermap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

/**
 * Created by nikola on 5/13/15.
 */
public class CityDataAdapter extends BaseAdapter{
    private JSONObject mCityData;
    private Context mContext;

    public CityDataAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public int getCount() {
        int val;

        if(mCityData == null) {
            return 0;
        }

        try {
            val = mCityData.getInt("count");

        } catch (Exception e) {
            val = 0;
            e.printStackTrace();
        }

        return val;
    }

    @Override
    public Object getItem(int position) {
        Object rv;

        try {
            rv = mCityData.getJSONArray("list").get(position);
        } catch (Exception e) {
            rv = null;
            e.printStackTrace();
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

        if(view == null) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.city_data_row, null);
        }

        TextView city = (TextView)view.findViewById(R.id.city_name);
        TextView country = (TextView)view.findViewById(R.id.country_name);
        TextView city_id = (TextView)view.findViewById(R.id.city_id);

        JSONObject obj = (JSONObject)getItem(position);

        if(obj == null) {
            return view;
        }

        try {
            city.setText(obj.getString("name"));

            String sCountry = obj.getJSONObject("sys").getString("country");
            country.setText(sCountry);

            city_id.setText(Integer.toString(obj.getInt("id")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setData(JSONObject data) {
        mCityData = data;
        notifyDataSetChanged();
    }
}