package com.shoppingcart.admin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shoppingcart.admin.order.OrderService;
import com.shoppingcart.admin.order.orderReportChart;
import com.shoppingcart.admin.order.reportProductCol;
import com.shoppingcart.common.entity.order.Order;

@Controller
public class MainController {
	@Autowired
	OrderService orderService;

	@GetMapping("") //localhost:8082/ShoppingCartAdmin
	public String viewHomePage(Model model,@RequestParam(required=false,name="fromDate")String fromDate,
			@RequestParam(required=false,name="toDate")String toDate,
			@RequestParam(required=false,name="fromDateCol")String fromDateCol,
			@RequestParam(required=false,name="toDateCol")String toDateCol) {
		if(fromDate==null) {
			fromDate="";
		}
		if(toDate==null) {
			toDate = getDateTime();
		}
		if(fromDateCol == null) {
			fromDateCol="";
		}
		if(toDateCol == null) {
			toDateCol = getDateTime();
		}
		List<String> list =  orderService.reportDays(fromDate,toDate);
		List<orderReportChart> listOrders = returnListOders(list);
		List<String> listCol = orderService.reportProductsCol(fromDateCol, toDateCol);
		List<reportProductCol> listProducts = returnListProductsCol(listCol);
		List<Order> listOrderOfDate = orderService.getListOrderOfDate();
		
		model.addAttribute("listOrderOfDate",listOrderOfDate);
		model.addAttribute("sizeListOrderOfDate", listOrderOfDate.size());
		model.addAttribute("fromDateCol",fromDateCol);
		model.addAttribute("toDateCol",toDateCol);
		model.addAttribute("fromDate",fromDate);
		model.addAttribute("toDate",toDate);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("listProducts", listProducts);
		return "index";
	}
	
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public List<reportProductCol> returnListProductsCol(List<String> listCol){
		List<reportProductCol> listProducts = new ArrayList<reportProductCol>();
		for(int i = 0; i<listCol.size();i++) {
			String[] temp = listCol.get(i).split(",");
			reportProductCol reportProductCol = new reportProductCol();
			reportProductCol.setSubtotal(temp[0]);
			reportProductCol.setProduct(temp[1]);
			listProducts.add(reportProductCol);
		}
		return listProducts;
	}
	
	public List<orderReportChart> returnListOders(List<String> list){
		List<orderReportChart> listOrders = new ArrayList<orderReportChart>();
		for(int i = 0;i<list.size();i++) {
			String [] temp =  list.get(i).toString().split(",");
			orderReportChart orderReportChart = new orderReportChart();
			orderReportChart.setTime_order(temp[0]);
			orderReportChart.setTotal(temp[1]);
			listOrders.add(orderReportChart);
		}
		return listOrders;
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();//trường hợp vừa login xong, đang ở trang index, bấm back lại thì nó vẫn giữ ở trang index
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {//nếu authentication == null thì có nghĩa là chưa login -->trả về trang login
			return "login";
		}
		return "redirect:/";
	}
}
