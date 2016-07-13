package grandwechantloup.meteo.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grandwechantloup.meteo.R;
import grandwechantloup.meteo.elements.LocalPreferenceManager;
import grandwechantloup.meteo.elements.SelectCityDialog;
import grandwechantloup.meteo.elements.WeatherAtTime;
import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

public class DressTodayActivity extends RefreshableActivity implements SendWeatherRequestListener {

    private static final int NB_MEASURES = 4;

    private double mMinTemp;
    private double mMaxTemp;
    private String mIcons[];
    private Handler mHandler;
    private int mStartTime;
    private int mStopTime;
    private ArrayList<ImageView> mImageViews;
    private ImageLoader mImageLoader;
    private TextView mTempMaxView;
    private TextView mTempMinView;
    private TextView mTimeView;
    private TextView mTitle;
    private String mWork;
    private String mHome;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dress_today);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mIcons = new String[NB_MEASURES *2];
        for (int i = 0 ; i < NB_MEASURES *2 ; i++){
            mIcons[i] = "";
        }

        mImageViews = new ArrayList<>();
        mImageViews.add((ImageView) findViewById(R.id.image_0_0));
        mImageViews.add((ImageView) findViewById(R.id.image_0_1));
        mImageViews.add((ImageView) findViewById(R.id.image_0_2));
        mImageViews.add((ImageView) findViewById(R.id.image_0_3));
        mImageViews.add((ImageView) findViewById(R.id.image_1_0));
        mImageViews.add((ImageView) findViewById(R.id.image_1_1));
        mImageViews.add((ImageView) findViewById(R.id.image_1_2));
        mImageViews.add((ImageView) findViewById(R.id.image_1_3));

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
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.dismiss();
        }

        for (int i = 0 ; i < NB_MEASURES *2 ; i++){
            if (mIcons[i].length() > 0) {
                mImageLoader.displayImage(String.format(getResources().getConfiguration().locale, getString(R.string.icons_url), mIcons[i]), mImageViews.get(i));
            } else {
                mImageViews.get(i).setImageDrawable(null);
            }
        }

        mTempMinView.setText(String.format(getResources().getConfiguration().locale, getString(R.string.display_temp), mMinTemp));
        mTempMaxView.setText(String.format(getResources().getConfiguration().locale, getString(R.string.display_temp), mMaxTemp));

        mTimeView.setText(String.format(getResources().getConfiguration().locale, getString(R.string.dress_date), WeatherAtTime.displayTime(mStartTime, getString(R.string.dress_time_format)), WeatherAtTime.displayTime(mStopTime, getString(R.string.dress_time_format))));

        mTitle.setText(String.format(getResources().getConfiguration().locale, getString(R.string.dress_title), mHome, mWork));
    }

    @Override
    public void refresh() {
        mProgressDialog = ProgressDialog.show(this, getString(R.string.please_wait), getString(R.string.loading), true);

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
