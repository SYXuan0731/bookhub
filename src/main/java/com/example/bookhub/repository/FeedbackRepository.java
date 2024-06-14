package com.example.bookhub.repository;

import com.example.bookhub.model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {
    List<Feedback> findByProductId(String productId);
    long countByProductId(String productId);
}
