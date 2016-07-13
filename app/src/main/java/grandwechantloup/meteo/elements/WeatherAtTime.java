package grandwechantloup.meteo.elements;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class WeatherAtTime {
    private static final boolean DEBUG = false;
    private final double mMin;
    private final double mMax;
    private final int mTime;
    private final String mIcon;

    public WeatherAtTime(int time, String icon, double min, double max) {
        mTime = time;
        mIcon = icon;
        mMin = min;
        mMax = max;
    }

    public String getTime(@SuppressWarnings("SameParameterValue") String style) {
        return displayTime(mTime, style);
    }

    public String getIcon() {
        return mIcon;
    }

    public static String displayTime(int dt, String style) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        if (DEBUG) {
            Log.d("Time zone: ", tz.getDisplayName());
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(style);

        if (DEBUG) {
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcTime = sdf.format(new Date(((long) dt) * 1000));
            Log.d("UTC Time: ", utcTime);
        }

        sdf.setTimeZone(tz);
        String localTime = sdf.format(new Date(((long)dt) * 1000));
        if (DEBUG) {
            Log.d("Local Time: ", localTime);
        }

        return localTime;
    }


    public double getTempMin() {
        return mMin;
    }

    public double getTempMax() {
        return mMax;
    }

}
