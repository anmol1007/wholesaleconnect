package com.wholesaleconnect.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ProductDTO - Data Transfer Object for Product
 *
 * Why use DTOs?
 * 1. Prevents lazy loading issues
 * 2. Controls what data is exposed in API
 * 3. Can combine data from multiple entities
 * 4. Avoids circular reference problems
 *
 * Interview Tip: "I used DTOs to decouple the API layer from the
 * persistence layer, improving security and preventing over-fetching."
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

  // Basic product info
  private Long id;
  private String name;
  private String description;
  private String category;
  private String brand;
  private BigDecimal mrp;
  private BigDecimal sellingPrice;
  private Integer stockQuantity;
  private Integer moq;
  private List<String> imageUrls;
  private Boolean isActive;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  // Seller info (only what we need to show)
  private Long sellerId;
  private String sellerName;
  private String sellerBusinessName;

  /**
   * Convert Entity to DTO
   * This method transforms Product entity to ProductDTO
   */
  public static ProductDTO fromEntity(com.wholesaleconnect.backend.entity.Product product) {
    return ProductDTO.builder()
        .id(product.getId())
        .name(product.getName())
        .description(product.getDescription())
        .category(product.getCategory())
        .brand(product.getBrand())
        .mrp(product.getMrp())
        .sellingPrice(product.getSellingPrice())
        .stockQuantity(product.getStockQuantity())
        .moq(product.getMoq())
        .imageUrls(product.getImageUrls())
        .isActive(product.getIsActive())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        // Safely access seller without triggering lazy load error
        .sellerId(product.getSeller() != null ? product.getSeller().getId() : null)
        .sellerName(product.getSeller() != null ? product.getSeller().getName() : null)
        .sellerBusinessName(product.getSeller() != null ? product.getSeller().getBusinessName() : null)
        .build();
  }
}
