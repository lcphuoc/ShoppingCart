package com.shoppingcart.common.entity;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shoppingcart.common.entity.product.Product;

@Entity
@Table(name = "RatingAndReview")
public class RatingAndReview extends IdBasedEntity {
	@Column(nullable = true, length = 1)
	private int rating;

	@Column(nullable = true, length = 524)
	private String review;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy = "ratingAndReview")
	private Set<LikeRatingAndReview> likeRatingAndReview;
	
	@Column(name = "created_time", nullable = false, updatable = false)
	private Date createdTime;
	
	@Transient
	private int countLike;
	
	@Transient
	private int countDislike;
	
	 @Transient
	 private int liked;
	 
	 @Transient
	 private int disLiked;
	
	public Set<LikeRatingAndReview> getLikeRatingAndReview() {
        return likeRatingAndReview;
    }

    public void setLikeRatingAndReview(Set<LikeRatingAndReview> likeRatingAndReview) {
        this.likeRatingAndReview = likeRatingAndReview;
    }
    
    
    @Transient
    public int getLiked() {
        return liked;
    }

    @Transient
    public void setLiked(int liked) {
        this.liked = liked;
    }

    @Transient
    public int getDisLiked() {
        return disLiked;
    }

    @Transient
    public void setDisLiked(int disLiked) {
        this.disLiked = disLiked;
    }

    @Transient
    public int getCountLike() {
        return countLike;
    }

    @Transient
    public void setCountLike(int countLike) {
        this.countLike = countLike;
    }

    @Transient
    public int getCountDislike() {
        return countDislike;
    }

    @Transient
    public void setCountDislike(int countDislike) {
        this.countDislike = countDislike;
    }

    public RatingAndReview() {
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
