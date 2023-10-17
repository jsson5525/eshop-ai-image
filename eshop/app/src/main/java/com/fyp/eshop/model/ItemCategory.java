package com.fyp.eshop.model;

import java.util.List;

public class ItemCategory {
    private String catTitle;
    private List<Item> nesData;

    public ItemCategory(String catTitle, List<Item> nesData) {
        this.catTitle = catTitle;
        this.nesData = nesData;
    }

    public String getCatTitle() {
        return catTitle;
    }

    public void setCatTitle(String catTitle) {
        this.catTitle = catTitle;
    }

    public List<Item> getNesData() {
        return nesData;
    }

    public void setNesData(List<Item> nesData) {
        this.nesData = nesData;
    }
}
