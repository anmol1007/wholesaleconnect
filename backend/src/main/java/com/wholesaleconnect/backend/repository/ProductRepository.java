package com.wholesaleconnect.backend.repository;

import com.wholesaleconnect.backend.entity.Product;
import com.wholesaleconnect.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  // === SPRING DATA JPA QUERY METHODS ===
  // Spring generates SQL automatically from method names!

  /**
   * Find all products by seller SQL: SELECT * FROM products WHERE seller_id = ?
   */
  List<Product> findBySeller(User seller);

  /**
   * Find products by seller ID SQL: SELECT * FROM products WHERE seller_id = ?
   */
  List<Product> findBySellerId(Long sellerId);

  /**
   * Find active products by seller SQL: SELECT * FROM products WHERE seller_id = ? AND is_active = ?
   */
  List<Product> findBySellerIdAndIsActive(Long sellerId, Boolean isActive);

  /**
   * Find products by category SQL: SELECT * FROM products WHERE category = ?
   */
  List<Product> findByCategory(String category);

  /**
   * Find products by category and active status SQL: SELECT * FROM products WHERE category = ? AND is_active = ?
   */
  List<Product> findByCategoryAndIsActive(String category, Boolean isActive);

  /**
   * Find products by name (case-insensitive partial match) SQL: SELECT * FROM products WHERE LOWER(name) LIKE
   * LOWER(?%)
   */
  List<Product> findByNameContainingIgnoreCase(String name);

  /**
   * Find products by brand SQL: SELECT * FROM products WHERE brand = ?
   */
  List<Product> findByBrand(String brand);

  /**
   * Find all active products SQL: SELECT * FROM products WHERE is_active = true
   */
  List<Product> findByIsActiveTrue();

  /**
   * Find products with stock less than minimum SQL: SELECT * FROM products WHERE stock_quantity < ?
   */
  List<Product> findByStockQuantityLessThan(Integer minStock);

  // === CUSTOM QUERIES WITH @Query ===
  // When method names get too complex, write SQL manually

  /**
   * Search products by keyword in name or description :keyword is a parameter placeholder
   */
  @Query("SELECT p FROM Product p WHERE " +
      "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
      "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Product> searchByKeyword(@Param("keyword") String keyword);

  /**
   * Find products in price range JPQL (Java Persistence Query Language) - not raw SQL!
   */
  @Query("SELECT p FROM Product p WHERE p.sellingPrice BETWEEN :minPrice AND :maxPrice")
  List<Product> findByPriceRange(
      @Param("minPrice") java.math.BigDecimal minPrice,
      @Param("maxPrice") java.math.BigDecimal maxPrice
  );

  /**
   * Count products by seller
   */
  long countBySellerId(Long sellerId);

  /**
   * Check if product exists by name and seller
   */
  boolean existsByNameAndSellerId(String name, Long sellerId);

}
