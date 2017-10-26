package com.bomcodigo.popmovies.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.bomcodigo.popmovies.PopMoviesApplication;

public class NetworkUtils {

    public boolean isOnline(){
        ConnectivityManager manager = (ConnectivityManager) PopMoviesApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
