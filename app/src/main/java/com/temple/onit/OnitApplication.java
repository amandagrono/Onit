package com.temple.onit;

import android.app.Application;

import retrofit2.Retrofit;

public class OnitApplication extends Application {


    public static OnitApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    private Retrofit createRetrofit(){


    }

}
