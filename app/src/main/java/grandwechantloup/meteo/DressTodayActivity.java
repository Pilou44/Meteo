package grandwechantloup.meteo;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

public class DressTodayActivity extends RefreshableActivity implements SendWeatherRequestListener {

    private static final String TAG = DressTodayActivity.class.getSimpleName();

    private static final int NB_MEASURES = 4;

    private double mMinTemp;
    private double mMaxTemp;
    private String mIcons[];
    private Handler mHandler;
    private int mStartTime;
    private int mStopTime;
    private ImageLoader mImageLoader;
    private ImageView mImage00;
    private ImageView mImage01;
    private ImageView mImage02;
    private ImageView mImage03;
    private ImageView mImage10;
    private ImageView mImage11;
    private ImageView mImage12;
    private ImageView mImage13;
    private TextView mTempMaxView;
    private TextView mTempMinView;
    private TextView mTimeView;
    private TextView mTitle;
    private String mWork;
    private String mHome;

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

        mImage00 = (ImageView) findViewById(R.id.image_0_0);
        mImage01 = (ImageView) findViewById(R.id.image_0_1);
        mImage02 = (ImageView) findViewById(R.id.image_0_2);
        mImage03 = (ImageView) findViewById(R.id.image_0_3);
        mImage10 = (ImageView) findViewById(R.id.image_1_0);
        mImage11 = (ImageView) findViewById(R.id.image_1_1);
        mImage12 = (ImageView) findViewById(R.id.image_1_2);
        mImage13 = (ImageView) findViewById(R.id.image_1_3);

        mTempMinView = (TextView) findViewById(R.id.temp_min);
        mTempMaxView = (TextView) findViewById(R.id.temp_max);

        mTimeView = (TextView) findViewById(R.id.time);

        mTitle = (TextView) findViewById(R.id.title);

        mHandler = new Handler();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dress_today, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home || id == R.id.action_work) {
            SelectCityDialog dialog = new SelectCityDialog(this, id);
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(JSONObject json) {
        try {
            String city = json.getJSONObject("city").getString("name");

            JSONArray list = json.getJSONArray("list");

            int index;
            if (LocalPreferenceManager.getHomeLocation(this).startsWith(city)) {
                index = 0;
                mHome = city;
            } else {
                index = 1;
                mWork = city;
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
                Log.d(TAG, "index: " + (index * NB_MEASURES + i));
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
        if (mIcons[0].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[0] + ".png", mImage00);
        } else {
            mImage00.setImageDrawable(null);
        }
        if (mIcons[1].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[1] + ".png", mImage01);
        } else {
            mImage01.setImageDrawable(null);
        }
        if (mIcons[2].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[2] + ".png", mImage02);
        } else {
            mImage02.setImageDrawable(null);
        }
        if (mIcons[3].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[3] + ".png", mImage03);
        } else {
            mImage03.setImageDrawable(null);
        }
        if (mIcons[4].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[4] + ".png", mImage10);
        } else {
            mImage10.setImageDrawable(null);
        }
        if (mIcons[5].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[5] + ".png", mImage11);
        } else {
            mImage11.setImageDrawable(null);
        }
        if (mIcons[6].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[6] + ".png", mImage12);
        } else {
            mImage12.setImageDrawable(null);
        }
        if (mIcons[7].length() > 0) {
            mImageLoader.displayImage("http://openweathermap.org/img/w/" + mIcons[7] + ".png", mImage13);
        } else {
            mImage13.setImageDrawable(null);
        }

        mTempMinView.setText("Min\n" + mMinTemp + "°C");
        mTempMaxView.setText("Max\n" + mMaxTemp + "°C");

        mTimeView.setText("From " + displayTime(mStartTime) + " to " + displayTime(mStopTime));

        mTitle.setText(mHome + " / " + mWork);
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

    @Override
    public void refresh() {
        String homeLocation = LocalPreferenceManager.getHomeLocation(this);
        String workLocation = LocalPreferenceManager.getWorkLocation(this);

        mIcons = new String[NB_MEASURES *2];
        for (int i = 0 ; i < NB_MEASURES *2 ; i++){
            mIcons[i] = "";
        }

        mMinTemp = Double.MAX_VALUE;
        mMaxTemp = Double.MIN_VALUE;

        mHome = "";
        mWork = "";

        SendWeatherRequestTask homeTask = new SendWeatherRequestTask(this, this);
        homeTask.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_CITY_NAME, homeLocation);
        SendWeatherRequestTask workTask = new SendWeatherRequestTask(this, this);
        workTask.execute(SendWeatherRequestTask.FORECAST, SendWeatherRequestTask.FROM_CITY_NAME, workLocation);
    }
}
