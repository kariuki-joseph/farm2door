package com.example.farm2door.models;

public class InventoryItem {
    private String productId;
    private String name;
   private double price;
   private int remainingQuantity;
    private String unitName;
    private String imageURL;

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setRemainingQuantity(int remainingQuantity) {
        this.remainingQuantity = remainingQuantity;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getProductId() {
        return productId;
    }
    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getRemainingQuantity() {
        return remainingQuantity;
    }

    public String getUnitName() {
        return unitName;
    }

    public String getImageURL() {
        return imageURL;
    }

}
