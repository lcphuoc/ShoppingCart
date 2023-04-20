package com.shoppingcart.admin.order;

public class reportProductCol {
	String product;
	String subtotal;

	public reportProductCol() {
	}

	public reportProductCol(String product, String subtotal) {
		super();
		this.product = product;
		this.subtotal = subtotal;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}

}
