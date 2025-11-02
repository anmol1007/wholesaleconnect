package com.wholesaleconnect.backend.repository;

import com.wholesaleconnect.backend.entity.Order;
import com.wholesaleconnect.backend.enums.OrderStatus;
import com.wholesaleconnect.backend.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

  // Find orders by buyer
  List<Order> findByBuyerId(Long buyerId);

  // Find orders by seller
  List<Order> findBySellerId(Long sellerId);

  // Find orders by buyer and status
  List<Order> findByBuyerIdAndOrderStatus(Long buyerId, OrderStatus status);

  // Find orders by seller and status
  List<Order> findBySellerIdAndOrderStatus(Long sellerId, OrderStatus status);

  // Find orders by payment status
  List<Order> findByPaymentStatus(PaymentStatus status);

  // Find overdue orders (due date passed, still pending)
  @Query("SELECT o FROM Order o WHERE o.dueDate < :today AND o.paymentStatus = 'PENDING'")
  List<Order> findOverdueOrders(@Param("today") LocalDate today);

  // Find orders with due date in next N days
  @Query("SELECT o FROM Order o WHERE o.dueDate BETWEEN :today AND :futureDate AND o.paymentStatus = 'PENDING'")
  List<Order> findOrdersDueSoon(
      @Param("today") LocalDate today,
      @Param("futureDate") LocalDate futureDate
  );

  // Count orders by status for a seller
  long countBySellerIdAndOrderStatus(Long sellerId, OrderStatus status);

  // Find recent orders (last N days)
  @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
  List<Order> findRecentOrders(@Param("startDate") java.time.LocalDateTime startDate);
}
