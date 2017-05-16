package com.luisrubenrodriguez.getyourguide.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by GamingMonster on 15.05.2017.
 */

public class ReviewInfo {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("total_reviews")
    @Expose
    private Integer totalReviews;
    @SerializedName("data")
    @Expose
    private List<Review> reviews = null;
    private Integer currentPage;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }
}
