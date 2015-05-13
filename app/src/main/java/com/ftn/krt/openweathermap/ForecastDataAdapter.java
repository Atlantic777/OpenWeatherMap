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

        if(mDailyForecast == null) {
            return 0;
        }
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
        TextView  min = (TextView)view.findViewById(R.id.min);
        TextView  max = (TextView)view.findViewById(R.id.max);
        TextView  date = (TextView)view.findViewById(R.id.date);

        SimpleDateFormat df = new SimpleDateFormat("dd MMM ''yy");


        JSONObject obj = (JSONObject)getItem(position);

        if(obj == null) {
            return view;
        }

        try {
            String s_image;
            byte[] b_image;
            s_image = obj.getString("icon_img");
            b_image = Base64.decode(s_image, Base64.DEFAULT);
            image.setImageBitmap(BitmapFactory.decodeByteArray(b_image,0,b_image.length));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONObject temp = obj.getJSONObject("temp");
            min.setText(Integer.toString(temp.getInt("min")));
            max.setText(Integer.toString(temp.getInt("max")));

            Date d = new Date(obj.getLong("dt")*1000);

            date.setText(df.format(d));


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
