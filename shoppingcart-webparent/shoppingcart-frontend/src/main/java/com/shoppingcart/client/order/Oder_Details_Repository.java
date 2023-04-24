package com.shoppingcart.client.order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.shoppingcart.common.entity.order.OrderDetail;

public interface Oder_Details_Repository extends JpaRepository<OrderDetail,Integer> {
    public int countByProductId(int productId);
}
