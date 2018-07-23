package com.example.productsservice.dto;

import com.example.productsservice.entity.Statistic;

public class StatisticResponse extends Statistic {

    String reason;
    String product;
    double percent;

    public StatisticResponse() {

    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getName() {
        return product;
    }

    public void setName(String name) {
        this.product = name;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
