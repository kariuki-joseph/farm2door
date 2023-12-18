package com.example.farm2door.models;

public class CartItem {
    private String id;
    private String productName;
    private String productImage;
    private String unitName ="kg";
    private double productPrice;
    private int productQuantity = 1;
    private double productTotalPrice = 0;

    public CartItem() {
    }

    public CartItem(String id, String productName, double productPrice, String unitName, String productImage) {
        this.id = id;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productTotalPrice = productPrice;
        this.unitName = unitName;
        this.productImage = productImage;
    }

    public String getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getProductTotalPrice() {
        return productTotalPrice;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductTotalPrice(double productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }
    public String getUnitName() {
        return unitName;
    }
}
