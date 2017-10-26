package com.bomcodigo.popmovies.api;

import android.support.annotation.NonNull;

import com.bomcodigo.popmovies.api.exceptions.NetworkNotAvailableException;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;
import com.bomcodigo.popmovies.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieProxy implements Callback<BaseResult<Movie>> {
    private TheMovieDbService mTheMovieDbService;
    private Call<BaseResult<Movie>> call = null;
    private Callbacks mCallBacks;


    public interface Callbacks{
        void onSuccess(BaseResult<Movie> movieBaseResult);
        void onFailure(Throwable t);
    }

    public void cancelRequest(){
        if (call != null && ! call.isExecuted() && ! call.isCanceled()){
            call.cancel();
        }
    }

    public MovieProxy(Callbacks callBacks) throws IOException {
        this.mTheMovieDbService = new ServiceFactory().getTheMovieDbService();
        this.mCallBacks = callBacks;
    }

    public void listMovies(OrderType orderType, int lastPage) throws IOException {
        switch (orderType) {
            case POPULAR:
                 call = this.mTheMovieDbService.listPopularMovies(lastPage);
                break;
            case TOP_RATED:
                call = this.mTheMovieDbService.listTopRatedMovies(lastPage);
                break;
        }

        if (! new NetworkUtils().isOnline()){
           onFailure(call,new NetworkNotAvailableException());
        }else {
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(@NonNull Call<BaseResult<Movie>> call, @NonNull Response<BaseResult<Movie>> response) {
        try {
            if (mCallBacks != null ){
                if (response.isSuccessful()) {
                    mCallBacks.onSuccess(response.body());
                }else{
                    ResponseBody errorBody = response.errorBody();
                    mCallBacks.onFailure(new Throwable(errorBody != null ? errorBody.string() : null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(@NonNull Call<BaseResult<Movie>> call, @NonNull Throwable t) {
        if (mCallBacks!=null){
            mCallBacks.onFailure(t);
        }
    }
}
