package com.example.augusto.clientapp;

public class Product {

    private int id;
    private String code;
    private String name;
    private float price;
    private int quantity;


    // constructors
    public Product() {
        this.id = 0;
        this.code = null;
        this.name = null;
        this.price = 0;
        this.quantity = 0;

    }

    public Product(String code,String name, float price, int quantity) {
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product(int id,String code,String name, float price, int quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    // getters
    public long getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public float getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

}
