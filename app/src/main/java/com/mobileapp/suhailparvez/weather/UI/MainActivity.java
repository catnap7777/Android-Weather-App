package com.mobileapp.suhailparvez.weather.UI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private boolean mFirstUse = false;
    private static final String FIRST_TIME = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!FirstUse()) {

            Intent x = new Intent(MainActivity.this, WelcomeSlider.class);
            startActivity(x);

            markAppUsed();
        } else {

            Intent x = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(x);
        }

    }

    private boolean FirstUse() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFirstUse = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mFirstUse;
    }

    private void markAppUsed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFirstUse = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mFirstUse).apply();
    }
}
