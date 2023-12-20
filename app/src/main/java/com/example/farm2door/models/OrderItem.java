package com.example.farm2door.models;

public class OrderItem {

    private String orderNumber;
    private String name;
    private double price;
    private String unitName;
    private int quantity;
    private String orderDate;
    private String imageURL;

    public OrderItem() {
    }

    public OrderItem(String orderNumber, String name, double price, String unitName, int quantity, String orderDate, String imageURL) {
        this.orderNumber = orderNumber;
        this.name = name;
        this.price = price;
        this.unitName = unitName;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.imageURL = imageURL;
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
}
