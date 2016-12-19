package com.shashavs.weatherforecastwhithmap;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private static final int REQUEST_ERROR = 0;

    private GoogleApiClient mClient;
    private double latitude;
    private double longitude;
    private String APIKEY;

    private ForecastFragment forecast;
    private MapFragment map;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        APIKEY = getString(R.string.APIKEY);

        // Create an instance of GoogleAPIClient.
        if(mClient == null) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {
//                            Log.d(TAG, "onCreate: onConnected = " );
                        }
                        @Override
                        public void onConnectionSuspended(int i) {
                        }
                    })
                    .build();
        }

        forecast = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.forecast_fragment);
        map = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private void getWeatherFromLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();

        Call<ResponseWeather> weatherCall = RestFactory.getWeatherAPI().getForecast(latitude, longitude, APIKEY);
        weatherCall.enqueue(callback);
    }

    //async request
    private Callback<ResponseWeather> callback = new Callback<ResponseWeather>() {

        @Override
        public void onResponse(Call<ResponseWeather> call, Response<ResponseWeather> response) {

            ResponseWeather respWeather = response.body();

            if(respWeather != null) {
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_CITY, respWeather.getCity());
                Calendar mDate = Calendar.getInstance();
                mDate.setTimeInMillis(respWeather.getTime()*1000);
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_TIME,
                        mDate.get(Calendar.DAY_OF_MONTH) + "/" + (mDate.get(Calendar.MONTH) + 1) + "/" + mDate.get(Calendar.YEAR) + " "
                                + mDate.get(Calendar.HOUR_OF_DAY) + ":" + mDate.get(Calendar.MINUTE));
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_TEMP, respWeather.getTemperature() + " °C");
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_WIND, respWeather.getWindSpeed() + " meter/sec");
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_PRESS, respWeather.getPressure() + " mm");
                QueryPreferences.setString(MainActivity.this, ForecastFragment.KEY_DESC, respWeather.getWeatherDescription());

                Toast.makeText(getApplicationContext(), getString(R.string.success_msg), Toast.LENGTH_SHORT).show();
            }

            progressBar.setVisibility(View.INVISIBLE);
            forecast.update();
            map.updateUI(latitude, longitude, true);
        }

        @Override
        public void onFailure(Call<ResponseWeather> call, Throwable t) {
            Log.d(TAG, "onFailure: " + call.request() + "| Throwable: "+ t);
            Toast.makeText(MainActivity.this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
            forecast.update();
            map.updateUI(latitude, longitude, true);
        }
    };

    private boolean isOnline(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public void onStart() {
        if(mClient != null) {
            mClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if(mClient != null) {
            mClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.update_map) {

            if(!isOnline()) {
                forecast.update();
                Toast.makeText(getApplicationContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                getWeatherFromLocation();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Log.d(TAG, "errorCode != ConnectionResult.SUCCESS");
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });
            errorDialog.show();
        }
    }
}
