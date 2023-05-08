package com.shoppingcart.admin.order;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shoppingcart.admin.user.UserNotFoundException;
import com.shoppingcart.admin.user.UserRepository;
import com.shoppingcart.admin.user.UserService;
import com.shoppingcart.admin.user.export.UserPdfExporter;
import com.shoppingcart.common.entity.Country;
import com.shoppingcart.common.entity.ShipperDTO;
import com.shoppingcart.common.entity.User;
import com.shoppingcart.common.entity.order.Order;
import com.shoppingcart.common.entity.order.OrderDetail;
import com.shoppingcart.common.entity.product.Product;
import com.shoppingcart.common.exception.OrderNotFoundException;

@Controller
public class OrderController {

	private String defaultRedirectURL = "redirect:/orders/page/1?sortField=orderTime&sortDir=desc";

	@Autowired
	private OrderService orderService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	@GetMapping("/orders")
	public String listFirstPage() {
		return defaultRedirectURL;
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		Page<Order> page = orderService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();

		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;

		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}

		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		List<User> listShipper = userRepository.listShipper();
		model.addAttribute("listShipper", listShipper);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);

		return "orders/orders";
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Order order = orderService.get(id);
			model.addAttribute("order", order);
			return "orders/order_details_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@GetMapping("/order/export/pdf/{id}")
	public void exportToPDF(@PathVariable("id") Integer id, HttpServletResponse response) throws IOException {
		try {
			Order order = orderService.get(id);
			orderService.Create_Jasper_Order(order, response,false);
		} catch (OrderNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			orderService.delete(id);
			;
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}

		return defaultRedirectURL;
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Order order = orderService.get(id);
			;

			List<Country> listCountries = orderService.listAllCountries();

			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			model.addAttribute("listCountries", listCountries);

			return "orders/order_form";

		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;
		}
	}

	@PostMapping("/orders/batchPrinting")
	public void batchPrinting(HttpServletResponse response, HttpServletRequest request) throws NumberFormatException, IOException, OrderNotFoundException, COSVisitorException {
		String orderIds = request.getParameter("orderIds");
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", " inline; filename=batchPrinting.pdf");
		PDFMergerUtility mergePdf = new PDFMergerUtility();
		if (!orderIds.isEmpty()) {
			orderIds = orderIds.substring(0, orderIds.length() - 1);
		}
		String[] ordersTable = orderIds.split(",");
		for (int i = 0; i < ordersTable.length; i++) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			orderService.Create_Jasper_Order_Batch(orderService.get(Integer.parseInt(ordersTable[i])), response,byteArrayOutputStream);
			mergePdf.addSource(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
			byteArrayOutputStream.flush();
			byteArrayOutputStream.close();
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		mergePdf.setDestinationStream(byteArrayOutputStream);
		mergePdf.mergeDocuments();
		byte c[] = byteArrayOutputStream.toByteArray();
		response.getOutputStream().write(c);
	}
	
	@GetMapping("/orders/changeStatus/{id}/{shipper_id}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("shipper_id") int  shipperId,
            RedirectAttributes redirectAttributes, Model model) throws OrderNotFoundException, UserNotFoundException {
	    Order order = orderService.get(id);
	    User user = userService.get(shipperId);
	    order.setShipper(user);
	    order.setStatus("Order confirmed");
	    orderService.save(order);
        String message = "The Order ID " + id + " has been change Status";
        redirectAttributes.addFlashAttribute("message", message);

        return defaultRedirectURL;
    }

	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra) {
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);

		updateProductDetails(order, request);

		orderService.save(order);

		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");

		return defaultRedirectURL;
	}

	private void updateProductDetails(Order order, HttpServletRequest request) {
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");

		Set<OrderDetail> orderDetails = order.getOrderDetails();

		for (int i = 0; i < detailIds.length; i++) {
			System.out.println("Detail ID: " + detailIds[i]);
			System.out.println("\t Prodouct ID: " + productIds[i]);
			System.out.println("\t Cost: " + productDetailCosts[i]);
			System.out.println("\t Quantity: " + quantities[i]);
			System.out.println("\t Subtotal: " + productSubtotals[i]);
			System.out.println("\t Ship cost: " + productShipCosts[i]);

			OrderDetail orderDetail = new OrderDetail();
			Integer detailId = Integer.parseInt(detailIds[i]);
			if (detailId > 0) {
				orderDetail.setId(detailId);
			}

			orderDetail.setOrder(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

			orderDetails.add(orderDetail);

		}

	}

}
