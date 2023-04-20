package com.shoppingcart.admin.shippingrate;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoppingcart.admin.product.ProductRepository;
import com.shoppingcart.common.entity.product.Product;

@Service
@Transactional
public class ShippingRateService {
	
	private static final int DIM_DIVISOR = 139;	
	@Autowired private ProductRepository productRepo;
	
	public float calculateShippingCost(Integer productId) {
		Product product = productRepo.findById(productId).get();
		
		float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
		float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
				
		return finalWeight;
	}
}
