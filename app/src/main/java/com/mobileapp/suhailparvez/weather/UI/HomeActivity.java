package com.mobileapp.suhailparvez.weather.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mobileapp.suhailparvez.weather.CALLBACK.WeatherService;
import com.mobileapp.suhailparvez.weather.DATABASE.WeatherDatabase;
import com.mobileapp.suhailparvez.weather.HELPER.Constant;
import com.mobileapp.suhailparvez.weather.HELPER.Utils;
import com.mobileapp.suhailparvez.weather.POJO.DatabasePOJO;
import com.mobileapp.suhailparvez.weather.POJO.WeatherPOJO;
import com.mobileapp.suhailparvez.weather.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener ,
         View.OnClickListener{

    private NavigationView mDrawer;
    private DrawerLayout mDrawerLayout;
    private WeatherDatabase mWeatherDatabase;
    private ShowcaseView showcaseView;
    public static int counter;

    private TextView main,description,temp,humidity,min,max,speed,name,country,sunrise,sunset;
    private ImageView image1;

    public static String user_firstName = null;
    public static javax.crypto.SecretKey SecretKey = null;
    public static IvParameterSpec Iv = null;
    public static byte[] iv = new byte[16];
    public static byte[] Ciphertext = null;
    public static String saltGlobal = null;
    private IvParameterSpec mIvParameterSpec;
    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 1000;
    private static final int KEY_LENGTH = 256; // bits

    private int mSelectedId;
    private boolean mUserSawDrawer = false;
    private static final String FIRST_TIME = "first_time";
    private static final String SELECTED_ITEM_ID = "selected_item_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        CollapsingToolbarLayout mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawer = findViewById(R.id.main_drawer);
        mWeatherDatabase = new WeatherDatabase(this);
        LoadPreferencesfirstName();
        configViews();

        View hView = mDrawer.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.GreetingHeader);
        nav_user.setText(greetings()[0]);
        TextView nav_no = hView.findViewById(R.id.UserNameHeader);
        nav_no.setText(user_firstName);
        Log.i("MainName",user_firstName);
        ImageView headerImage = hView.findViewById(R.id.imageView);
        if(greetings()[1].equals("0")){ headerImage.setImageResource(R.drawable.morning);}
        else {headerImage.setImageResource(R.drawable.evening);}

        mDrawer.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        //show Navigation drawer the first time
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }

        int mSelectedId = savedInstanceState == null ? R.id.navigation_item_2 : savedInstanceState.getInt(SELECTED_ITEM_ID);
        navigate(mSelectedId);

        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));

        getNextSalt();
        mIvParameterSpec = new IvParameterSpec(iv);

        try {
            hashPassword((getString(R.string.parse_application_id)), saltGlobal);
            Encrypt();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException | BadPaddingException
                | NoSuchPaddingException | InvalidParameterSpecException | UnsupportedEncodingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        loadWeatherFeed();

    }

    private String[] greetings() {
        
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String greeting = "Hey";
        String morning = "0";
        if(timeOfDay >= 0 && timeOfDay < 12){
            greeting = "Good Morning";
            morning = "0";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            greeting = "Good Afternoon";
            morning = "1";
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            greeting = "Good Evening";
            morning = "1";
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            greeting = "Good Night";
            morning = "1";
        }
      return new String[]{ greeting, morning };

    }

    private void loadWeatherFeed() {

        if (shouldAskPermissions()) {

            Intent intent = new Intent(this, PermissionAboveMarshmellow.class);
            this.startActivity(intent);

            WeatherFeed();

        } else {

            WeatherFeed();
        }

    }

    private void Decrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException,
            IllegalBlockSizeException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException {
        /* Decrypt the message, given derived key and initialization vector. */
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, SecretKey, Iv);
        String plaintext = new String(cipher.doFinal(Ciphertext), "UTF-8");
        UserLiveData(plaintext);
    }

    private void Encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException,
            UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {

        /* Encrypt the message. */
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKey);
        RANDOM.nextBytes(iv);
        Iv = new IvParameterSpec(iv);
        Ciphertext = cipher.doFinal((getString(R.string.parse_application_id)).getBytes("UTF-8"));
    }

    public static byte[] getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        saltGlobal = Arrays.toString(salt);
        return salt;
    }

    private void hashPassword(String password, String salt) throws InvalidKeySpecException, NoSuchAlgorithmException {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                passwordChars,
                saltBytes,
                ITERATIONS,
                KEY_LENGTH
        );

        /* Derive the key, given password and salt. */
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey tmp = factory.generateSecret(spec);
        SecretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

    }

    private void WeatherFeed() {

        if (getNetworkAvailability()) {
            try {
                Decrypt();
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | IllegalBlockSizeException |
                    UnsupportedEncodingException | BadPaddingException | InvalidAlgorithmParameterException
                    | InvalidKeyException e) {
                e.printStackTrace();
            }
        } else {
            UseLocalData();
        }
    }

    private void UserLiveData(String passwordString) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constant.HTTP.CLIENTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GPSTracker gpsTracker = new GPSTracker(this);
        WeatherService service = retrofit.create(WeatherService.class);

        Call<WeatherPOJO> listCall = service.getWeather(gpsTracker.getLatitude(),gpsTracker.getLongitude(),
                "metric",passwordString);
        listCall.enqueue(new Callback<WeatherPOJO>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Response<WeatherPOJO> response, Retrofit retrofit) {

                if (response.isSuccess()) {
                    WeatherPOJO weatherList = response.body();

                    Snackbar snackbar = Snackbar
                            .make(mDrawerLayout, "Loading live data", Snackbar.LENGTH_LONG);
                    ViewGroup group = (ViewGroup) snackbar.getView();
                    group.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
                    snackbar.show();

                    for (int z = 0; z < weatherList.getWeather().size(); z++) {
                        Log.i("NAMEEEEEEEE", String.valueOf(weatherList.getWeather().get(z).getMain()));

                        if (mWeatherDatabase.TableNotEmpty()){

                            mWeatherDatabase.updateValues("1",weatherList.getWeather().get(z).getMain(),
                                    weatherList.getWeather().get(z).getDescription(),
                                    weatherList.getWeather().get(z).getIcon(),weatherList.getMain().getTemp().toString(),
                                    weatherList.getMain().getHumidity().toString(),weatherList.getMain().getTempMin().toString(),
                                    weatherList.getMain().getTempMax().toString(),weatherList.getWind().getSpeed().toString(),
                                    weatherList.getSys().getCountry(),weatherList.getSys().getSunrise().toString(),
                                    weatherList.getSys().getSunset().toString(),weatherList.getName());


                        } else {

                            mWeatherDatabase.addDataInDB(weatherList.getWeather().get(z).getMain(),
                                    weatherList.getWeather().get(z).getDescription(),
                                    weatherList.getWeather().get(z).getIcon(),weatherList.getMain().getTemp().toString(),
                                    weatherList.getMain().getHumidity().toString(),weatherList.getMain().getTempMin().toString(),
                                    weatherList.getMain().getTempMax().toString(),weatherList.getWind().getSpeed().toString(),
                                    weatherList.getSys().getCountry(),weatherList.getSys().getSunrise().toString(),
                                    weatherList.getSys().getSunset().toString(),weatherList.getName());

                        }

                        main.setText(String.valueOf(weatherList.getWeather().get(z).getMain()));
                        description.setText(String.valueOf(weatherList.getWeather().get(z).getDescription()));
                        temp.setText(String.valueOf(weatherList.getMain().getTemp())+ GetUnit(String.valueOf(getApplication().getResources().getConfiguration().locale.getCountry())));
                        humidity.setText(String.valueOf(weatherList.getMain().getHumidity())+ " per cent");
                        min.setText(String.valueOf(weatherList.getMain().getTempMin())+ " min");
                        max.setText(String.valueOf(weatherList.getMain().getTempMax())+ " max");
                        speed.setText(String.valueOf(weatherList.getWind().getSpeed()) + " miles/hour");
                        name.setText(String.valueOf(weatherList.getName()));
                        country.setText(String.valueOf(weatherList.getSys().getCountry()));
                        sunrise.setText(UnixTime(Long.parseLong(String.valueOf(weatherList.getSys().getSunrise()))));
                        sunset.setText(UnixTime(Long.parseLong(String.valueOf(weatherList.getSys().getSunset()))));

                        switch (weatherList.getWeather().get(z).getIcon()){
                            case "01d":
                                image1.setImageResource(R.drawable.sunny);
                                break;
                            case "02d":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "03d":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "04d":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "04n":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "10d":
                                image1.setImageResource(R.drawable.rain);
                                break;
                            case "11d":
                                image1.setImageResource(R.drawable.storm);
                                break;
                            case "13d":
                                image1.setImageResource(R.drawable.snowflake);
                                break;
                            case "01n":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "02n":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "03n":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "10n":
                                image1.setImageResource(R.drawable.cloud);
                                break;
                            case "11n":
                                image1.setImageResource(R.drawable.rain);
                                break;
                            case "13n":
                                image1.setImageResource(R.drawable.snowflake);
                                break;

                        }
                    }
                } else {
                    int sc = response.code();
                    switch (sc) {
                        case 400:
                            Log.e("Error 400", "Bad Request");
                            NotConnected();
                            break;
                        case 404:
                            Log.e("Error 404", "Not Found");
                            NotConnected();
                            break;
                        default:
                            Log.e("Error", "Generic Error");
                            NotConnected();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("Errorrrrr", t.getMessage());
                NotConnected();
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private String UnixTime(long timex) {

        Date date = new Date(timex *1000L);
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    private String GetUnit(String value) {
        Log.i("unitttttt", value);
        String val = "°C";

        if (("US".equals(value)) || ("LR".equals(value)) || ("MM".equals(val))) {
            val = "°F";
        }

        return val;
    }

    @SuppressLint("SetTextI18n")
    private void UseLocalData() {

        List<DatabasePOJO> dataList = mWeatherDatabase.getAllData();

        Snackbar snackbar = Snackbar
                .make(mDrawerLayout, "Loading local stored data", Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        snackbar.show();

        for (DatabasePOJO data : dataList) {

            main.setText(String.valueOf(data.getMain()));
            description.setText(String.valueOf(data.getDescription()));
            temp.setText(String.valueOf(data.getTemp())+ GetUnit(String.valueOf(this.getResources().getConfiguration().locale.getCountry())));
            humidity.setText(String.valueOf(data.getHumidity())+ " per cent");
            min.setText(String.valueOf(data.getTemp_min()) + " min");
            max.setText(String.valueOf(data.getTemp_max())+ " max");
            speed.setText(String.valueOf(data.getSpeed()) + " miles/hour");
            name.setText(String.valueOf(data.getName()));
            country.setText(String.valueOf(data.getCountry()));
            sunrise.setText(UnixTime(Long.parseLong(String.valueOf(data.getSunrise()))));
            sunset.setText(UnixTime(Long.parseLong(String.valueOf(data.getSunset()))));


            switch (data.getIcon()){
                case "01d":
                    image1.setImageResource(R.drawable.sunny);
                    break;
                case "02d":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "03d":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "04d":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "04n":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "10d":
                    image1.setImageResource(R.drawable.rain);
                    break;
                case "11d":
                    image1.setImageResource(R.drawable.storm);
                    break;
                case "13d":
                    image1.setImageResource(R.drawable.snowflake);
                    break;
                case "01n":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "02n":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "03n":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "10n":
                    image1.setImageResource(R.drawable.cloud);
                    break;
                case "11n":
                    image1.setImageResource(R.drawable.rain);
                    break;
                case "13n":
                    image1.setImageResource(R.drawable.snowflake);
                    break;

            }

        }

    }

    private void NotConnected(){

        Snackbar snackbar = Snackbar
                .make(mDrawerLayout, "Not connected to the api.", Snackbar.LENGTH_LONG);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        snackbar.show();

    }

    private void configViews() {

        main = findViewById(R.id.main);
        description = findViewById(R.id.description);
        temp = findViewById(R.id.temp);
        humidity = findViewById(R.id.humidity);
        min = findViewById(R.id.min);
        max = findViewById(R.id.max);
        speed = findViewById(R.id.speed);
        name = findViewById(R.id.name);
        country = findViewById(R.id.country);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);

        image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);
        ImageView image3 = findViewById(R.id.image3);
        ImageView image4 = findViewById(R.id.image4);
        ImageView image5 = findViewById(R.id.image5);
        ImageView image6 = findViewById(R.id.image6);
        ImageView image7 = findViewById(R.id.image7);

        image2.setImageResource(R.drawable.humidity);
        image3.setImageResource(R.drawable.temperature);
        image4.setImageResource(R.drawable.wind);
        image5.setImageResource(R.drawable.location);
        image6.setImageResource(R.drawable.sunrise);
        image7.setImageResource(R.drawable.sunset);

    }

    private void navigate(int mSelectedId) {

        if (mSelectedId == R.id.navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            loadWeatherFeed();
        }

        if (mSelectedId == R.id.close) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            //closing and sending the app to the background , the session starts from login activity.
            moveTaskToBack(true);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();
        navigate(mSelectedId);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_help) {
            counter = 0;
            callShowcaseView();
            return true;
        }

        if (id == R.id.sync) {
            loadWeatherFeed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("PrivateResource")
    private void callShowcaseView() {

        final Toolbar toolbar =  findViewById(R.id.toolbar);
        configViews();

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        if (diagonalInches >= 6.5) {
            // 6.5inch device or bigger

            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            //24sp
            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
            title.setColor(Color.rgb(228, 76, 61));

            TextPaint text = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            text.setTextSize(getResources().getDimension(R.dimen.abc_text_size_headline_material));
            text.setColor(Color.rgb(225, 225, 225));

            Target homeTarget = new Target() {
                @Override
                public Point getPoint() {
                    // Get approximate position of home icon's center
                    int actionBarSize = toolbar.getMeasuredState();
                    int x = actionBarSize / 2;
                    int y = actionBarSize / 2;
                    return new Point(x, y);
                }
            };

            showcaseView = new ShowcaseView.Builder(HomeActivity.this)
                    .setTarget(homeTarget)
                    .setOnClickListener(this)
                    .withHoloShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText("Explore the app using the navigation")
                    .setContentTitle("Navigation Bar  ")
                    .setContentTitlePaint(title)
                    .setContentTextPaint(text)
                    .build();
            showcaseView.setButtonText(getString(R.string.next));

        } else {
            // smaller device
            //18sp
            TextPaint title = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            title.setTextSize(getResources().getDimension(R.dimen.abc_text_size_title_material));
            title.setColor(Color.rgb(228, 76, 61));

            TextPaint text = new TextPaint(Paint.ANTI_ALIAS_FLAG);
            text.setTextSize(getResources().getDimension(R.dimen.abc_text_size_title_material));
            text.setColor(Color.rgb(255, 255, 255));

            Target homeTarget = new Target() {
                @Override
                public Point getPoint() {
                    // Get approximate position of home icon's center
                    int actionBarSize = toolbar.getMeasuredState();
                    int x = actionBarSize / 2;
                    int y = actionBarSize / 2;
                    return new Point(x, y);
                }
            };

            showcaseView = new ShowcaseView.Builder(HomeActivity.this)
                    .setTarget(homeTarget)
                    .setOnClickListener(this)
                    .withHoloShowcase()
                    .setStyle(R.style.CustomShowcaseTheme2)
                    .setContentText("Explore the app using the navigation")
                    .setContentTitle("Navigation Bar ")
                    .setContentTitlePaint(title)
                    .setContentTextPaint(text)
                    .build();
            showcaseView.setButtonText(getString(R.string.next));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public boolean getNetworkAvailability() {
        return Utils.isNetworkAvailable(getApplicationContext());
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    private void LoadPreferencesfirstName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user_firstName = sharedPreferences.getString("user_firstName", "");
    }

    @Override
    public void onClick(View v) {
        switch (counter) {
            case 0:
                main =  findViewById(R.id.main);
                showcaseView.setShowcase(new ViewTarget(main), true);
                showcaseView.setContentTitle("Weather Conditions");
                showcaseView.setContentText("Present weather conditions will be shown according to the users location");
                setAlpha(1.0f, main, speed, temp, name, sunrise);
                break;

            case 1:
                temp = findViewById(R.id.temp);
                showcaseView.setShowcase(new ViewTarget(temp), true);
                showcaseView.setContentTitle("Present Temperature");
                showcaseView.setContentText("Present temperature and humidity level.");
                setAlpha(0.1f,temp);
                break;

            case 2:

                speed = findViewById(R.id.speed);
                showcaseView.setShowcase(new ViewTarget(speed), true);
                showcaseView.setContentTitle("Current wind speed");
                showcaseView.setContentText("Current wind speed in metric unit");
                setAlpha(1.0f, speed);
                break;

            case 3:

                name = findViewById(R.id.name);
                showcaseView.setShowcase(new ViewTarget(name), true);
                showcaseView.setContentTitle("Current location");
                showcaseView.setContentText("Current location and the country.");
                setAlpha(1.0f, name);
                break;

            case 4:
                sunrise = findViewById(R.id.sunrise);
                showcaseView.setShowcase(new ViewTarget(sunrise), true);
                showcaseView.setContentTitle("Sunrise");
                showcaseView.setContentText("Sunrise time.");
                setAlpha(1.0f,sunrise);
                break;

            case 5:

                sunset = findViewById(R.id.sunset);
                showcaseView.setShowcase(new ViewTarget(sunset), true);
                showcaseView.setContentTitle("Sunset");
                showcaseView.setContentText("Sunset time.");
                showcaseView.setButtonText("Close");
                setAlpha(1.0f, sunset);
                break;

            case 6:
                showcaseView.hide();
                setAlpha(1.0f, main, temp, speed, name, sunrise, sunset);
                break;
        }
        counter++;
    }

    private void setAlpha(float alpha, View... views) {
        for (View view : views) {
            view.setAlpha(alpha);
        }
    }
}


