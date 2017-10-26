package com.bomcodigo.popmovies.api;

import com.bomcodigo.popmovies.PopMoviesApplication;
import com.bomcodigo.popmovies.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ServiceFactory{

    public TheMovieDbService getTheMovieDbService() throws IOException {
        String apiBaseUrl = PopMoviesApplication.getInstance().getString(R.string.api_base_url);
        String apiKey = PopMoviesApplication.getInstance().getString(R.string.api_key);
        String apiLanguage = PopMoviesApplication.getInstance().getString(R.string.api_default_language);

        OkHttpClient client = new CustomClientOkHttpBuilder().withApiKey(apiKey)
                                        .withLanguage(apiLanguage).build();

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(apiBaseUrl)
                .client(client)
                .build();

        return retrofit.create(TheMovieDbService.class);
    }

}
