package com.example.fileservice.entity;

public class Access {

    long fileId;
    long userId;
    long grantedAccess;

    public Access(){

    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getGrantedAccess() {
        return grantedAccess;
    }

    public void setGrantedAccess(long grantedAccess) {
        this.grantedAccess = grantedAccess;
    }
}
