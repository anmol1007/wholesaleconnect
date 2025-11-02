package com.wholesaleconnect.backend.controller;

import com.wholesaleconnect.backend.entity.Order;
import com.wholesaleconnect.backend.enums.OrderStatus;
import com.wholesaleconnect.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderRepository orderRepository;

  /**
   * GET /api/orders Get all orders
   */
  @GetMapping
  public ResponseEntity<List<Order>> getAllOrders() {
    List<Order> orders = orderRepository.findAll();
    return ResponseEntity.ok(orders);
  }

  /**
   * GET /api/orders/{id} Get order by ID
   */
  @GetMapping("/{id}")
  public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
    return orderRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * GET /api/orders/buyer/{buyerId} Get all orders placed by a buyer
   */
  @GetMapping("/buyer/{buyerId}")
  public ResponseEntity<List<Order>> getOrdersByBuyer(@PathVariable Long buyerId) {
    List<Order> orders = orderRepository.findByBuyerId(buyerId);
    return ResponseEntity.ok(orders);
  }

  /**
   * GET /api/orders/seller/{sellerId} Get all orders received by a seller
   */
  @GetMapping("/seller/{sellerId}")
  public ResponseEntity<List<Order>> getOrdersBySeller(@PathVariable Long sellerId) {
    List<Order> orders = orderRepository.findBySellerId(sellerId);
    return ResponseEntity.ok(orders);
  }

  /**
   * GET /api/orders/seller/{sellerId}/status/{status} Get orders by seller and status
   * <p>
   * Example: GET /api/orders/seller/2/status/PENDING_APPROVAL
   */
  @GetMapping("/seller/{sellerId}/status/{status}")
  public ResponseEntity<List<Order>> getOrdersBySellerAndStatus(
      @PathVariable Long sellerId,
      @PathVariable OrderStatus status) {

    List<Order> orders = orderRepository.findBySellerIdAndOrderStatus(sellerId, status);
    return ResponseEntity.ok(orders);
  }

  /**
   * POST /api/orders Create new order
   */
  @PostMapping
  public ResponseEntity<Order> createOrder(@RequestBody Order order) {
    // Calculate totals before saving
    order.calculateTotals();

    // Calculate due date if Udar payment
    order.calculateDueDate();

    // Save to database
    Order savedOrder = orderRepository.save(order);
    return ResponseEntity.ok(savedOrder);
  }

  /**
   * PUT /api/orders/{id}/status Update order status
   * <p>
   * Example request body: {"status": "APPROVED"}
   */
  @PutMapping("/{id}/status")
  public ResponseEntity<Order> updateOrderStatus(
      @PathVariable Long id,
      @RequestBody OrderStatusUpdateRequest request) {

    return orderRepository.findById(id)
        .map(order -> {
          order.setOrderStatus(request.getStatus());

          // Set timestamp based on status
          switch (request.getStatus()) {
            case APPROVED:
              order.setApprovedAt(java.time.LocalDateTime.now());
              break;
            case REJECTED:
              order.setRejectedAt(java.time.LocalDateTime.now());
              break;
            case SHIPPED:
              order.setShippedAt(java.time.LocalDateTime.now());
              break;
            case DELIVERED:
              order.setDeliveredAt(java.time.LocalDateTime.now());
              break;
            case CANCELLED:
              order.setCancelledAt(java.time.LocalDateTime.now());
              break;
          }

          Order updated = orderRepository.save(order);
          return ResponseEntity.ok(updated);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Inner class for status update request
   */
  @lombok.Data
  static class OrderStatusUpdateRequest {
    private OrderStatus status;
  }
}
