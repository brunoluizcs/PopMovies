package com.bomcodigo.popmovies.api.exceptions;

import com.bomcodigo.popmovies.PopMoviesApplication;
import com.bomcodigo.popmovies.R;

public class NetworkNotAvailableException extends Exception{
    public NetworkNotAvailableException() {
        super(PopMoviesApplication.getInstance().getString(R.string.network_error));
    }
}
