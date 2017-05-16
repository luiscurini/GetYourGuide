package com.luisrubenrodriguez.getyourguide;

import com.luisrubenrodriguez.getyourguide.model.Review;
import com.luisrubenrodriguez.getyourguide.model.ReviewInfo;

/**
 * Created by GamingMonster on 15.05.2017.
 * Interface that MainActivity must implement.
 */

interface MainView {

    void showLoading();

    void hideLoading();

    void showConnectionError();

    void tryLoadingCachedData();

    void loadReviewList(ReviewInfo reviewInfo);

    void addLocalReview(Review review);

}
