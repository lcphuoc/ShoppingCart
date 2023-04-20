package com.shoppingcart.admin.order;

public class OrderDetalsPDF {
	int ordinalNumbers;
	String productImage;
	String productName;
	String unitPrice;
	int quantity;
	String subtotal;
	String shippingCost;
	String tax;
	String barcode;
	
	public OrderDetalsPDF() {
		this.ordinalNumbers=0;
		this.productImage="";
		this.productName="";
		this.unitPrice="";
		this.quantity=0;
		this.subtotal="s";
		this.shippingCost="";
		this.tax="";
		this.barcode="";
	}

	public OrderDetalsPDF(int ordinalNumbers, String productImage, String productName, String unitPrice,
			int quantity, String subtotal, String shippingCost, String tax, String barcode) {
		super();
		this.ordinalNumbers = ordinalNumbers;
		this.productImage = productImage;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.subtotal = subtotal;
		this.shippingCost = shippingCost;
		this.tax = tax;
		this.barcode = barcode;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public int getOrdinalNumbers() {
		return ordinalNumbers;
	}

	public void setOrdinalNumbers(int ordinalNumbers) {
		this.ordinalNumbers = ordinalNumbers;
	}

	public String getProductImage() {
		return productImage;
	}

	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(String subtotal) {
		this.subtotal = subtotal;
	}

	public String getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(String shippingCost) {
		this.shippingCost = shippingCost;
	}

	public String getTax() {
		return tax;
	}

	public void setTax(String tax) {
		this.tax = tax;
	}

	
}
