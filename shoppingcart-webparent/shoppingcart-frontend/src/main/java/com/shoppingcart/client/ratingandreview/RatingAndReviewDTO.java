package com.shoppingcart.client.ratingandreview;

import java.util.Date;

import com.shoppingcart.common.entity.product.Product;

public class RatingAndReviewDTO {
	int id;
	int rating;
	String review;
	String fullName;
	String createdTime;
	int fiveStar;
	int fourStar;
	int threeStar;
	int twoStar;
	int oneStar;
	double totalRating;
	

	public RatingAndReviewDTO(int id, int rating, String review, 
			String  fullName, String createdTime, int fiveStar, 
			int fourStar, int threeStar, int twoStar, int oneStar, double totalRating) {
		super();
		this.id = id;
		this.rating = rating;
		this.review = review;
		this.fullName = fullName;
		this.createdTime = createdTime;
		this.fiveStar = fiveStar;
		this.fourStar = fourStar;
		this.threeStar = threeStar;
		this.twoStar = twoStar;
		this.oneStar = oneStar;
		this.totalRating = totalRating;
	}
	
	public RatingAndReviewDTO(int id, int rating, String review, 
			String  fullName, String createdTime ) {
		super();
		this.id = id;
		this.rating = rating;
		this.review = review;
		this.fullName = fullName;
		this.createdTime = createdTime;
	}

	public double getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(double totalRating) {
		this.totalRating = totalRating;
	}

	public int getFiveStar() {
		return fiveStar;
	}

	public void setFiveStar(int fiveStar) {
		this.fiveStar = fiveStar;
	}

	public int getFourStar() {
		return fourStar;
	}

	public void setFourStar(int fourStar) {
		this.fourStar = fourStar;
	}

	public int getThreeStar() {
		return threeStar;
	}

	public void setThreeStar(int threeStar) {
		this.threeStar = threeStar;
	}

	public int getTwoStar() {
		return twoStar;
	}

	public void setTwoStar(int twoStar) {
		this.twoStar = twoStar;
	}

	public int getOneStar() {
		return oneStar;
	}

	public void setOneStar(int oneStar) {
		this.oneStar = oneStar;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public String getReview() {
		return review;
	}
	public void setReview(String review) {
		this.review = review;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

}
