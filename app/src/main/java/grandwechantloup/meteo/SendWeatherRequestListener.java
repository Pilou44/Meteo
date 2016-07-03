package grandwechantloup.meteo;

import org.json.JSONObject;

/**
 * Created by Administrateur on 02/07/16.
 */
public interface SendWeatherRequestListener {
    void onResult(JSONObject json);
}
