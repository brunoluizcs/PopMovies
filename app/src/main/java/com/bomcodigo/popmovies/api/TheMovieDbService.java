package com.bomcodigo.popmovies.api;


import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDbService {

    @GET("movie/popular")
    Call<BaseResult<Movie>> listPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Call<BaseResult<Movie>> listTopRatedMovies(@Query("page") int page);

}
