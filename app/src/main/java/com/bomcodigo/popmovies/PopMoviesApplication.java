package com.bomcodigo.popmovies;

import android.app.Application;


public class PopMoviesApplication extends Application {
    private static PopMoviesApplication instance;

    public static PopMoviesApplication getInstance() {
        if (instance == null){
            instance = new PopMoviesApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
