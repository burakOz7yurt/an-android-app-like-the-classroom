package com.example.plproject;

public class PostNode {
    private PostNode next;
    private String data;
    private int number;
    PostNode(String data)
    {
        this.data=data;
        next=null;
    }
    public PostNode getNext() {
        return next;
    }

    public void setNext(PostNode next) {
        this.next = next;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
