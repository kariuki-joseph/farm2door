package com.example.farm2door.models;

public class CustomerFeedback {
    private String customerName;
    private String customerFeedback;
    private float customerRating;
    private String date;

    public CustomerFeedback(String customerName, String customerFeedback, float customerRating, String date) {
        this.customerName = customerName;
        this.customerFeedback = customerFeedback;
        this.customerRating = customerRating;
        this.date = date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerFeedback() {
        return customerFeedback;
    }

    public float getCustomerRating() {
        return customerRating;
    }

    public String getDate() {
        return date;
    }
}
