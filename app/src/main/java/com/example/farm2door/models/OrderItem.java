package com.example.farm2door.models;

public class OrderItem {

    private String id;
    private String orderNumber;
    private String name;
    private double price;
    private String unitName;
    private int quantity;
    private String orderDate;
    private String imageURL;

    // location of order delivery
    private double longitude;
    private double latitude;
    private String farmerId;
    private String customerId;

    public OrderItem() {
        // required empty public constructor
    }
    public void setId(String id) {
        this.id = id;
    }

    // setters
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    // getters
    public String getId() {
        return id;
    }
    public String getOrderNumber() {
        return orderNumber;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getUnitName() {
        return unitName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getImageURL() {
        return imageURL;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public String getCustomerId() {
        return customerId;
    }

}
