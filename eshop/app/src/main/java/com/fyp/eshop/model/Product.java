package com.fyp.eshop.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Product implements Serializable {

    public Product() {
    }

    private String docId;
    private String productId;
    private String productName;
    private String productDesc;
    private String productThumb;
    private String productCategory;
    private int selling_price;
}
