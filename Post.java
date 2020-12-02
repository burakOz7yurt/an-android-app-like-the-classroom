package com.example.plproject;

public class Post extends Page {
    private String data;
    private String term;
    private String subject;
    Post(String code,PostList postList) {
        super(code, "Post Name");
        data="";
        term="post term";
        subject="post subject";
    }





    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
