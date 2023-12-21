package com.example.farm2door.models;

public class InventoryItem {
    private String name;
   private double price;
   private int remainingQuantity;
    private String unitName;
    private String imageURL;

    public InventoryItem(String name, double price, int remainingQuantity, String unitName, String imageURL) {
        this.name = name;
        this.price = price;
        this.remainingQuantity = remainingQuantity;
        this.unitName = unitName;
        this.imageURL = imageURL;
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
