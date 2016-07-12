package grandwechantloup.meteo;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by gbeguin on 12/07/2016.
 */
public class WeatherAtTime {
    private static final String DEF_STYLE = "dd/MM/yyyy HH:mm:ss";
    private int mTime;
    private String mIcon;

    public WeatherAtTime(int time, String icon) {
        mTime = time;
        mIcon = icon;
    }

    public String getTime(String style) {
        return displayTime(mTime, style);
    }

    public String getIcon() {
        return mIcon;
    }

    public static String displayTime(int dt) {
        return displayTime(dt, DEF_STYLE);
    }

    public static String displayTime(int dt, String style) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat(style);

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
