package com.wholesaleconnect.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // === RELATIONSHIPS ===

  /**
   * Many order items → One order Example: One order has many items
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  /**
   * Many order items → One product Example: Same product can be in many orders
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  // === ORDER DETAILS ===

  @Column(nullable = false)
  private Integer quantity;  // How many units ordered

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal price;  // Price per unit at time of order

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal subtotal;  // quantity * price

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // === HELPER METHOD ===

  /**
   * Calculate subtotal Called before saving to database
   */
  @PrePersist  // This runs automatically before saving
  @PreUpdate   // This runs automatically before updating
  public void calculateSubtotal() {
    if (this.quantity != null && this.price != null) {
      this.subtotal = this.price.multiply(new BigDecimal(this.quantity));
    }
  }
}
