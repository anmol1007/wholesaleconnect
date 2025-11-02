package com.wholesaleconnect.backend.repository;

import com.wholesaleconnect.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  // Find all items in an order
  List<OrderItem> findByOrderId(Long orderId);

  // Find all orders containing a specific product
  List<OrderItem> findByProductId(Long productId);

  // Get top selling products
  @Query("SELECT oi.product.id, oi.product.name, SUM(oi.quantity) as total " +
      "FROM OrderItem oi " +
      "GROUP BY oi.product.id, oi.product.name " +
      "ORDER BY total DESC")
  List<Object[]> findTopSellingProducts();
}
