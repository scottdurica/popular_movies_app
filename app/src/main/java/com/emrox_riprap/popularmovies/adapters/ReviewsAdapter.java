package com.emrox_riprap.popularmovies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentObject;
import com.emrox_riprap.popularmovies.POJO.Review;
import com.emrox_riprap.popularmovies.POJO.ReviewChildObject;
import com.emrox_riprap.popularmovies.R;

import java.util.List;

/**
 * Created by administrator on 8/1/16.
 */
public class ReviewsAdapter extends ExpandableRecyclerAdapter<ReviewsAdapter.ReviewParentViewHolder,ReviewsAdapter.ReviewChildViewHolder>{

    private LayoutInflater mInflater;
    private List<ParentObject> mDataList;

    public ReviewsAdapter(Context context, List<ParentObject> parentItemList) {
        super(context, parentItemList);
        mDataList = parentItemList;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public ReviewParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_review_parent_item, viewGroup, false);
        return new ReviewParentViewHolder(view);
    }

    @Override
    public ReviewChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.list_item_review_child_item, viewGroup, false);
        return new ReviewChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(ReviewParentViewHolder reviewParentViewHolder, int position, Object parentObject) {
        Review review = (Review) parentObject;
        reviewParentViewHolder.mAuthor.setText(review.getAuthor());
    }

    @Override
    public void onBindChildViewHolder(ReviewChildViewHolder reviewChildViewHolder, int position, Object childObject) {
        ReviewChildObject reviewChildObject = (ReviewChildObject) childObject;
        reviewChildViewHolder.mReviewText.setText(reviewChildObject.getReviewText());
    }



    class ReviewParentViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {

        public ImageButton mParentDropDownArrow;
        public TextView mAuthor;


        public ReviewParentViewHolder(View itemView) {
            super(itemView);

            mParentDropDownArrow = (ImageButton) itemView.findViewById(R.id.ib_list_item_expand_arrow);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_reviews_parent_item);
        }
    }
    class ReviewChildViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder {

        public TextView mReviewText;

        public ReviewChildViewHolder(View itemView) {
            super(itemView);

            mReviewText = (TextView) itemView.findViewById(R.id.tv_reviews_child_item);

        }
    }
}
