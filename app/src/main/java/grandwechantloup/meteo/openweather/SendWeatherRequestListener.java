package grandwechantloup.meteo.openweather;

import org.json.JSONObject;

public interface SendWeatherRequestListener {
    void onResult(JSONObject json);
}
