package com.example.productmanagementservice.dto;

import com.example.productmanagementservice.entity.User;

public class UserRequest extends User {

    int id;
    String login;
    String password;
    int security;
    String name;
    String description;

    public UserRequest() {
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
