package com.example.plproject;

public class Page {
    private String name;
    private String code;

    Page(String code,String name)
    {
        this.name=name;
        this.code=code;

    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
