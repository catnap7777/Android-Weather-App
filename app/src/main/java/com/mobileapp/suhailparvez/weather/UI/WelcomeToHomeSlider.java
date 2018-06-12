package com.mobileapp.suhailparvez.weather.UI;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileapp.suhailparvez.weather.R;

import java.util.Calendar;

import agency.tango.materialintroscreen.SlideFragment;

public class WelcomeToHomeSlider extends SlideFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_welcom_home, container, false);

        TextView greeting = view.findViewById(R.id.Greeting);
        ImageView imageView = view.findViewById(R.id.imageView);

        if (greetings()[1].equals("0")) {
            imageView.setImageResource(R.drawable.morning);
        } else {
            imageView.setImageResource(R.drawable.evening);
        }

        greeting.setText(greetings()[0]);

        return view;
    }

    public boolean GoToHome() {
        return true;
    }

    private String[] greetings() {

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = "Hey";
        String morning = "0";
        if (timeOfDay >= 0 && timeOfDay < 12) {
            greeting = "Good Morning";
            morning = "0";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            greeting = "Good Afternoon";
            morning = "1";
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            greeting = "Good Evening";
            morning = "1";
        } else if (timeOfDay >= 21 && timeOfDay < 24) {
            greeting = "Good Night";
            morning = "1";
        }
        return new String[]{greeting, morning};

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
        return GoToHome();
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.error_message);
    }
}
