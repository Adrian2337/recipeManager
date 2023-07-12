package com.example.recipemanagerspring;

public class Product {
    private int id;
    private String name;
    private String quantityName;
    private int quantity;


    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return this.name;
    }

    public void setName (String name){
        this.name = name;
    }

    public String getQuantityName() {
        return quantityName;
    }

    public void setQuantityName(String quantityName) {
        this.quantityName = quantityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
