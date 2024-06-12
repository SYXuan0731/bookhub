package com.example.bookhub.repository;

import com.example.bookhub.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
    Product findById(String id);
}
