package com.shoppingcart.admin.order;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.util.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shoppingcart.admin.setting.country.CountryRepository;
import com.shoppingcart.common.entity.Country;
import com.shoppingcart.common.entity.order.Order;
import com.shoppingcart.common.entity.order.OrderDetail;
import com.shoppingcart.common.exception.OrderNotFoundException;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class OrderService {

	public static final int ORDERS_PER_PAGE = 10;

	@Autowired
	private OrderRepository orderRepo;
	@Autowired
	private CountryRepository countryRepo;

	public Page<Order> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = null;
		if ("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));// sắp xếp các orders theo thứ tự
																					// country > state > city
		} else {
			sort = Sort.by(sortField);
		}

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

		if (keyword != null) {
			return orderRepo.findAll(keyword, pageable);
		}

		return orderRepo.findAll(pageable);
	}
	
	public List<Order> getListOrderOfDate(){
		String Date = getDateTime();
		return orderRepo.findByDate(Date);
	}
	
	public List<String> reportDays(String fromDate,String toDate){
		if(fromDate.isEmpty() || toDate.isEmpty()) {
			return orderRepo.findAllSumTotal();
		}else {
			return orderRepo.findAllSumTotal(fromDate, toDate);
		}
	}
	
	public List<String> reportProductsCol(String fromDateCol,String toDateCol){
		if(fromDateCol.isEmpty() || toDateCol.isEmpty()) {
			return orderRepo.findAllProductsCol();
		}else {
			return orderRepo.findAllProductsCol(fromDateCol, toDateCol);
		}
	}

	public void Create_Jasper_Order(Order order, HttpServletResponse response, boolean checkBatch) throws IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PDFMergerUtility mergePdf = new PDFMergerUtility();
		Barcode_Image.createImage(order.getId().toString() + ".png", order.getId().toString());
		String jasperPath = "C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\shoppingcart-backend\\pathPrint\\deliveryNote.jasper";
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("customerName", order.getCustomer().getFullName());
		parameterMap.put("customerAddress", order.getShippingAddressForPDF());
		parameterMap.put("customerPhone", order.getCustomer().getPhoneNumber());
		parameterMap.put("customerEmail", order.getCustomer().getEmail());
		parameterMap.put("totalSubtotal","$ " +  order.getSubtotal());
		parameterMap.put("totalShippingCost","$ " +  order.getShippingCost());
		parameterMap.put("totalTax","$ " +  order.getTax());
		parameterMap.put("totalPayment","$ " +  order.getTotal());
		parameterMap.put("paymentMethod", order.getPaymentMethod());
		parameterMap.put("invoiceNumber", order.getId());
		parameterMap.put("orderDate", order.getOrderTime());
		parameterMap.put("nowDate", this.getDate());
		parameterMap.put("barcode",
				"C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\shoppingcart-backend\\barcode\\"
						+ order.getId() + ".png");

		// orderDetails
		int index = 1;
		List<OrderDetalsPDF> listOrderDetails = new ArrayList<OrderDetalsPDF>();
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			OrderDetalsPDF orderDetalsPDF = new OrderDetalsPDF();
			orderDetalsPDF.setOrdinalNumbers(index);
			orderDetalsPDF.setProductImage(
					"C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\product-images\\"
							+ orderDetail.getProduct().getId() + "\\" + orderDetail.getProduct().getMainImage());
			orderDetalsPDF.setProductName(orderDetail.getProduct().getName());
			orderDetalsPDF.setUnitPrice("$ " + orderDetail.getUnitPrice());
			orderDetalsPDF.setQuantity(orderDetail.getQuantity());
			orderDetalsPDF.setSubtotal("$ " + orderDetail.getSubtotal());
			orderDetalsPDF.setShippingCost("$ " + orderDetail.getShippingCost());
			orderDetalsPDF.setTax("$ " + 0);
			orderDetalsPDF.setBarcode(order.getId().toString());
			index++;
			listOrderDetails.add(orderDetalsPDF);
		}

		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ServletOutputStream servletOutputStream = response.getOutputStream();
			try {
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.font.name", "false"); 
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8"); 
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", " inline; filename=deliveryNote"+order.getId()+".pdf");
				InputStream reportStream = new FileInputStream(jasperPath);
				JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameterMap,
						new JRBeanCollectionDataSource(listOrderDetails));
				JasperExportManager.exportReportToPdfStream(jasperPrint,
						(checkBatch) ? byteArrayOutputStream : servletOutputStream);
				if (checkBatch) {
					byteArrayOutputStream.flush();
					byteArrayOutputStream.close();
				} else {
					servletOutputStream.flush();
					servletOutputStream.close();
				}
			} catch (Exception e) {
				// display stack trace in the browser
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				response.setContentType("text/plain");
				response.getOutputStream().print(stringWriter.toString());
				e.printStackTrace();
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception print PDF: " + e.getMessage());
		}
	}

	public void Create_Jasper_Order_Batch(Order order, HttpServletResponse response,
			ByteArrayOutputStream byteArrayOutputStream) throws IOException {
		Barcode_Image.createImage(order.getId().toString() + ".png", order.getId().toString());
		String jasperPath = "C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\shoppingcart-backend\\pathPrint\\deliveryNote.jasper";
		HashMap<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("customerName", order.getCustomer().getFullName());
		parameterMap.put("customerAddress", order.getShippingAddressForPDF());
		parameterMap.put("customerPhone", order.getCustomer().getPhoneNumber());
		parameterMap.put("customerEmail", order.getCustomer().getEmail());
		parameterMap.put("totalSubtotal","$ "+ order.getSubtotal());
		parameterMap.put("totalShippingCost","$ "+ order.getShippingCost());
		parameterMap.put("totalTax","$ "+ order.getTax());
		parameterMap.put("totalPayment","$ "+ order.getTotal());
		parameterMap.put("paymentMethod",order.getPaymentMethod());
		parameterMap.put("invoiceNumber", order.getId());
		parameterMap.put("orderDate", order.getOrderTime());
		parameterMap.put("nowDate", this.getDate());
		parameterMap.put("barcode",
				"C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\shoppingcart-backend\\barcode\\"
						+ order.getId() + ".png");

		// orderDetails
		int index = 1;
		List<OrderDetalsPDF> listOrderDetails = new ArrayList<OrderDetalsPDF>();
		for (OrderDetail orderDetail : order.getOrderDetails()) {
			OrderDetalsPDF orderDetalsPDF = new OrderDetalsPDF();
			orderDetalsPDF.setOrdinalNumbers(index);
			orderDetalsPDF.setProductImage(
					"C:\\Users\\PhuocLuu\\Desktop\\ShoppingCart\\ShoppingCart\\shoppingcart-webparent\\product-images\\"
							+ orderDetail.getProduct().getId() + "\\" + orderDetail.getProduct().getMainImage());
			orderDetalsPDF.setProductName(orderDetail.getProduct().getName());
			orderDetalsPDF.setUnitPrice("$ " + orderDetail.getUnitPrice());
			orderDetalsPDF.setQuantity(orderDetail.getQuantity());
			orderDetalsPDF.setSubtotal("$ " + orderDetail.getSubtotal());
			orderDetalsPDF.setShippingCost("$ " + orderDetail.getShippingCost());
			orderDetalsPDF.setTax("$ " + 0);
			orderDetalsPDF.setBarcode(order.getId().toString());
			index++;
			listOrderDetails.add(orderDetalsPDF);
		}

		try {
			try {
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.font.name", "false"); 
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.encoding", "UTF-8"); 
//				JRPropertiesUtil.getInstance(context).setProperty("net.sf.jasperreports.default.pdf.embedded", "true");
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", " inline; filename=deliveryNote"+order.getId()+".pdf");
				InputStream reportStream = new FileInputStream(jasperPath);
				JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameterMap,
						new JRBeanCollectionDataSource(listOrderDetails));
				JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

				byteArrayOutputStream.flush();
				byteArrayOutputStream.close();

			} catch (Exception e) {
				// display stack trace in the browser
				StringWriter stringWriter = new StringWriter();
				PrintWriter printWriter = new PrintWriter(stringWriter);
				e.printStackTrace(printWriter);
				response.setContentType("text/plain");
				response.getOutputStream().print(stringWriter.toString());
				e.printStackTrace();
			}
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception print PDF: " + e.getMessage());
		}
	}

	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return orderRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}

	public void delete(Integer id) throws OrderNotFoundException {
		Long count = orderRepo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}

		orderRepo.deleteById(id);
	}

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}

	public void save(Order orderInForm) {
		Order orderInDB = orderRepo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());

		orderRepo.save(orderInForm);
	}

	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return dateFormat.format(date);
	}
	
	private String getDateTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
