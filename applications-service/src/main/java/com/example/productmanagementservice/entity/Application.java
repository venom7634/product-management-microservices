package com.example.productmanagementservice.entity;

public class Application {

    public enum statusApp {
        CREATED(0),
        SENT(1),
        APPROVED(2),
        NEGATIVE(3);

        private int status;

        statusApp(int status) {
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }

    private int clientId;
    int status;
    int id;
    private String product;
    private String limit;
    private String amount;
    private String timeInMonth;

    private String description;

    public Application() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTimeInMonth() {
        return timeInMonth;
    }

    public void setTimeInMonth(String timeInMonth) {
        this.timeInMonth = timeInMonth;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
