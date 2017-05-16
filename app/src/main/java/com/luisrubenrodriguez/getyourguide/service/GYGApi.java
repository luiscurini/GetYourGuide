package com.luisrubenrodriguez.getyourguide.service;

import com.luisrubenrodriguez.getyourguide.model.Review;
import com.luisrubenrodriguez.getyourguide.model.ReviewInfo;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by GamingMonster on 15.05.2017.
 * Retrofit Interface. It will call the GetYourGuide API to get the reviews.
 * The second request will be mocked.
 */

public interface GYGApi {
    String BASE_URL = "https://www.getyourguide.com/";
    String MOCKADDREVIEWPATH = "/addreview";

    @Headers({
            "User-Agent: com.luisrubenrodriguez.getyourguide"
    })
    @GET("berlin-l17/tempelhof-2-hour-airport-history-tour-berlin-airlift-more-t23776/reviews.json")
    Observable<ReviewInfo> getReviews(@Query("count") int count, @Query("page") int page, @Query("rating") int rating,
                                      @Query("sortBy") String sortBy, @Query("direction") String direction);

    @POST(MOCKADDREVIEWPATH)
    Observable<Review> addReview(@Body Review review);
}
