package com.example.plproject;

public class Announce extends Page{
    private String email;
    private String data;
    Announce(String code,String email) {
        super(code, "Announce Name");
        this.email=email;
        this.data="announce data";
         }

    public String getEmail() {
        return email;
    }

    public void setEmail(String myEmail) {
        this.email = myEmail;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
