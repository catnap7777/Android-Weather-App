package com.mobileapp.suhailparvez.weather.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.view.View;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.animations.IViewTranslation;

public class WelcomeSlider extends MaterialIntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        enableLastSlideAlphaExitTransition(true);

        getBackButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(new UsernameSlider());

        addSlide(new WelcomeToHomeSlider());

    }

    @Override
    public void onFinish() {
        super.onFinish();
        Intent x = new Intent(WelcomeSlider.this, HomeActivity.class);
        startActivity(x);
    }

}
