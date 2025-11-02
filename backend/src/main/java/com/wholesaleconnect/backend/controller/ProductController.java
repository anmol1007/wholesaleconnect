package com.wholesaleconnect.backend.controller;

import com.wholesaleconnect.backend.dto.ProductDTO;
import com.wholesaleconnect.backend.entity.Product;
import com.wholesaleconnect.backend.repository.ProductRepository;
import com.wholesaleconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductController handles all product-related API requests
 *
 * @RestController = This class handles HTTP requests and returns JSON
 * @RequestMapping = Base URL is /api/products
 * @RequiredArgsConstructor = Lombok generates constructor for 'final' fields (dependency injection)
 */

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

  // === DEPENDENCY INJECTION ===
  // Spring automatically creates these objects and injects them
  private final ProductRepository productRepository;
  private final UserRepository userRepository;

  /**
   * GET /api/products Get all products
   *
   * @return List of all products as JSON
   * <p>
   * Example response: [ {"id": 1, "name": "Dairy Milk", "price": 45.00}, {"id": 2, "name": "5 Star", "price": 18.00} ]
   */
  @GetMapping
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    List<ProductDTO> products = productRepository.findAll()
        .stream()                           // Convert List to Stream
        .map(ProductDTO::fromEntity)        // Transform each Product to ProductDTO
        .collect(Collectors.toList());      // Collect back to List

    return ResponseEntity.ok(products);
  }

  /**
   * GET /api/products/{id} Get product by ID
   *
   * @param id Product ID from URL
   * @return Product details or 404 if not found
   * <p>
   * Example: GET /api/products/1 Response: {"id": 1, "name": "Dairy Milk", ...}
   */
  @GetMapping("/{id}")
  public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
    return productRepository.findById(id)
        .map(ProductDTO::fromEntity)        // Convert to DTO
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * GET /api/products/seller/{sellerId} Get all products by a specific seller
   *
   * @param sellerId Seller's user ID
   * @return List of products sold by this seller
   * <p>
   * Example: GET /api/products/seller/2
   */
  @GetMapping("/seller/{sellerId}")
  public ResponseEntity<List<ProductDTO>> getProductsBySeller(@PathVariable Long sellerId) {
    List<ProductDTO> products = productRepository.findBySellerId(sellerId)
        .stream()
        .map(ProductDTO::fromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(products);
  }

  /**
   * GET /api/products/category/{category} Get products by category
   *
   * @param category Category name
   * @return List of products in this category
   * <p>
   * Example: GET /api/products/category/Chocolates
   */
  @GetMapping("/category/{category}")
  public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
    List<ProductDTO> products = productRepository.findByCategoryAndIsActive(category, true)
        .stream()
        .map(ProductDTO::fromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(products);
  }

  /**
   * GET /api/products/search?keyword=milk Search products by keyword
   *
   * @param keyword Search term
   * @return List of matching products
   * <p>
   * Example: GET /api/products/search?keyword=dairy
   */
  @GetMapping("/search")
  public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
    List<ProductDTO> products = productRepository.searchByKeyword(keyword)
        .stream()
        .map(ProductDTO::fromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(products);
  }

  /**
   * GET /api/products/active Get all active products
   *
   * @return List of active products
   */
  @GetMapping("/active")
  public ResponseEntity<List<ProductDTO>> getActiveProducts() {
    List<ProductDTO> products = productRepository.findByIsActiveTrue()
        .stream()
        .map(ProductDTO::fromEntity)
        .collect(Collectors.toList());

    return ResponseEntity.ok(products);
  }

  /**
   * POST /api/products Create new product
   *
   * @param product Product data from request body (JSON)
   * @return Created product with ID
   * <p>
   * Example request body: { "name": "Dairy Milk 50g", "sellingPrice": 45.00, "seller": {"id": 2} }
   */
  @PostMapping
  public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
    Product savedProduct = productRepository.save(product);
    return ResponseEntity.ok(ProductDTO.fromEntity(savedProduct));
  }

  /**
   * PUT /api/products/{id} Update existing product
   *
   * @param id             Product ID to update
   * @param productDetails Updated product data
   * @return Updated product or 404 if not found
   */
  @PutMapping("/{id}")
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable Long id,
      @RequestBody Product productDetails) {

    return productRepository.findById(id)
        .map(product -> {
          product.setName(productDetails.getName());
          product.setDescription(productDetails.getDescription());
          product.setCategory(productDetails.getCategory());
          product.setBrand(productDetails.getBrand());
          product.setMrp(productDetails.getMrp());
          product.setSellingPrice(productDetails.getSellingPrice());
          product.setStockQuantity(productDetails.getStockQuantity());
          product.setMoq(productDetails.getMoq());
          product.setIsActive(productDetails.getIsActive());

          Product updated = productRepository.save(product);
          return ResponseEntity.ok(ProductDTO.fromEntity(updated));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * DELETE /api/products/{id} Delete product
   *
   * @param id Product ID to delete
   * @return 200 OK if deleted, 404 if not found
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    return productRepository.findById(id)
        .map(product -> {
          productRepository.delete(product);
          return ResponseEntity.ok().<Void>build();
        })
        .orElse(ResponseEntity.notFound().build());
  }

}
