package com.example.farm2door.models;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {

    private String productId;
    private String farmerId;
    private String farmerName;
    private String name;
    private double price;
    private int totalInStock;
    private List<String> images;
    private String description;
    private String unitName = "kg";
    private double latitude, longitude;

    public Product() {
    }

    public Product(String name, double price, String unitName, List<String> images) {
        this.name = name;
        this.price = price;
        this.unitName = unitName;
        this.images = images;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }
    public void setFarmerName(String farmerName){
        this.farmerName = farmerName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTotalInStock(int totalInStock) {
        this.totalInStock = totalInStock;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductId() { return productId; }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
    public void setLatitude(double latitude){
        this.latitude = latitude;
    }
    public void setLongitude(double longitude){
        this.longitude = longitude;
    }
    public String getName() {
        return name;
    }
    public String getFarmerId() {
        return farmerId;
    }
    public String getFarmerName() {return this.farmerName;}
    public double getPrice() {
        return price;
    }

    public int getTotalInStock() { return totalInStock; }

    public List<String> getImages() {
        return images;
    }

    public String getDescription() {
        return description;
    }

    public String getUnitName() {
        return unitName;
    }
    public double getLatitude(){
        return latitude;
    }
    public double getLongitude(){
        return longitude;
    }
}

