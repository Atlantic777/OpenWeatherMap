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
 * \file ForecastDataListener.java
 * \brief.
 *      This file contains definition of ForecastDataListener interface
 *      which is needed for connecting an object that needs to get
 *      information on forecast data results.
 *
 * Created on 13.05.2015
 *
 * @Author Nikola Hardi
 *
 **********************************************************************/

package com.ftn.krt.openweathermap;

/**
 * Created by nikola on 5/7/15.
 *
 */
public interface ForecastDataListener {
    void pushForecastData(String forecastData);
}