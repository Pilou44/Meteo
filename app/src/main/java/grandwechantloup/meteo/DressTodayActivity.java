package grandwechantloup.meteo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

public class DressTodayActivity extends AppCompatActivity implements SendWeatherRequestListener {

    private static final String TAG = DressTodayActivity.class.getSimpleName();

    private static final int NB_MEASURES = 4;

    private double mMinTemp;
    private double mMaxTemp;
    private String mIcons[];
    private Handler mHandler;
    private int mStartTime;
    private int mStopTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_today);
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIcons = new String[NB_MEASURES *2];
        for (int i = 0 ; i < NB_MEASURES *2 ; i++){
            mIcons[i] = "";
        }

        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String homeLocation = PreferenceSingleton.getInstance().getHomeLocation(this);
        String workLocation = PreferenceSingleton.getInstance().getWorkLocation(this);

        mMinTemp = Double.MAX_VALUE;
        mMaxTemp = Double.MIN_VALUE;

        SendWeatherRequestTask homeTask = new SendWeatherRequestTask(this, this);
        homeTask.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_CITY_NAME, homeLocation);
        SendWeatherRequestTask workTask = new SendWeatherRequestTask(this, this);
        workTask.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_CITY_NAME, workLocation);
    }

    @Override
    public void onResult(JSONObject json) {
        try {
            String city = json.getJSONObject("city").getString("name");

            JSONArray list = json.getJSONArray("list");

            int index;
            if (city.equals(PreferenceSingleton.getInstance().getHomeLocation(this))) {
                index = 0;
            } else {
                index = 1;
            }

            for (int i = 0 ; i < list.length() && i < NB_MEASURES ; i++){
                JSONObject current = list.getJSONObject(i);
                int dt = current.getInt("dt");
                if (i == 0) {
                    mStartTime = dt;
                } else if (i == NB_MEASURES - 1 || i == list.length() - 1) {
                    mStopTime = dt;
                }
                Double minTemp = current.getJSONObject("main").getDouble("temp_min");
                if (minTemp < mMinTemp) {
                    mMinTemp = minTemp;
                }
                Double maxTemp = current.getJSONObject("main").getDouble("temp_max");
                if (maxTemp > mMaxTemp) {
                    mMaxTemp = maxTemp;
                }
                String icon = current.getJSONArray("weather").getJSONObject(0).getString("icon");
                mIcons[index * NB_MEASURES + i] = icon;
                Log.d(TAG, city + "@" + displayTime(dt) + " = " + minTemp + "°C, " + maxTemp + "°C, " + icon);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void update() {

    }

    private String displayTime(int dt) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        /* print your timestamp and double check it's the date you expect */
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String utcTime = sdf.format(new Date(((long)dt) * 1000));
        Log.d("UTC Time: ", utcTime);


        /* print your timestamp and double check it's the date you expect */
        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(((long)dt) * 1000));
        Log.d("Local Time: ", localTime);

        return localTime;
    }
}
