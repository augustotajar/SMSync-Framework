package com.server.augusto.smserver;

public class User {

    int id;
    String name;
    String phone_number;

    // constructors
    public User() {
    }

    public User(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    public User(int id, String name, String phone_number) {
        this.id = id;
        this.name = name;
        this.phone_number = phone_number;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    // getters
    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getPhone_number() {
        return this.phone_number;
    }
}