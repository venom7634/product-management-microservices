package com.example.datausersservice.dto;

public class Account {

    String login;
    String password;

    public Account(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Account() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
