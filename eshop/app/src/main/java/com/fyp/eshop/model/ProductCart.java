package com.fyp.eshop.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductCart implements Serializable {

    public ProductCart() {
    }

    @ServerTimestamp
    private int index;
    private String docId;
    private Product product;
    private String productDocId;
    private String productId;
    private int qty;
    private int totalPrice;
    private Timestamp createdDt;
}
