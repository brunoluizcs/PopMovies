package com.bomcodigo.popmovies.api;


import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CustomClientOkHttpBuilder {
    private final OkHttpClient.Builder mBuilder;

    public CustomClientOkHttpBuilder() {
        this.mBuilder = new OkHttpClient().newBuilder();
    }

    public CustomClientOkHttpBuilder withApiKey(final String apiKey){
        this.mBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("api_key", apiKey).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        return this;
    }

    public CustomClientOkHttpBuilder withLanguage(final String language) throws IOException {
        this.mBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(@NonNull Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder().addQueryParameter("language",language).build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });
        return this;
    }

    public OkHttpClient build(){
        return this.mBuilder.build();
    }

}
