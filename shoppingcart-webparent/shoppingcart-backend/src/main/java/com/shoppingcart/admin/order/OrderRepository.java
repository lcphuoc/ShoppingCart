package com.shoppingcart.admin.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shoppingcart.common.entity.order.Order;

public interface OrderRepository extends PagingAndSortingRepository<Order, Integer> {
	
	@Query("SELECT o FROM Order o WHERE CONCAT('#', o.id) LIKE %?1% OR "
			+ " CONCAT(o.firstName, ' ', o.lastName) LIKE %?1% OR"
			+ " o.firstName LIKE %?1% OR"
			+ " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
			+ " o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR"
			+ " o.postalCode LIKE %?1% OR o.city LIKE %?1% OR"
			+ " o.state LIKE %?1% OR o.country LIKE %?1% OR"
			+ " o.paymentMethod LIKE %?1% OR"
			+ " o.customer.firstName LIKE %?1% OR"
			+ " o.customer.lastName LIKE %?1%")
	public Page<Order> findAll(String keyword, Pageable pageable);
	
	@Query(nativeQuery = true,value = "select date(order_time),sum(total) from orders \r\n"
			+ "group by date(order_time)\r\n"
			+ "limit 5")
	public List<String> findAllSumTotal();
	
	@Query(nativeQuery = true,value = "SELECT date(order_time),sum(total) FROM orders "
			+ "WHERE  date(order_time)>=(:fromDate) AND date(order_time)<=(:toDate) \r\n"
			+ "GROUP BY date(order_time) \r\n"
			+ "limit 5")
	public List<String> findAllSumTotal(@Param("fromDate")String fromDate,@Param("toDate")String toDate);
	
	@Query(nativeQuery = true,value = "select sum(a.subtotal) as subtotal,b.id from order_details a\r\n"
			+ "inner join products b  on a.product_id = b.id\r\n"
			+ "group by product_id\r\n"
			+ "order by  sum(subtotal) desc\r\n"
			+ "limit 5")
	public List<String> findAllProductsCol();
	
	@Query(nativeQuery = true,value = "select sum(a.subtotal) as subtotal,b.id from order_details a\r\n"
			+ "inner join products b  on a.product_id = b.id\r\n"
			+ "inner join orders c on c.id = a.order_id\r\n"
			+ "Where date(c.order_time) >=(:fromDateCol) and date(c.order_time)<=(:toDateCol)\r\n"
			+ "group by product_id\r\n"
			+ "order by  sum(subtotal) desc\r\n"
			+ "limit 5")
	public List<String> findAllProductsCol(@Param("fromDateCol")String fromDateCol,@Param("toDateCol")String toDateCol);
	
	public Long countById(Integer id);
	
	@Query(nativeQuery = true,value = "select * from orders where date(order_time) = (:Date)")
	public List<Order> findByDate(@Param("Date")String Date);
	
}
