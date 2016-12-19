package com.shashavs.weatherforecastwhithmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ForecastFragment extends Fragment {

    private final String TAG = "ForecastFragment";

    private TextView city;
    private TextView time;
    private TextView temperature;
    private TextView wind;
    private TextView pressure;
    private TextView description;

    public static final String KEY_CITY = "city";
    public static final String KEY_TIME = "time";
    public static final String KEY_TEMP = "temperature";
    public static final String KEY_WIND = "wind";
    public static final String KEY_PRESS = "pressure";
    public static final String KEY_DESC = "description";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_forecast, container, false);

        city = (TextView) v.findViewById(R.id.city);
        time = (TextView) v.findViewById(R.id.time);
        temperature = (TextView) v.findViewById(R.id.temperature);
        wind = (TextView) v.findViewById(R.id.wind);
        pressure = (TextView) v.findViewById(R.id.pressure);
        description = (TextView) v.findViewById(R.id.description);

        update();

        return v;
    }

    public void update() {
        city.setText(getString(R.string.city) + " " + QueryPreferences.getSrting(getActivity(), KEY_CITY));
        time.setText(getString(R.string.time) + " " + QueryPreferences.getSrting(getActivity(), KEY_TIME));
        temperature.setText(getString(R.string.temperature) + " " + QueryPreferences.getSrting(getActivity(), KEY_TEMP));
        wind.setText(getString(R.string.wind) + " " + QueryPreferences.getSrting(getActivity(), KEY_WIND));
        pressure.setText(getString(R.string.pressure) + " " + QueryPreferences.getSrting(getActivity(), KEY_PRESS));
        description.setText(getString(R.string.description) + " " + QueryPreferences.getSrting(getActivity(), KEY_DESC));
    }
}
