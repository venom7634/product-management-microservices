package com.example.productmanagementservice.dto;


public class CreditCash {

    int timeInMonth;
    int amount;

    public CreditCash() {

    }

    public int getTimeInMonth() {
        return timeInMonth;
    }

    public void setTimeInMonth(int limit) {
        this.timeInMonth = limit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
