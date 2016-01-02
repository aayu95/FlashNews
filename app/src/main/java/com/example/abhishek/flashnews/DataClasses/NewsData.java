package com.example.abhishek.flashnews.DataClasses;

/**
 * Created by abhishek on 1/2/16.
 */
public class NewsData {
    private String title;
    private String author;
    private String category;
    private String url;

    public String getTitle() { return this.title; }
    public void setTitle(String brand) { this.title = brand; }
    public String getAuthor() { return author; }
    public void setAuthor(String model) { this.author = model; }
    public String getCategory() { return category; }
    public void setCategory(String price) { this.category = price; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}