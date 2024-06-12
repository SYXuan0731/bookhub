package com.example.bookhub.model;

import org.springframework.data.annotation.Id;

public class Product {

    @Id
    private String productId;
    private String title;
    private String author;
    private String publishDate;
    private String category;
    private String description;
    private String image;

    public Product() {}

    public Product(String id, String title, String author, String publishDate, String category, String description, String image) {
        this.productId = id;
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
        this.description = description;
        this.image = image;
    }

    public String getProductId() {
        return productId;
    }

    public void setId(String id) {
        this.productId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
