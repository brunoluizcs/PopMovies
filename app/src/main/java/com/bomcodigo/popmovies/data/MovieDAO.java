package com.bomcodigo.popmovies.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bomcodigo.popmovies.api.MovieProxy;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;

import java.util.ArrayList;
import java.util.List;


public class MovieDAO {
    private final String TAG = MovieDAO.class.getSimpleName();

    private Context mContext;

    public MovieDAO(Context context){
        mContext = context;
    }

    public boolean add(@NonNull Movie movie){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieEntry._ID, movie.getId());
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, movie.getOriginalTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        Uri uri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);
        return uri != null;
    }

    public void query(final MovieProxy.Callbacks callback){
        new Runnable(){

            @Override
            public void run() {
                Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                                    null,
                                    null,
                                    null,
                                    MovieContract.MovieEntry.COLUMN_TITLE);
                BaseResult<Movie> results = parseCursor(cursor);
                if (callback!=null)
                    callback.onSuccess(results);
            }
        }.run();

    }

    public BaseResult<Movie> query(int movieId){
        Cursor cursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry._ID + "=?",
                new String[]{String.valueOf(movieId)},
                MovieContract.MovieEntry.COLUMN_TITLE);
        return parseCursor(cursor);
    }


    public boolean delete(int movieId){
        String stringId = Integer.toString(movieId);
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(stringId).build();
        return mContext.getContentResolver().delete(uri, null, null) > 0;
    }

    private BaseResult<Movie> parseCursor(Cursor cursor){
        List<Movie> movies = new ArrayList<>();
        int idIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
        int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
        int originalTitleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE);
        int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        int votOverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        int releaseDateIndex    = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        if  (cursor.moveToFirst()) {
            do {
                Movie m = new Movie();
                m.setId(cursor.getInt(idIndex));
                m.setTitle(cursor.getString(titleIndex));
                m.setOriginalTitle(cursor.getString(originalTitleIndex));
                m.setOverview(cursor.getString(overviewIndex));
                m.setVoteAverage(cursor.getDouble(votOverageIndex));
                m.setReleaseDate(cursor.getString(releaseDateIndex));
                m.setPosterPath(cursor.getString(posterPathIndex));
                movies.add(m);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return new BaseResult<>(movies,1,cursor.getCount(),1);

    }

}
