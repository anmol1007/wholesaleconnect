package com.wholesaleconnect.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    //test controllers
    @GetMapping
    public String test() {
        return "WholesaleConnect Backend is Working 🚀";
    }

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return "Hello, " + name + "! Welcome to WholesaleConnect!";
    }

}
