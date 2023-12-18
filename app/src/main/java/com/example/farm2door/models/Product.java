package com.example.farm2door.models;

import java.io.Serializable;

public class Product implements Serializable {

    private String productId;
    private String name;
    private double price;
    private int totalQuantity;
    private String imageURL;
    private String description;
    private String unitName = "kg";

    public Product() {
    }

    public Product(String name, double price, String unitName, String imageURL) {
        this.name = name;
        this.price = price;
        this.unitName = unitName;
        this.imageURL = imageURL;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() { return productId; }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getTotalQuantity() { return totalQuantity; }

    public String getImageURL() {
        return imageURL;
    }

    public String getDescription() {
        return description;
    }

    public String getUnitName() {
        return unitName;
    }
}

