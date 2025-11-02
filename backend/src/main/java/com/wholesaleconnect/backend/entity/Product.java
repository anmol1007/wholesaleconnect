package com.wholesaleconnect.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

  @Id  // This is the primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment by database
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "seller_id", nullable = false)  // seller_id cannot be null
  @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
  private User seller;

  @Column(nullable = false)  // This field is required (NOT NULL in DB)
  private String name;

  @Column(columnDefinition = "TEXT")  // TEXT type for long descriptions
  private String description;

  private String category;  // No @Column = uses default settings

  private String brand;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal mrp;  // Maximum Retail Price

  @Column(name = "selling_price", nullable = false, precision = 10, scale = 2)
  private BigDecimal sellingPrice;  // Actual selling price

  // === INVENTORY FIELDS ===
  @Column(name = "stock_quantity", nullable = false)
  private Integer stockQuantity = 0;  // Default value = 0

  @Column(nullable = false)
  private Integer moq = 1;

  @Column(name = "image_urls", columnDefinition = "TEXT[]")
  private List<String> imageUrls = new ArrayList<>();

  // === STATUS FIELD ===
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;  // true = product is visible to buyers

  // === TIMESTAMPS ===
  // These are automatically managed by Hibernate
  @CreationTimestamp  // Set once when row is created
  @Column(name = "created_at", updatable = false)  // updatable=false = never changes
  private LocalDateTime createdAt;

  @UpdateTimestamp  // Updated every time row is modified
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  /**
   * Check if product is in stock
   *
   * @return true if stock > 0
   */
  public boolean isInStock() {
    return this.stockQuantity != null && this.stockQuantity > 0;
  }

  /**
   * Check if quantity can be ordered
   *
   * @param quantity Requested quantity
   * @return true if enough stock available
   */
  public boolean canOrder(int quantity) {
    return this.stockQuantity != null && this.stockQuantity >= quantity;
  }

  /**
   * Reduce stock after order
   *
   * @param quantity Quantity sold
   */
  public void reduceStock(int quantity) {
    if (this.stockQuantity >= quantity) {
      this.stockQuantity -= quantity;
    }
  }
}
