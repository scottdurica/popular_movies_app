package com.emrox_riprap.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emrox_riprap.popularmovies.POJO.Trailer;
import com.emrox_riprap.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by administrator on 8/1/16.
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.Viewholder> {

    public interface TrailerViewCLickListener {
        void trailerItemClicked(Trailer trailer);
    }

    private ArrayList<Trailer> mDataList;
    private Context mContext;
    private TrailersAdapter.TrailerViewCLickListener mListener;
    private Trailer mTrailer;
    private int mTrailerIndex =0;
    public TrailersAdapter(Context context, TrailersAdapter.TrailerViewCLickListener listener) {
        this.mContext = context;
        mDataList = new ArrayList<Trailer>();
        this.mListener = listener;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, null);
        return new Viewholder(mContext, view);
    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {
        mTrailer = mDataList.get(position);
        holder.bindTrailer(mTrailer);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
    public void swapData(ArrayList<Trailer> newList) {

        mDataList.clear();
        mDataList.addAll(newList);
        notifyDataSetChanged();
    }

    public void appendData(ArrayList<Trailer> newList) {

        mDataList.addAll(newList);
        notifyDataSetChanged();
    }

    class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Trailer trailer;
        private TextView mTitle;
        private Context mContext;
        private ImageView mThumbnail;



        public Viewholder(Context context, View itemView) {
            super(itemView);

            this.mContext = context;
            mTitle = (TextView)itemView.findViewById(R.id.tv_trailer_name);
            mThumbnail = (ImageView)itemView.findViewById(R.id.iv_trailer_thumb);

            itemView.setOnClickListener(this);

        }
        private void bindTrailer(Trailer t){
            this.trailer = t;
            String path = "http://img.youtube.com/vi/" + mTrailer.getKey() + "/hqdefault.jpg";
            Picasso.with(mContext).load(path).into(mThumbnail);
            mTrailerIndex++;
            this.mTitle.setText("Trailer " + mTrailerIndex);
        }

        @Override
        public void onClick(View view) {
                mListener.trailerItemClicked(trailer);
        }
    }
}
