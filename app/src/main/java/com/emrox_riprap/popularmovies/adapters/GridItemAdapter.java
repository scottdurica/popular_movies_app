package com.emrox_riprap.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.emrox_riprap.popularmovies.POJO.Movie;
import com.emrox_riprap.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by scott on 6/25/2016.
 */
public class GridItemAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<Movie> mDataList;

    public GridItemAdapter() {

    }

    public GridItemAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<Movie>();

    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = mDataList.get(position);
        String path = movie.getPoster_path();
        ViewHolder holder;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView)convertView.findViewById(R.id.iv_movie_poster);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)convertView.getTag();
        }

//        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_movie_poster);
        Picasso.with(mContext).load(path).into(holder.imageView);

        return convertView;
    }

    static class ViewHolder{
        ImageView imageView;
    }
}
