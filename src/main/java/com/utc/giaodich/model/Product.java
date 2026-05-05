package com.utc.giaodich.model;

public class Product {
    private int id;
    private int sellerId;
    private String sellerUsername; // Thêm username người bán để chat P2P chính xác
    private String name;
    private double price;
    private int quantity;
    private String description;
    private String imagePath;
    private String status;

    public Product() {}

    public Product(int id, int sellerId, String name, double price, int quantity, String description, String imagePath, String status) {
        this.id = id;
        this.sellerId = sellerId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
    }

    // --- GETTER & SETTER ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSellerId() { return sellerId; }
    public void setSellerId(int sellerId) { this.sellerId = sellerId; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}