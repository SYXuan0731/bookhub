package com.example.bookhub.resource;

import org.springframework.data.annotation.Id;

public class ProductRequest {

    private String title;
    private String author;
    private String publishDate;
    private String category;
    private String description;

    public ProductRequest() {}

    public ProductRequest(String title, String author, String publishDate, String category, String description) {
        this.title = title;
        this.author = author;
        this.publishDate = publishDate;
        this.category = category;
        this.description = description;
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

}
