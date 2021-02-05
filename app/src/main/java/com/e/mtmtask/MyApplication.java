package com.e.mtmtask;

import android.app.Application;
import android.os.StrictMode;

import com.e.mtmtask.Models.DestinationLocationPojo;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

/**
 * Created by Hussein on 04/02/2021
 */
public class MyApplication extends Application {

    private static MyApplication _INSTANCE;
    private static Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    Timber.DebugTree debugTree = new Timber.DebugTree();

    private static final int REQUEST_TIMEOUT = 60;

    //Base URL
    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/";

    public static synchronized MyApplication getInstance() {
        return _INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _INSTANCE = this;

        //initial Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(debugTree);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(policy);
    }

    public static Retrofit getRetrofit() {
        if (okHttpClient == null)
            initOkHttp();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


    public Call<DestinationLocationPojo> getDestinationLocation(String input, String inputType, String[] fields, String key) {
        return MyApplication.getRetrofit().create(Services.class).getDestinationLocation(input, inputType, fields, key);
    }

    private static void initOkHttp() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Timber.d("getRetrofit: %s", message));
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        httpClient.build();
        okHttpClient = httpClient.build();
    }

}
