package com.example.mobileliarsdice.Models;

public abstract class UserInfo {
    String id;
    String userName;
    String url;

    public UserInfo(){}

    public UserInfo(String id, String userName, String url) {
        this.id = id;
        this.userName = userName;
        this.url = url;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //public UserInfo FuckIt() {return this;}
}