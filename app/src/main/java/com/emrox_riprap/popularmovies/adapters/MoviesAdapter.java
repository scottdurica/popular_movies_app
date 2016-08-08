package com.emrox_riprap.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewholder> {


    public interface RecyclerViewCLickListener {
        public void recyclerViewListClicked(Movie movie);
    }

    public ArrayList<Movie> mDataList;
    private Context mContext;

    private RecyclerViewCLickListener mListener;

    public MoviesAdapter(Context context, RecyclerViewCLickListener listener) {

        mDataList = new ArrayList<Movie>();
        this.mContext = context;
        this.mListener = listener;

    }

    @Override
    public MovieViewholder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
        return new MovieViewholder(mContext, view);

    }

    @Override
    public void onBindViewHolder(MovieViewholder holder, int position) {

        Movie m = mDataList.get(position);
        holder.bindMovie(m);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void swapData(ArrayList<Movie> newList) {

        mDataList.clear();
        mDataList.addAll(newList);
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<Movie> newList) {

        mDataList.addAll(newList);
        notifyDataSetChanged();
    }


    class MovieViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private ImageView mPoster;
        private Movie mMovie;
        private Context mContext;

        public MovieViewholder(Context context, View itemView) {
            super(itemView);

            this.mContext = context;
            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            itemView.setOnClickListener(this);
        }

        public void bindMovie(Movie movie) {
            this.mMovie = movie;
            String path = mMovie.getPoster_path();
            Picasso.with(mContext).load(path).placeholder(R.drawable.placeholder_image).into(mPoster);

        }


        @Override
        public void onClick(View view) {

            if (this.mMovie != null) {
                mListener.recyclerViewListClicked(mMovie);
            }

        }
    }
}
