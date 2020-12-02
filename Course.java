package com.example.plproject;

import java.util.ArrayList;

public class Course extends Page{


    private String term;

    Course(String code)
    {
        super(code,"Course Name");
        this.term="Course Term";
    }


    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
