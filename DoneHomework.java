package com.example.plproject;

public class DoneHomework extends Homework{
    private String email;
    public DoneHomework(String data,String email) {
        super(data);
    }
    public DoneHomework() {
        super(" ");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
