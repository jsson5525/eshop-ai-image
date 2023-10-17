package com.fyp.eshop.model;

public class Item {
    private String itemID;
    private String itemTitle;
    private double price;

    public Item(String itemID, String itemTitle, double price) {
        this.itemID = itemID;
        this.itemTitle = itemTitle;
        this.price = price;
    }

    public Item(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
