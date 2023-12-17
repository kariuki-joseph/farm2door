package com.example.farm2door.models;

public class Product {

    private String productId;
    private String name;
    private String price;
    private String quantity;
    private String imageURL;
    private String description;

    public Product() {
    }

    public Product(String name, String price,String imageURL) {
        this.name = name;
        this.price = price;
        this.imageURL = imageURL;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() { return productId; }


    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() { return quantity; }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }
}

