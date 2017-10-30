package com.bomcodigo.popmovies.api;


import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;
import com.bomcodigo.popmovies.api.model.Review;
import com.bomcodigo.popmovies.api.model.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbService {

    @GET("movie/popular")
    Call<BaseResult<Movie>> listPopularMovies(@Query("page") int page);

    @GET("movie/top_rated")
    Call<BaseResult<Movie>> listTopRatedMovies(@Query("page") int page);

    @GET("movie/{id}/videos")
    Call<BaseResult<Video>> listMovieTrailers(@Path("id") int id);

    @GET("movie/{id}/reviews")
    Call<BaseResult<Review>> listMovieReviews(@Path("id") int id);


}
