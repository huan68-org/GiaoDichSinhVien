package com.utc.giaodich.model;

public class Product {
    private int id;
    private int sellerId;
    private String name;
    private double price;
    private String description;
    private String imagePath; // Đường dẫn ảnh trong thư mục uploads
    private String status;    // 'AVAILABLE' hoặc 'SOLD'

    public Product() {}

    public Product(int id, int sellerId, String name, double price, String description, String imagePath, String status) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
    }

    // Tương tự, dùng Cmd + N để tạo Getter/Setter nhé

    public int getId() {
        return id;
    }

    public int getSellerId() {
        return sellerId;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}