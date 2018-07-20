package com.example.productsservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Product {

    public enum type {
        DEBIT_CARD("debit-card", 1),
        CREDIT_CARD("credit-card", 2),
        CREDIT_CASH("credit-cash", 3);

        private String name;
        private int id;

        type(String name, int id){
            this.name = name;
        }

        public String getName(){
            return this.name;
        }

        public int getId() {
            return id;
        }
    }

    String description;

    @JsonIgnore
    Integer id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    public Product() {

    }

    public Product(String description, Integer id, String name) {
        this.description = description;
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
