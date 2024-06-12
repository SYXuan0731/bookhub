package com.example.bookhub.controller;

import com.example.bookhub.model.Product;
import com.example.bookhub.repository.ProductRepository;
import com.example.bookhub.resource.ProductRequest;
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

    @GetMapping("/product-redirect")
    public ResponseEntity redirect(HttpSession session) {
        checkUserAuth(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirected to successfully");
        response.put("redirect", "../Product/product-index.html");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts(HttpSession session) {
        checkUserAuth(session);
        System.out.println(this.productRepository.findAll());
        return ResponseEntity.ok(this.productRepository.findAll());
    }

    @RequestMapping(value = "/product/{id}", method = RequestMethod.GET)
    public ResponseEntity getProductById(@PathVariable String id, HttpSession session) {
        checkUserAuth(session);

        Optional<Product> product = Optional.ofNullable(this.productRepository.findByProductId(id));

        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.ok("The product with id: " + id + " was not found.");
        }
    }

//    @RequestMapping(value = "/{title}", method = RequestMethod.GET)
//    public ResponseEntity getProductByTitle(@PathVariable String title) {
//
//        Optional<Product> product = Optional.ofNullable(this.productRepository.findByTitle(title));
//
//        if (product.isPresent()) {
//            return ResponseEntity.ok(product.get());
//        } else {
//            return ResponseEntity.ok("The product with id: " + title + " was not found.");
//        }
//    }
//
//    @RequestMapping(value = "/{category}", method = RequestMethod.GET)
//    public ResponseEntity getProductByCategory(@PathVariable String category) {
//
//        Optional<Product> product = Optional.ofNullable(this.productRepository.findByCategory(category));
//
//        if (product.isPresent()) {
//            return ResponseEntity.ok(product.get());
//        } else {
//            return ResponseEntity.ok("The product with id: " + category + " was not found.");
//        }
//    }
//
//    @RequestMapping(value = "/{author}", method = RequestMethod.GET)
//    public ResponseEntity getProductByAuthor(@PathVariable String author) {
//
//        Optional<Product> product = Optional.ofNullable(this.productRepository.findByAuthor(author));
//
//        if (product.isPresent()) {
//            return ResponseEntity.ok(product.get());
//        } else {
//            return ResponseEntity.ok("The product with id: " + author + " was not found.");
//        }
//    }

    @PostMapping("/product")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody ProductRequest productRequest, HttpSession session) {
        checkUserAuth(session);

        Product product = new Product();
        product.setTitle(productRequest.getTitle());
        product.setAuthor(productRequest.getAuthor());
        product.setPublishDate(productRequest.getPublishDate());
        product.setCategory(productRequest.getCategory());
        product.setDescription(productRequest.getDescription());

        // Save the user to the database
        Product savedProduct = this.productRepository.save(product);

        // Create the response message and redirect URL
        Map<String, String> response = new HashMap<>();
        response.put("message", "Add New Product Successfully");
        response.put("redirect", "../Product/product-index.html");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/product/{id}")
    public ResponseEntity updateProductById(@PathVariable String id, @RequestBody ProductRequest productRequest, HttpSession session) {
        checkUserAuth(session);

        Optional<Product> product = Optional.ofNullable(this.productRepository.findByProductId(id));
        if (product.isPresent()) {
            Product updatedProduct = product.get();

            updatedProduct.setId(id);
            updatedProduct.setTitle(productRequest.getTitle());
            updatedProduct.setAuthor(productRequest.getAuthor());
            updatedProduct.setPublishDate(productRequest.getPublishDate());
            updatedProduct.setCategory(productRequest.getCategory());
            updatedProduct.setDescription(productRequest.getDescription());
            this.productRepository.save(updatedProduct);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Product Updated successfully");
            response.put("redirect", "/admin/product-index.html");

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok("The user with id: " + id + " was not found.");
        }
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity deleteProductById(@PathVariable String id, HttpSession session) {
        checkUserAuth(session);
        Optional<Product> product =Optional.ofNullable(this.productRepository.findByProductId(id));

        if (product.isPresent()) {
            this.productRepository.deleteById(id);
            return ResponseEntity.ok("Success.");
        } else {
            return ResponseEntity.ok("The product with id: " + id + " was not found.");
        }
    }

    private void checkUserAuth(HttpSession session) {
        if (session.getAttribute("user") == null) {
            ResponseEntity.status(401).body("Unauthorized");
        }
    }
}
