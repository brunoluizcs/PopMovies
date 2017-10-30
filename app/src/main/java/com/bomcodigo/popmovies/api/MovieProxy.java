package com.bomcodigo.popmovies.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bomcodigo.popmovies.PopMoviesApplication;
import com.bomcodigo.popmovies.api.exceptions.NetworkNotAvailableException;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;
import com.bomcodigo.popmovies.data.MovieDAO;
import com.bomcodigo.popmovies.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bomcodigo.popmovies.api.OrderType.BookMark;

public class MovieProxy implements Callback<BaseResult<Movie>> {
    private TheMovieDbService mTheMovieDbService;
    private Call<BaseResult<Movie>> call = null;
    private Callbacks mCallBacks;
    private MovieDAO mMovieDAO;


    public interface Callbacks{
        void onSuccess(BaseResult<Movie> movieBaseResult);
        void onFailure(Throwable t);
    }

    public void cancelRequest(){
        if (call != null && ! call.isExecuted() && ! call.isCanceled()){
            call.cancel();
        }
    }

    public MovieProxy(@Nullable Callbacks callBacks) throws IOException {
        this.mTheMovieDbService = new ServiceFactory().getTheMovieDbService();
        this.mMovieDAO = new MovieDAO(PopMoviesApplication.getInstance());
        this.mCallBacks = callBacks;
    }

    public void listMovies(OrderType orderType, int lastPage) throws IOException {
        if (! new NetworkUtils().isOnline()){
            deliveryOfflineResult();
        }else{
            deliveryOnlineResult(orderType, lastPage);
        }
    }

    private void deliveryOnlineResult(OrderType orderType, int lastPage) {
        switch (orderType) {
            case Popular:
                call = this.mTheMovieDbService.listPopularMovies(lastPage);
                call.enqueue(this);
                break;
            case TopRated:
                call = this.mTheMovieDbService.listTopRatedMovies(lastPage);
                call.enqueue(this);
                break;
            case BookMark:
                mMovieDAO.query(mCallBacks);
                break;
        }
    }

    private void deliveryOfflineResult() {
        onFailure(call, new NetworkNotAvailableException());
        mMovieDAO.query(mCallBacks);
    }

    public boolean isBookmark(int movieId) {
        BaseResult<Movie> results = mMovieDAO.query(movieId);
        return results != null && results.getResults() != null && results.getResults().size() > 0;
    }

    public boolean saveAsFavorite(Movie movie){
        return mMovieDAO.add(movie);
    }

    public boolean removeFromFavorite(int movieId){
        return mMovieDAO.delete(movieId);
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
