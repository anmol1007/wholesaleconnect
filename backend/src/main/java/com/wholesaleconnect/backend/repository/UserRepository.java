package com.wholesaleconnect.backend.repository;

import com.wholesaleconnect.backend.entity.User;
import com.wholesaleconnect.backend.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  // Find user by email
  Optional<User> findByEmail(String email);

  // Find user by phone
  Optional<User> findByPhone(String phone);

  // Find all users by role
  List<User> findByRole(UserRole role);

  // Find active users by role
  List<User> findByRoleAndIsActive(UserRole role, Boolean isActive);

  // Check if email exists
  boolean existsByEmail(String email);

  // Check if phone exists
  boolean existsByPhone(String phone);

}
