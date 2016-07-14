package grandwechantloup.meteo.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import grandwechantloup.meteo.R;
import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;
import grandwechantloup.meteo.openweather.WeatherConditions;

public class MainActivity extends AppCompatActivity implements SendWeatherRequestListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler;
    private ImageView mBackgroundLayout;
    private ImageView mHead;
    private ImageView mBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mBackgroundLayout = (ImageView) findViewById(R.id.background);
        mHead = (ImageView) findViewById(R.id.head);
        mBody = (ImageView) findViewById(R.id.body);

        mHead.setOnClickListener(this);
        mBody.setOnClickListener(this);

        mHandler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            refreshWeatherData();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (    grantResults.length == 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                refreshWeatherData();
            }
        }
    }

    private void refreshWeatherData() {
        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Location gpsLocation = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            long gpsTime = 0;
            if (gpsLocation != null) {
                gpsTime = gpsLocation.getTime();
            }

            Location networkLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            long networkTime = networkLocation.getTime();

            double latitude;
            double longitude;

            if (gpsTime > networkTime) {
                //noinspection ConstantConditions
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
            } else {
                latitude = networkLocation.getLatitude();
                longitude = networkLocation.getLongitude();
            }

            SendWeatherRequestTask task = new SendWeatherRequestTask(this, this);
            task.execute(SendWeatherRequestTask.CURRENT_WEATHER, SendWeatherRequestTask.FROM_LATLNG, latitude, longitude);
        } catch (SecurityException e){
            Log.e(TAG, "Permission denied");
            e.printStackTrace();
        }
    }

    @Override
    public void onResult(JSONObject json) {
        final AtomicInteger background = new AtomicInteger(0);
        final AtomicInteger head = new AtomicInteger(R.drawable.sun);
        final AtomicInteger body = new AtomicInteger(R.drawable.jacket);
        try {
            JSONArray weather = json.getJSONArray("weather");
            JSONObject object = weather.getJSONObject(0);
            String id = object.getString("id");

            if (id.startsWith(WeatherConditions.WEATHER_CONDITION_CLEAR)) {
                background.set(R.drawable.clear_600);
                head.set(R.drawable.sun);
                body.set(R.drawable.jacket);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_THUNDERSTORM)) {
                background.set(R.drawable.thunderstorm_600);
                head.set(R.drawable.lightning);
                body.set(R.drawable.rain_coat);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_DRIZZLE)) {
                background.set(R.drawable.drizzle_600);
                head.set(R.drawable.dark_cloud);
                body.set(R.drawable.rain_coat);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_RAIN)) {
                background.set(R.drawable.rain_600);
                head.set(R.drawable.rain);
                body.set(R.drawable.rain_coat);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_SNOW)) {
                background.set(R.drawable.snow_600);
                head.set(R.drawable.snow);
                body.set(R.drawable.snow_coat);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_ATMOSPHERE)) {
                background.set(R.drawable.fog_600);
                head.set(R.drawable.dark_cloud);
                body.set(R.drawable.jacket);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_CLOUDS)) {
                background.set(R.drawable.clouds_600);
                head.set(R.drawable.cloud);
                body.set(R.drawable.jacket);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_EXTREME)) {
                background.set(R.drawable.extreme_600);
                head.set(R.drawable.lightning);
                body.set(R.drawable.snow_coat);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (background.get() != 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            mBackgroundLayout.setImageDrawable(MainActivity.this.getResources().getDrawable(background.get(), null));
                        } else {
                            //noinspection deprecation
                            mBackgroundLayout.setImageDrawable(MainActivity.this.getResources().getDrawable(background.get()));
                        }
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBackgroundLayout.setImageDrawable(null);
                    }
                });
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mHead.setImageDrawable(MainActivity.this.getResources().getDrawable(head.get(), null));
                        mBody.setImageDrawable(MainActivity.this.getResources().getDrawable(body.get(), null));
                    } else {
                        //noinspection deprecation
                        mHead.setImageDrawable(MainActivity.this.getResources().getDrawable(head.get()));
                        //noinspection deprecation
                        mBody.setImageDrawable(MainActivity.this.getResources().getDrawable(body.get()));
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.head:
                intent = new Intent(this, WeatherByCityActivity.class);
                break;
            case R.id.body:
                intent = new Intent(this, DressTodayActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}
