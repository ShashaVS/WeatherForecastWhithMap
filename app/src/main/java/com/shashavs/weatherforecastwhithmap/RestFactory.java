package com.shashavs.weatherforecastwhithmap;

import android.support.annotation.NonNull;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestFactory {

    private static final String BaseURL = "http://api.openweathermap.org";

    @NonNull
    public static WeatherAPI getWeatherAPI() {
        return getRetrofitJson().create(WeatherAPI.class);
    }

    @NonNull
    private static Retrofit getRetrofitJson() {

        //----------for log----------------------------------------------
/*        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor).build();*/
        //--------------------------------------------------------------

        return new Retrofit.Builder()
//                .client(client) //---------->for log
                .baseUrl(BaseURL)
                .addConverterFactory(GsonConverterFactory.create())   //get GSON
                .build();
    }
}
