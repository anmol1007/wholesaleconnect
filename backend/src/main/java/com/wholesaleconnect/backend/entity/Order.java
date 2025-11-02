package com.wholesaleconnect.backend.entity;

import com.wholesaleconnect.backend.enums.OrderStatus;
import com.wholesaleconnect.backend.enums.PaymentMethod;
import com.wholesaleconnect.backend.enums.PaymentStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // === RELATIONSHIPS ===

  /**
   * Many orders → One buyer (User) Example: One retailer places many orders
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "buyer_id", nullable = false)
  private User buyer;

  /**
   * Many orders → One seller (User) Example: One distributor receives many orders
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)
  private User seller;

  /**
   * One order → Many order items Example: One order contains multiple products
   * <p>
   * CascadeType.ALL = If order is deleted, all items are deleted orphanRemoval = true = If item is removed from list,
   * delete from DB mappedBy = "order" = The "order" field in OrderItem owns this relationship
   */
  @OneToMany(
      mappedBy = "order",           // OrderItem has "order" field
      cascade = CascadeType.ALL,    // Delete items when order is deleted
      orphanRemoval = true,         // Remove items if removed from list
      fetch = FetchType.LAZY        // Don't load items until needed
  )
  private List<OrderItem> orderItems = new ArrayList<>();

  // === AMOUNT FIELDS ===

  @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal totalAmount;  // Sum of all items (before tax)

  @Column(name = "gst_amount", nullable = false, precision = 10, scale = 2)
  private BigDecimal gstAmount;  // 18% GST

  @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
  private BigDecimal grandTotal;  // Total + GST

  // === PAYMENT FIELDS ===

  @Enumerated(EnumType.STRING)  // Save as "CASH", "ONLINE", "UDAR" (not 0,1,2)
  @Column(name = "payment_method", nullable = false)
  private PaymentMethod paymentMethod;

  @Enumerated(EnumType.STRING)
  @Column(name = "payment_status", nullable = false)
  private PaymentStatus paymentStatus = PaymentStatus.PENDING;

  /**
   * Udar duration in days (7, 15, or 30) Only used if paymentMethod = UDAR
   */
  @Column(name = "udar_duration")
  private Integer udarDuration;

  /**
   * Payment due date Calculated as: orderDate + udarDuration Only used if paymentMethod = UDAR
   */
  @Column(name = "due_date")
  private LocalDate dueDate;

  // === ORDER STATUS ===

  @Enumerated(EnumType.STRING)
  @Column(name = "order_status", nullable = false)
  private OrderStatus orderStatus = OrderStatus.PENDING_APPROVAL;

  // === TIMESTAMPS ===

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "approved_at")
  private LocalDateTime approvedAt;

  @Column(name = "rejected_at")
  private LocalDateTime rejectedAt;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @Column(name = "delivered_at")
  private LocalDateTime deliveredAt;

  @Column(name = "cancelled_at")
  private LocalDateTime cancelledAt;

  // === HELPER METHODS ===

  /**
   * Add item to order Maintains bidirectional relationship
   */
  public void addOrderItem(OrderItem item) {
    orderItems.add(item);
    item.setOrder(this);  // Set reverse relationship
  }

  /**
   * Remove item from order Maintains bidirectional relationship
   */
  public void removeOrderItem(OrderItem item) {
    orderItems.remove(item);
    item.setOrder(null);  // Clear reverse relationship
  }

  /**
   * Calculate total from all items
   */
  public void calculateTotals() {
    this.totalAmount = orderItems.stream()
        .map(OrderItem::getSubtotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 18% GST
    this.gstAmount = this.totalAmount.multiply(new BigDecimal("0.18"));

    this.grandTotal = this.totalAmount.add(this.gstAmount);
  }

  /**
   * Calculate due date for Udar payment
   */
  public void calculateDueDate() {
    if (this.paymentMethod == PaymentMethod.UDAR && this.udarDuration != null) {
      this.dueDate = LocalDate.now().plusDays(this.udarDuration);
    }
  }
}
