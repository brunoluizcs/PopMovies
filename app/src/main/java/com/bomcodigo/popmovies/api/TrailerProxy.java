package com.bomcodigo.popmovies.api;

import android.support.annotation.NonNull;

import com.bomcodigo.popmovies.api.exceptions.NetworkNotAvailableException;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Video;
import com.bomcodigo.popmovies.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailerProxy implements Callback<BaseResult<Video>> {
    private TheMovieDbService mTheMovieDbService;
    private Call<BaseResult<Video>> call = null;
    private Callbacks mCallBacks;


    public interface Callbacks{
        void onSuccess(BaseResult<Video> movieBaseResult);
        void onFailure(Throwable t);
    }

    public void cancelRequest(){
        if (call != null && ! call.isExecuted() && ! call.isCanceled()){
            call.cancel();
        }
    }

    public TrailerProxy(Callbacks callBacks) throws IOException {
        this.mTheMovieDbService = new ServiceFactory().getTheMovieDbService();
        this.mCallBacks = callBacks;
    }

    public void listTrailers(int movieId) throws IOException {
        call = this.mTheMovieDbService.listMovieTrailers(movieId);
        if (! new NetworkUtils().isOnline()){
           onFailure(call,new NetworkNotAvailableException());
        }else {
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(@NonNull Call<BaseResult<Video>> call, @NonNull Response<BaseResult<Video>> response) {
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
    public void onFailure(@NonNull Call<BaseResult<Video>> call, @NonNull Throwable t) {
        if (mCallBacks!=null){
            mCallBacks.onFailure(t);
        }
    }
}
