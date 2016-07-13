package grandwechantloup.meteo.elements;

import java.util.ArrayList;

public class WeatherCity {

    private final String                   mName;
    private final ArrayList<WeatherAtTime> mIcons;
    private boolean mIsCurrentPosition;

    public WeatherCity(String name) {
        mName = name;
        mIcons = new ArrayList<>();
    }

    public String getName() {
        return mName;
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

    public void setIsCurrentPosition() {
        mIsCurrentPosition = true;
    }

    public boolean isCurrentPosition() {
        return mIsCurrentPosition;
    }
}
