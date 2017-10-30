package com.bomcodigo.popmovies.ui;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.adapters.ReviewContentAdapter;
import com.bomcodigo.popmovies.api.ReviewProxy;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Review;

import java.io.IOException;

import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE_ID;
import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE_TITLE;

public class ReviewActivity extends AppCompatActivity implements ReviewProxy.Callbacks {

    private RecyclerView mRecyclerView;
    private ReviewProxy mReviewProxy;
    private ProgressBar mIndicatorProgressBar;
    private static ReviewContentAdapter sAdapter;
    private int mMovieId;
    private String mMovieTitle;
    private TextView mNoReviewTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        if (sAdapter == null)
            sAdapter = new ReviewContentAdapter();

        mIndicatorProgressBar = (ProgressBar) findViewById(R.id.pb_review_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_reviews);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter(sAdapter);
        mNoReviewTextView = (TextView) findViewById(R.id.tv_no_review_available);

        try {
            mMovieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
            if (getIntent().hasExtra(EXTRA_MOVIE_TITLE))
                mMovieTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);
            setTitle(mMovieTitle);
            mReviewProxy = new ReviewProxy(this);

            if (savedInstanceState == null && sAdapter.getItemCount() > 0) {
                sAdapter.clear();
            }

            if (savedInstanceState == null){
                mReviewProxy.listReview(mMovieId);
            }else{
                showNoReviewAvailableMessage(sAdapter.getItemCount() == 0);
                showLoadIndicator(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void showNoReviewAvailableMessage(boolean show){
        mNoReviewTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLoadIndicator(boolean show){
        mIndicatorProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onSuccess(BaseResult<Review> movieBaseResult) {
        showLoadIndicator(false);
        sAdapter.addAll(movieBaseResult.getResults());
        showNoReviewAvailableMessage(sAdapter.getItemCount() == 0);
    }

    @Override
    public void onFailure(Throwable t) {
        showLoadIndicator(false);
        showSnackBar(t);
    }

    private void showSnackBar(Throwable t) {
        View view = findViewById(R.id.activity_review);
        Snackbar snackbar = Snackbar.make(view,t.getMessage(), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.retry), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mReviewProxy.listReview(mMovieId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        snackbar.show();
    }
}
