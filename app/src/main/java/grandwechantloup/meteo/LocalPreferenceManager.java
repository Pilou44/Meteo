package grandwechantloup.meteo;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Administrateur on 10/07/16.
 */
public class LocalPreferenceManager {
    private static final String HOME_LOCATION = "home";
    private static final String WORK_LOCATION = "work";

    public static String getHomeLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(HOME_LOCATION, "Cergy,fr");
    }

    public static String getWorkLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(WORK_LOCATION, "Paris,fr");
    }

    public static void setHomeLocation(Context context, String city) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(HOME_LOCATION, city).apply();
    }

    public static void setWorkLocation(Context context, String city) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(WORK_LOCATION, city).apply();
    }
}
