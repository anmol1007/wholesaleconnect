package com.wholesaleconnect.backend.enums;

public enum OrderStatus {

  PENDING_APPROVAL,  // Waiting for seller to approve
  APPROVED,          // Seller approved, ready to ship
  REJECTED,          // Seller rejected the order
  SHIPPED,           // Order is on the way
  DELIVERED,         // Order received by buyer
  CANCELLED          // Order cancelled by buyer/seller

}
