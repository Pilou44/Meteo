package grandwechantloup.meteo.elements;

import android.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import grandwechantloup.meteo.R;
import grandwechantloup.meteo.activities.RefreshableActivity;
import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

public class SelectCityDialog implements SendWeatherRequestListener {

    private static final String TAG = SelectCityDialog.class.getSimpleName();
    private final RefreshableActivity mActivity;
    private final int mId;
    private EditText mCityEditText;
    private AlertDialog mDialog;

    public SelectCityDialog(RefreshableActivity activity, int id) {
        mActivity = activity;
        mId = id;
    }


    public void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.select_city_title);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.select_city, null);

        mCityEditText = (EditText) dialogLayout.findViewById(R.id.select_city_edit);

        builder.setView(dialogLayout);

        builder.setPositiveButton(R.string.ok, null);
        builder.setNeutralButton(R.string.cancel, null);

        mDialog = builder.show();

        mDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendWeatherRequestTask task = new SendWeatherRequestTask(mActivity, SelectCityDialog.this);
                task.execute(SendWeatherRequestTask.CURRENT_WEATHER, SendWeatherRequestTask.FROM_CITY_NAME, mCityEditText.getText().toString());
            }
        });
    }

    @Override
    public void onResult(JSONObject json) {

        try {
            String city = json.getString("name");
            if (mCityEditText.getText().toString().startsWith(city)){
                Log.i(TAG, "City " + mCityEditText.getText().toString() + " has been found");
                switch (mId) {
                    case R.id.action_home:
                        LocalPreferenceManager.setHomeLocation(mActivity, city);
                        break;
                    case R.id.action_work:
                        LocalPreferenceManager.setWorkLocation(mActivity, city);
                        break;
                    case R.id.action_add_city:
                        LocalPreferenceManager.addCity(mActivity, city);
                        break;
                }
                mDialog.dismiss();
                mActivity.refresh();
            } else {
                Log.i(TAG, "City " + mCityEditText.getText().toString() + " has not been found, replace by " + city + "?");
                mCityEditText.setText(city);
                mDialog.setTitle(R.string.are_you_looking_for);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i(TAG, "City " + mCityEditText.getText().toString() + " has not been found");
            Toast.makeText(mActivity, R.string.city_not_found, Toast.LENGTH_LONG).show();
        }
    }
}
