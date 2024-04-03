package com.example.farm2door.models;

public class CartItem {
    private String productId;
    private String productName;
    private String productImage;
    private String unitName ="kg";
    private double productPrice;
    private double deliveryFees;
    private int productQuantity = 1;
    private double productTotalPrice = 0;
    private String farmerId;
    private String farmerName;

    private double latitude = -0.391396;
    private double longitude = 36.933992;

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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductTotalPrice(double productTotalPrice) {
        this.productTotalPrice = productTotalPrice;
    }

    public void setDeliveryFees(double deliveryFees){
        this.deliveryFees = deliveryFees;
    }
    public double getDeliveryFees(){ return deliveryFees;}
    public String getUnitName() {
        return unitName;
    }
    public String getFarmerId(){
        return farmerId;
    }
    public void setFarmerName(String farmerName){
        this.farmerName = farmerName;
    }
    public String getFarmerName() {return farmerName;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString(){
        return "{"+
                "productId: "+productId+
                ", productName: "+productName+
                ", productImage: "+productImage+
                ", productPrice: "+productPrice+
                ", productQuantity: "+productQuantity+
                ", productTotalPrice: "+productTotalPrice+
                ", deliveryFees: "+deliveryFees+
                ", unitName: "+unitName+
                ", farmerId: "+farmerId+
                ", farmerName: "+farmerName+
                ", orderLat: "+ latitude +
                ", orderLng: "+ longitude +
                "}";
    }
}
