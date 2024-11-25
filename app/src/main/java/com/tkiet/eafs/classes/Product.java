package com.tkiet.eafs.classes;
public class Product {
    private String productId; // Add this field
    private String title;
    private String description;
    private String price;
    private String category;
    private String imageUrl;
    private String type;  // Add this field

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }

    private String addedBy;
    // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    public Product() {
    }

    // Constructor with productId
    public Product(String productId, String title, String description, String price, String category, String imageUrl) {
        this.productId = productId; // Initialize the productId
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    // Getter method for productId
    public String getProductId() {
        return productId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
