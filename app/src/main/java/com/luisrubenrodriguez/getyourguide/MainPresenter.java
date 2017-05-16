package com.luisrubenrodriguez.getyourguide;

import android.util.Log;

import com.luisrubenrodriguez.getyourguide.model.Review;
import com.luisrubenrodriguez.getyourguide.model.ReviewInfo;
import com.luisrubenrodriguez.getyourguide.service.GYGService;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by GamingMonster on 15.05.2017.
 */

public class MainPresenter {

    private static final String TAG = "MainPresenter";
    private final GYGService mGYGService;
    private final MainView mMainView;
    private CompositeSubscription mSubscription;

    //Used for the spinner mapping
    public static final String SORT_BY_DATE = "date_of_review";
    public static final String SORT_BY_RATING = "rating";
    public static final String DIRECTION_DESC = "DESC";
    public static final String DIRECTION_ASC = "ASC";


    public MainPresenter(GYGService GYGService, MainView mainView) {
        mGYGService = GYGService;
        mMainView = mainView;
        mSubscription = new CompositeSubscription();
    }

    /**
     * This will call the API and get the reviews
     *
     * @param page         page to request
     * @param filterOption Spinner selected option
     */
    public void getReviews(final int page, final int filterOption) {

        //It should only be shown for the first page, for the next pages a loading icon while be shown inside of the RecyclerView.
        if (page == MainActivity.FIRST_PAGE) {
            mMainView.showLoading();
        }
        String sort_by;
        String direction;

        //Todo move this mapping to util.
        switch (filterOption) {
            case 0:
                sort_by = SORT_BY_DATE;
                direction = DIRECTION_DESC;
                break;
            case 1:
                sort_by = SORT_BY_DATE;
                direction = DIRECTION_ASC;
                break;
            case 2:
                sort_by = SORT_BY_RATING;
                direction = DIRECTION_DESC;
                break;
            case 3:
                sort_by = SORT_BY_RATING;
                direction = DIRECTION_ASC;
                break;
            default:
                sort_by = SORT_BY_DATE;
                direction = DIRECTION_DESC;
                break;
        }

        Subscription subscription = mGYGService.getReviews(page, sort_by, direction, new GYGService.GetReviewsCallback() {
            @Override
            public void onDataFailure() {
                mMainView.hideLoading();
                mMainView.showConnectionError();

                //Only load from cache if first request fails.
                if (page == 0) {
                    mMainView.tryLoadingCachedData();
                }
            }

            @Override
            public void onDataAvailable(ReviewInfo reviewInfo) {
                mMainView.hideLoading();
                if (reviewInfo != null) {
                    reviewInfo.setCurrentPage(page);
                    mMainView.loadReviewList(reviewInfo);
                }
            }
        });

        mSubscription.add(subscription);
    }

    /**
     * Calls the mock api to add a review.
     *
     * @param review Review to be added
     */
    public void addReview(final Review review) {
        Subscription subscription = mGYGService.addReview(review, new GYGService.AddReviewCallback() {
            @Override
            public void onAddReviewFailure() {
                Log.d(TAG, "onDataFailure: ");
            }

            @Override
            public void onAddReviewSuccess(Review review) {
                mMainView.addLocalReview(review);

            }
        });

        mSubscription.add(subscription);
    }
}
