package com.ftn.krt.openweathermap;

/**
 * Created by nikola on 5/7/15.
 */
public interface ForecastDataListener {
    void pushForecastData(String forecastData);
}