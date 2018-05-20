package com.heady.sat.model;

import java.util.List;

public class Category {

    private String id;
    private String name;
    private List<Product> products = null;
    private List<String> childCategories = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<String> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<String> childCategories) {
        this.childCategories = childCategories;
    }

}
