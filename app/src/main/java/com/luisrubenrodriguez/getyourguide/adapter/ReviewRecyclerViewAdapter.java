package com.luisrubenrodriguez.getyourguide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.luisrubenrodriguez.getyourguide.R;
import com.luisrubenrodriguez.getyourguide.model.Review;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.luisrubenrodriguez.getyourguide.model.Review.TYPE_LOAD;
import static com.luisrubenrodriguez.getyourguide.model.Review.TYPE_REVIEW;

/**
 * Created by GamingMonster on 15.05.2017.
 * RecyclerReview adapter for the review list.
 */

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ReviewRecyclerViewAdapt";
    public static final int NOMOREPAGES = -1;

    private Context mContext;
    private List<Review> mReviews;
    private boolean isLoading = false;
    private Integer nextPage = NOMOREPAGES;
    private OnLoadMoreListener mLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore(Integer page);
    }

    public ReviewRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        if (viewType == TYPE_REVIEW) {
            return new ReviewRecyclerViewHolder(inflater.inflate(R.layout.review_item, parent, false));
        } else {
            return new LoadViewHolder(inflater.inflate(R.layout.load_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_REVIEW) {
            Review review = mReviews.get(position);

            ReviewRecyclerViewHolder newHolder = (ReviewRecyclerViewHolder) holder;
            newHolder.title.setText(review.getTitle());
            newHolder.message.setText(review.getMessage());

            //Todo move to another place, util class.
            String author = review.getAuthor() == null ? "GetYourGuide user" : review.getAuthor();
            String date = review.getDate() == null ? "Unknown date" : review.getDate();
            String country = review.getReviewerCountry() == null ? "Unknown location" : review.getReviewerCountry();

            newHolder.rating.setRating(Float.parseFloat(review.getRating()));

            String info = author + " - " + country + " - " + date;
            newHolder.info.setText(info);
        } else if (nextPage > 0 && !isLoading && mLoadMoreListener != null) {
            isLoading = true;
            //Log.d(TAG, "onBindViewHolder: loading info for nextPage: " + nextPage);
            mLoadMoreListener.onLoadMore(nextPage);
        }

    }

    @Override
    public int getItemCount() {
        return ((mReviews != null) && (mReviews.size() != 0)) ? mReviews.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return mReviews.get(position).getType() == null ? TYPE_REVIEW : TYPE_LOAD;
    }

    public void loadNewData(List<Review> newReviews) {
        mReviews = newReviews;
        isLoading = false;
        notifyDataSetChanged();
    }

    static class ReviewRecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.review_title)
        TextView title;
        @BindView(R.id.review_rating)
        RatingBar rating;
        @BindView(R.id.review_message)
        TextView message;
        @BindView(R.id.review_info)
        TextView info;

        ReviewRecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LoadViewHolder extends RecyclerView.ViewHolder {
        LoadViewHolder(View itemView) {
            super(itemView);
        }
    }


    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    public void setNextPage(Integer nextPage) {
        this.nextPage = nextPage;
    }

    public Integer getNextPage() {
        return nextPage;
    }
}
