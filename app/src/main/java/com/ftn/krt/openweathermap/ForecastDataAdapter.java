package com.ftn.krt.openweathermap;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nikola on 5/9/15.
 */
public class ForecastDataAdapter extends BaseAdapter {
    private JSONObject mDailyForecast;
    private Context mContext;
    private final String TAG = "ADAPTER";

    public ForecastDataAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public int getCount() {
        int val;
        try {
            val = mDailyForecast.getInt("cnt");
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
            rv = mDailyForecast.getJSONArray("list").get(position);
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.forecast_data_row, null);
        }

        ImageView image = (ImageView)view.findViewById(R.id.image);
        TextView  text  = (TextView)view.findViewById(R.id.text);

        JSONObject o = (JSONObject)getItem(position);
        text.setText(o.toString());

        Log.d(TAG, "Calling adapter with position: " + position);

        return view;
    }

    public void setData(JSONObject data) {
        this.mDailyForecast = data;
        notifyDataSetChanged();
    }
}
