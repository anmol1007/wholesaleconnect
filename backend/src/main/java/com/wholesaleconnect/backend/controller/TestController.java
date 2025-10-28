package com.wholesaleconnect.backend.controller;

import com.wholesaleconnect.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

  private final UserRepository userRepository;

  @GetMapping
  public String test() {
    return "WholesaleConnect Backend is Working! 🚀";
  }

  @GetMapping("/hello/{name}")
  public String hello(@PathVariable String name) {
    return "Hello, " + name + "! Welcome to WholesaleConnect!";
  }

  @GetMapping("/db")
  public String testDatabase() {
    long count = userRepository.count();
    return "Database Connected! ✅ Total users in database: " + count;
  }
}
