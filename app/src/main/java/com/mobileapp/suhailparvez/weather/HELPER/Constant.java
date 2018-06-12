package com.mobileapp.suhailparvez.weather.HELPER;

/**
 * Created by SuhailMirji on 11/03/2018.
 */

public class Constant {

    public static final class HTTP {

        public static final String CLIENTS_URL = "http://api.openweathermap.org/data/";
    }

    public static final class WeatherDatabaseTable {

        public static final String DB_NAME = "weather";
        public static final String TABLE_NAME = "weathertable";
        public static final int DB_VERSION = +1;

        public static final String DROP_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String GET_PRODUCTS_QUERY = "SELECT * FROM " + TABLE_NAME;

        public static final String KEY_ID = "keyid";
        public static final String MAIN = "main";
        public static final String DESCRIPTION = "description";
        public static final String ICON = "icon";
        public static final String TEMP = "temp";
        public static final String HUMIDITY = "humidity";
        public static final String TEMP_MIN = "temp_min";
        public static final String TEMP_MAX = "temp_max";
        public static final String SPEED = "speed";
        public static final String COUNTRY = "country";
        public static final String SUNRISE = "sunrise";
        public static final String SUNSET = "sunset";
        public static final String NAME = "name";

        public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + MAIN + " TEXT,"
                + DESCRIPTION + " TEXT,"
                + ICON + " TEXT,"
                + TEMP + " TEXT,"
                + HUMIDITY + " TEXT,"
                + TEMP_MIN + " TEXT,"
                + TEMP_MAX + " TEXT,"
                + SPEED + " TEXT,"
                + COUNTRY + " TEXT,"
                + SUNRISE + " TEXT,"
                + SUNSET + " TEXT,"
                + NAME + " TEXT" + ")";

    }

    public static final class Config {
        public static final String PACKAGE_NAME = "com.mobileapp.suhailparvez.weather.HELPER";
    }
}
