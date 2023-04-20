package com.shoppingcart.client.ratingandreview;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.shoppingcart.common.entity.RatingAndReview;

public interface RatingAndReviewRespository extends CrudRepository<RatingAndReview,Integer> {
	
	@Query(nativeQuery = true,value = "select * from rating_and_review where product_id = (:productId)")
	public List<RatingAndReview> findAllByProductId(@Param("productId")int productId);
	
	public int countByProductId(int productid);
	
	public int countByRatingAndProductId(int rating,int productid);
	
	public List<RatingAndReview> findAllByProductIdAndRating(int productid, int rating);
	
	@Query(nativeQuery = true,value = "select *  from rating_and_review \r\n"
			+ "order by created_time desc\r\n"
			+ "limit 1")
	public RatingAndReview findFirstOrderBycreatedTime();
	
	@Query(nativeQuery = true, value = "select count(is_rating) as count from order_details a\r\n"
			+ "inner join orders b on a.order_id = b.id\r\n"
			+ "where a.product_id = (:productId) and b.customer_id = (:customerId) and a.is_rating = 1")
	public int isRating (@Param("productId")int productId, @Param("customerId")int customerId);
	
	@Transactional
	@Query(nativeQuery = true, value = "update order_details a\r\n"
			+ "inner join orders b on a.order_id = b.id set a.is_rating = 0\r\n"
			+ "where a.product_id = (:productId) and b.customer_id = (:customerId) and a.is_rating = 1\r\n"
			+ "limit 1")
	@Modifying
	public void updateIsRating(@Param("productId") int productId, @Param("customerId")int customerId);
}
