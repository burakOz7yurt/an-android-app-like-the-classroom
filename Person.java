package com.example.plproject;

public class Person {

    private String name;
    private String surname;
    private String email;
    private String password;
    public Person()
    {

    }
    public Person(String name,String surname,String email,String password)
    {
        this.name=name;
        this.surname=surname;
        this.email=email;
        this.password=password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getSurname() {
        return surname;
    }

}