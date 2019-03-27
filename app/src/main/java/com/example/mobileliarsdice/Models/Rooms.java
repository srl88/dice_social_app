package com.example.mobileliarsdice.Models;

public class Rooms {
    String id_1;
    String id_2;
    String name_1;
    String name_2;
    String url1;
    String url2;
    Boolean accepted1;
    Boolean accepted2;

    public Rooms(){

    }
    public Rooms(String id_1, String id_2, String name1, String name2,Boolean accepted1, Boolean accepted2) {
        this.id_1 = id_1;
        this.id_2 = id_2;
        this.name_1 = name1;
        this.name_2 = name2;
        this.accepted1 = accepted1;
        this.accepted2 = accepted2;
    }

    public Rooms(String id_1, String id_2, String name_1, String name_2,Boolean accepted1, Boolean accepted2, String url1, String url2) {
        this.id_1 = id_1;
        this.id_2 = id_2;
        this.name_1 = name_1;
        this.name_2 = name_2;
        this.url1 = url1;
        this.url2 = url2;
        this.accepted1 = accepted1;
        this.accepted2 = accepted2;
    }

    public String getName_1() {
        return name_1;
    }

    public void setName_1(String name_1) {
        this.name_1 = name_1;
    }

    public String getName_2() {
        return name_2;
    }

    public void setName_2(String name_2) {
        this.name_2 = name_2;
    }


    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public String getId_1() {
        return id_1;
    }

    public void setId_1(String id_1) {
        this.id_1 = id_1;
    }

    public String getId_2() {
        return id_2;
    }

    public void setId_2(String id_2) {
        this.id_2 = id_2;
    }

    public Boolean getAccepted1() {
        return accepted1;
    }

    public void setAccepted1(Boolean accepted) {
        this.accepted1 = accepted;
    }

    public Boolean getAccepted2() {
        return accepted2;
    }

    public void setAccepted2(Boolean rejected) {
        this.accepted2 = rejected;
    }
}
