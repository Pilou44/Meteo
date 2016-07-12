package grandwechantloup.meteo;

import java.util.ArrayList;

/**
 * Created by gbeguin on 12/07/2016.
 */
public class WeatherCity {

    private String                   mName;
    private double                   mMinTemp;
    private double                   mMaxTemp;
    private ArrayList<WeatherAtTime> mIcons;
    private boolean                  mIsCurrentPosition;

    public WeatherCity(String name) {
        mName = name;
        mIcons = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public double getMinTemp() {
        return mMinTemp;
    }

    public void setMinTemp(double minTemp) {
        this.mMinTemp = minTemp;
    }

    public double getMaxTemp() {
        return mMaxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.mMaxTemp = maxTemp;
    }

    public WeatherAtTime getWeatherAtTime(int index) {
        if (index >= 0 && index < mIcons.size()) {
            return mIcons.get(index);
        } else {
            return null;
        }
    }

    public void addWeatherAtTime(WeatherAtTime wat) {
        mIcons.add(wat);
    }

    public void setIsCurrentPosition(boolean isCurrentPosition) {
        mIsCurrentPosition = isCurrentPosition;
    }

    public boolean isCurrentPosition() {
        return mIsCurrentPosition;
    }
}
