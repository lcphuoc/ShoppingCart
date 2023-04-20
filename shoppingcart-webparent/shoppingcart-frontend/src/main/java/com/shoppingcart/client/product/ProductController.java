package com.shoppingcart.client.product;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shoppingcart.client.ControllerHelper;
import com.shoppingcart.client.Utility;
import com.shoppingcart.client.category.CategoryService;
import com.shoppingcart.client.ratingandreview.RatingAndReviewRespository;
import com.shoppingcart.client.ratingandreview.RatingAndReviewService;
import com.shoppingcart.common.entity.Category;
import com.shoppingcart.common.entity.Customer;
import com.shoppingcart.common.entity.RatingAndReview;
import com.shoppingcart.common.entity.product.Product;
import com.shoppingcart.common.exception.CategoryNotFoundException;
import com.shoppingcart.common.exception.ProductNotFoundException;

@Controller
public class ProductController {
	
	@Autowired private ProductService productService;
	@Autowired private CategoryService categoryService;
	@Autowired private RatingAndReviewService ratingAndReviewService;
	@Autowired private ControllerHelper controllerHelper;
	NumberFormat Formatter = new DecimalFormat("#,###,###.#");

	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,
			Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") int pageNum,
			Model model) {
		try {
			Category category = categoryService.getCategory(alias);		
			List<Category> listCategoryParents = categoryService.getCategoryParents(category);//lấy ra tất cả categories cha,ông,...của category hiện tại
			
			Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());//lấy ra tất cả products thuộc về category hiện tại và tất cả products thuộc về categories con,cháu,... của category hiện tại
			List<Product> listProducts = pageProducts.getContent();
			
			long startCount = (pageNum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
			long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
			if (endCount > pageProducts.getTotalElements()) {
				endCount = pageProducts.getTotalElements();
			}
			
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageProducts.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageProducts.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listProducts", listProducts);
			model.addAttribute("category", category);
			
			return "product/products_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}
	}
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable("product_alias") String alias, Model model,
			HttpServletRequest request) {
		try {
			double sumRating = 0;
			int isRating = 0;
			int countRating = 0;
			int fiveStar = 0;
			int fourStar = 0;
			int threeStar = 0;
			int twoStar = 0;
			int oneStar= 0;
			double totalRating = 0;
			Product product = productService.getProduct(alias);
			Customer customer = controllerHelper.getAuthenticatedCustomer(request);
			List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());//lấy ra tất cả categories cha,ông,...của category hiện tại
			List<RatingAndReview> listRatingAndReview = ratingAndReviewService.listRatingAndReview(product.getId());
			for(int i =0;i<listRatingAndReview.size();i++) {
				sumRating += listRatingAndReview.get(i).getRating();
			}
			if(Utility.getEmailOfAuthenticatedCustomer(request) != null) {
				isRating = ratingAndReviewService.isRating(product.getId(), customer.getId());
			}
			countRating = ratingAndReviewService.countAllRating(product.getId());
			fiveStar = ratingAndReviewService.countByRating(5,product.getId());
			fourStar = ratingAndReviewService.countByRating(4,product.getId());
			threeStar = ratingAndReviewService.countByRating(3,product.getId());
			twoStar = ratingAndReviewService.countByRating(2,product.getId());
			oneStar = ratingAndReviewService.countByRating(1,product.getId());
			if(isRating>0) {
				model.addAttribute("isRating", true);
			}else {
				model.addAttribute("isRating", false);
			}
			if(Double.parseDouble(Formatter.format(sumRating/listRatingAndReview.size()).replaceAll(",", ""))>0) {
				totalRating = Double.parseDouble(Formatter.format(sumRating/listRatingAndReview.size()).replaceAll(",", ""));
			}
			model.addAttribute("countRating", countRating);
			model.addAttribute("fiveStar", fiveStar);
			model.addAttribute("fourStar", fourStar);
			model.addAttribute("threeStar", threeStar);
			model.addAttribute("twoStar", twoStar);
			model.addAttribute("oneStar", oneStar);
			model.addAttribute("totalRating", totalRating);
			model.addAttribute("listRatingAndReview", listRatingAndReview);
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("product", product);
			model.addAttribute("pageTitle", product.getShortName());
			
			return "product/product_detail";
		} catch (ProductNotFoundException e) {
			return "error/404";
		}
	}
	
	@GetMapping("/search")
	public String searchFirstPage(String keyword, Model model) {
		return searchByPage(keyword, 1, model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(String keyword, @PathVariable("pageNum") int pageNum, Model model) {
		Page<Product> pageProducts = productService.search(keyword, pageNum);//dùng FULL TEXT SEARCH trong SQL
		List<Product> listResult = pageProducts.getContent();//khi bấm search thì chỉ cần lấy ra tất cả products có name,short_description,full_description trùng với keyword, ko lấy ra categories
		
		long startCount = (pageNum - 1) * ProductService.SEARCH_RESULTS_PER_PAGE + 1;
		long endCount = startCount + ProductService.SEARCH_RESULTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - Search Result");
		
		model.addAttribute("keyword", keyword);
		model.addAttribute("searchKeyword", keyword);
		model.addAttribute("listResult", listResult);
		
		return "product/search_result";
	}		
	
}
