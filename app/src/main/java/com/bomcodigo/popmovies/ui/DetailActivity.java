package com.bomcodigo.popmovies.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.adapters.VideoContentAdapter;
import com.bomcodigo.popmovies.api.MovieProxy;
import com.bomcodigo.popmovies.api.TrailerProxy;
import com.bomcodigo.popmovies.api.model.BaseResult;
import com.bomcodigo.popmovies.api.model.Movie;
import com.bomcodigo.popmovies.api.model.Video;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE;
import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE_ID;
import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE_TITLE;

public class DetailActivity extends AppCompatActivity implements
        TrailerProxy.Callbacks,
        VideoContentAdapter.ItemClickListener{

    private final String TAG = DetailActivity.class.getSimpleName();

    private MovieProxy mMovieProxy;
    private TrailerProxy mTrailerProxy;

    /**
     * Handle favorite click
     */
    private final View.OnClickListener mFavoriteClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                mMovieProxy = new MovieProxy(null);
                int movieId = mCurrentMovie.getId();
                boolean isFavorite = mMovieProxy.isBookmark(movieId);
                if (isFavorite){
                    isFavorite = ! mMovieProxy.removeFromFavorite(movieId);
                }else{
                    isFavorite = mMovieProxy.saveAsFavorite(mCurrentMovie);
                }
                changeFavoriteLabel(isFavorite);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Handle Review click
     */
    private final View.OnClickListener mReviewClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(DetailActivity.this,ReviewActivity.class);
            intent.putExtra(EXTRA_MOVIE_ID,mCurrentMovie.getId());
            intent.putExtra(EXTRA_MOVIE_TITLE,mCurrentMovie.getTitle());
            startActivity(intent);
        }
    };

    private Movie mCurrentMovie;
    private ImageView mCoverImage;
    private TextView mOverViewDisplay;
    private TextView mRatingDisplay;
    private TextView mReleaseDateDisplay;
    private RecyclerView mRecyclerTrailer;
    private VideoContentAdapter mTrailerAdapter;
    private View mRootView;
    private ProgressBar mTrailerLoadingIndicator;
    private Button mBookmarkButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(R.string.title_detail_activity);
        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.TRANSPARENT);
        setSupportActionBar(toolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTrailerAdapter = new VideoContentAdapter(this);

        try {
            mMovieProxy = new MovieProxy(null);
            mTrailerProxy = new TrailerProxy(this);
        }catch (Exception e){
            e.printStackTrace();
        }

        mRootView = findViewById(R.id.root_detail);

        mCoverImage = (ImageView) findViewById(R.id.iv_media_cover_detail);
        mOverViewDisplay = (TextView) findViewById(R.id.tv_overview_detail);
        mRatingDisplay = (TextView) findViewById(R.id.tv_rating_detail);
        mReleaseDateDisplay = (TextView) findViewById(R.id.tv_release_date_detail);
        mRecyclerTrailer = (RecyclerView) findViewById(R.id.recycler_trailers);
        mTrailerLoadingIndicator = (ProgressBar) findViewById(R.id.progress_trailer_indicator);
        mBookmarkButton = (Button) findViewById(R.id.bt_bookmark);
        mBookmarkButton.setOnClickListener(mFavoriteClick);


        Button reviewButton = (Button) findViewById(R.id.bt_review);
        reviewButton.setOnClickListener(mReviewClick);

        RecyclerView.ItemDecoration dividerDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);

        mRecyclerTrailer.addItemDecoration(dividerDecoration);
        mRecyclerTrailer.setHasFixedSize(true);
        mRecyclerTrailer.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRecyclerTrailer.setAdapter(mTrailerAdapter);

        if (intent.hasExtra(EXTRA_MOVIE)){
            mCurrentMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            changeFavoriteLabel(mMovieProxy.isBookmark(mCurrentMovie.getId()));
            showMovieInfo();
            syncTrailers();
        }else{
            Toast.makeText(this,getString(R.string.movie_not_found_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMovieInfo(){
        setupImageView();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mCurrentMovie.getTitle());
        mOverViewDisplay.setText(mCurrentMovie.getOverview());

        String ratingLabel = getString(R.string.label_rating);
        mRatingDisplay.setText(String.format(ratingLabel,mCurrentMovie.getVoteAverage()));
        mReleaseDateDisplay.setText(mCurrentMovie.getReleaseDate());
    }

    private void showTrailerLoadingIndicator(boolean show){
        mTrailerLoadingIndicator.setVisibility(show ? View.VISIBLE : View.GONE);
        mRecyclerTrailer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void syncTrailers(){
        try {
            showTrailerLoadingIndicator(true);
            mTrailerProxy.listTrailers(mCurrentMovie.getId());
        } catch (IOException e) {
            Toast.makeText(this,getString(R.string.error_sync_trailer), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "syncTrailers: " + e.getMessage() );
        }
    }

    private void setupImageView() {
        String coverUrlString =  getString(R.string.api_base_url_for_images);

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.encodedPath(coverUrlString)
                .appendEncodedPath(mCurrentMovie.getPosterPath())
                .build();
        Uri uri = uriBuilder.build();
        Picasso.with(this).load(uri.toString()).into(mCoverImage);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSuccess(BaseResult<Video> movieBaseResult) {
        showTrailerLoadingIndicator(false);
        mTrailerAdapter.addAll(movieBaseResult.getResults());
    }

    @Override
    public void onFailure(Throwable t) {
        Log.e(TAG, "onFailure: " + t.getMessage() );
        showTrailerLoadingIndicator(false);
        Snackbar.make(mRootView,R.string.error_sync_trailer,Snackbar.LENGTH_LONG)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        syncTrailers();
                    }
                })
                .show();

    }

    @Override
    public void playTrailerClick(Video video) {
        String youtubeBase = getString(R.string.youtube_base_url,video.getKey());
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeBase)));
    }

    private void changeFavoriteLabel(boolean isFavorite){
        String text = isFavorite ? getString(R.string.prompt_remove_favorite) : getString(R.string.prompt_mark_as_favorite);
        mBookmarkButton.setText(text);
    }
}
