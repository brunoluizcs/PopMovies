package com.bomcodigo.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.api.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewContentAdapter extends RecyclerView.Adapter<ReviewContentAdapter.ReviewContentHolder>{
    private List<Review> mDataSet;


    public ReviewContentAdapter() {
        mDataSet = new ArrayList<>();
    }

    public void addAll(List<Review> reviews){
        mDataSet.addAll(reviews);
        notifyDataSetChanged();
    }

    public List<Review> getDataSet() {
        return mDataSet;
    }


    @Override
    public ReviewContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cell_review,parent,false);
        return new ReviewContentHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewContentHolder holder, int position) {
        holder.bind(mDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? this.mDataSet.size() : 0;
    }

    public void clear() {
        this.mDataSet.clear();
        this.notifyDataSetChanged();
    }

    public class ReviewContentHolder extends RecyclerView.ViewHolder {
        private TextView mAuthorTextView;
        private TextView mContentTextView;

        public ReviewContentHolder(View itemView) {
            super(itemView);
            mAuthorTextView = (TextView) itemView.findViewById(R.id.tv_author);
            mContentTextView = (TextView) itemView.findViewById(R.id.tv_content);
        }

        public void bind(Review review){
            mAuthorTextView.setText(review.getAuthor());
            mContentTextView.setText(review.getContent());
        }

    }
}



