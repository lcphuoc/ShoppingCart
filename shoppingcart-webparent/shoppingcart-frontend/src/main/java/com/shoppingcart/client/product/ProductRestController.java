package com.shoppingcart.client.product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingcart.client.ControllerHelper;
import com.shoppingcart.client.ratingandreview.RatingAndReviewDTO;
import com.shoppingcart.client.ratingandreview.RatingAndReviewRespository;
import com.shoppingcart.client.ratingandreview.RatingAndReviewService;
import com.shoppingcart.common.entity.Customer;
import com.shoppingcart.common.entity.RatingAndReview;
import com.shoppingcart.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {
	@Autowired ProductService productService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired RatingAndReviewRespository ratingAndReviewRespository;
	@Autowired RatingAndReviewService ratingAndReviewService;
	NumberFormat Formatter = new DecimalFormat("#,###,###.#");
	
	@PostMapping("/products/saveRatingAndReview")
	public RatingAndReviewDTO SaveRatingAndReview(int rating, String review, String productId,HttpServletRequest request) throws NumberFormatException, ProductNotFoundException {
		double sumRating=0;
		RatingAndReview ratingAndReview = new RatingAndReview();
		ratingAndReview.setRating(rating);
		ratingAndReview.setReview(review);
		ratingAndReview.setProduct(productService.getProduct(Integer.parseInt(productId)));
		
		Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		
		ratingAndReview.setCustomer(customer);
		ratingAndReview.setCreatedTime(new Date());
		ratingAndReviewRespository.save(ratingAndReview);
		ratingAndReviewRespository.updateIsRating(Integer.parseInt(productId), customer.getId());
		List<RatingAndReview> listRatingAndReview = ratingAndReviewService.listRatingAndReview(Integer.parseInt(productId));
		for(int i =0;i<listRatingAndReview.size();i++) {
			sumRating += listRatingAndReview.get(i).getRating();
		}
		
		int fiveStar = ratingAndReviewRespository.countByRatingAndProductId(5, ratingAndReview.getProduct().getId());
		int fourStar = ratingAndReviewRespository.countByRatingAndProductId(4, ratingAndReview.getProduct().getId());
		int threeStar = ratingAndReviewRespository.countByRatingAndProductId(3, ratingAndReview.getProduct().getId());
		int twoStar = ratingAndReviewRespository.countByRatingAndProductId(2,ratingAndReview.getProduct().getId());
		int oneStar = ratingAndReviewRespository.countByRatingAndProductId(1, ratingAndReview.getProduct().getId());
		
		ratingAndReview  = ratingAndReviewRespository.findFirstOrderBycreatedTime();
		RatingAndReviewDTO ratingAndReviewDTO = new RatingAndReviewDTO(ratingAndReview.getId(), 
				ratingAndReview.getRating(), ratingAndReview.getReview(), 
				ratingAndReview.getCustomer().getFullName(), ratingAndReview.getCreatedTime()+"", 
				fiveStar, fourStar, threeStar, twoStar, oneStar, ratingAndReviewService.totalRatingByProduct(ratingAndReview.getProduct().getId()));
		return ratingAndReviewDTO;
	}
	
	@PostMapping("/products/viewRatingByStar")
	public List<RatingAndReviewDTO> viewRatingByStar(String ratingStar, int productId,HttpServletRequest request) throws NumberFormatException, ProductNotFoundException {
		List<RatingAndReview> list = new ArrayList<RatingAndReview>();
		List<RatingAndReviewDTO> listDTO = new ArrayList<RatingAndReviewDTO>();
		if(ratingStar.trim().length()>2) {
			list = ratingAndReviewRespository.findAllByProductId(productId);
		}else {
			list = ratingAndReviewRespository.findAllByProductIdAndRating(productId,Integer.parseInt(ratingStar));
		}
		for(int i = 0; i<list.size();i++) {
			RatingAndReviewDTO ratingAndReviewDTO = new RatingAndReviewDTO(list.get(i).getId(), list.get(i).getRating(), 
					list.get(i).getReview(), list.get(i).getCustomer().getFullName(), list.get(i).getCreatedTime()+"");
			listDTO.add(ratingAndReviewDTO);
		}
		return listDTO;
	}
}
