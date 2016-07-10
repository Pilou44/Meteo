package grandwechantloup.meteo;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Administrateur on 10/07/16.
 */
public class PreferenceSingleton {
    private static final String HOME_LOCATION = "home";
    private static final String WORK_LOCATION = "work";

    private static PreferenceSingleton ourInstance = new PreferenceSingleton();

    public static PreferenceSingleton getInstance() {
        return ourInstance;
    }

    private PreferenceSingleton() {
    }

    public String getHomeLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(HOME_LOCATION, "Cergy,fr");
    }

    public String getWorkLocation(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(WORK_LOCATION, "Paris,fr");
    }
}
