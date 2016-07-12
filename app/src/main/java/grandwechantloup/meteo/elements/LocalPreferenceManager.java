package grandwechantloup.meteo.elements;

import android.content.Context;
import android.preference.PreferenceManager;

import java.util.HashSet;

import grandwechantloup.meteo.R;

public class LocalPreferenceManager {
    private static final String HOME_LOCATION = "home";
    private static final String WORK_LOCATION = "work";
    private static final String CITIES        = "cities";

    public static String getHomeLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(HOME_LOCATION, context.getString(R.string.default_home));
    }

    public static String getWorkLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(WORK_LOCATION, context.getString(R.string.default_work));
    }

    public static void setHomeLocation(Context context, String city) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(HOME_LOCATION, city).apply();
    }

    public static void setWorkLocation(Context context, String city) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(WORK_LOCATION, city).apply();
    }

    public static void addCity(Context context, String city) {
        HashSet<String> set = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(CITIES, new HashSet<String>());
        set.add(city);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(CITIES, set).apply();
    }

    public static HashSet<String> getCities(Context context) {
        return (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(CITIES, new HashSet<String>());
    }

    public static void removeCity(Context context, String city) {
        HashSet<String> set = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet(CITIES, new HashSet<String>());
        set.remove(city);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet(CITIES, set).apply();
    }
}
