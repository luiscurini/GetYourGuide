package com.luisrubenrodriguez.getyourguide.service;

import com.luisrubenrodriguez.getyourguide.model.Review;
import com.luisrubenrodriguez.getyourguide.model.ReviewInfo;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by GamingMonster on 15.05.2017.
 */

public class GYGService {

    public static final int REVIEW_COUNT = 20;
    private final GYGApi mGYGApi;

    GYGService(GYGApi GYGApi) {
        mGYGApi = GYGApi;
    }

    /**
     * Gets the reviews from the API
     *
     * @param page      Pagination
     * @param sortBy    sorBy parameter
     * @param direction direction parameter
     * @param callback  to call in case of success/failure
     * @return
     */
    public Subscription getReviews(Integer page, String sortBy, String direction, final GetReviewsCallback callback) {
        return mGYGApi.getReviews(REVIEW_COUNT, page, 0, sortBy, direction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ReviewInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //e.printStackTrace();
                        callback.onDataFailure();
                    }

                    @Override
                    public void onNext(ReviewInfo reviewInfo) {
                        callback.onDataAvailable(reviewInfo);
                    }
                });
    }

    /**
     * Adds a new review with the Mock API
     *
     * @param review   to be added
     * @param callback to be called in case of success/failure
     * @return
     */
    public Subscription addReview(Review review, final AddReviewCallback callback) {

        return mGYGApi.addReview(review)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Review>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        //e.printStackTrace();
                        callback.onAddReviewFailure();
                    }

                    @Override
                    public void onNext(Review review) {
                        callback.onAddReviewSuccess(review);
                    }
                });
    }

    /**
     * Callback interface to getReviews
     */
    public interface GetReviewsCallback {
        void onDataFailure();

        void onDataAvailable(ReviewInfo reviewInfo);
    }

    /**
     * Callback interface to addReviews.
     */
    public interface AddReviewCallback {
        void onAddReviewFailure();

        void onAddReviewSuccess(Review review);
    }
}
