package com.bomcodigo.popmovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.api.model.Movie;
import com.squareup.picasso.Picasso;

import static com.bomcodigo.popmovies.ui.MainActivity.EXTRA_MOVIE;

public class DetailActivity extends AppCompatActivity {
    private Movie mCurrentMovie;
    private TextView mTitleDisplay;
    private ImageView mCoverImage;
    private TextView mOverViewDisplay;
    private TextView mRatingDisplay;
    private TextView mReleaseDateDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setTitle(R.string.title_detail_activity);
        Intent intent = getIntent();
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mTitleDisplay = (TextView) findViewById(R.id.tv_original_title);
        mCoverImage = (ImageView) findViewById(R.id.iv_media_cover_detail);
        mOverViewDisplay = (TextView) findViewById(R.id.tv_overview_detail);
        mRatingDisplay = (TextView) findViewById(R.id.tv_rating_detail);
        mReleaseDateDisplay = (TextView) findViewById(R.id.tv_release_date_detail);

        if (intent.hasExtra(EXTRA_MOVIE)){
            mCurrentMovie = intent.getParcelableExtra(EXTRA_MOVIE);
            showMovieInfo();
        }else{
            Toast.makeText(this,getString(R.string.movie_not_found_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void showMovieInfo(){
        setupImageView();
        mTitleDisplay.setText(mCurrentMovie.getOriginalTitle());
        mOverViewDisplay.setText(mCurrentMovie.getOverview());

        String ratingLabel = getString(R.string.label_rating);
        mRatingDisplay.setText(String.format(ratingLabel,mCurrentMovie.getVoteAverage()));
        mReleaseDateDisplay.setText(mCurrentMovie.getReleaseDate());
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
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
