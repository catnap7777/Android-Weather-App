package com.mobileapp.suhailparvez.weather.UI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.mobileapp.suhailparvez.weather.R;

import agency.tango.materialintroscreen.SlideFragment;

public class UsernameSlider extends SlideFragment {
    private EditText userName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_slide, container, false);

        userName =  view.findViewById(R.id.userName);
        return view;
    }

   public boolean CheckUserNameField () {
        Boolean ValEntered;

        if (userName.getText().length() <= 0){
            ValEntered = false;
        } else {
            ValEntered = true;
            SavePreferencesfirstName("user_firstName", userName.getText().toString());
        }
        return ValEntered;
    }

    private void SavePreferencesfirstName(String key, String value) {
        Log.i("valueName",value);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        return CheckUserNameField();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }
}
