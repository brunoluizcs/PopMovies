package com.bomcodigo.popmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bomcodigo.popmovies.R;
import com.bomcodigo.popmovies.api.model.Video;

import java.util.ArrayList;
import java.util.List;

public class VideoContentAdapter extends RecyclerView.Adapter<VideoContentAdapter.VideoContentHolder>{
    private List<Video> mDataSet;
    final private ItemClickListener mClickListener;

    public VideoContentAdapter(ItemClickListener clickListener) {
        this.mDataSet = new ArrayList<>();
        this.mClickListener = clickListener;
    }

    public void addAll(List<Video> videos){
        mDataSet.addAll(videos);
        notifyDataSetChanged();
    }

    public List<Video> getDataSet() {
        return mDataSet;
    }

    public interface ItemClickListener{
        void playTrailerClick(Video video);
    }


    @Override
    public VideoContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cell_trailer,parent,false);
        return new VideoContentHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoContentHolder holder, int position) {
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

    public class VideoContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        final Context context;
        private ImageView mButtonTrailer;
        private TextView mTextName;
        private TextView mTextSite;

        public VideoContentHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            mButtonTrailer = (ImageView) itemView.findViewById(R.id.image_button_trailer);
            mButtonTrailer.setOnClickListener(this);

            mTextName = (TextView) itemView.findViewById(R.id.tv_trailer_name);
            mTextSite = (TextView) itemView.findViewById(R.id.tv_trailer_site);

        }

        public void bind(Video video){
            mTextName.setText(video.getName());
            mTextSite.setText(video.getSite());

        }

        @Override
        public void onClick(View view) {
            if  (mClickListener!=null){
                mClickListener.playTrailerClick(mDataSet.get(getAdapterPosition()));
            }
        }
    }
}



