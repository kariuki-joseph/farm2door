package com.example.farm2door.models;

public class PaymentItem {
    private String farmerId;
    private String farmerName;
    private double deliveryFees;
    private double itemsTotalCost;

    public PaymentItem() {
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public void setDeliveryFees(double deliveryFees) {
        this.deliveryFees = deliveryFees;
    }

    public void setItemsTotalCost(double itemsTotalCost) {
        this.itemsTotalCost = itemsTotalCost;
    }

    public String getFarmerId(){
        return farmerId;
    }
    public String getFarmerName() {
        return farmerName;
    }

    public double getDeliveryFees() {
        return deliveryFees;
    }

    public double getItemsTotalCost() {
        return itemsTotalCost;
    }
}
