package com.example.fileservice.entity;

public class FileUser {

    public enum accessibility{
        CLOSED(0),
        OPEN(1);

        int access;
        accessibility(int access){
            this.access = access;
        }

        public int getAccess() {
            return access;
        }
    }
    long id;
    long userId;
    String name;
    long size;
    int accessibility;

    public FileUser() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }
}
