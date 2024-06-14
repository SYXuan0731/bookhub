package com.example.bookhub.controller;

import com.example.bookhub.model.Feedback;
import com.example.bookhub.repository.FeedbackRepository;
import com.example.bookhub.resource.FeedbackRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        Feedback feedback = new Feedback();
        feedback.setUserId(feedbackRequest.getUserId());
        feedback.setProductId(feedbackRequest.getProductId());
        feedback.setComment(feedbackRequest.getComment());
        feedback.setCreatedDate(feedbackRequest.getCreatedDate());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return ResponseEntity.ok(savedFeedback);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Feedback>> getFeedbackByProductId(@PathVariable String productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductId(productId);
        return ResponseEntity.ok(feedbacks);
    }

    @GetMapping("/count/{productId}")
    public ResponseEntity<Long> getFeedbackCountByProductId(@PathVariable String productId) {
        long count = feedbackRepository.countByProductId(productId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable String id) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isPresent()) {
            return ResponseEntity.ok(feedback.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(@PathVariable String id, @RequestBody FeedbackRequest feedbackRequest) {
        Optional<Feedback> existingFeedback = feedbackRepository.findById(id);
        if (existingFeedback.isPresent()) {
            Feedback feedback = existingFeedback.get();
            feedback.setComment(feedbackRequest.getComment());
            feedback.setCreatedDate(feedbackRequest.getCreatedDate());
            Feedback updatedFeedback = feedbackRepository.save(feedback);
            return ResponseEntity.ok(updatedFeedback);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable String id) {
        Optional<Feedback> existingFeedback = feedbackRepository.findById(id);
        if (existingFeedback.isPresent()) {
            feedbackRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
