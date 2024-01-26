package com.example.farm2door.models;

public class CartItem {
    private String productId;
    private String productName;
    private String productImage;
    private String unitName ="kg";
    private double productPrice;
    private int productQuantity = 1;
    private double productTotalPrice = 0;
    private String farmerId;

    public CartItem() {
    }

    public CartItem(String productId, String productName, double productPrice, String unitName, String productImage, String farmerId) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productTotalPrice = productPrice;
        this.unitName = unitName;
        this.productImage = productImage;
        this.farmerId = farmerId;
    }

    public String getProductId() {
        return productId;
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
    public String getFarmerId(){
        return farmerId;
    }
}
