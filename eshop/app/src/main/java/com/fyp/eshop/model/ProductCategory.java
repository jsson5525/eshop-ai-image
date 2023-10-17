package com.fyp.eshop.model;

import java.util.List;

public class ProductCategory {
    private String catTitle;
    private String catID;
    private List<Product> nesData;

    public ProductCategory(String catTitle, List<Product> nesData) {
        this.catTitle = catTitle;
        this.nesData = nesData;
    }

    public ProductCategory(String catTitle, String catID, List<Product> nesData) {
        this.catTitle = catTitle;
        this.catID = catID;
        this.nesData = nesData;
    }

    public String getCatTitle() {
        return catTitle;
    }

    public void setCatTitle(String catTitle) {
        this.catTitle = catTitle;
    }

    public String getCatID() {
        return catID;
    }

    public void setCatID(String catID) {
        this.catID = catID;
    }

    public List<Product> getNesData() {
        return nesData;
    }

    public void setNesData(List<Product> nesData) {
        this.nesData = nesData;
    }
}
