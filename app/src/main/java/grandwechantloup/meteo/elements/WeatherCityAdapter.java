package grandwechantloup.meteo.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import grandwechantloup.meteo.R;

public class WeatherCityAdapter extends ArrayAdapter<WeatherCity> {

    private final ImageLoader mImageLoader;
    private final Context mContext;

    public WeatherCityAdapter(Context context) {
        super(context, 0);

        mContext = context;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(config);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_city_layout, parent, false);
        }

        final WeatherCity element = getItem(position);

        WeatherCityHolder viewHolder = (WeatherCityHolder) convertView.getTag();
        if(viewHolder == null){
            viewHolder = new WeatherCityHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.delete = (ImageView) convertView.findViewById(R.id.delete);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LocalPreferenceManager.removeCity(mContext, element.getName());
                    WeatherCityAdapter.this.remove(element);
                }
            });
            viewHolder.date0 = (TextView) convertView.findViewById(R.id.date0);
            viewHolder.date1 = (TextView) convertView.findViewById(R.id.date1);
            viewHolder.date2 = (TextView) convertView.findViewById(R.id.date2);
            viewHolder.date3 = (TextView) convertView.findViewById(R.id.date3);
            viewHolder.date4 = (TextView) convertView.findViewById(R.id.date4);
            viewHolder.date5 = (TextView) convertView.findViewById(R.id.date5);
            viewHolder.image0 = (ImageView) convertView.findViewById(R.id.image0);
            viewHolder.image1 = (ImageView) convertView.findViewById(R.id.image1);
            viewHolder.image2 = (ImageView) convertView.findViewById(R.id.image2);
            viewHolder.image3 = (ImageView) convertView.findViewById(R.id.image3);
            viewHolder.image4 = (ImageView) convertView.findViewById(R.id.image4);
            viewHolder.image5 = (ImageView) convertView.findViewById(R.id.image5);
            viewHolder.temp0 = (TextView) convertView.findViewById(R.id.temp0);
            viewHolder.temp1 = (TextView) convertView.findViewById(R.id.temp1);
            viewHolder.temp2 = (TextView) convertView.findViewById(R.id.temp2);
            viewHolder.temp3 = (TextView) convertView.findViewById(R.id.temp3);
            viewHolder.temp4 = (TextView) convertView.findViewById(R.id.temp4);
            viewHolder.temp5 = (TextView) convertView.findViewById(R.id.temp5);
            convertView.setTag(viewHolder);
        }

        viewHolder.name.setText(element.getName());

        if (element.isCurrentPosition()) {
            viewHolder.delete.setVisibility(View.GONE);
        } else {
            viewHolder.delete.setVisibility(View.VISIBLE);
        }

        viewHolder.date0.setText(element.getWeatherAtTime(0).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(0).getIcon() + ".png", viewHolder.image0);
        viewHolder.temp0.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(0).getTempMin(), element.getWeatherAtTime(0).getTempMax()));
        viewHolder.date1.setText(element.getWeatherAtTime(1).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(1).getIcon() + ".png", viewHolder.image1);
        viewHolder.temp1.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(1).getTempMin(), element.getWeatherAtTime(1).getTempMax()));
        viewHolder.date2.setText(element.getWeatherAtTime(2).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(2).getIcon() + ".png", viewHolder.image2);
        viewHolder.temp2.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(2).getTempMin(), element.getWeatherAtTime(2).getTempMax()));
        viewHolder.date3.setText(element.getWeatherAtTime(3).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(3).getIcon() + ".png", viewHolder.image3);
        viewHolder.temp3.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(3).getTempMin(), element.getWeatherAtTime(3).getTempMax()));
        viewHolder.date4.setText(element.getWeatherAtTime(4).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(4).getIcon() + ".png", viewHolder.image4);
        viewHolder.temp4.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(4).getTempMin(), element.getWeatherAtTime(4).getTempMax()));
        viewHolder.date5.setText(element.getWeatherAtTime(5).getTime(mContext.getString(R.string.city_time_format)));
        mImageLoader.displayImage("http://openweathermap.org/img/w/" + element.getWeatherAtTime(5).getIcon() + ".png", viewHolder.image5);
        viewHolder.temp5.setText(String.format(mContext.getResources().getConfiguration().locale, mContext.getResources().getString(R.string.city_temp), element.getWeatherAtTime(5).getTempMin(), element.getWeatherAtTime(5).getTempMax()));

        return convertView;
    }

    private class WeatherCityHolder{
        public TextView  name;
        public ImageView delete;
        public TextView  date0;
        public TextView  date1;
        public TextView  date2;
        public TextView  date3;
        public TextView  date4;
        public TextView  date5;
        public ImageView image0;
        public ImageView image1;
        public ImageView image2;
        public ImageView image3;
        public ImageView image4;
        public ImageView image5;
        public TextView  temp0;
        public TextView  temp1;
        public TextView  temp2;
        public TextView  temp3;
        public TextView  temp4;
        public TextView  temp5;
    }
}
