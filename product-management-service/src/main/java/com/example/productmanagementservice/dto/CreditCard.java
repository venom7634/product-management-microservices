package com.example.productmanagementservice.dto;

import com.example.productmanagementservice.entity.Product;

public class CreditCard extends Product {

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
