package com.example.plproject;

public class Message {
    private String whoPerson;
    private String data="";
    private String name;

    public Message(String whoPerson) {
        this.whoPerson = whoPerson;
    }

    public Message(String whoPerson, String data) {
        this.whoPerson = whoPerson;
        this.data = data;
        this.name=name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWhoPerson() {
        return whoPerson;
    }

    public void setWhoPerson(String whoPerson) {
        this.whoPerson = whoPerson;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
