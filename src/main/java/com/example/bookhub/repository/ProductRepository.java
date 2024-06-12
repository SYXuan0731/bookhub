package com.example.bookhub.repository;

import com.example.bookhub.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Product findByProductId(String id);
//    Product findByTitle(String title);
//    Product findByAuthor(String author);
//    Product findByCategory(String category);
}
