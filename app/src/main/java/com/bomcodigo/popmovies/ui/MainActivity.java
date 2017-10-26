package com.bomcodigo.popmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.adapters.MovieContentAdapter;
import com.bomcodigo.popmovies.api.BaseModelParser;
import com.bomcodigo.popmovies.api.MovieProxy;
import com.bomcodigo.popmovies.api.OrderType;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MainActivity extends AppCompatActivity implements MovieProxy.Callbacks, MovieContentAdapter.ItemClickListener {
    public static final String EXTRA_MOVIE = "extra_movie";
    private static final String EXTRA_OBJECT_STATE = "extra_object_state";
    private static final String EXTRA_OBJECT_PAGE = "extra_object_page";

    private final String TAG = MainActivity.class.getSimpleName();
    private final int GRID_DEFAULT_SPAN_PORTRAIT = 2;
    private final int GRID_DEFAULT_SPAN_LANDSCAPE = 3;

    private RecyclerView mMovieContentRecyclerView;
    private MovieContentAdapter mAdapter;
    private MovieProxy mMovieProxy;
    private ProgressBar mLoadingIndicator;
    private OrderType mOrderType;
    private int lastPage = 1;

    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy>0){
                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int lastVisible = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                if((visibleItemCount + lastVisible) >= totalItemCount){
                    Log.d(TAG, "onScrolled: loading " + lastPage);
                    try {
                        if (mMovieProxy!=null) {
                            showLoadingIndicator();
                            mMovieProxy.listMovies(mOrderType, lastPage + 1);
                            recyclerView.removeOnScrollListener(this);
                            lastPage++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.mOrderType = OrderType.POPULAR;

        int intOrientation = getResources().getConfiguration().orientation;
        int gridSpan = intOrientation == ORIENTATION_LANDSCAPE ? GRID_DEFAULT_SPAN_LANDSCAPE : GRID_DEFAULT_SPAN_PORTRAIT;
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, gridSpan);

        this.mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        this.mMovieContentRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_content);
        this.mMovieContentRecyclerView.setLayoutManager(mGridLayoutManager);
        this.mMovieContentRecyclerView.setHasFixedSize(true);
        this.mMovieContentRecyclerView.addOnScrollListener(mOnScrollListener);

        this.mAdapter = new MovieContentAdapter(this);
        this.mMovieContentRecyclerView.setAdapter(mAdapter);

        try {
            boolean hasCache = savedInstanceState != null && ! TextUtils.isEmpty(savedInstanceState.getString(EXTRA_OBJECT_STATE,""));
            this.mMovieProxy = new MovieProxy(this);
            if (! hasCache) {
                this.mMovieProxy.listMovies(this.mOrderType,lastPage);
                this.showLoadingIndicator();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void showLoadingIndicator(){
        this.mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideLoadingIndicator(){
        this.mLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSuccess(BaseResult<Movie> movieBaseResult) {
        if (movieBaseResult != null) {
            this.mMovieContentRecyclerView.setVisibility(View.VISIBLE);
            this.hideLoadingIndicator();
            this.mAdapter.addAll(movieBaseResult.getResults());

            mMovieContentRecyclerView.clearOnScrollListeners();
            mMovieContentRecyclerView.addOnScrollListener(mOnScrollListener);
            Log.d(TAG, "onSuccess: Size " + movieBaseResult.getResults().size() );
            Log.d(TAG, "onSuccess: CurrentPage " + movieBaseResult.getPage());
            Log.d(TAG, "onSuccess: " + mAdapter.getItemCount());
        }
    }

    @Override
    public void onFailure(Throwable t) {
        Log.d(TAG, "onFailure: " + t.getMessage());
        showSnackBar(t);
    }

    private void showSnackBar(Throwable t) {
        View view = findViewById(R.id.activity_main);
        Snackbar snackbar = Snackbar.make(view,t.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mMovieProxy.listMovies(mOrderType,lastPage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        snackbar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_popular){
            this.mOrderType = OrderType.POPULAR;
            changeOrder();
            return true;
        }else if (id == R.id.action_top_rated){
            this.mOrderType = OrderType.TOP_RATED;
            changeOrder();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeOrder() {
        try {
            this.showLoadingIndicator();
            this.lastPage = 1;
            this.mMovieContentRecyclerView.scrollToPosition(0);
            this.mAdapter.clear();
            this.mMovieProxy.cancelRequest();
            this.mMovieProxy.listMovies(this.mOrderType,this.lastPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(Movie movie) {
        Intent intentToDetailActivity = new Intent(MainActivity.this,DetailActivity.class);
        intentToDetailActivity.putExtra(EXTRA_MOVIE,movie);
        startActivity(intentToDetailActivity);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null){
            String json = savedInstanceState.getString(EXTRA_OBJECT_STATE,"");
            if (! TextUtils.isEmpty(json)) {
                Type apiResultType = new TypeToken<List<Movie>>() {}.getType();
                List<Movie> movies = new BaseModelParser<List<Movie>>().parserStringToObject(json,apiResultType);
                this.mAdapter.addAll(movies);
                this.lastPage = savedInstanceState.getInt(EXTRA_OBJECT_PAGE,1);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (this.mAdapter != null ){
            String json = new BaseModelParser<List<Movie>>().parserObjectToString(this.mAdapter.getDataSet());
            outState.putString(EXTRA_OBJECT_STATE,json);
            outState.putInt(EXTRA_OBJECT_PAGE,lastPage);
        }
    }
}
