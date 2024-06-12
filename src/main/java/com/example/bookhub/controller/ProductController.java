package com.example.bookhub.controller;

import com.example.bookhub.model.Product;
import com.example.bookhub.model.User;
import com.example.bookhub.repository.ProductRepository;
import com.example.bookhub.resource.UserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ProductController {
    private final ProductRepository productRepository;


    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public List<Product> getProducts() {

    }

    @GetMapping("/products/{id}")
    public List<Product> getProduct(@PathVariable String id) {

        Optional<Product> product = this.productRepository.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.ok("The product with id: " + id + " was not found.");
        }
    }

    @PostMapping("/product")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody UserRequest userRequest, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body(null);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole() != null ? userRequest.getRole() : "Customer");

        // Save the user to the database
        User savedUser = this.userRepository.save(user);

        // Create the response message and redirect URL
        Map<String, String> response = new HashMap<>();
        response.put("message", "Add New User Successfully");
        response.put("redirect", "/admin/list-user.html");

//        return ResponseEntity.status(201).body(this.userRepository.save(user));
        return ResponseEntity.ok(response);


    }
