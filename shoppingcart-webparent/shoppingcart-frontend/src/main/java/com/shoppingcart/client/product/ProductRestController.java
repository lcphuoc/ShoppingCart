package com.shoppingcart.client.product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shoppingcart.client.ControllerHelper;
import com.shoppingcart.client.Utility;
import com.shoppingcart.client.ratingandreview.LikeRatingAndReviewDTO;
import com.shoppingcart.client.ratingandreview.LikeRatingAndReviewRepository;
import com.shoppingcart.client.ratingandreview.RatingAndReviewDTO;
import com.shoppingcart.client.ratingandreview.RatingAndReviewRespository;
import com.shoppingcart.client.ratingandreview.RatingAndReviewService;
import com.shoppingcart.common.entity.Customer;
import com.shoppingcart.common.entity.LikeRatingAndReview;
import com.shoppingcart.common.entity.RatingAndReview;
import com.shoppingcart.common.exception.ProductNotFoundException;

@RestController
public class ProductRestController {
	@Autowired ProductService productService;
	@Autowired private ControllerHelper controllerHelper;
	@Autowired RatingAndReviewRespository ratingAndReviewRespository;
	@Autowired RatingAndReviewService ratingAndReviewService;
	@Autowired LikeRatingAndReviewRepository likeRatingAndReviewRepository;
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
//		ratingAndReviewRespository.save(ratingAndReview);
		ratingAndReview = ratingAndReviewRespository.save(ratingAndReview);
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
		int countLike = likeRatingAndReviewRepository.countLike(ratingAndReview.getId());
		int countDislike = likeRatingAndReviewRepository.countDislike(ratingAndReview.getId());
		
		ratingAndReview  = ratingAndReviewRespository.findFirstOrderBycreatedTime();
		RatingAndReviewDTO ratingAndReviewDTO = new RatingAndReviewDTO(ratingAndReview.getId(), 
				ratingAndReview.getRating(), ratingAndReview.getReview(), 
				ratingAndReview.getCustomer().getFullName(), ratingAndReview.getCreatedTime()+"", 
				fiveStar, fourStar, threeStar, twoStar, oneStar, 
				ratingAndReviewService.totalRatingByProduct(ratingAndReview.getProduct().getId()),
				countLike,countDislike);
		return ratingAndReviewDTO;
	}
	
	@PostMapping("/products/likeOrDislike")
	public LikeRatingAndReviewDTO SaveLikeOrDislike(int likeordislike, int ratingId,int likedOrDisliked, HttpServletRequest request) {
	    Customer customer = controllerHelper.getAuthenticatedCustomer(request);
        LikeRatingAndReviewDTO DTO = new LikeRatingAndReviewDTO();
	    if(customer==null) {
	        DTO.setComment("You must login to like/dislike this rating.");
	    }else if(likeordislike==1 && likedOrDisliked==0) {
	        RatingAndReview ratingAndReview = ratingAndReviewService.findById(ratingId);
	        LikeRatingAndReview likeRatingAndReview = new LikeRatingAndReview();
	        likeRatingAndReview.setCustomer(customer);
	        likeRatingAndReview.setRatingAndReview(ratingAndReview);
            likeRatingAndReview.setLikeRating(true);
            likeRatingAndReview.setDisLikeRating(false);
	        likeRatingAndReviewRepository.save(likeRatingAndReview);
	        likeRatingAndReviewRepository.deleteDislike(customer.getId(), ratingId, true);
	        DTO.setComment("LIKED");
	    }else if(likeordislike==1 && likedOrDisliked==1) {
	        likeRatingAndReviewRepository.deleteLike(customer.getId(), ratingId, true);
	        DTO.setComment("DELETELIKED");
	    }else if(likeordislike==0 && likedOrDisliked==0) {
	        RatingAndReview ratingAndReview = ratingAndReviewService.findById(ratingId);
            LikeRatingAndReview likeRatingAndReview = new LikeRatingAndReview();
            likeRatingAndReview.setCustomer(customer);
            likeRatingAndReview.setRatingAndReview(ratingAndReview);
            likeRatingAndReview.setLikeRating(false);
            likeRatingAndReview.setDisLikeRating(true);
            likeRatingAndReviewRepository.save(likeRatingAndReview);
            likeRatingAndReviewRepository.deleteLike(customer.getId(), ratingId, true);
            DTO.setComment("DISLIKED");
	    }else if(likeordislike==0 && likedOrDisliked==1) {
	        likeRatingAndReviewRepository.deleteDislike(customer.getId(), ratingId, true);
	        DTO.setComment("DELETEDISLIKED");
	    }
	    int countLike = likeRatingAndReviewRepository.countLike(ratingId);
        int countDislike = likeRatingAndReviewRepository.countDislike(ratingId);
        DTO.setDislike(countDislike);
        DTO.setLike(countLike);
	    return DTO;
	}
	
	@PostMapping("/products/viewRatingByStar")
	public List<RatingAndReviewDTO> viewRatingByStar(String ratingStar, int productId,HttpServletRequest request) throws NumberFormatException, ProductNotFoundException {
		List<RatingAndReview> list = new ArrayList<RatingAndReview>();
		List<RatingAndReviewDTO> listDTO = new ArrayList<RatingAndReviewDTO>();
		RatingAndReviewDTO ratingAndReviewDTO;
		if(ratingStar.trim().length()>2) {
			list = ratingAndReviewRespository.findAllByProductId(productId);
		}else {
			list = ratingAndReviewRespository.findAllByProductIdAndRating(productId,Integer.parseInt(ratingStar));
		}
		for(int i = 0; i<list.size();i++) {
		    int ratingId = list.get(i).getId();
		    int countLike = likeRatingAndReviewRepository.countLike(ratingId);
		    int countDislike = likeRatingAndReviewRepository.countDislike(ratingId);
		    if(Utility.getEmailOfAuthenticatedCustomer(request) != null) {
		        Customer customer = controllerHelper.getAuthenticatedCustomer(request);
		        int liked = likeRatingAndReviewRepository.isLikedByCustomer(ratingId, customer.getId(), true);
		        int disliked = likeRatingAndReviewRepository.isDislikedByCustomer(ratingId, customer.getId(), true);
		        ratingAndReviewDTO = new RatingAndReviewDTO(list.get(i).getId(), list.get(i).getRating(), 
	                    list.get(i).getReview(), list.get(i).getCustomer().getFullName(), list.get(i).getCreatedTime()+"",countLike,countDislike,liked,disliked);
            }else {
                ratingAndReviewDTO = new RatingAndReviewDTO(list.get(i).getId(), list.get(i).getRating(), 
                        list.get(i).getReview(), list.get(i).getCustomer().getFullName(), list.get(i).getCreatedTime()+"",countLike,countDislike,0,0);
            }
			listDTO.add(ratingAndReviewDTO);
		}
		return listDTO;
	}
}
