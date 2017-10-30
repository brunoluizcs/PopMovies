package com.bomcodigo.popmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.api.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieContentAdapter extends RecyclerView.Adapter<MovieContentAdapter.MovieContentHolder>{
    private List<Movie> mDataSet;
    final private ItemClickListener mClickListener;

    public MovieContentAdapter(ItemClickListener clickListener) {
        this.mDataSet = new ArrayList<>();
        this.mClickListener = clickListener;
    }

    public void addAll(List<Movie> movies){
        mDataSet.addAll(movies);
        notifyDataSetChanged();
    }

    public List<Movie> getDataSet() {
        return mDataSet;
    }

    public interface ItemClickListener{
        void onItemClick(Movie movie);
    }


    @Override
    public MovieContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cell_movie_cover,parent,false);
        return new MovieContentHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieContentHolder holder, int position) {
        holder.bind(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return this.mDataSet.size();
    }

    public void clear() {
        this.mDataSet.clear();
        this.notifyDataSetChanged();
    }

    public class MovieContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final Context context;
        ImageView mCoverImageView;
        public MovieContentHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mCoverImageView = (ImageView) itemView.findViewById(R.id.iv_movie_cover);
            mCoverImageView.setOnClickListener(this);
        }

        public void bind(Movie movie){
            String coverUrlString =  context.getString(R.string.api_base_url_for_images);

            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.encodedPath(coverUrlString)
                    .appendEncodedPath(movie.getPosterPath())
                    .build();
            Uri uri = uriBuilder.build();
            Picasso.with(context).load(uri.toString()).into(mCoverImageView);
        }

        @Override
        public void onClick(View view) {
            if  (mClickListener!=null){
                mClickListener.onItemClick(mDataSet.get(getAdapterPosition()));
            }
        }
    }
}



