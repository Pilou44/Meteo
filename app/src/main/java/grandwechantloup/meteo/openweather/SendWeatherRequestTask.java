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
    private static final String TAG = SendWeatherRequestTask.class.getSimpleName();
    private final Context mContext;
    private final SendWeatherRequestListener mListener;

    public SendWeatherRequestTask(@NonNull Context context, @NonNull SendWeatherRequestListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        int type = (int)params[0];
        String search = "";
        switch (type) {
            case FROM_LATLNG:
                double latitude = (double) params[1];
                double longitude = (double) params[2];
                search = "lat=" + latitude + "&lon=" + longitude;
                break;
        }
        JSONObject mainObject = null;
        try {
            search += "&appid=" + mContext.getString(R.string.openmap_key);
            //String searchStr = URLEncoder.encode(search, "utf-8");
            String urlString = mContext.getString(R.string.openmap_base_url) + search;

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