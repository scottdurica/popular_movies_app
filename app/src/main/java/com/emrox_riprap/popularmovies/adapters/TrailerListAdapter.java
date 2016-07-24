package com.emrox_riprap.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.emrox_riprap.popularmovies.POJO.ListItemObject;
import com.emrox_riprap.popularmovies.POJO.Review;
import com.emrox_riprap.popularmovies.POJO.Trailer;
import com.emrox_riprap.popularmovies.R;

import java.util.ArrayList;

/**
 * Created by administrator on 6/27/16.
 */

public class TrailerListAdapter extends BaseAdapter {


    private Context mContext;

    public ArrayList<ListItemObject> mDataList;


    public TrailerListAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<ListItemObject>();
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
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
//        Log.e("POSITON IS : ", "" + position);
//        Trailer t = mDataList.get(position);
//        if (convertView == null){
//            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer,null);
//        }
//        TextView textView = (TextView)convertView.findViewById(R.id.tv_trailer_name);
//        textView.setText(t.getName());
//        return convertView;
        ViewHolder viewHolder;

        if (convertView == null) {
            //Inflate the layout
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, null);

            //Initialize the ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.tv_trailer_name);
//            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.iv_play_image);

            //store the viewHolder with the view
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (mDataList.get(position) instanceof Trailer){
            Trailer t = (Trailer)mDataList.get(position);
            viewHolder.title.setText(t.getName());
        }
        else{
            Review r = (Review)mDataList.get(position);
            viewHolder.title.setText("Review by " + r.getAuthor());
        }



//        if (t != null){
//        viewHolder.title.setText(t.getName());
//        }

        return convertView;
    }


    static class ViewHolder {
        //        ImageView imageView;
        TextView title;
    }
}
