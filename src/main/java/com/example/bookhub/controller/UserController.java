package com.example.bookhub.controller;

import com.example.bookhub.model.User;
import com.example.bookhub.repository.UserRepository;
import com.example.bookhub.resource.UserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<List<User>> getAllUsers(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body(null);
        }
        System.out.println(this.userRepository.findAll());
        return ResponseEntity.ok(this.userRepository.findAll());
    }

    @PostMapping("/user")
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

    @GetMapping("/user/{id}")
    public ResponseEntity getUserById(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = this.userRepository.findById(id);

        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.ok("The user with id: " + id + " was not found.");
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteUserById(@PathVariable String id, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = this.userRepository.findById(id);

        if (user.isPresent()) {
            this.userRepository.deleteById(id);
            return ResponseEntity.ok("Success.");
        } else {
            return ResponseEntity.ok("The user with id: " + id + " was not found.");
        }
    }

    @PatchMapping("/user/{id}")
    public ResponseEntity updateUserById(@PathVariable String id, @RequestBody UserRequest userRequest, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()) {
            User updatedUser = user.get();

            updatedUser.setId(id);
            updatedUser.setName(userRequest.getName());
            updatedUser.setEmail(userRequest.getEmail());
            updatedUser.setRole(userRequest.getRole());
            this.userRepository.save(updatedUser);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User Updated successfully");
            response.put("redirect", "/admin/list-user.html");

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("The user with id: " + id + " was not found.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserRequest userRequest, HttpSession session) {
        User user = userRepository.findByEmail(userRequest.getEmail());
        if (user != null && passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
            session.setAttribute("user", user); // Set user attribute in session
            User sessionUser = (User) session.getAttribute("user");

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged in successfully");
            response.put("redirect", "/" + sessionUser.getRole() + "/dashboard.html" + "?userId=" + sessionUser.getId());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body(Collections.singletonMap("message", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserRequest userRequest, HttpSession session) {

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRole(userRequest.getRole() != null ? userRequest.getRole() : "Customer");

        // Save the user to the database
        User savedUser = this.userRepository.save(user);

        // Set the session attribute after saving the user
        session.setAttribute("user", savedUser);
        User sessionUser = (User) session.getAttribute("user");

        // Create the response message and redirect URL
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registered successfully");
        response.put("redirect", "/" + sessionUser.getRole() + "/dashboard.html");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.removeAttribute("user");
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("redirect", "/../index.html");

        return ResponseEntity.ok(response);
    }
}
