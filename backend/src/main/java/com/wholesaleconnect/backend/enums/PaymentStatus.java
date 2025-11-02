package com.wholesaleconnect.backend.enums;

public enum PaymentStatus {

  PENDING,    // Payment not yet made
  PAID,       // Payment completed
  OVERDUE,    // Payment due date passed
  CANCELLED   // Order cancelled, payment void

}
