package com.mobileapp.suhailparvez.weather.DATABASE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobileapp.suhailparvez.weather.HELPER.Constant;
import com.mobileapp.suhailparvez.weather.POJO.DatabasePOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SuhailMirji on 11/03/2018.
 */

public class WeatherDatabase extends SQLiteOpenHelper {

    private static final String TAG = WeatherDatabase.class.getSimpleName();
    private static final String TABLE_NAME = WeatherDatabase.class.getName();

    public WeatherDatabase(Context context) {
        super(context, Constant.WeatherDatabaseTable.DB_NAME, null, Constant.WeatherDatabaseTable.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(Constant.WeatherDatabaseTable.CREATE_TABLE_QUERY);
        } catch (SQLException ex) {
            Log.d(TAG, ex.getMessage());
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(Constant.WeatherDatabaseTable.DROP_QUERY);
        this.onCreate(db);
        db.close();

    }

    public void deleteProducts() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Constant.WeatherDatabaseTable.TABLE_NAME, null, null);
        db.close();

    }

    public void addDataInDB(String s, String s0, String s1, String s2, String s3, String s4,
                            String s5, String s6, String s7, String s8, String s9, String s10) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.WeatherDatabaseTable.MAIN, s);
        values.put(Constant.WeatherDatabaseTable.DESCRIPTION, s0);
        values.put(Constant.WeatherDatabaseTable.ICON, s1);
        values.put(Constant.WeatherDatabaseTable.TEMP, s2);
        values.put(Constant.WeatherDatabaseTable.HUMIDITY, s3);
        values.put(Constant.WeatherDatabaseTable.TEMP_MIN, s4);
        values.put(Constant.WeatherDatabaseTable.TEMP_MAX, s5);
        values.put(Constant.WeatherDatabaseTable.SPEED, s6);
        values.put(Constant.WeatherDatabaseTable.COUNTRY, s7);
        values.put(Constant.WeatherDatabaseTable.SUNRISE, s8);
        values.put(Constant.WeatherDatabaseTable.SUNSET, s9);
        values.put(Constant.WeatherDatabaseTable.NAME, s10);

        try {

            db.insert(Constant.WeatherDatabaseTable.TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        db.close();

    }

    public List<DatabasePOJO> getAllData() {
        List<DatabasePOJO> contactList = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(Constant.WeatherDatabaseTable.GET_PRODUCTS_QUERY, null);

        if (cursor.moveToFirst()) {
            do {
                DatabasePOJO contact = new DatabasePOJO();
                contact.setMain(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.MAIN)));
                contact.setDescription(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.DESCRIPTION)));
                contact.setIcon(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.ICON)));
                contact.setTemp(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.TEMP)));
                contact.setHumidity(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.HUMIDITY)));
                contact.setTemp_min(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.TEMP_MIN)));
                contact.setTemp_max(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.TEMP_MAX)));
                contact.setSpeed(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.SPEED)));
                contact.setCountry(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.COUNTRY)));
                contact.setSunrise(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.SUNRISE)));
                contact.setSunset(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.SUNSET)));
                contact.setName(cursor.getString(cursor.getColumnIndex(Constant.WeatherDatabaseTable.NAME)));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    public void updateValues(String x, String s, String s0, String s1, String s2, String s3, String s4, String s5,
                             String s6, String s7, String s8, String s9, String s10) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constant.WeatherDatabaseTable.MAIN, s);
        values.put(Constant.WeatherDatabaseTable.DESCRIPTION, s0);
        values.put(Constant.WeatherDatabaseTable.ICON, s1);
        values.put(Constant.WeatherDatabaseTable.TEMP, s2);
        values.put(Constant.WeatherDatabaseTable.HUMIDITY, s3);
        values.put(Constant.WeatherDatabaseTable.TEMP_MIN, s4);
        values.put(Constant.WeatherDatabaseTable.TEMP_MAX, s5);
        values.put(Constant.WeatherDatabaseTable.SPEED, s6);
        values.put(Constant.WeatherDatabaseTable.COUNTRY, s7);
        values.put(Constant.WeatherDatabaseTable.SUNRISE, s8);
        values.put(Constant.WeatherDatabaseTable.SUNSET, s9);
        values.put(Constant.WeatherDatabaseTable.NAME, s10);

        // updating row
        db.update(Constant.WeatherDatabaseTable.TABLE_NAME, values, Constant.WeatherDatabaseTable.KEY_ID + " = ?",
                new String[]{String.valueOf(x)});

        db.close();

    }

    public boolean TableNotEmpty() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor mCursor = db.rawQuery("SELECT * FROM " + Constant.WeatherDatabaseTable.TABLE_NAME, null);
        Boolean rowExists;

        if (mCursor.moveToFirst()) {
            // DO SOMETHING WITH CURSOR
            mCursor.close();
            rowExists = true;
            db.close();

        } else {
            // I AM EMPTY
            mCursor.close();
            rowExists = false;
            db.close();
        }
        db.close();
        return rowExists;
    }
}
