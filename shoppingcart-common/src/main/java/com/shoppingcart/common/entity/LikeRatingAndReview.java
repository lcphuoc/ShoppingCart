package com.shoppingcart.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LikeRatingAndReview")
public class LikeRatingAndReview extends IdBasedEntity {
    @Column(nullable = true, length = 1)
    private boolean likeRating;
    
    @Column(nullable = true, length = 1)
    private boolean disLikeRating;
    
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "ratingandreview_id")
    private RatingAndReview ratingAndReview;

    public RatingAndReview getRatingAndReview() {
        return ratingAndReview;
    }

    public void setRatingAndReview(RatingAndReview ratingAndReview) {
        this.ratingAndReview = ratingAndReview;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isLikeRating() {
        return likeRating;
    }

    public void setLikeRating(boolean likeRating) {
        this.likeRating = likeRating;
    }

    public boolean isDisLikeRating() {
        return disLikeRating;
    }

    public void setDisLikeRating(boolean disLikeRating) {
        this.disLikeRating = disLikeRating;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    
}
