package grandwechantloup.meteo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;

import grandwechantloup.meteo.openweather.SendWeatherRequestListener;
import grandwechantloup.meteo.openweather.SendWeatherRequestTask;

/**
 * Created by Administrateur on 11/07/16.
 */
public class SelectCityDialog implements SendWeatherRequestListener {

    private final Activity mActivity;
    private final int mId;

    public SelectCityDialog(Activity activity, int id) {
        mActivity = activity;
        mId = id;
    }


    public void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.select_city_title);

        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.select_city, null);

        final EditText cityEditText = (EditText) dialoglayout.findViewById(R.id.select_city_edit);

        builder.setView(dialoglayout);

        builder.setPositiveButton(R.string.ok, null);
        builder.setNeutralButton(R.string.cancel, null);

        AlertDialog dialog = builder.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendWeatherRequestTask task = new SendWeatherRequestTask(mActivity, SelectCityDialog.this);
                task.execute(SendWeatherRequestTask.CURRENT_WEATHER, SendWeatherRequestTask.FROM_CITY_NAME, cityEditText.getText().toString());
            }
        });
    }

    @Override
    public void onResult(JSONObject json) {

    }
}
