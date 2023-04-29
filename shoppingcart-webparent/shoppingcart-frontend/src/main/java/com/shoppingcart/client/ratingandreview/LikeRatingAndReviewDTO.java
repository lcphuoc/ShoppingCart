package com.shoppingcart.client.ratingandreview;

public class LikeRatingAndReviewDTO {
    int like;
    int dislike;
    String comment;
    
    public LikeRatingAndReviewDTO() {
    }

    public LikeRatingAndReviewDTO(int like, int dislike, String comment) {
        super();
        this.like = like;
        this.dislike = dislike;
        this.comment = comment;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getDislike() {
        return dislike;
    }

    public void setDislike(int dislike) {
        this.dislike = dislike;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
}
