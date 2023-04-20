package com.shoppingcart.admin.order;

public class orderReportChart {
	String time_Order;
	String total;
	public orderReportChart() {
	}
	
	public orderReportChart(String time_Order, String total) {
		super();
		this.time_Order = time_Order;
		this.total = total;
	}

	public String getTime_order() {
		return time_Order;
	}
	public void setTime_order(String time_order) {
		this.time_Order = time_order;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	
	
	
}
