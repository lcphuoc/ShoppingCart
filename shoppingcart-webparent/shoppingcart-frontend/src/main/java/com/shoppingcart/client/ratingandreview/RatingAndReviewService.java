package com.shoppingcart.client.ratingandreview;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppingcart.common.entity.RatingAndReview;

@Service
public class RatingAndReviewService {
	@Autowired RatingAndReviewRespository ratingAndReviewRespository;
	
	public List<RatingAndReview> listRatingAndReview(int productId){
		List<RatingAndReview> list = new ArrayList<RatingAndReview>();
		list = ratingAndReviewRespository.findAllByProductId(productId);
		return list;
	}
	
	public int countAllRating (int productId) {
		return ratingAndReviewRespository.countByProductId(productId);
	}
	
	public int countByRating (int rating, int productId) {
		return ratingAndReviewRespository.countByRatingAndProductId(rating,productId);
	}
	
	public int isRating (int productId, int customerId) {
		return ratingAndReviewRespository.isRating(productId, customerId);
	}
	
	public double totalRatingByProduct(int productId) {
	    return ratingAndReviewRespository.totalRatingByProduct(productId);
	}
	
	public RatingAndReview findById(int ratingId) {
	    return ratingAndReviewRespository.findById(ratingId).get();
	}
}
