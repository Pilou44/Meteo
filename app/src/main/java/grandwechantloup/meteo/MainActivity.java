package grandwechantloup.meteo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;
import grandwechantloup.meteo.openweather.WeatherConditions;

public class MainActivity extends AppCompatActivity implements SendWeatherRequestListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Handler mHandler;
    private ImageView mBackgroundLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mBackgroundLayout = (ImageView) findViewById(R.id.background);
        Button head = (Button) findViewById(R.id.head);
        Button body = (Button) findViewById(R.id.body);

        head.setOnClickListener(this);
        body.setOnClickListener(this);

        mHandler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        }
    }

    @Override
    public void onResult(JSONObject json) {
        final AtomicInteger drawable = new AtomicInteger(0);
        try {
            JSONArray weather = json.getJSONArray("weather");
            JSONObject object = weather.getJSONObject(0);
            String id = object.getString("id");
            String main = object.getString("main");
            String description = object.getString("description");
            String icon = object.getString("icon");
            Log.i(TAG, main + ", " + description + ", " + icon);

            if (id.startsWith(WeatherConditions.WEATHER_CONDITION_CLEAR)) {
                drawable.set(R.drawable.clear_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_THUNDERSTORM)) {
                drawable.set(R.drawable.thunderstorm_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_DRIZZLE)) {
                drawable.set(R.drawable.drizzle_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_RAIN)) {
                drawable.set(R.drawable.rain_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_SNOW)) {
                drawable.set(R.drawable.snow_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_ATMOSPHERE)) {
                drawable.set(R.drawable.fog_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_CLOUDS)) {
                drawable.set(R.drawable.clouds_600);
            } else if (id.startsWith(WeatherConditions.WEATHER_CONDITION_EXTREME)) {
                drawable.set(R.drawable.extreme_600);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (drawable.get() != 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mBackgroundLayout.setImageDrawable(MainActivity.this.getResources().getDrawable(drawable.get()));
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
