package com.example.bookhub.controller;

import com.example.bookhub.model.Product;
import com.example.bookhub.repository.ProductRepository;
import com.example.bookhub.resource.ProductRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class ProductController {
    private final ProductRepository productRepository;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/src/main/resources/static/images/";

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/product-redirect")
    public ResponseEntity redirect(HttpSession session) {
        checkUserAuth(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Redirected to successfully");
        response.put("redirect", "../Admin/product-index.html");

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

    @PostMapping("/product")
    public ResponseEntity<Map<String, String>> createProduct(@RequestParam String title, @RequestParam String author, @RequestParam String publishDate, @RequestParam String category, @RequestParam String description, @RequestParam("image") MultipartFile image, HttpSession session) throws Exception {
        checkUserAuth(session);

        // Add file to local
        StringBuilder fileName = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, image.getOriginalFilename());
        fileName.append(image.getOriginalFilename());
        Files.write(fileNameAndPath, image.getBytes());

        Product product = new Product();
        product.setTitle(title);
        product.setAuthor(author);
        product.setPublishDate(publishDate);
        product.setCategory(category);
        product.setDescription(description);
        product.setImage("/images/" +fileName.toString());

        // Save the user to the database
        Product savedProduct = this.productRepository.save(product);

        // Create the response message and redirect URL
        Map<String, String> response = new HashMap<>();
        response.put("message", "Add New Product Successfully");
        response.put("redirect", "../Admin/product-index.html");

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/product/{id}")
    public ResponseEntity updateProductById(@PathVariable String id, @RequestParam String title, @RequestParam String author, @RequestParam String publishDate, @RequestParam String category, @RequestParam String description, @RequestParam("image") MultipartFile image, HttpSession session) throws Exception {
        checkUserAuth(session);



        // Add file to local
        StringBuilder fileName = new StringBuilder();
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, image.getOriginalFilename());
        fileName.append(image.getOriginalFilename());
        Files.write(fileNameAndPath, image.getBytes());

        Optional<Product> product = Optional.ofNullable(this.productRepository.findByProductId(id));
        if (product.isPresent()) {
            Product updatedProduct = product.get();

            updatedProduct.setTitle(title);
            updatedProduct.setAuthor(author);
            updatedProduct.setPublishDate(publishDate);
            updatedProduct.setCategory(category);
            updatedProduct.setDescription(description);
            updatedProduct.setImage("/images/" +fileName.toString());
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
        Optional<Product> product = Optional.ofNullable(this.productRepository.findByProductId(id));

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

//    public String uploadImage(MultipartFile file) throws IOException {
//        StringBuilder fileNames = new StringBuilder();
//        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
//        fileNames.append(file.getOriginalFilename());
//        Files.write(fileNameAndPath, file.getBytes());
//        model.addAttribute("msg", "Uploaded images: " + fileNames.toString());
//        return "imageupload/index";
//    }
}
