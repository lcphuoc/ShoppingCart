package com.shoppingcart.client.ratingandreview;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.shoppingcart.common.entity.LikeRatingAndReview;

public interface LikeRatingAndReviewRepository extends CrudRepository<LikeRatingAndReview,Integer> {
    @Query(nativeQuery = true,value="select count(*) from like_rating_and_review where ratingandreview_id = (:ratingId) and like_rating = true")
    public int countLike(@Param("ratingId")int ratingId);
    
    @Query(nativeQuery = true,value="select count(*) from like_rating_and_review where ratingandreview_id = (:ratingId) and dis_like_rating = true")
    public int countDislike(@Param("ratingId")int ratingId);
    
    @Transactional
    @Query(nativeQuery = true, value =  "delete  from like_rating_and_review where customer_id = (:customerId) and ratingandreview_id = (:ratingId) and like_rating=(:like)")
    @Modifying
    public void deleteLike(@Param("customerId")int customerId, @Param("ratingId") int ratingId, @Param("like") boolean like);
    
    @Transactional
    @Query(nativeQuery = true, value =  "delete  from like_rating_and_review where customer_id = (:customerId) and ratingandreview_id = (:ratingId) and dis_like_rating=(:dislike)")
    @Modifying
    public void deleteDislike(@Param("customerId")int customerId, @Param("ratingId") int ratingId, @Param("dislike") boolean dislike);
    
    @Query(nativeQuery = true, value = "select count(*) from like_rating_and_review where customer_id = (:customerId) and ratingandreview_id = (:ratingId) and like_rating=(:like)")
    public int isLikedByCustomer(@Param("ratingId") int ratingId, @Param("customerId") int customerId, @Param("like")boolean like);
    
    @Query(nativeQuery = true, value = "select count(*) from like_rating_and_review where customer_id = (:customerId) and ratingandreview_id = (:ratingId) and dis_like_rating=(:dislike)")
    public int isDislikedByCustomer(@Param("ratingId") int ratingId, @Param("customerId") int customerId, @Param("dislike")boolean dislike);
}
