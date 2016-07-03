package grandwechantloup.meteo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements SendWeatherRequestListener {

    private static final String TAG = MainActivity.class.getSimpleName();

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
            long gpsTime = gpsLocation.getTime();

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
            task.execute(SendWeatherRequestTask.FROM_LATLNG, latitude, longitude);
        } catch (SecurityException e){
            Log.e(TAG, "Permission denied");
        }
    }

    @Override
    public void onResult(JSONObject json) {
        try {
            JSONObject weather = json.getJSONObject("weather");
            String main = weather.getString("main");
            String description = weather.getString("description");
            String icon = weather.getString("icon");
            Log.i(TAG, main + ", " + description + ", " + icon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
