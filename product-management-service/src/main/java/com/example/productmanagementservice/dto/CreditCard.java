package com.example.productmanagementservice.dto;


public class CreditCard {

    int limit;

    public CreditCard() {

    }

    public CreditCard(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
