package com.emrox_riprap.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class BlankFragment extends Fragment {



    public BlankFragment() {
    }

    public static BlankFragment newInstance(int imageResource) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putInt("image_resource",imageResource);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.iv_placeholder_image);
        imageView.setImageResource(getArguments().getInt("image_resource"));
        return rootView;
    }



}
