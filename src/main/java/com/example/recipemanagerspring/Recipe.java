package com.example.recipemanagerspring;

import java.util.List;

public class Recipe {

    //TODO support for multiple categories per recipe

    private int id;
    private String name;
    private String shortDescription;
    private String recipeText;
    private List<Product> products;
    private Category category;


    public int getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category categoryObject) {
        this.category = categoryObject;
    }

    public String getName() {
        return name;
    }

    public String getRecipeText() {
        return recipeText;
    }

    public String getShortDescription() {
        return shortDescription;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRecipeText(String recipeText) {
        this.recipeText = recipeText;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }


    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }


}
