package grandwechantloup.meteo.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

import grandwechantloup.meteo.R;
import grandwechantloup.meteo.elements.LocalPreferenceManager;
import grandwechantloup.meteo.elements.SelectCityDialog;
import grandwechantloup.meteo.elements.WeatherAtTime;
import grandwechantloup.meteo.elements.WeatherCity;
import grandwechantloup.meteo.elements.WeatherCityAdapter;
import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

public class WeatherByCityActivity extends RefreshableActivity implements SendWeatherRequestListener {

    private static final int NB_MEASURES = 6;
    private static final String TAG      = WeatherByCityActivity.class.getSimpleName();
    private static final String CURRENT  = "current";
    private WeatherCityAdapter mAdapter;
    private Dialog             mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_by_city);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView list = (ListView) findViewById(R.id.list);
        mAdapter = new WeatherCityAdapter(this);
        list.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        populate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_city, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_city) {
            SelectCityDialog dialog = new SelectCityDialog(this, id);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void populate() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.loading), true);

        mAdapter.clear();

        //Add current location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

            SendWeatherRequestTask task = new SendWeatherRequestTask(this, this, CURRENT);
            task.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_LATLNG, latitude, longitude);
        }

        HashSet<String> cities = LocalPreferenceManager.getCities(this);
        Log.i(TAG, cities.size() + " cities found");
        for (String city : cities) {
            SendWeatherRequestTask task = new SendWeatherRequestTask(this, this);
            task.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_CITY_NAME, city);
        }
    }

    @Override
    public void onResult(JSONObject json) {
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        try {
            WeatherCity city = new WeatherCity(json.getJSONObject("city").getString("name"));

            JSONArray list = json.getJSONArray("list");

            String option = json.optString(SendWeatherRequestTask.OPTION);

            for (int i = 0 ; i < list.length() && i < NB_MEASURES ; i++){
                JSONObject current = list.getJSONObject(i);

                double min = current.getJSONObject("main").getDouble("temp_min");

                double max = current.getJSONObject("main").getDouble("temp_max");

                int dt = current.getInt("dt");
                String icon = current.getJSONArray("weather").getJSONObject(0).getString("icon");
                WeatherAtTime wat = new WeatherAtTime(dt, icon, min, max);
                city.addWeatherAtTime(wat);
            }

            if (option.equals(CURRENT)) {
                city.setIsCurrentPosition();
                mAdapter.insert(city, 0);
            } else {
                mAdapter.add(city);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        populate();
    }
}
