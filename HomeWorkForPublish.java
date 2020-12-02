package com.example.plproject;

public class HomeWorkForPublish extends Homework{
    private String dueDate;
    private String name;
    private String code;


    public HomeWorkForPublish(String code) {
        super(" ");
        this.code = code;
        dueDate="24.06.2020";
        name="NAME";

    }

    public HomeWorkForPublish(String dueDate, String name, String code, String data) {
        super(data);
        this.dueDate = dueDate;
        this.name = name;
        this.code = code;
    }


    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
