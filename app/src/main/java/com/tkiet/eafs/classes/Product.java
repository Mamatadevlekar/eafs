package com.tkiet.eafs.classes;


public class Product {
    private String productId;
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    private String addedBy;

    // Default constructor required for calls to DataSnapshot.getValue(Product.class)
    public Product() {
    }

    public Product(String productId, String title, String description, String price, String imageUrl, String addedBy) {
        this.productId = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.addedBy = addedBy;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(String addedBy) {
        this.addedBy = addedBy;
    }
}
