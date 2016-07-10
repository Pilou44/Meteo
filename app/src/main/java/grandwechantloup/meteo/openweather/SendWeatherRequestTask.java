package grandwechantloup.meteo.openweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import grandwechantloup.meteo.R;

public class SendWeatherRequestTask extends AsyncTask<Object, Void, JSONObject> {
    public static final int FROM_LATLNG = 0;
    public static final int FROM_CITY_NAME = 1;

    public static final int CURRENT_WEATHER = 0;
    public static final int FORECAST = 1;

    private static final String TAG = SendWeatherRequestTask.class.getSimpleName();
    private final Context mContext;
    private final SendWeatherRequestListener mListener;

    public SendWeatherRequestTask(@NonNull Context context, @NonNull SendWeatherRequestListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        int when = (int)params[0];
        int type = (int)params[1];
        JSONObject mainObject = null;
        try {
            String urlString = mContext.getString(R.string.openmap_base_url);

            switch (when) {
                case CURRENT_WEATHER:
                    urlString += mContext.getString(R.string.openmap_current_weather);
                    break;
                case FORECAST:
                    urlString += mContext.getString(R.string.openmap_forecast);
                    break;
            }

            switch (type) {
                case FROM_LATLNG:
                    urlString += "lat=" + params[2] + "&lon=" + params[3];
                    break;
                case FROM_CITY_NAME:
                    urlString += "q=" + params[2];
                    break;
            }

            urlString += "&units=metric";
            urlString += "&appid=" + mContext.getString(R.string.openmap_key);

            Log.i(TAG, "URL: " + urlString);

            URL url = new URL(urlString);

            URLConnection urlConnection = url.openConnection();
            InputStream response = urlConnection.getInputStream();
            String res = readStream(response);

            Log.i(TAG, "Response: " + res);

            mainObject = new JSONObject(res);
            response.close();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }

        return mainObject;
    }

    private String readStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(JSONObject data) {
        mListener.onResult(data);
    }

}