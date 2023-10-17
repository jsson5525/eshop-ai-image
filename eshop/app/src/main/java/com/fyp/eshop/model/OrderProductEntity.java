package com.fyp.eshop.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class OrderProductEntity implements Serializable {

    public OrderProductEntity() {
    }

    private String productDocId;
    private String productId;
    private String productName;
    private String productThumb;
    private int qty;
    private int selling_price;
    private int total_price;
}
